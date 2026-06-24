document.addEventListener('DOMContentLoaded', () => {
    
    // Clipboard Copy Logic for Contact Pills
    const badges = document.querySelectorAll('.btn-rollno, .btn-phone, .btn-mail, .btn-course');

    badges.forEach(badge => {
        badge.addEventListener('click', function() {
            const span = badge.querySelector('span');
            if (span) {
                let text = span.textContent.trim();
                navigator.clipboard.writeText(text)
                    .then(() => {
                        // Visual feedback on the element itself
                        const originalHtml = badge.innerHTML;
                        badge.innerHTML = '<i class="fas fa-check text-success"></i><span>Copied!</span>';
                        badge.classList.add('border-success', 'bg-white');
                        
                        setTimeout(() => {
                            badge.innerHTML = originalHtml;
                            badge.classList.remove('border-success', 'bg-white');
                        }, 1500);

                        // Global Notification fallback
                        if (typeof Notiflix !== 'undefined') {
                            Notiflix.Notify.Success(`${text} copied to clipboard`);
                        } else {
                            console.log(`${text} copied to clipboard`);
                        }
                    })
                    .catch(err => console.error("Copy failed", err));
            }
        });
    });

    // Clickable Cards Navigation
    document.querySelectorAll(".clickable-card").forEach(card => {
        card.addEventListener("click", function () {
            const url = this.dataset.url;
            if (url && url !== '#') {
                window.location.href = url;
            }
        });
    });
});

$(document).ready(function() {

    // 1. Check if the #calendar element exists in HTML
    // 2. Check if the .MEC() function (library) is loaded
    if ($("#calendar").length > 0 && typeof $.fn.MEC === 'function') {
        $("#calendar").MEC();
    }

    // Navigate to URL on card click (Alternative setup)
    document.querySelectorAll('.navigate-to-url').forEach(card => {
        card.addEventListener('click', (e) => {
            // Don't navigate if clicking on show more/less link
            if (!e.target.classList.contains('showmore')) {
                const url = card.getAttribute('data-url');
                if (url && url !== '#') window.location.href = url;
            }
        });
    });

    // Show more/less functionality for programs table
    $('#programsTable tbody').on('click', 'td a.showmore', function (e) {
        e.preventDefault();
        e.stopPropagation();

        var row = $(this).closest('td');
        var morespan = row.find('span.more');
        var lessspan = row.find('span.less');

        if (morespan.is(':hidden')) {
            $(this).html('<i class="fas fa-chevron-up me-1"></i> Show less');
            lessspan.hide();
            morespan.fadeIn(200);
        } else {
            $(this).html('<i class="fas fa-chevron-down me-1"></i> Show more');
            lessspan.show();
            morespan.hide();
        }
    });

    /* // NOTIFICATIONS MODAL LOGIC (Updated for Bootstrap 5)
    // Listen for BS5 modal show event
    document.getElementById('notificationsModal').addEventListener('show.bs.modal', function () {
        const modalBody = $('#all-notifications-list');
        modalBody.empty(); // Clear previous content

        // Get data from the window object
        const allNotifications = (window.pageData && window.pageData.notifications) ? window.pageData.notifications : [];

        if (allNotifications.length > 0) {
            let listHtml = '<ul class="list-group list-group-flush">';

            allNotifications.forEach(n => {
                // 1. Safe Date Formatting
                let dateStr = "";
                if(n.entrydate) {
                    const date = new Date(n.entrydate);
                    if (!isNaN(date.getTime())) {
                        const day = String(date.getDate()).padStart(2, '0');
                        const month = String(date.getMonth() + 1).padStart(2, '0');
                        const year = date.getFullYear();
                        dateStr = `${day}-${month}-${year}`;
                    }
                }

                // 2. Check for Link
                let contentHtml = "";
                if (n.link && n.link.trim() !== "") {
                    contentHtml = `<a href="${n.link}" target="_blank" class="text-decoration-none fw-semibold">
                                     <i class="fas fa-external-link-alt fa-xs me-1"></i> ${n.notification}
                                   </a>`;
                } else {
                    contentHtml = `<p class="mb-1 fw-medium text-dark">${n.notification}</p>`;
                }

                // 3. Append to list
                listHtml += `
                    <li class="list-group-item px-0 py-3">
                        ${contentHtml}
                        <div class="small text-muted mt-1"><i class="far fa-clock me-1"></i> ${dateStr}</div>
                    </li>`;
            });

            listHtml += '</ul>';
            modalBody.html(listHtml);
        } else {
            modalBody.html('<div class="text-center p-4 text-muted"><i class="far fa-bell-slash fs-2 mb-2"></i><br>No notifications found.</div>');
        }
    });
    */
});