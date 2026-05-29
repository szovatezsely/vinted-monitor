/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,ts,js}'],
  theme: {
    extend: {
      colors: {
        // Vinted-style teal palette.
        vinted: {
          DEFAULT: '#007782',
          50: '#e7f4f5',
          100: '#c5e4e7',
          200: '#9bd2d7',
          500: '#09b1ba',
          600: '#007782',
          700: '#015f68',
          800: '#01464d'
        }
      }
    }
  },
  plugins: []
}
