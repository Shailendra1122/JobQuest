package com.jobquest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * JPA Entity representing a single job/internship application.
 *
 * Maps to the "job_applications" table in SQLite.
 * Contains all fields the user tracks: company, role, status, dates, links,
 * notes,
 * and optional file-upload paths for resume / cover letter.
 */
@Entity
@Table(name = "job_applications")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company name is required")
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @NotBlank(message = "Role is required")
    @Column(nullable = false)
    private String role;

    @Column(name = "date_applied")
    private String dateApplied;

    /**
     * Application status — one of:
     * Applied, Online Assessment, Interviewing, Offer, Rejected
     */
    @Column(nullable = false)
    private String status = "Applied";

    @Column(name = "job_link")
    private String jobLink;

    @Column(length = 2000)
    private String notes;

    /** Filesystem path to the uploaded resume file (nullable). */
    @Column(name = "resume_path")
    private String resumePath;

    /** Filesystem path to the uploaded cover letter file (nullable). */
    @Column(name = "cover_letter_path")
    private String coverLetterPath;

    // ── Constructors ──────────────────────────────────────────────

    public JobApplication() {
        this.dateApplied = LocalDate.now().toString();
    }

    public JobApplication(String companyName, String role, String dateApplied,
            String status, String jobLink, String notes) {
        this.companyName = companyName;
        this.role = role;
        this.dateApplied = dateApplied;
        this.status = status;
        this.jobLink = jobLink;
        this.notes = notes;
    }

    // ── Computed Property ─────────────────────────────────────────

    /**
     * Returns the number of days since the application was submitted.
     * Useful for tracking how long ago an application was made.
     */
    @Transient
    public long getDaysSinceApplied() {
        if (dateApplied == null || dateApplied.isEmpty())
            return 0;
        try {
            return ChronoUnit.DAYS.between(LocalDate.parse(dateApplied), LocalDate.now());
        } catch (Exception e) {
            return 0;
        }
    }

    // ── Getters & Setters ─────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDateApplied() {
        return dateApplied;
    }

    public void setDateApplied(String dateApplied) {
        this.dateApplied = dateApplied;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJobLink() {
        return jobLink;
    }

    public void setJobLink(String jobLink) {
        this.jobLink = jobLink;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getResumePath() {
        return resumePath;
    }

    public void setResumePath(String resumePath) {
        this.resumePath = resumePath;
    }

    public String getCoverLetterPath() {
        return coverLetterPath;
    }

    public void setCoverLetterPath(String coverLetterPath) {
        this.coverLetterPath = coverLetterPath;
    }
}
