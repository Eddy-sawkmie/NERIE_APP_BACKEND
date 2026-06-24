$(document).ready(function () {
    $('#approveprogram').DataTable({
        pageLength: 5,
        lengthMenu: [[5, 10, 20, 50, -1], [5, 10, 20, 50, "All"]],

        dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
             'rtip',

        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fas fa-file-excel me-1"></i> Excel',
                title: 'Overall Feedback List',
                className: 'btn btn-success btn-modern btn-sm mb-3 shadow-sm',
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                }
            }
        ]
    });
});