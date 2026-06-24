document.addEventListener("DOMContentLoaded", function () {

    const markAllBtn = document.getElementById("mark-all-read-btn");
    if (markAllBtn) {
        markAllBtn.addEventListener("click", function () {
            fetch(API.markAllNotificationsAsRead, { 
                method: 'POST'
            })
            .then(response => {
                if (response.ok) {
                    // 1. Update all Sidebar/Header Badges
                    const allBadges = document.querySelectorAll(".js-unread-badge");
                    allBadges.forEach(badge => {
                        badge.textContent = "0";
                        badge.classList.add("d-none");
                    });

                    // 2. Update Table UI (If user is currently on the notifications page)
                    const rows = document.querySelectorAll("tr.unread-row");
                    rows.forEach(row => {
                        row.classList.remove("unread-row", "fw-semibold", "font-weight-bold");
                        
                        // Update the status badge in the row
                        const statusBadge = row.querySelector(".status-badge");
                        if (statusBadge) {
                            statusBadge.textContent = "Read";
                            statusBadge.classList.replace("text-danger", "text-success");
                            statusBadge.classList.replace("badge-warning", "badge-success");
                        }

                        // Update the button in the row
                        const rowBtn = row.querySelector(".toggle-btn");
                        if (rowBtn) {
                            rowBtn.textContent = "Mark Unread";
                            rowBtn.classList.remove("btn-primary", "shadow-sm");
                            rowBtn.classList.add("btn-secondary");
                        }
                    });
                    
                    // 3. SHOW THE SUCCESS TOAST
                    const toastLiveExample = document.getElementById('successToast');
                    if (toastLiveExample) {
                        const toast = new bootstrap.Toast(toastLiveExample);
                        toast.show();
                    }
                }
            })
            .catch(error => console.error("Error marking all as read:", error));
        });
    }

    document.addEventListener("click", function (event) {
        if (event.target.classList.contains("toggle-btn")) {
            const button = event.target;
            const id = button.getAttribute("data-id");
            
            fetch(API.toggleNotification, {
                method: 'POST',
                body: new URLSearchParams({ id: id })
            })
            .then(response => response.json())
            .then(isRead => {
                const row = document.getElementById("row-" + id);
                const badge = row.querySelector(".status-badge");
                
                // --- 1. TABLE UI UPDATES ---
                if (isRead) {
                    button.textContent = "Mark Unread";
                    button.classList.remove("btn-primary", "btn-primary", "shadow-sm");
                    button.classList.add("btn-secondary", "btn-secondary");
                    
                    row.classList.remove("font-weight-bold", "unread-row", "fw-semibold");
                    
                    badge.textContent = "Read";
                    badge.classList.remove("badge-warning", "text-danger");
                    badge.classList.add("badge-success", "text-success");
                } else {
                    button.textContent = "Mark Read";
                    button.classList.remove("btn-secondary", "btn-secondary");
                    button.classList.add("btn-primary", "btn-primary", "shadow-sm");
                    
                    row.classList.add("font-weight-bold", "unread-row", "fw-semibold");
                    
                    badge.textContent = "Unread";
                    badge.classList.remove("badge-success", "text-success");
                    badge.classList.add("badge-warning", "text-danger");
                }

                // --- 2. DYNAMIC MENU BADGE UPDATES ---
                // Select ALL badges (handles mobile + desktop duplicate menus)
                const sidebarBadges = document.querySelectorAll(".js-unread-badge");
                
                sidebarBadges.forEach(sidebarBadge => {
                    // .trim() removes any weird HTML whitespace, 10 ensures base-10 math
                    let currentCount = parseInt(sidebarBadge.textContent.trim(), 10) || 0;

                    if (isRead) {
                        // Marked as read -> decrease unread count
                        currentCount = Math.max(0, currentCount - 1); 
                    } else {
                        // Marked as unread -> increase unread count
                        currentCount += 1;
                    }

                    // Update the text
                    sidebarBadge.textContent = currentCount;

                    // Toggle Bootstrap's display-none class based on the new count
                    if (currentCount > 0) {
                        sidebarBadge.classList.remove("d-none");
                    } else {
                        sidebarBadge.classList.add("d-none");
                    }
                });
            })
            .catch(error => console.error("Error toggling notification:", error));
        }
    });
});