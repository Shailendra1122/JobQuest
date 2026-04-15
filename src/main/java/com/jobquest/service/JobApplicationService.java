package com.jobquest.service;

import com.jobquest.model.JobApplication;
import com.jobquest.model.User;
import com.jobquest.repository.JobApplicationRepository;
import com.jobquest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
 * All operations are scoped to the currently authenticated user.
 */
@Service
public class JobApplicationService {

    private final JobApplicationRepository repository;
    private final UserRepository userRepository;
    private final Path uploadDir;

    public JobApplicationService(JobApplicationRepository repository,
            UserRepository userRepository,
            @Value("${file.upload-dir:uploads}") String uploadDir) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.uploadDir = Paths.get(uploadDir);
        // Ensure the upload directory exists on startup
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
        }
    }

    // ── User Context ─────────────────────────────────────────────

    /** Get the currently authenticated user's ID. */
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        return user != null ? user.getId() : null;
    }

    /** Get the currently authenticated user. */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userRepository.findByUsername(auth.getName()).orElse(null);
    }

    // ── CRUD ──────────────────────────────────────────────────────

    /** Return every application belonging to the current user. */
    public List<JobApplication> findAll() {
        Long userId = getCurrentUserId();
        if (userId == null) return Collections.emptyList();
        return repository.findByUserId(userId);
    }

    /** Find a single application by its ID; throws if not found or not owned. */
    public JobApplication findById(Long id) {
        JobApplication app = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Application not found with id: " + id));
        // Ensure the current user owns this application
        Long userId = getCurrentUserId();
        if (userId != null && !userId.equals(app.getUserId())) {
            throw new NoSuchElementException("Application not found with id: " + id);
        }
        return app;
    }

    /** Persist a new application, associating it with the current user. */
    public JobApplication save(JobApplication application) {
        Long userId = getCurrentUserId();
        if (userId != null) {
            application.setUserId(userId);
        }
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

    /** Delete an application by ID (only if owned by current user). */
    public void delete(Long id) {
        // Verify ownership first
        findById(id);
        repository.deleteById(id);
    }

    // ── Filtering ─────────────────────────────────────────────────

    /**
     * Filter applications by status and/or company name.
     * Both parameters are optional — pass null or empty to skip that filter.
     * Results are scoped to the current user.
     */
    public List<JobApplication> filter(String status, String company) {
        Long userId = getCurrentUserId();
        if (userId == null) return Collections.emptyList();

        boolean hasStatus = status != null && !status.isBlank();
        boolean hasCompany = company != null && !company.isBlank();

        if (hasStatus && hasCompany) {
            return repository.findByUserIdAndStatusAndCompanyNameContainingIgnoreCase(userId, status, company);
        } else if (hasStatus) {
            return repository.findByUserIdAndStatus(userId, status);
        } else if (hasCompany) {
            return repository.findByUserIdAndCompanyNameContainingIgnoreCase(userId, company);
        }
        return repository.findByUserId(userId);
    }

    // ── Dashboard Stats ───────────────────────────────────────────

    /** Build a map of dashboard statistics for the current user. */
    public Map<String, Object> getDashboardStats() {
        Long userId = getCurrentUserId();
        Map<String, Object> stats = new LinkedHashMap<>();
        if (userId == null) {
            stats.put("total", 0L);
            stats.put("interviewing", 0L);
            stats.put("offers", 0L);
            stats.put("rejected", 0L);
            stats.put("applied", 0L);
            stats.put("oa", 0L);
            return stats;
        }
        stats.put("total", repository.countByUserId(userId));
        stats.put("interviewing", repository.countByUserIdAndStatus(userId, "Interviewing"));
        stats.put("offers", repository.countByUserIdAndStatus(userId, "Offer"));
        stats.put("rejected", repository.countByUserIdAndStatus(userId, "Rejected"));
        stats.put("applied", repository.countByUserIdAndStatus(userId, "Applied"));
        stats.put("oa", repository.countByUserIdAndStatus(userId, "Online Assessment"));
        return stats;
    }

    /** Get the 10 most recent applications for the current user. */
    public List<JobApplication> getRecentApplications() {
        Long userId = getCurrentUserId();
        if (userId == null) return Collections.emptyList();
        return repository.findTop10ByUserIdOrderByDateAppliedDesc(userId);
    }

    /** Get all applications currently in "Interviewing" status for the current user. */
    public List<JobApplication> getInterviewAlerts() {
        Long userId = getCurrentUserId();
        if (userId == null) return Collections.emptyList();
        return repository.findByUserIdAndStatusOrderByDateAppliedDesc(userId, "Interviewing");
    }

    // ── Analytics ─────────────────────────────────────────────────

    /** Monthly application counts for the current user. */
    public Map<String, Long> getMonthlyApplicationCounts() {
        Long userId = getCurrentUserId();
        Map<String, Long> result = new LinkedHashMap<>();
        if (userId == null) return result;
        for (Object[] row : repository.countApplicationsByMonthAndUserId(userId)) {
            result.put((String) row[0], ((Number) row[1]).longValue());
        }
        return result;
    }

    /** Status distribution for the current user. */
    public Map<String, Long> getStatusDistribution() {
        Long userId = getCurrentUserId();
        Map<String, Long> result = new LinkedHashMap<>();
        if (userId == null) return result;
        for (Object[] row : repository.countByStatusGroupAndUserId(userId)) {
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
