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

document.addEventListener("DOMContentLoaded", function () {
    
    // Delegation for Action Buttons
    $(document).on("click", ".acceptbtn", function (e) {
        e.preventDefault();
        let programId = $(this).attr("data-program-id");
        let someValue = $(this).attr("data-some-value");
        acceptprogramfunc(programId, someValue);
    });

    $(document).on("click", ".rejectbtn", function (e) {
        e.preventDefault();
        let programId = $(this).attr("data-program-id");
        let someValue = $(this).attr("data-some-value");
        rejectprogramfunc(programId, someValue);
    });

    $(document).on("click", ".detetebtn", function (e) {
        e.preventDefault();
        let programId = $(this).attr("data-program-id");
        let programName = $(this).attr("data-some-value");
        deleteprogramfunc(programId, programName);
    });
});


$(document).ready(function () {

    // 1. Initialize DataTable with Modern UI Layout
    if ($.fn.DataTable && $("#approveprogram").length) {
        $("#approveprogram").DataTable({
            dom: '<"d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-3"Bf>rt<"d-flex flex-column flex-md-row justify-content-between align-items-md-center mt-3"ip>',
            pageLength: 10,
            lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "All"]],
            language: {
                emptyTable: $('#emptyTableMessage').html(), // Use custom UI empty state
                search: "_INPUT_",
                searchPlaceholder: "Search programs..."
            },
            buttons: [
                {
                    extend: 'excelHtml5',
                    text: '<i class="fas fa-file-excel me-1"></i> Export Excel',
                    title: 'Programs',
                    exportOptions: { columns: ':visible:not(.noExport)' },
                    className: 'btn btn-sm btn-success btn-modern px-3 py-1'
                }
            ]
        });

        // Style the DataTables native search input to match Bootstrap 5
        $('.dataTables_filter input').addClass('form-control form-control-sm modern-input d-inline-block w-auto');
    }

    // 2. Accept Program Form Submit
    $("#acceptprogramfid").submit(function (e) {
        e.preventDefault();
        if (!validateApprovedDoc()) return;

        const $btn = $("#submitAcceptBtn");
        const originalText = $btn.html();
        $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-2"></i> Processing...');

        var formData = new FormData($(this)[0]);
        
        $.ajax({
            type: "POST",
            url: API.acceptPrograms,
            data: formData,
            processData: false,
            contentType: false,
            success: function (data) {
                $btn.prop('disabled', false).html(originalText);
                bootstrap.Modal.getInstance(document.getElementById('modalaccept-modal')).hide();
                handleCommonResponse(data, "Program Approved Successfully!", "Please Upload Approval Letter");
            },
            error: function (jqXHR, textStatus, errorThrown) {
                $btn.prop('disabled', false).html(originalText);
                bootstrap.Modal.getInstance(document.getElementById('modalaccept-modal')).hide();
                handleAjaxError(jqXHR, textStatus, errorThrown);
            }
        });
    });

    // 3. Reject Program Form Submit
    $("#rejectprogramfid").submit(function (e) {
        e.preventDefault();
        if (!validateRejectDoc()) return;

        const $btn = $("#submitRejectBtn2");
        const originalText = $btn.html();
        $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-2"></i> Processing...');

        var formData = new FormData($(this)[0]);
        
        $.ajax({
            type: "POST",
            url: API.rejectPrograms,
            data: formData,
            processData: false,
            contentType: false,
            success: function (data) {
                $btn.prop('disabled', false).html(originalText);
                bootstrap.Modal.getInstance(document.getElementById('modalreject-modal')).hide();
                handleCommonResponse(data, "Program Rejected Successfully!", "Please Upload Rejection Letter");
            },
            error: function (jqXHR, textStatus, errorThrown) {
                $btn.prop('disabled', false).html(originalText);
                bootstrap.Modal.getInstance(document.getElementById('modalreject-modal')).hide();
                handleAjaxError(jqXHR, textStatus, errorThrown);
            }
        });
    });
});


// =================================================================
// FORM ACTIONS & VALIDATIONS
// =================================================================

function deleteprogramfunc(programcode, programname) {
    showModalConfirm(`Are you sure you want to delete the Program <b>${programname}</b>?`, "Confirm Deletion", function() {
        $.ajax({
            method: "GET",
            url: API.deletePrograms,
            data: { programcode: programcode }
        })
        .done(function (response) {
            if (response === "1") {
                showModalAlert("Program was Deleted successfully.", "Success", function() {
                    window.location.reload();
                });
            } else {
                showModalAlert("Unable to Delete program. Please try again!", "Error");
            }
        })
        .fail(function () {
            showModalAlert("Network Error: Something went wrong.", "Error");
        });
    });
}

function handleCommonResponse(data, successMsg, errorMsg1) {
    if (data === "2") {
        showModalAlert(successMsg, "Success", function() {
            window.location.reload();
        });
    } else if (data === "1") {
        showModalAlert(errorMsg1, "Validation Error");
    } else if (data === "4") {
        showModalAlert("Uploaded File is not allowed. Kindly check filetype or filename.", "File Error");
        $('input[type="file"]').val(null);
    } else {
        showModalAlert("Save Failed!!! Please try again.", "Error");
        $('input[type="file"]').val(null);
    }
}

// Modal Data Population Functions
function acceptprogramfunc(pdid, cname) {
    $("#acceptprogramname").html(cname);
    $("#aprogramdetailid").val(pdid);
    $("#file1").val(""); // clear previous files
}

function rejectprogramfunc(pdid, cname) {
    $("#rejectprogramname").html(cname);
    $("#rprogramdetailid").val(pdid);
    $("#rejectremark").val(""); // clear previous remarks
    $("#file2").val(""); // clear previous files
}

// Validation Functions
function validateApprovedDoc() { return checkFileType("file1", "Approval Letter"); }
function validateRejectDoc() { return checkFileType("file2", "Rejection Letter"); }

function checkFileType(inputId, docName) {
    var fileInput = document.getElementById(inputId);
    if (fileInput.files.length !== 0) {
        var mext = $("#" + inputId).val().split(".").pop().toLowerCase();
        if (mext !== "jpg" && mext !== "jpeg" && mext !== "pdf") {
            showModalAlert(`${docName} should be of type PDF, JPG, or JPEG only.`, "Invalid File Type");
            $("#" + inputId).val("");
            return false;
        }
    }
    return true;
}

function checkapprovedoctype() { validateApprovedDoc(); }
function checkrejectdoctype() { validateRejectDoc(); }

function handleAjaxError(jqXHR, textStatus, errorThrown) {
    showModalAlert(`A server error occurred: ${textStatus}`, "Network Error");
}