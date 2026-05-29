# Vinted Monitor

Local Kotlin + Vue app that polls **Vinted.hu** every 5 minutes for new listings
matching a configurable watch list (e.g. board games like *Settlers of Catan*) and
emails a digest when fresh matches appear.

```
adroit/vinted-monitor/
├── backend/             Spring Boot 3 + Kotlin + H2 + Spring Scheduling + JavaMail
├── frontend/            Vue 3 + Vite + Pinia + Tailwind
├── docker-compose.yml   One-command stack (backend + nginx-served frontend)
└── .env.example         Copy to .env and fill in your Gmail credentials
```

## Quickstart with Docker (recommended)

If you just want it running:

1. Install **Docker Desktop** (Windows / Mac) or `docker` + `docker compose` (Linux).
2. Copy the env template and fill in your Gmail credentials:
   ```powershell
   Copy-Item .env.example .env
   notepad .env
   ```
3. Bring the stack up:
   ```powershell
   docker compose up -d --build
   ```
4. Open **http://localhost:5173**.

That's it — the backend's H2 database is persisted to the `backend-data` Docker
volume, so your watch list and seen-items dedup table survive restarts.

Useful commands:

```powershell
docker compose logs -f backend    # tail backend logs
docker compose logs -f frontend   # tail nginx access log
docker compose down               # stop (keeps the data volume)
docker compose down -v            # stop AND wipe the H2 database
docker compose up -d --build      # rebuild after code changes
```

The frontend container runs nginx, which serves the built Vue app and
reverse-proxies `/api/*` to `backend:8080` on the internal compose network.
The backend port is **not exposed to the host** by default — uncomment the
`ports:` block in `docker-compose.yml` if you want to `curl localhost:8080`
directly.

---

## Manual / development setup

Use this if you want hot-reload while developing (instead of Docker).

### Prerequisites

- **JDK 17+** (Temurin / Microsoft / Oracle)
- **Node 18+** with npm
- A **Gmail account with an app password** (Google Account → Security → 2-step verification → App passwords)

### 1. Configure email credentials

Set these environment variables before launching the backend so the SMTP sender
and the recipient are wired up. (PowerShell syntax:)

```powershell
$env:MAIL_USERNAME = "your.address@gmail.com"
$env:MAIL_PASSWORD = "xxxx xxxx xxxx xxxx"   # 16-char Gmail app password
$env:NOTIFY_TO     = "your.address@gmail.com"
```

You can change `NOTIFY_TO` later from the Settings page in the UI.

### 2. Run the backend

From the repository root:

```powershell
cd backend
.\gradlew.bat bootRun
```

> First run: if `gradlew.bat` doesn't exist yet, run `gradle wrapper --gradle-version 8.10` once
> (or open the folder in IntelliJ IDEA — it will generate the wrapper automatically).

The backend listens on **http://localhost:8080** and persists everything to
`backend/data/vinted-monitor.mv.db` (H2 file mode).

### 3. Run the frontend

In a second terminal:

```powershell
cd frontend
npm install
npm run dev
```

Open **http://localhost:5173**. The Vite dev server proxies `/api/*` to the
backend, so CORS is not an issue in development.

## How it works

1. The backend's `PollingScheduler` fires every 5 minutes (`vinted.poll-interval-ms`).
2. For each enabled watch, `VintedClient` calls
   `GET https://www.vinted.hu/api/v2/catalog/items?search_text=...&order=newest_first`.
   - Cookies/session are bootstrapped by GETting the homepage first.
   - Cookies are kept in memory via a custom `CookieJar`; on 401/403 the jar is
     cleared and the request retried once.
3. `WatchService.recordNewMatches` applies the price filter and (when
   **Hungarian listings only** is enabled) drops listings from non-HUF sellers,
   deduplicates against the `seen_items` table so we never email about the same
   listing twice, then writes the new ones to `matched_listings`.
4. After all watches are polled, `EmailService.sendDigest` sends a single email
   summarizing the new matches per watch.

## Adjusting the poll interval

Edit `backend/src/main/resources/application.yml`:

```yaml
vinted:
  poll-interval-ms: 300000   # 5 min — drop or raise as you like
```

Keep it sensible — Vinted's anti-abuse will rate-limit aggressive polling.

## Hungarian-only filter

Vinted.hu also surfaces Greek, Polish, Romanian, etc. listings. With **Hungarian
listings only** enabled (the default), each listing is filtered by the **seller's
currency**: vinted.hu prices everything in HUF and attaches a `conversion` block
carrying the seller's own currency whenever it differs. So a non-HUF seller currency
(`PLN` = Poland, `RON` = Romania, `EUR` = eurozone such as Greece, …) means the
seller is abroad and the listing is dropped. A HUF seller (no conversion block) is
treated as domestic. This catches foreign listings even when the title looks
language-neutral.

Toggle it from the **Settings** page, or set the startup default with the
`FILTER_HUNGARIAN_ONLY` env var (`true`/`false`). Like the recipient setting,
a runtime toggle is held in memory and resets to the env/default on restart.

> Note: a HUF-based seller whose listing title happens to be in another language
> (e.g. a Romanian-titled item sold from Hungary) is kept — the seller is domestic.

> **Inspecting the raw API.** Vinted's API is undocumented. Set
> `VINTED_LOG_RAW_ITEMS=true` (or `vinted.log-raw-items` in `application.yml`), run
> one poll, and the backend log prints a sample item's raw JSON. Turn it back off
> afterwards — it's noisy.

## API endpoints (for reference)

| Method | Path                       | Purpose                          |
|--------|----------------------------|----------------------------------|
| GET    | `/api/watches`             | List all watches                 |
| POST   | `/api/watches`             | Create a watch                   |
| PUT    | `/api/watches/{id}`        | Update a watch                   |
| DELETE | `/api/watches/{id}`        | Delete a watch                   |
| GET    | `/api/feed?limit=50`       | Recent matched listings          |
| GET    | `/api/settings`            | Current settings (recipient, filter, poll interval) |
| PUT    | `/api/settings`            | Update recipient and/or the Hungarian-only filter   |
| POST   | `/api/settings/poll-now`   | Trigger a poll immediately       |

## Caveats

- Vinted's `/api/v2/catalog/items` endpoint is **unofficial**. If they change
  the schema or tighten anti-bot measures, you'll see warnings in the backend
  log and may need to adjust `VintedClient` (e.g. add an `X-CSRF-Token` header).
- 5-minute polling per watch with 2.5 s spacing between watches is well within
  polite limits for personal use. Don't crank it down to seconds.
- Email digests are sent **per polling tick**, not per match — so if 3 listings
  appear in one tick, you get one email summarizing all three.
