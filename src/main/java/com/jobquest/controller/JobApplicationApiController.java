package com.jobquest.controller;

import com.jobquest.model.JobApplication;
import com.jobquest.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API Controller for JobQuest.
 *
 * Provides JSON endpoints consumed by the Kanban board's drag-and-drop
 * and the analytics charts (Chart.js). Also serves as a standalone API
 * layer if a separate JS frontend is used in the future.
 */
@RestController
@RequestMapping("/api/applications")
public class JobApplicationApiController {

    private final JobApplicationService service;

    public JobApplicationApiController(JobApplicationService service) {
        this.service = service;
    }

    // ── CRUD Endpoints ────────────────────────────────────────────

    /** GET /api/applications — List all applications. */
    @GetMapping
    public List<JobApplication> getAll() {
        return service.findAll();
    }

    /** GET /api/applications/{id} — Get a single application. */
    @GetMapping("/{id}")
    public JobApplication getById(@PathVariable Long id) {
        return service.findById(id);
    }

    /** POST /api/applications — Create a new application. */
    @PostMapping
    public JobApplication create(@Valid @RequestBody JobApplication application) {
        return service.save(application);
    }

    /** PUT /api/applications/{id} — Update an existing application. */
    @PutMapping("/{id}")
    public JobApplication update(@PathVariable Long id,
            @Valid @RequestBody JobApplication application) {
        return service.update(id, application);
    }

    /** DELETE /api/applications/{id} — Delete an application. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── Status Update (Kanban) ────────────────────────────────────

    /** PATCH /api/applications/{id}/status — Update only the status field. */
    @PatchMapping("/{id}/status")
    public JobApplication updateStatus(@PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return service.updateStatus(id, body.get("status"));
    }

    // ── Statistics & Analytics ────────────────────────────────────

    /** GET /api/applications/stats — Dashboard statistics. */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return service.getDashboardStats();
    }

    /** GET /api/applications/analytics/monthly — Monthly application counts. */
    @GetMapping("/analytics/monthly")
    public Map<String, Long> getMonthlyStats() {
        return service.getMonthlyApplicationCounts();
    }

    /** GET /api/applications/analytics/status — Status distribution. */
    @GetMapping("/analytics/status")
    public Map<String, Long> getStatusDistribution() {
        return service.getStatusDistribution();
    }
}
