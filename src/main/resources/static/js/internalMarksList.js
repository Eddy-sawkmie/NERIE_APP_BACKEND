var marksTable;

$(document).ready(function () {
    marksTable = $('#internalmarkstable').DataTable({
        // Modern layout for DataTables controls (aligns search and buttons)
        dom: '<"d-flex justify-content-between align-items-center mb-3"Bf>rt<"d-flex justify-content-between align-items-center mt-3"ip>',
        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fas fa-file-excel me-1"></i> Export Excel',
                title: 'My Internal Evaluation Marks',
                className: 'btn btn-sm btn-success btn-modern',
                exportOptions: {
                    columns: ':visible'
                }
            }
        ],
        pageLength: 10,
        lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "All"]],
        language: { 
            emptyTable: "<div class='text-center p-4'><i class='fas fa-clipboard fs-1 text-muted mb-3'></i><br>Select a subject to view your marks.</div>",
            search: "_INPUT_",
            searchPlaceholder: "Search marks..."
        }
    });

    // Style the default DataTables search input to match the modern-select styling
    $('.dataTables_filter input').addClass('form-control form-control-sm modern-select d-inline-block w-auto');

    $('#subjectcode').on('change', function() {
        const subjectCode = $(this).val();
        const marksCard = $('#marks-table-card');

        marksTable.clear().draw();
        marksCard.hide();
        $('#tabletitle').html('<i class="fas fa-list text-primary me-2"></i> Marks Details');

        if (subjectCode === '-1' || subjectCode === null) {
            return;
        }

        const subjectText = $("#subjectcode option:selected").text();
        $('#tabletitle').html(`<i class="fas fa-list text-primary me-2"></i> Marks for <span class="text-primary">${subjectText}</span>`);

        Notiflix.Loading.Standard('Loading your marks...');

        $.ajax({
            type: "GET",
            url: API.internalEvaluationMarks,
            data: { subjectcode: subjectCode },
            success: function (data) {
                Notiflix.Loading.Remove();

                if (data && data.length > 0) {
                    let marksRows = [];
                    data.forEach(mark => {
                        // Apply styling to the row data to make it look clean
                        const testName = mark[0] ? `<span class="fw-semibold text-dark">${mark[0]}</span>` : 'N/A';
                        const fullMarks = mark[1] !== null ? `<span class="text-muted">${mark[1]}</span>` : 'N/A';
                        const marksObtained = mark[2] !== null ? `<span class="fw-bold text-success">${mark[2]}</span>` : '<span class="text-muted">Not Available</span>';
                        
                        // Added text-center classes via DataTables column definitions or directly here if needed
                        // But since we put text-center on the headers, DataTables usually follows suit. 
                        // To be safe, wrapping in divs for alignment:
                        marksRows.push([
                            testName, 
                            `<div class="text-center">${fullMarks}</div>`, 
                            `<div class="text-center">${marksObtained}</div>`
                        ]);
                    });
                    marksTable.rows.add(marksRows).draw();
                } else {
                    marksTable.clear().draw();
                    // Inject a nicely styled empty state when no marks exist
                    marksTable.settings()[0].oLanguage.sEmptyTable = "<div class='text-center p-4'><i class='fas fa-folder-open fs-1 text-muted mb-3'></i><br>No marks have been uploaded for this subject yet.</div>";
                    marksTable.draw();
                }
                marksCard.fadeIn(300); // Changed to fadeIn for a smoother reveal
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Notiflix.Loading.Remove();
                showModalAlert("An error occurred while loading your marks: " + textStatus + ". Please contact support.", "Error");
            }
        });
    });
});

function showModalAlert(message, title = 'Message') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);
    
    // Updated to use Bootstrap 5's JS initialization if jQuery modal doesn't trigger properly
    // Note: If you have issues opening this, uncomment the native BS5 approach below:
    // var myModal = new bootstrap.Modal(document.getElementById('feedbackModal'));
    // myModal.show();
    
    $('#feedbackModal').modal('show');
}