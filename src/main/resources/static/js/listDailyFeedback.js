/*
 * HELPER FUNCTIONS
 */
function showModalAlert(message, title = 'Message') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);

    $('#feedbackModal .modal-footer').html(
        '<button type="button" class="btn btn-primary btn-modern px-4" data-bs-dismiss="modal">OK</button>'
    );

    var modalElement = document.getElementById('feedbackModal');

    // Move modal to the body to prevent backdrop overlapping issues
    if (modalElement.parentNode !== document.body) {
        document.body.appendChild(modalElement);
    }

    // Use getOrCreateInstance to prevent double-backdrop bugs
    var myModal = bootstrap.Modal.getOrCreateInstance(modalElement);
    myModal.show();
}

/*
 * MAIN LOGIC
 */
let feedbackTable;

$(document).ready(function () {
    $("#statsrow").hide();

    feedbackTable = $('#approveprogram').DataTable({
        pageLength: 5,
        lengthMenu: [[5, 10, 20, 50, -1], [5, 10, 20, 50, "All"]],
        destroy: true, // Crucial for re-initializing after ajax calls

        // Modern BS5 DOM structure
        dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
             'rtip',

        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fas fa-file-excel me-1"></i> Excel',
                title: 'Daily Feedback List',
                className: 'btn btn-success btn-modern btn-sm mb-3 shadow-sm',
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                }
            }
        ]
    });

    $('.getlist').on('click', function () {
        getdayfeedbacks();
    });
});

function getdayfeedbacks() {
    const programCode = $("#programtimetablecode").val();

    if (programCode === "") {
        showModalAlert("Please Select a Subject", "Message");
        return;
    }

    $.ajax({
        type: "GET",
        url: API.listDailyFeedback,
        data: {
            programtimetablecode: programCode
        },
        success: function (data) {
            feedbackTable.clear(); // Clear old data

            if (data.length > 0) {
                // Map the data to incorporate the modern styling classes
                const newRows = data.map((item, index) => {
                    return [
                        `<div class="text-center fw-semibold text-muted">${index + 1}</div>`,
                        `<span class="text-dark d-block">${item.feedback || ''}</span>`,
                        `<span class="fw-bold text-dark">${item.usercode?.username || ''}</span>`
                    ];
                });

                feedbackTable.rows.add(newRows).draw(); // Add and draw
                $("#statsrow").show();
            } else {
                showModalAlert("No Feedbacks Yet", "Information");
                $("#statsrow").hide();
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            showModalAlert(`An error occurred while retrieving data.<br><b>Error:</b> ${textStatus} - ${errorThrown}`, "Error");
        }
    });
}