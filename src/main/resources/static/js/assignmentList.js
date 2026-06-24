$(document).ready(function () {

    $('#assignmentlist').DataTable({
        dom: '<"d-flex justify-content-between align-items-center mb-3"Bf>rt<"d-flex justify-content-between align-items-center mt-3"ip>', // Modern layout for DataTables controls
        pageLength: 10,
        lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
        language: {
            emptyTable: "<div class='text-center p-4'><i class='fas fa-folder-open fs-1 text-muted mb-3'></i><br>No Study Material Available for the selected subject</div>",
            search: "_INPUT_",
            searchPlaceholder: "Search Assignments..."
        },
        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fas fa-file-excel me-1"></i> Export Excel',
                title: 'Study Materials',
                exportOptions: { columns: ':visible:not(.noExport)' },
                className: 'btn btn-sm btn-success btn-modern' // Added modern styling
            }
        ]
    });

    // Show Description
    $('#assignmenttabletbody').on('click', '.show-description-btn', function () {
        showDescriptionModal(this.id);
    });

    // Upload / Edit
    $('#assignmenttabletbody').on('click', '.this_is_upload_button_class, .edit-assignment-btn', function () {
        const assignmentId = this.id;
        const isEdit = $(this).hasClass('edit-assignment-btn');
        const action = isEdit ? 'edit' : 'submit';
        showAssignmentActionModal(assignmentId, action);
    });

    // Form Submission
    $("#assignmentActionForm").submit(function (e) {
        e.preventDefault();

        if ($("#assignmentFile").get(0).files.length === 0) {
            alert("Please select a file to upload.");
            $("#assignmentFile").focus();
            return false;
        }

        var formData = new FormData(this);
        const submitBtn = $("#submitAssignmentBtn");

        submitBtn.prop('disabled', true).text('Processing...');

        $.ajax({
            type: "POST",
            url: API.uploadAssignment,
            data: formData,
            processData: false,
            contentType: false,
            success: function (data) {

                if (data === '-1') {
                    Notiflix.Notify.Failure('There was a failure processing the assignment.');
                } else {

                    // ✅ Bootstrap 5 hide modal
                    const modal = bootstrap.Modal.getOrCreateInstance(
                        document.getElementById('assignmentActionModal')
                    );
                    modal.hide();

                    Notiflix.Notify.Success('Assignment processed successfully!');
                    setTimeout(() => window.location.reload(), 1500);
                }
            },
            error: function (jqXHR, textStatus) {
                Notiflix.Notify.Failure('An error occurred: ' + textStatus);
            },
            complete: function () {
                submitBtn.prop('disabled', false);
            }
        });
    });

    // File Validation
    $("#assignmentFile").change(function () {
        if (this.files.length > 0) {
            const fileName = this.files[0].name;
            const fileExtension = fileName.split('.').pop().toLowerCase();

            if (fileExtension !== "pdf") {
                alert("Only PDF files are allowed.");
                $(this).val('');
            }
        }
    });
});

function showDescriptionModal(assignmentId) {
    $("#descriptiontext").html($("#desc-" + assignmentId).val());
}

function showAssignmentActionModal(assignmentId, action) {

    const form = document.getElementById("assignmentActionForm");
    if (form) form.reset();

    document.getElementById("hiddenAssignmentId").value = assignmentId;

    const subjectName = $("#subn-" + assignmentId).val();
    const assignmentTitle = $("#testn-" + assignmentId).val();

    if (action === 'edit') {
        $("#assignmentActionModalLabel").text("Edit Assignment Submission");
        $("#assignmentActionMessage").html(`
            You are about to <strong>edit your submission</strong> for:<br/>
            <strong>Subject:</strong> ${subjectName}<br/>
            <strong>Assignment:</strong> ${assignmentTitle}
        `);
        $("#submitAssignmentBtn").text("Update Submission");
    } else {
        $("#assignmentActionModalLabel").text("Upload Assignment");
        $("#assignmentActionMessage").html(`
            You are about to <strong>submit</strong> the assignment for:<br/>
            <strong>Subject:</strong> ${subjectName}<br/>
            <strong>Assignment:</strong> ${assignmentTitle}
        `);
        $("#submitAssignmentBtn").text("Upload Assignment");
    }

    // ❌ NO modal.show() here
}