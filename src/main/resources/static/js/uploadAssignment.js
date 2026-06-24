// Document Ready & Event Listeners
$(document).ready(function () {
    $('#assignmentslist').DataTable({
        retrieve: true,
        bDestroy: true,
        ordering: false,
        pageLength: 10,
        lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
        language: { emptyTable: "No assignments found." },
        dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
             'rtip',
        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                title: 'Assignments List',
                exportOptions: { columns: ':visible:not(.noExport)' },
                className: 'btn btn-success btn-sm mb-3'
            }
        ]
    });

    // Setup Mark Validation
    setupInstantMarkValidation('#fullmarkCreate', '#passmarkCreate');
    setupInstantMarkValidation('#edfullmarkEdit', '#edpassmarkEdit');

    // Bind Edit Button Click
    $('#assignmentslist tbody').on('click', '.edit-button', function () {
        showEditModal(this);
    });

    // Handle AJAX: Create Form
    $("#assignmentid").submit(function (e) {
        e.preventDefault();
        if (!validateForm('#assignmentid')) return;

        var formData = new FormData(this);
        Notiflix.Loading.Standard('Uploading Assignment...');
        $.ajax({
            type: "POST",
            url: $(this).attr('action'),
            data: formData,
            processData: false,
            contentType: false,
            success: function (data) {
                Notiflix.Loading.Remove();
                if (data === '-1') {
                    showModalMessage("An error occurred. Please try again.");
                } else {
                    showModalMessage("Assignment successfully uploaded.");
                    $('#feedbackModal').one('hidden.bs.modal', () => window.location.reload());
                }
            },
            error: (jqXHR, textStatus) => {
                Notiflix.Loading.Remove();
                showModalMessage("Error: " + textStatus);
            }
        });
    });

    // Handle AJAX: Edit Form
    $("#editassignmentForm").submit(function (e) {
        e.preventDefault();
        if (!validateForm('#editassignmentForm')) return;

        var formDataed = new FormData(this);
        Notiflix.Loading.Standard('Updating Assignment...');
        $.ajax({
            type: "POST",
            url: $(this).attr('action'),
            data: formDataed,
            processData: false,
            contentType: false,
            success: function (data) {
                Notiflix.Loading.Remove();
                const editModal = bootstrap.Modal.getInstance(document.getElementById('editModal'));
                if (data === '-1') {
                    showModalMessage("An error occurred during update.");
                } else {
                    editModal.hide();
                    $('#editModal').one('hidden.bs.modal', () => {
                         showModalMessage("Assignment successfully updated.");
                         $('#feedbackModal').one('hidden.bs.modal', () => window.location.reload());
                    });
                }
            },
            error: (jqXHR, textStatus) => {
                Notiflix.Loading.Remove();
                showModalMessage("Edit Error: " + textStatus);
            }
        });
    });

    // Datepickers initialization
    if ($.fn.datepicker) {
        $('.datepicker').datepicker({
            dateFormat: "dd-mm-yy",
            orientation: 'bottom',
            autoclose: true
        });
        var today = new Date();
        $("#lastdateCreate, #edlastdateEdit").datepicker('option', 'minDate', today);
        $("#assignmentdateCreate, #edassignmentdateEdit").datepicker('option', 'minDate', today);

        $("#assignmentdateCreate").on("change", function() {
            $("#lastdateCreate").datepicker("option", "minDate", $(this).datepicker("getDate"));
        });
        $("#edassignmentdateEdit").on("change", function() {
            $("#edlastdateEdit").datepicker("option", "minDate", $(this).datepicker("getDate"));
        });
    }

    // Character Counters
    $("#AssignmentDescUploadCreate").on("keyup", function () {
        $("#charactercountdescCreate").html("Characters left: " + (500 - $(this).val().length));
    });
    $("#edAssignmentDescUploadEdit").on("keyup", function () {
        $("#edcharactercountdescEdit").html("Characters left: " + (500 - $(this).val().length));
    });

    // UI Logic: Submission Type Toggle
    const handleSubmissionTypeChange = (context) => {
        const C = context === 'Create';
        const prefix = C ? "" : "ed";
        const suffix = C ? "Create" : "Edit";

        const noneRadio = $(`#${prefix}NoneRadio${C ? "" : "Edit"}`);
        const fileRadio = $(`#${prefix}fileRadio${suffix}`);
        const linkRadio = $(`#${prefix}linkRadio${suffix}`);

        const fileDiv = $(`#${C ? "fileUploadDivCreate" : "edfileInputDivEdit"}`);
        const linkDiv = $(`#${C ? "linkInputDivCreate" : "edlinkInputDivEdit"}`);

        const fileInput = $(`#${C ? "file1Create" : "edfile1Edit"}`);
        const linkInput = $(`#${C ? "linkInputCreate" : "edlinkInputEdit"}`);
        const fileMessage = C ? null : $("#edfilemessage");

        if (noneRadio.is(":checked")) {
            fileDiv.hide(); linkDiv.hide();
            fileInput.val(''); linkInput.val('');
            if (!C) fileMessage.text("");
        }
        else if (fileRadio.is(":checked")) {
            fileDiv.show(); linkDiv.hide();
            linkInput.val('');
            if (!C) fileMessage.text("If you re-upload a document, the existing one will be overridden.").addClass("text-danger");
        }
        else if (linkRadio.is(":checked")) {
            fileDiv.hide(); linkDiv.show();
            fileInput.val('');
            if (!C) fileMessage.text("");
        }
    };

    $('input[name="submissiontype"]').on("change", function() {
        const context = $(this).closest('form').attr('id') === 'assignmentid' ? 'Create' : 'Edit';
        handleSubmissionTypeChange(context);
    });

    // Student List Loader
    $('#subjectCodeCreate').change(function () {
        let subjectcode = $(this).val();
        if (!subjectcode) {
            $('#studentListContainer').html('<p class="text-muted small">Select subject to load students</p>');
            $('#studentCountBadge').text(0);
            return;
        }

        $.ajax({
            url: '/assignments/students-by-subject',
            type: 'GET',
            data: { subjectcode: subjectcode },
            success: function (data) {
                let html = '';
                if (!data || data.length === 0) {
                    html = '<div class="text-danger small p-2">No students found.</div>';
                    $('#studentCountBadge').text(0);
                } else {
                    html += `<div class="container-fluid p-0"><div class="row g-1 m-0" style="overflow-x: hidden;">`;
                    data.forEach(function (student) {
                        html += `
                            <div class="col-12 col-md-6 small px-2 py-1 text-truncate">
                                ${student.studentname} <small class="text-muted">(Sem: ${student.currentperiod})</small>
                            </div>`;
                    });
                    html += `</div></div>`;

                    $('#studentCountBadge').text(data.length);
                    const bsCollapse = new bootstrap.Collapse(document.getElementById('studentCollapse'), { toggle: false });
                    bsCollapse.show();
                }
                $('#studentListContainer').html(html);
            }
        });
    });
});

// Helper Functions
function validateForm(formId) {
    const $form = $(formId);
    if ($form.find('select[id*="subjectCode"]').val() === "") { showModalMessage("Please select a Subject."); return false; }
    if ($form.find('input[id*="assignmentName"]').val().trim() === "") { showModalMessage("Please enter an Assignment Name."); return false; }

    const subType = $form.find('input[name="submissiontype"]:checked').val();
    if (!subType) { showModalMessage("Please select a Submission Type."); return false; }

    if (subType === 'LINK' && $form.find('input[name="submissionLink"]').val().trim() === "") {
        showModalMessage("Please enter a submission link."); return false;
    }

    const fullMark = parseInt($form.find('input[name="fullmark"]').val(), 10);
    const passMark = parseInt($form.find('input[name="passmark"]').val(), 10);
    if (isNaN(fullMark) || fullMark > 100) { Notiflix.Notify.Warning("Invalid Full Mark."); return false; }
    if (passMark > fullMark) { Notiflix.Notify.Warning("Pass Mark exceeds Full Mark."); return false; }

    return true;
}

function showEditModal(button) {
    const $btn = $(button);
    const modalEl = document.getElementById('editModal');
    const $modal = $(modalEl);

    $("#assignmentIdEdit").val($btn.data("id"));
    $("#edassignmentNameEdit").val($btn.data("title"));
    $("#edAssignmentDescUploadEdit").val($btn.data("description")).trigger('keyup');
    $("#subjectCodeEdit").val($btn.data("subject"));
    $("#edassignmentdateEdit").val($btn.data("uploaddate"));
    $("#edlastdateEdit").val($btn.data("submissiondate"));
    $("#edfullmarkEdit").val($btn.data("fullmark"));
    $("#edpassmarkEdit").val($btn.data("passmark"));

    const subType = $btn.data("submissiontype") || 'FILE';
    $(`input[name="submissiontype"][value="${subType}"]`).prop("checked", true).trigger('change');

    if (subType === 'LINK') $("#edlinkInputEdit").val($btn.data("linkurl"));

    const bsModal = new bootstrap.Modal(modalEl);
    bsModal.show();
}

function showModalMessage(message) {
    $("#feedbackModalBody").html(message);
    const bsFeedback = new bootstrap.Modal(document.getElementById('feedbackModal'));
    bsFeedback.show();
}

function setupInstantMarkValidation(f, p) {
    $(f + "," + p).on('input', function() { this.value = this.value.replace(/[^0-9]/g, ''); });
    $(f).on('blur', function() {
        if (parseInt($(this).val()) > 100) { Notiflix.Notify.Warning("Max 100."); $(this).val(100); }
        $(p).trigger('blur');
    });
    $(p).on('blur', function() {
        const fm = parseInt($(f).val());
        if (parseInt($(this).val()) > fm) { Notiflix.Notify.Warning("Exceeds Full Mark."); $(this).val(fm); }
    });
}

function assignmentfile(context) {
    const fileInput = context === 'Edit' ? $("#edfile1Edit") : $("#file1Create");
    if (fileInput[0].files.length > 0) {
        if (fileInput.val().split('.').pop().toUpperCase() !== "PDF") {
            fileInput.val(""); showModalMessage("Only PDF allowed."); return false;
        }
    }
    return true;
}