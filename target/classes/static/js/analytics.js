/**
 * JobQuest — Analytics Charts JavaScript
 *
 * Fetches data from the REST API and renders:
 * 1. Monthly applications bar/line chart
 * 2. Status distribution doughnut chart
 * 3. Computed conversion rates
 *
 * Uses Chart.js v4.
 */

document.addEventListener('DOMContentLoaded', async () => {

    // ── Chart.js Global Defaults ────────────────────────────────
    Chart.defaults.color = '#94a3b8';
    Chart.defaults.font.family = "'Inter', sans-serif";
    Chart.defaults.plugins.legend.labels.padding = 16;
    Chart.defaults.plugins.legend.labels.usePointStyle = true;

    try {
        // Fetch data in parallel
        const [monthlyRes, statusRes, statsRes] = await Promise.all([
            fetch('/api/applications/analytics/monthly'),
            fetch('/api/applications/analytics/status'),
            fetch('/api/applications/stats')
        ]);

        const monthlyData = await monthlyRes.json();
        const statusData  = await statusRes.json();
        const statsData   = await statsRes.json();

        // ── Render Monthly Chart ────────────────────────────────
        renderMonthlyChart(monthlyData);

        // ── Render Status Chart ─────────────────────────────────
        renderStatusChart(statusData);

        // ── Compute Rates ───────────────────────────────────────
        computeRates(statsData);

    } catch (error) {
        console.error('Error loading analytics:', error);
    }
});

/**
 * Render the "Applications Over Time" bar + line combination chart.
 */
function renderMonthlyChart(data) {
    const ctx = document.getElementById('monthlyChart');
    if (!ctx) return;

    const labels = Object.keys(data);
    const values = Object.values(data);

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels.map(formatMonth),
            datasets: [{
                label: 'Applications',
                data: values,
                backgroundColor: createGradient(ctx, '#6366f1', '#8b5cf6'),
                borderColor: '#6366f1',
                borderWidth: 0,
                borderRadius: 8,
                borderSkipped: false,
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: 'rgba(17, 24, 39, 0.9)',
                    borderColor: 'rgba(99, 102, 241, 0.3)',
                    borderWidth: 1,
                    cornerRadius: 8,
                    padding: 12,
                }
            },
            scales: {
                x: {
                    grid: { display: false },
                    border: { display: false },
                },
                y: {
                    beginAtZero: true,
                    ticks: { stepSize: 1 },
                    grid: { color: 'rgba(255,255,255,0.04)' },
                    border: { display: false },
                }
            }
        }
    });
}

/**
 * Render the status distribution doughnut chart.
 */
function renderStatusChart(data) {
    const ctx = document.getElementById('statusChart');
    if (!ctx) return;

    const labels = Object.keys(data);
    const values = Object.values(data);
    const colors = {
        'Applied':             '#3b82f6',
        'Online Assessment':   '#8b5cf6',
        'Interviewing':        '#f97316',
        'Offer':               '#10b981',
        'Rejected':            '#ef4444',
    };

    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: labels.map(l => colors[l] || '#6366f1'),
                borderColor: 'rgba(10, 14, 26, 0.8)',
                borderWidth: 3,
                hoverOffset: 8,
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '65%',
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: { padding: 16, font: { size: 12 } }
                },
                tooltip: {
                    backgroundColor: 'rgba(17, 24, 39, 0.9)',
                    borderColor: 'rgba(99, 102, 241, 0.3)',
                    borderWidth: 1,
                    cornerRadius: 8,
                    padding: 12,
                }
            }
        }
    });
}

/**
 * Compute and display interview / offer / rejection rates.
 */
function computeRates(stats) {
    const total = stats.total || 0;
    if (total === 0) return;

    const interviewRate = ((stats.interviewing / total) * 100).toFixed(1);
    const offerRate     = ((stats.offers / total) * 100).toFixed(1);
    const rejectionRate = ((stats.rejected / total) * 100).toFixed(1);

    const convEl = document.getElementById('conversionRate');
    const offEl  = document.getElementById('offerRate');
    const rejEl  = document.getElementById('rejectionRate');

    if (convEl) convEl.textContent = interviewRate + '%';
    if (offEl)  offEl.textContent  = offerRate + '%';
    if (rejEl)  rejEl.textContent  = rejectionRate + '%';
}

/**
 * Format "2026-03" → "Mar 2026"
 */
function formatMonth(ym) {
    if (!ym) return '';
    const [year, month] = ym.split('-');
    const months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
    return months[parseInt(month) - 1] + ' ' + year;
}

/**
 * Create a vertical gradient for Chart.js bar fills.
 */
function createGradient(ctx, color1, color2) {
    const canvas = ctx.getContext ? ctx : ctx.canvas;
    const context = canvas.getContext('2d');
    const gradient = context.createLinearGradient(0, 0, 0, 300);
    gradient.addColorStop(0, color1);
    gradient.addColorStop(1, color2 + '44');
    return gradient;
}
