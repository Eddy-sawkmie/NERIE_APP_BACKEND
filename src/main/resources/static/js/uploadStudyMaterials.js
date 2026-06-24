function showModalAlert(message, title = 'Message', reloadOnClose = false) {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').text(message);
    $('#feedbackModal').off('hidden.bs.modal');

    if (reloadOnClose) {
        $('#feedbackModal').on('hidden.bs.modal', () => window.location.reload());
    }

    $('#feedbackModal').modal('show');
}

function validateForm() {
    if ($("#subject").val() === "-1" || $("#subject").val() === null) {
        showModalAlert("Please select a Subject.");
        return false;
    }

    const title = $("#assignmentName").val().trim();
    if (title === "") {
        showModalAlert("Please enter a Study Material Title.");
        return false;
    }

    const isEditing = $("#studymaterialid").val() !== "";
    if (!isEditing && document.getElementById("file1").files.length === 0) {
        showModalAlert("Please upload a study material file.");
        return false;
    }

    return true;
}

$(document).ready(function () {
    // ----------------------------------------------------
    // DATATABLE INITIALIZATION
    // ----------------------------------------------------
    var assignmentsTable = $('#assignmentslist').DataTable({
        retrieve: true,
        bDestroy: true,
        ordering: false,
        pageLength: 10,
        lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
        language: { emptyTable: "No study materials found." },
        dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
             'rtip',
        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                title: 'Study Materials List',
                exportOptions: { columns: ':visible:not(.noExport)' },
                className: 'btn btn-success btn-sm mb-3'
            }
        ]
    });

    // Form submission
    $('#studymaterials').on('submit', function(event) {
        event.preventDefault();

        if (!validateForm()) {
            return;
        }

        Notiflix.Loading.Standard('Uploading...');
        var formData = new FormData(this);

        $.ajax({
            type: "POST",
            url: $(this).attr('action'),
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                Notiflix.Loading.Remove();
                if (response === "1") {
                    showModalAlert('Successfully Uploaded Study Material.', 'Upload Success', true);
                } else {
                    showModalAlert('There was an error processing your request. Please try again.', 'Upload Failed');
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                Notiflix.Loading.Remove();
                showModalAlert(`AJAX error: ${textStatus} - ${errorThrown}`, 'Error');
            }
        });
    });

    // Handle Edit via event delegation
    $('#assignmentslist').on('click', '.edit-study-material', function(event) {
        event.preventDefault();
        const studyMaterialId = $(this).data('id');
        const title = $(this).data('title');
        const subject = $(this).data('subject');

        $("#subject").val(String(subject || '-1'));
        $("#assignmentName").val(String(title || ''));
        $("#studymaterialid").val(String(studyMaterialId || ''));

        $("#filemessage").html('<span class="text-danger small">If you re-upload a document, the existing one will be overridden.</span>');
        $('html, body').animate({ scrollTop: 0 }, 'fast');
        $("#assignmentName").focus();
    });

    // Handle Subject filter
    $("#subject").change(function () {
        var sub = $(this).val();
        if (sub === "-1") return;

        Notiflix.Loading.Standard('Fetching materials...');
        $.ajax({
            type: "GET",
            url: API.getStudyMaterialsListSubject,
            data: { subjectcode: sub },
            success: function (data) {
                Notiflix.Loading.Remove();
                assignmentsTable.clear();

                if (data && data.length > 0) {
                    data.forEach(item => {
                        const uploadDate = new Date(item[2]);
                        const formattedDate = ('0' + uploadDate.getDate()).slice(-2) + '-' +
                                          uploadDate.toLocaleString('default', { month: 'short' }) + '-' +
                                          uploadDate.getFullYear() + ' ' +
                                          ('0' + uploadDate.getHours()).slice(-2) + ':' +
                                          ('0' + uploadDate.getMinutes()).slice(-2) + ':' +
                                          ('0' + uploadDate.getSeconds()).slice(-2);

                        const viewButton = `<a href="viewStudyMaterialDocument?sid=${item[1]}" class="btn btn-sm btn-secondary" target="_blank"><i class="fa fa-eye"></i> View</a>`;
                        const editButton = `<button class="btn btn-sm btn-info edit-study-material"
                                            data-id="${item[1]}"
                                            data-title="${item[0]}"
                                            data-subject="${item[3]}">
                                        <i class="fa fa-edit"></i> Edit
                                    </button>`;

                        assignmentsTable.row.add([item[0], viewButton, formattedDate, editButton]);
                    });
                }
                assignmentsTable.draw();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Notiflix.Loading.Remove();
                showModalAlert(`AJAX error: ${textStatus} - ${errorThrown}`, 'Error');
            }
        });
    });
});

function assignmentfile() {
    const fileInput = document.getElementById("file1");
    if (fileInput.files.length > 0) {
        var mext = fileInput.value.split('.').pop().toLowerCase();
        if (mext === "pdf") {
            $("#filemessage").text("");
            return true;
        } else {
            fileInput.value = "";
            $("#filemessage").html('<span class="text-danger small">Invalid file type. Only PDF allowed.</span>');
            showModalAlert('Please select a PDF file only.', 'Invalid File Type');
            return false;
        }
    }
}