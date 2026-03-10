/**
 * JobQuest — Main JavaScript
 *
 * Handles:
 * - Bootstrap toast auto-dismiss
 * - Form validation feedback
 * - Navbar active-link highlighting
 * - Smooth row animations
 */

document.addEventListener('DOMContentLoaded', () => {

    // ── Auto-dismiss toast notifications after 4 seconds ────────
    document.querySelectorAll('.toast.show').forEach(toast => {
        setTimeout(() => {
            const bsToast = bootstrap.Toast.getOrCreateInstance(toast);
            bsToast.hide();
        }, 4000);
    });

    // ── Highlight active nav link based on current URL ──────────
    const currentPath = window.location.pathname;
    document.querySelectorAll('.nav-link').forEach(link => {
        const href = link.getAttribute('href');
        if (href === currentPath ||
            (currentPath.startsWith('/applications') && href === '/applications') ||
            (currentPath === '/kanban' && href === '/kanban') ||
            (currentPath === '/analytics' && href === '/analytics') ||
            (currentPath === '/' && href === '/')) {
            link.classList.add('active');
            link.style.color = '#f1f5f9';
        }
    });

    // ── Bootstrap form validation ───────────────────────────────
    document.querySelectorAll('.needs-validation').forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });

    // ── Stagger animation for table rows ────────────────────────
    document.querySelectorAll('.app-row').forEach((row, index) => {
        row.style.animationDelay = `${index * 50}ms`;
    });

    // ── Delete confirmation is already inline (onclick),
    //    but we also add keyboard accessibility:
    document.querySelectorAll('[title="Delete"]').forEach(btn => {
        btn.setAttribute('role', 'button');
        btn.setAttribute('aria-label', 'Delete this application');
    });
});
