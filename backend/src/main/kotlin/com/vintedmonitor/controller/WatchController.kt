package com.vintedmonitor.controller

import com.vintedmonitor.dto.WatchRequest
import com.vintedmonitor.dto.WatchResponse
import com.vintedmonitor.service.WatchService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/watches")
class WatchController(private val watchService: WatchService) {

    @GetMapping
    fun list(): List<WatchResponse> =
        watchService.listAll().map(WatchResponse::from)

    @PostMapping
    fun create(@Valid @RequestBody req: WatchRequest): WatchResponse =
        WatchResponse.from(watchService.create(req))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody req: WatchRequest
    ): ResponseEntity<WatchResponse> {
        val updated = watchService.update(id, req) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(WatchResponse.from(updated))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> =
        if (watchService.delete(id)) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
}
