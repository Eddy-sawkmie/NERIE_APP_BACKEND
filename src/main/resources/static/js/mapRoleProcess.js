function showModalAlert(message, title = 'Message') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);

    // Updated for Bootstrap 5
    $('#feedbackModal .modal-footer').html(
        '<button type="button" class="btn btn-primary btn-modern px-4" data-bs-dismiss="modal">OK</button>'
    );

    $('#feedbackModal').modal('show');
}

function showModalAndRedirect(message, url) {
    showModalAlert(message, 'Success');
    $('#feedbackModal').one('hidden.bs.modal', function () {
        window.location.href = url;
    });
}

function updateSelectedCount() {
    const count = $(".processCheck:checked").length;
    $("#selectedCount").text(`${count} Selected`);
}

// Bind counter update to checkbox changes
$(document).on('change', '.processCheck', function() {
    updateSelectedCount();
});

$("#saveMapping").click(function () {
    var roleid = $("#roleSelect").val();
    var processcodes = [];

    $(".processCheck:checked").each(function () {
        processcodes.push($(this).val());
    });

    if (!roleid) {
        showModalAlert('<i class="fas fa-exclamation-triangle text-warning me-2"></i> Please select a target role first!', 'Validation Error');
        return;
    }

    // Visual loading state
    var $btn = $(this);
    var originalText = $btn.html();
    $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-2"></i> Saving...');

    $.ajax({
        url: API.saveRoleProcesses,
        type: "POST",
        data: { roleid: roleid, processcodes: processcodes },
        traditional: true,
        success: function (res) {
            $btn.prop('disabled', false).html(originalText);
            if (res === "success") {
                showModalAlert('<i class="fas fa-check-circle text-success me-2"></i> Mapping saved successfully!', 'Success');
            } else {
                showModalAlert("An error occurred while saving the mapping!", "Error");
            }
        },
        error: function () {
            $btn.prop('disabled', false).html(originalText);
            showModalAlert("A server error occurred while saving data!", "Server Error");
        }
    });
});

$('#roleSelect').change(function() {
    var rolecode = $(this).val();

    // Reset all checks
    $('.processCheck').prop('checked', false);
    updateSelectedCount();

    if (!rolecode) return;

    // Visual loading indication for checkboxes
    $('.checkbox-container').css('opacity', '0.5').css('pointer-events', 'none');

    $.ajax({
        type: "GET",
        url: API.getProcessesByRole,
        data: { rolecode: rolecode },
        success: function(response) {
            $('.checkbox-container').css('opacity', '1').css('pointer-events', 'auto');
            
            $.each(response, function(index, code) {
                $('#proc' + code).prop('checked', true);
            });
            updateSelectedCount();
        },
        error: function() {
            $('.checkbox-container').css('opacity', '1').css('pointer-events', 'auto');
            showModalAlert('Failed to load existing processes for this role.', 'Fetch Error');
        }
    });
});

document.addEventListener("DOMContentLoaded", function () {
    // Initialization
    updateSelectedCount();

    document.querySelectorAll("[data-action='reset']").forEach(button => {
        button.addEventListener("click", function () {
            if (typeof customReset === 'function') {
                customReset();
            }
        });
    });
});