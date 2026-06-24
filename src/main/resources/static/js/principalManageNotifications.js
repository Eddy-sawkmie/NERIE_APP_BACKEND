// =================================================================
// MODAL HELPER FUNCTIONS (BS5 Stacking Fix included)
// =================================================================
function showModalAlert(message, title = 'Message', callback = null) {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);

    $('#feedbackModalFooter').html(
        '<button type="button" id="modalAlertOkBtn" class="btn btn-primary btn-modern px-4" data-bs-dismiss="modal">OK</button>'
    );

    const feedbackModalEl = document.getElementById('feedbackModal');
    if (feedbackModalEl.parentNode !== document.body) {
        document.body.appendChild(feedbackModalEl);
    }

    if (callback && typeof callback === 'function') {
        $(feedbackModalEl).off('hidden.bs.modal').one('hidden.bs.modal', callback);
    } else {
        $(feedbackModalEl).off('hidden.bs.modal');
    }

    const myModal = bootstrap.Modal.getOrCreateInstance(feedbackModalEl);
    myModal.show();
}

function showModalConfirm(message, title = 'Confirmation', onConfirmCallback) {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);

    $('#feedbackModalFooter').html(
        '<button type="button" class="btn btn-light border btn-modern px-4" data-bs-dismiss="modal">Cancel</button>' +
        '<button type="button" id="modalConfirmOkBtn" class="btn btn-danger btn-modern px-4">Confirm</button>'
    );

    const feedbackModalEl = document.getElementById('feedbackModal');
    if (feedbackModalEl.parentNode !== document.body) {
        document.body.appendChild(feedbackModalEl);
    }

    const myModal = bootstrap.Modal.getOrCreateInstance(feedbackModalEl);

    $('#modalConfirmOkBtn').off('click').one('click', function () {
        // Wait for modal to completely hide before triggering callback
        $(feedbackModalEl).off('hidden.bs.modal').one('hidden.bs.modal', function () {
            if (typeof onConfirmCallback === 'function') {
                onConfirmCallback();
            }
        });
        myModal.hide();
    });

    myModal.show();
}

// =================================================================
// MAIN LOGIC
// =================================================================
$(document).ready(function () {
    
    // 1. Initialize DataTable
    if ($.fn.DataTable) {
        $("#notificationlist").DataTable({
            dom: '<"d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-3"Bf>rt<"d-flex flex-column flex-md-row justify-content-between align-items-md-center mt-3"ip>',
            pageLength: 10,
            lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
            language: {
                emptyTable: "<div class='text-center p-4'><i class='fas fa-bell-slash fs-1 text-muted mb-3'></i><br>No notifications found.</div>",
                search: "_INPUT_",
                searchPlaceholder: "Search Notifications..."
            },
            buttons: [
                {
                    extend: 'excelHtml5',
                    text: '<i class="fas fa-file-excel me-1"></i> Export Excel',
                    title: 'Notifications',
                    exportOptions: { columns: ':visible:not(.noExport)' },
                    className: 'btn btn-sm btn-success btn-modern px-3 py-1' 
                }
            ]
        });

        // Style the DataTables native search input to match Bootstrap 5
        $('.dataTables_filter input').addClass('form-control form-control-sm modern-input d-inline-block w-auto');
    }

    // 2. Add New Notification
    $("#add_notification_btn").click(function () {
        const notificationMsg = $("#notification").val().trim();
        const receiver = $("#userType").val();

        if (!notificationMsg) {
            showModalAlert("Please enter a notification message.", "Validation Error");
            return;
        }

        if (receiver === null || receiver === "-1") {
            showModalAlert("Please select a target audience.", "Validation Error");
            return;
        }

        const $btn = $(this);
        const originalText = $btn.html();
        $btn.prop("disabled", true).html('<i class="fas fa-spinner fa-spin me-1"></i> Publishing...');

        $.ajax({
            type: "POST",
            url: API.uploadNotification,
            data: {
                notification: notificationMsg,
                usertype: receiver
            }
        })
        .done(function (response) {
            $btn.prop("disabled", false).html(originalText);
            if (response === "1") {
                showModalAlert("<i class='fas fa-check-circle text-success fs-4 d-block mb-2'></i> Successfully Published", "Success", () => window.location.reload());
            } else {
                showModalAlert("Something went wrong. Could not add the notification.", "Error");
            }
        })
        .fail(function () {
            $btn.prop("disabled", false).html(originalText);
            showModalAlert("Network or server error occurred.", "Failed");
        });
    });

    // 3. Delete Notification (Using Event Delegation for DataTables compatibility)
    $('#notificationlist tbody').on('click', '.delete-notification-btn', function () {
        const id = $(this).attr("data-id");

        showModalConfirm("Are you sure you want to delete this notification?", "Confirm Deletion", function() {
            $.ajax({
                type: "GET",
                url: API.deleteNotification,
                data: { notificationid: id }
            })
            .done(function (response) {
                if (response === "1") {
                    showModalAlert("Notification Deleted.", "Success", () => window.location.reload());
                } else {
                    showModalAlert("Something went wrong. Could not delete.", "Error");
                }
            })
            .fail(function () {
                showModalAlert("Failed to communicate with the server.", "Error");
            });
        });
    });

    // 4. Populate Edit Modal (Using Event Delegation)
    $('#notificationlist tbody').on('click', '.edit-notification-btn', function () {
        const id = $(this).attr("data-id");
        const msg = $(this).attr("data-notification");
        const type = $(this).attr("data-usertype");

        $("#editNotificationId").val(id);
        $("#editNotification").val(msg);
        $("#editUserType").val(type);
    });

    // 5. Update Notification via Edit Modal
    $(".update-notification-btn").click(function () {
        const id = $("#editNotificationId").val();
        const notificationMsg = $("#editNotification").val().trim();
        const receiver = $("#editUserType").val();

        if (!notificationMsg) {
            showModalAlert("Notification message cannot be empty.", "Validation Error");
            return;
        }

        const $btn = $(this);
        const originalText = $btn.html();
        $btn.prop("disabled", true).html('<i class="fas fa-spinner fa-spin me-1"></i> Saving...');

        $.ajax({
            type: "POST",
            url: API.uploadNotification,
            data: {
                notificationid: id,
                notification: notificationMsg,
                usertype: receiver
            }
        })
        .done(function (response) {
            $btn.prop("disabled", false).html(originalText);
            
            // Hide the edit modal first
            const editModalEl = document.getElementById('editNotificationModal');
            const editModal = bootstrap.Modal.getInstance(editModalEl);
            if (editModal) editModal.hide();

            if (response === "1") {
                // Wait for the modal to hide before showing the success alert
                $(editModalEl).one('hidden.bs.modal', function() {
                    showModalAlert("Notification updated successfully.", "Success", () => window.location.reload());
                });
            } else {
                $(editModalEl).one('hidden.bs.modal', function() {
                    showModalAlert("Something went wrong. Could not update.", "Error Updating");
                });
            }
        })
        .fail(function () {
            $btn.prop("disabled", false).html(originalText);
            showModalAlert("Failed to communicate with server.", "Network Error");
        });
    });

    // UI Toggles (If left over from legacy sidebar script)
    $("#menu-toggle").click(function (e) {
        e.preventDefault();
        $("#wrapper").toggleClass("toggled");
    });
    $(".sub-menu ul").hide();
    $(".sub-sub-menu ul").hide();
    $(".sub-menu a").click(function () {
        $(this).parent(".sub-menu").children("ul").slideToggle("100");
        $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
    });
    $(".sub-sub-menu a").click(function () {
        $(this).parent(".sub-sub-menu").children("ul").slideToggle("100");
        $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
    });
});