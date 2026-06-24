function showSuccessAndRedirect(message, url, title = 'Success') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(`<p class="text-start fw-bold">${message}</p>`);

    const okButton = $('<button type="button" class="btn btn-primary">OK</button>');
    okButton.on('click', function() {
        $('#feedbackModal').modal('hide');
    });

    const footer = $('<div class="modal-footer"></div>').append(okButton);
    $('#feedbackModal .modal-footer').replaceWith(footer);

    $('#feedbackModal').modal('show');

    $('#feedbackModal').one('hidden.bs.modal', function () {
        window.location.href = url;
    });
}

function showActionModal(action, studentLeaveId) {
    let rolecodeform = $('#formrolecode').val();
    const formrolecodeprincideanEl = document.getElementById("formrolecodeprincidean");
    if (formrolecodeprincideanEl && formrolecodeprincideanEl.value === '9') {
        rolecodeform = 9;
    }

    let modalConfig = {
        title: '',
        body: '',
        footer: null
    };

    if (action === 'approve') {
        const isFinalApproval = (rolecodeform == 9);
        const actionText = isFinalApproval ? 'approve' : 'forward';

        modalConfig.title = 'Confirm Action';
        modalConfig.body = `<p class="text-center">Are you sure you want to <strong>${actionText}</strong> this application?</p>`;

        const confirmBtn = $(`<button type="button" class="btn btn-success">Yes, Proceed</button>`);
        confirmBtn.on('click', function() {
            $(this).prop('disabled', true).html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Processing...');

            $.ajax({
                type: "POST",
                url: `${API.approveLeaveApplication}?rolecode=${encodeURIComponent(rolecodeform)}`,
                data: { studentleaveid: studentLeaveId },
                success: function (data) {
                    $('#feedbackModal').modal('hide');
                    $('#feedbackModal').one('hidden.bs.modal', function () {
                        if (data === "-1") {
                            Notiflix.Report.Failure('Failure', 'Approval Failed!', 'Ok');
                        } else {
                            const successMessage = isFinalApproval ? "Application Approved!" : "Successfully Forwarded!";
                            showSuccessAndRedirect(successMessage, API.redirectSuccessURL);
                        }
                    });
                },
                error: (jqXHR) => {
                    $('#feedbackModal').modal('hide');
                    Notiflix.Report.Failure('Error', `Request Failed: ${jqXHR.statusText}`, 'Ok');
                }
            });
        });

        modalConfig.footer = $('<div class="modal-footer"></div>')
            .append('<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>')
            .append(confirmBtn);

    } else if (action === 'reject') {
        modalConfig.title = 'Reject Application';
        modalConfig.body = `
            <div class="mb-3">
              <label for="modalRejectionReason">Reason For Rejection:<span class="text-danger">*</span></label>
              <textarea id="modalRejectionReason" name="rejectionreason" maxlength="250" rows="4" class="form-control" required placeholder="Enter reason here..."></textarea>
              <div class="invalid-feedback">Reason is required.</div>
            </div>`;

        const confirmBtn = $(`<button type="button" class="btn btn-danger">Confirm Rejection</button>`);
        confirmBtn.on('click', function() {
            const rejectionReason = $('#modalRejectionReason').val().trim();
            if (!rejectionReason) {
                $('#modalRejectionReason').addClass('is-invalid');
                return;
            }
            $('#modalRejectionReason').removeClass('is-invalid');

            $(this).prop('disabled', true).html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Processing...');

            $.ajax({
                type: "POST",
                url: `${API.rejectLeaveApplication}?rolecode=${encodeURIComponent(rolecodeform)}`,
                data: {
                    studentleaveid: studentLeaveId,
                    rejectionreason: rejectionReason
                },
                success: function (data) {
                    $('#feedbackModal').modal('hide');
                    $('#feedbackModal').one('hidden.bs.modal', function () {
                        if (data === "-1") {
                            Notiflix.Report.Failure('Failure', 'Rejection Failed!', 'Ok');
                        } else if (data === "-2") {
                            Notiflix.Report.Warning('Validation Error', 'Rejection Reason Cannot be Empty!', 'Ok');
                        } else {
                            showSuccessAndRedirect("Application Rejected!", API.redirectSuccessURL, "Rejected");
                        }
                    });
                },
                error: (jqXHR) => {
                     $('#feedbackModal').modal('hide');
                     Notiflix.Report.Failure('Error', `Request Failed: ${jqXHR.statusText}`, 'Ok');
                }
            });
        });

        modalConfig.footer = $('<div class="modal-footer"></div>')
            .append('<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>')
            .append(confirmBtn);
    }

    $('#feedbackModalLabel').text(modalConfig.title);
    $('#feedbackModalBody').html(modalConfig.body);
    $('#feedbackModal .modal-footer').replaceWith(modalConfig.footer);
    $('#feedbackModal').modal('show');
}

$(document).ready(function () {
    $("#menu-toggle").click(function (e) {
        e.preventDefault();
        $("#wrapper").toggleClass("toggled");
    });

    $('#backtotop').click(function () {
        $("html, body").animate({ scrollTop: 0 }, 600);
        return false;
    });

    $('.sub-menu ul, .sub-sub-menu ul').hide();
    $(".sub-menu a, .sub-sub-menu a").click(function () {
        $(this).parent().children("ul").slideToggle("100");
        $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
    });

    $('.action-btn-approve').on('click', function() {
        const studentLeaveId = $(this).data('studentleaveid');
        showActionModal('approve', studentLeaveId);
    });

    $('.action-btn-reject').on('click', function() {
        const studentLeaveId = $(this).data('studentleaveid');
        showActionModal('reject', studentLeaveId);
    });
});