package com.jobquest.controller;

import com.jobquest.model.JobApplication;
import com.jobquest.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Thymeleaf MVC Controller for JobQuest.
 *
 * Serves HTML pages for the dashboard, application list,
 * add/edit forms, kanban board, and analytics page.
 */
@Controller
public class JobApplicationController {

    private final JobApplicationService service;

    /** All possible status values — shared with forms and filters. */
    private static final List<String> STATUSES = Arrays.asList(
            "Applied", "Online Assessment", "Interviewing", "Offer", "Rejected");

    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    // ── Dashboard ─────────────────────────────────────────────────

    /**
     * GET / — Dashboard page.
     * Shows summary statistics, recent applications, and interview alerts.
     */
    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("stats", service.getDashboardStats());
        model.addAttribute("recentApps", service.getRecentApplications());
        model.addAttribute("interviewAlerts", service.getInterviewAlerts());
        // Add user display name for greeting
        var currentUser = service.getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("displayName", currentUser.getDisplayName());
        }
        return "dashboard";
    }

    // ── Application List ──────────────────────────────────────────

    /**
     * GET /applications — Paginated, filterable application list.
     * Supports optional query parameters: ?status=...&company=...
     */
    @GetMapping("/applications")
    public String listApplications(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String company,
            Model model) {
        model.addAttribute("applications", service.filter(status, company));
        model.addAttribute("statuses", STATUSES);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCompany", company);
        return "applications";
    }

    // ── Add Application ───────────────────────────────────────────

    /** GET /applications/new — Show the blank add-application form. */
    @GetMapping("/applications/new")
    public String showAddForm(Model model) {
        model.addAttribute("application", new JobApplication());
        model.addAttribute("statuses", STATUSES);
        model.addAttribute("pageTitle", "Add Application");
        return "form";
    }

    /** POST /applications — Handle form submission to create a new application. */
    @PostMapping("/applications")
    public String addApplication(@Valid @ModelAttribute("application") JobApplication application,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", STATUSES);
            model.addAttribute("pageTitle", "Add Application");
            return "form";
        }
        service.save(application);
        redirectAttributes.addFlashAttribute("successMessage", "Application added successfully!");
        return "redirect:/applications";
    }

    // ── Edit Application ──────────────────────────────────────────

    /** GET /applications/edit/{id} — Show the pre-filled edit form. */
    @GetMapping("/applications/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("application", service.findById(id));
        model.addAttribute("statuses", STATUSES);
        model.addAttribute("pageTitle", "Edit Application");
        return "form";
    }

    /**
     * POST /applications/edit/{id} — Handle form submission to update an
     * application.
     */
    @PostMapping("/applications/edit/{id}")
    public String updateApplication(@PathVariable Long id,
            @Valid @ModelAttribute("application") JobApplication application,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", STATUSES);
            model.addAttribute("pageTitle", "Edit Application");
            return "form";
        }
        service.update(id, application);
        redirectAttributes.addFlashAttribute("successMessage", "Application updated successfully!");
        return "redirect:/applications";
    }

    // ── Delete Application ────────────────────────────────────────

    /** GET /applications/delete/{id} — Delete and redirect back to the list. */
    @GetMapping("/applications/delete/{id}")
    public String deleteApplication(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Application deleted successfully!");
        return "redirect:/applications";
    }

    // ── File Upload ───────────────────────────────────────────────

    /** POST /applications/{id}/upload — Upload resume and/or cover letter. */
    @PostMapping("/applications/{id}/upload")
    public String uploadFiles(@PathVariable Long id,
            @RequestParam(value = "resume", required = false) MultipartFile resume,
            @RequestParam(value = "coverLetter", required = false) MultipartFile coverLetter,
            RedirectAttributes redirectAttributes) throws IOException {
        service.uploadResume(id, resume);
        service.uploadCoverLetter(id, coverLetter);
        redirectAttributes.addFlashAttribute("successMessage", "Files uploaded successfully!");
        return "redirect:/applications/edit/" + id;
    }

    // ── Kanban Board ──────────────────────────────────────────────

    /** GET /kanban — Kanban board page with applications grouped by status. */
    @GetMapping("/kanban")
    public String kanbanBoard(Model model) {
        model.addAttribute("statuses", STATUSES);
        model.addAttribute("applications", service.findAll());
        return "kanban";
    }

    // ── Analytics ─────────────────────────────────────────────────

    /** GET /analytics — Analytics/charts page. */
    @GetMapping("/analytics")
    public String analytics(Model model) {
        model.addAttribute("stats", service.getDashboardStats());
        return "analytics";
    }
}
