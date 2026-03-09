package com.jobquest.service;

import com.jobquest.model.JobApplication;
import com.jobquest.repository.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Business-logic layer for JobApplication operations.
 *
 * Encapsulates CRUD, filtering, statistics computation,
 * and file-upload handling so the controllers stay thin.
 */
@Service
public class JobApplicationService {

    private final JobApplicationRepository repository;
    private final Path uploadDir;

    public JobApplicationService(JobApplicationRepository repository,
            @Value("${file.upload-dir:uploads}") String uploadDir) {
        this.repository = repository;
        this.uploadDir = Paths.get(uploadDir);
        // Ensure the upload directory exists on startup
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
        }
    }

    // ── CRUD ──────────────────────────────────────────────────────

    /** Return every application in the database. */
    public List<JobApplication> findAll() {
        return repository.findAll();
    }

    /** Find a single application by its ID; throws if not found. */
    public JobApplication findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Application not found with id: " + id));
    }

    /** Persist a new application. */
    public JobApplication save(JobApplication application) {
        return repository.save(application);
    }

    /** Update an existing application's fields. */
    public JobApplication update(Long id, JobApplication updated) {
        JobApplication existing = findById(id);
        existing.setCompanyName(updated.getCompanyName());
        existing.setRole(updated.getRole());
        existing.setDateApplied(updated.getDateApplied());
        existing.setStatus(updated.getStatus());
        existing.setJobLink(updated.getJobLink());
        existing.setNotes(updated.getNotes());
        return repository.save(existing);
    }

    /** Delete an application by ID. */
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // ── Filtering ─────────────────────────────────────────────────

    /**
     * Filter applications by status and/or company name.
     * Both parameters are optional — pass null or empty to skip that filter.
     */
    public List<JobApplication> filter(String status, String company) {
        boolean hasStatus = status != null && !status.isBlank();
        boolean hasCompany = company != null && !company.isBlank();

        if (hasStatus && hasCompany) {
            return repository.findByStatusAndCompanyNameContainingIgnoreCase(status, company);
        } else if (hasStatus) {
            return repository.findByStatus(status);
        } else if (hasCompany) {
            return repository.findByCompanyNameContainingIgnoreCase(company);
        }
        return repository.findAll();
    }

    // ── Dashboard Stats ───────────────────────────────────────────

    /** Build a map of dashboard statistics. */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", repository.count());
        stats.put("interviewing", repository.countByStatus("Interviewing"));
        stats.put("offers", repository.countByStatus("Offer"));
        stats.put("rejected", repository.countByStatus("Rejected"));
        stats.put("applied", repository.countByStatus("Applied"));
        stats.put("oa", repository.countByStatus("Online Assessment"));
        return stats;
    }

    /** Get the 10 most recent applications. */
    public List<JobApplication> getRecentApplications() {
        return repository.findTop10ByOrderByDateAppliedDesc();
    }

    /** Get all applications currently in "Interviewing" status. */
    public List<JobApplication> getInterviewAlerts() {
        return repository.findByStatusOrderByDateAppliedDesc("Interviewing");
    }

    // ── Analytics ─────────────────────────────────────────────────

    /** Monthly application counts: {"2026-01": 5, "2026-02": 8, ...} */
    public Map<String, Long> getMonthlyApplicationCounts() {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : repository.countApplicationsByMonth()) {
            result.put((String) row[0], ((Number) row[1]).longValue());
        }
        return result;
    }

    /** Status distribution: {"Applied": 12, "Offer": 3, ...} */
    public Map<String, Long> getStatusDistribution() {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : repository.countByStatusGroup()) {
            result.put((String) row[0], ((Number) row[1]).longValue());
        }
        return result;
    }

    // ── Status Update (Kanban) ────────────────────────────────────

    /**
     * Update only the status of an application (used by the Kanban drag-and-drop).
     */
    public JobApplication updateStatus(Long id, String newStatus) {
        JobApplication app = findById(id);
        app.setStatus(newStatus);
        return repository.save(app);
    }

    // ── File Upload ───────────────────────────────────────────────

    /**
     * Save an uploaded file to the uploads directory and return the relative path.
     * Files are stored as: uploads/{applicationId}_{type}_{originalFilename}
     */
    public String saveFile(MultipartFile file, Long applicationId, String type) throws IOException {
        if (file == null || file.isEmpty())
            return null;

        String filename = applicationId + "_" + type + "_" + file.getOriginalFilename();
        Path target = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    /** Attach a resume file to an existing application. */
    public void uploadResume(Long id, MultipartFile file) throws IOException {
        String path = saveFile(file, id, "resume");
        if (path != null) {
            JobApplication app = findById(id);
            app.setResumePath(path);
            repository.save(app);
        }
    }

    /** Attach a cover letter file to an existing application. */
    public void uploadCoverLetter(Long id, MultipartFile file) throws IOException {
        String path = saveFile(file, id, "coverletter");
        if (path != null) {
            JobApplication app = findById(id);
            app.setCoverLetterPath(path);
            repository.save(app);
        }
    }
}
