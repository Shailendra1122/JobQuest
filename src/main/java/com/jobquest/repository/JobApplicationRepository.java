package com.jobquest.repository;

import com.jobquest.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the JobApplication entity.
 *
 * Provides automatic CRUD operations plus custom query methods
 * used by the dashboard and filtering features.
 * All queries are scoped by userId for per-user data isolation.
 */
@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    /** Find all applications with a given status (e.g. "Interviewing"). */
    List<JobApplication> findByStatus(String status);

    /**
     * Find applications whose company name contains the search term
     * (case-insensitive).
     */
    List<JobApplication> findByCompanyNameContainingIgnoreCase(String companyName);

    /** Filter by both status and company name (case-insensitive partial match). */
    List<JobApplication> findByStatusAndCompanyNameContainingIgnoreCase(String status, String companyName);

    /** Count applications by status — used for dashboard stats. */
    long countByStatus(String status);

    /** Get the most recent applications, ordered by dateApplied descending. */
    List<JobApplication> findTop10ByOrderByDateAppliedDesc();

    /** Find all applications that are currently in 'Interviewing' status. */
    List<JobApplication> findByStatusOrderByDateAppliedDesc(String status);

    /**
     * Monthly application counts for the analytics chart.
     * Returns rows like ["2026-03", 5].
     */
    @Query(value = "SELECT strftime('%Y-%m', date_applied) AS month, COUNT(*) AS count " +
            "FROM job_applications " +
            "WHERE date_applied IS NOT NULL " +
            "GROUP BY month ORDER BY month", nativeQuery = true)
    List<Object[]> countApplicationsByMonth();

    /**
     * Count of applications grouped by status — used for pie/doughnut charts.
     * Returns rows like ["Applied", 12].
     */
    @Query(value = "SELECT status, COUNT(*) FROM job_applications GROUP BY status", nativeQuery = true)
    List<Object[]> countByStatusGroup();

    // ── User-scoped queries ───────────────────────────────────────

    /** Find all applications belonging to a specific user. */
    List<JobApplication> findByUserId(Long userId);

    /** Find applications by user and status. */
    List<JobApplication> findByUserIdAndStatus(Long userId, String status);

    /** Find applications by user and company name (case-insensitive partial). */
    List<JobApplication> findByUserIdAndCompanyNameContainingIgnoreCase(Long userId, String companyName);

    /** Find applications by user, status, and company name. */
    List<JobApplication> findByUserIdAndStatusAndCompanyNameContainingIgnoreCase(Long userId, String status, String companyName);

    /** Count applications by user and status. */
    long countByUserIdAndStatus(Long userId, String status);

    /** Count all applications for a user. */
    long countByUserId(Long userId);

    /** Get the most recent 10 applications for a user. */
    List<JobApplication> findTop10ByUserIdOrderByDateAppliedDesc(Long userId);

    /** Find applications by user and status, ordered by date. */
    List<JobApplication> findByUserIdAndStatusOrderByDateAppliedDesc(Long userId, String status);

    /** Monthly counts scoped by user. */
    @Query(value = "SELECT strftime('%Y-%m', date_applied) AS month, COUNT(*) AS count " +
            "FROM job_applications " +
            "WHERE date_applied IS NOT NULL AND user_id = :userId " +
            "GROUP BY month ORDER BY month", nativeQuery = true)
    List<Object[]> countApplicationsByMonthAndUserId(@Param("userId") Long userId);

    /** Status distribution scoped by user. */
    @Query(value = "SELECT status, COUNT(*) FROM job_applications WHERE user_id = :userId GROUP BY status", nativeQuery = true)
    List<Object[]> countByStatusGroupAndUserId(@Param("userId") Long userId);
}
