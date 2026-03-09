/**
 * JobQuest — Kanban Board JavaScript
 *
 * Implements drag-and-drop functionality for the Kanban board.
 * When a card is dropped into a new column, it sends a PATCH request
 * to /api/applications/{id}/status to update the application's status.
 */

document.addEventListener('DOMContentLoaded', () => {

    const cards = document.querySelectorAll('.kanban-card');
    const dropZones = document.querySelectorAll('.kanban-cards');

    // ── Drag Events on Cards ────────────────────────────────────

    cards.forEach(card => {
        card.addEventListener('dragstart', e => {
            card.classList.add('dragging');
            e.dataTransfer.setData('text/plain', card.dataset.id);
            e.dataTransfer.effectAllowed = 'move';
        });

        card.addEventListener('dragend', () => {
            card.classList.remove('dragging');
        });
    });

    // ── Drop Zone Events ────────────────────────────────────────

    dropZones.forEach(zone => {
        zone.addEventListener('dragover', e => {
            e.preventDefault();
            e.dataTransfer.dropEffect = 'move';
            zone.classList.add('drag-over');
        });

        zone.addEventListener('dragleave', () => {
            zone.classList.remove('drag-over');
        });

        zone.addEventListener('drop', async e => {
            e.preventDefault();
            zone.classList.remove('drag-over');

            const cardId = e.dataTransfer.getData('text/plain');
            const newStatus = zone.dataset.status;
            const card = document.querySelector(`.kanban-card[data-id="${cardId}"]`);

            if (!card) return;

            // Move the card into the new column in the DOM
            zone.appendChild(card);

            // Update the status on the server via REST API
            try {
                const response = await fetch(`/api/applications/${cardId}/status`, {
                    method: 'PATCH',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ status: newStatus })
                });

                if (!response.ok) {
                    throw new Error('Failed to update status');
                }

                // Update column counts
                updateColumnCounts();

            } catch (error) {
                console.error('Error updating status:', error);
                // Reload the page to revert to server state
                window.location.reload();
            }
        });
    });

    /**
     * Recalculate and update the card count badges in each column header.
     */
    function updateColumnCounts() {
        document.querySelectorAll('.kanban-column').forEach(column => {
            const count = column.querySelectorAll('.kanban-card').length;
            const badge = column.querySelector('.kanban-count');
            if (badge) badge.textContent = count;
        });
    }
});
