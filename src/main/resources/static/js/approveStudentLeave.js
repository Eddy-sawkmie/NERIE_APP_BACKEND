$(document).ready(function () {
    $('#leavelist').DataTable({
        retrieve: true,
        bDestroy: true,
        ordering: false,
        pageLength: 10,
        lengthMenu: [[10, 25, 50, -1],[10, 25, 50, "All"]],
        language: { emptyTable: "No student leave records." },
        dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
             'rtip',
        buttons:[
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                title: 'Leave Applications List',
                exportOptions: { columns: ':visible:not(.noExport)' },
                className: 'btn btn-success btn-sm mb-3'
            }
        ]
    });

    $(document).on('click', '.delete-leave-btn', function() {
        const leaveId = $(this).attr('data-id');
        showDeleteConfirmation(leaveId);
    });
});

// Show Confirmation Modal
function showDeleteConfirmation(leaveId) {
    $('#feedbackModalLabel').text('Confirm Deletion');
    $('#feedbackModalBody').html('<p class="text-center">Are you sure you want to <strong>delete</strong> this leave application?</p>');

    const confirmBtn = $('<button type="button" id="btn-confirm-delete" class="btn btn-danger">Yes, Delete</button>');

    confirmBtn.data('leave-id', leaveId);

    const footer = $('<div class="modal-footer"></div>')
        .append('<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>')
        .append(confirmBtn);

    $('#feedbackModal .modal-footer').replaceWith(footer);
    $('#feedbackModal').modal('show');

    $('#btn-confirm-delete').one('click', function() {
        deleteLeaveApplication($(this));
    });
}

// Perform Ajax Delete
function deleteLeaveApplication(btnElement) {
    const leaveId = btnElement.data('leave-id');

    btnElement.prop('disabled', true).html('<span class="spinner-border spinner-border-sm"></span> Deleting...');

    $.ajax({
        url: API.deleteStudentLeave,
        type: 'POST',
        data: { leaveid: leaveId },
        success: function (response) {
            $('#feedbackModal').modal('hide');

            $('#feedbackModal').one('hidden.bs.modal', function() {
                if (response.status === 'success') {
                    showSuccessModal(response.message || "Deleted successfully!");
                } else {
                    showAlertModal("Failure", response.message || "Failed to delete.");
                }
            });
        },
        error: function (xhr) {
             $('#feedbackModal').modal('hide');
             $('#feedbackModal').one('hidden.bs.modal', function() {
                 let msg = (xhr.responseJSON && xhr.responseJSON.message) ? xhr.responseJSON.message : "An error occurred.";
                 showAlertModal("Error", msg);
             });
        }
    });
}

// Show Success Modal (Reuse same modal)
function showSuccessModal(message) {
    $('#feedbackModalLabel').text('Success');
    $('#feedbackModalBody').html('<div class="text-center text-success"><i class="fa fa-check-circle fa-3x mb-3"></i><br>' + message + '</div>');

    // Button reloads page on click
    const footer = $('<div class="modal-footer"></div>')
        .append('<button type="button" class="btn btn-primary" id="btn-reload">OK</button>');

    $('#feedbackModal .modal-footer').replaceWith(footer);
    $('#feedbackModal').modal('show');

    // Reload when OK is clicked
    $('#btn-reload').one('click', function() {
        window.location.reload();
    });
}

// Show Generic Alert Modal (Reuse same modal)
function showAlertModal(title, message) {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html('<p class="text-center">' + message + '</p>');

    const footer = $('<div class="modal-footer"></div>')
        .append('<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>');

    $('#feedbackModal .modal-footer').replaceWith(footer);
    $('#feedbackModal').modal('show');
}