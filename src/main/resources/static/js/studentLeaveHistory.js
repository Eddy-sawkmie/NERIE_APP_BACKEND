$(document).ready(function () {
    const dtConfig = {
        retrieve: true,
        bDestroy: true,
        ordering: false,
        pageLength: 10,
        lengthMenu: [[5, 10, 20, 50, -1], [5, 10, 20, 50, "All"]],
        dom: '<"row"<"col-12 d-flex justify-content-end mb-2"B>>' +
             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
             'rtip',
        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                className: 'btn btn-success btn-sm',
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                }
            },
            {
                extend: 'pdfHtml5',
                text: '<i class="fa fa-file-pdf-o"></i> PDF',
                className: 'btn btn-danger btn-sm',
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                }
            }
        ]
    };

    $('#leavelist').DataTable(dtConfig);

    const table2Element = $('#leavelist-all');
    if (table2Element.length) {
        table2Element.DataTable(dtConfig);

        const toggle = $('#toggleLeaveView');
        const current = $('#page-content-wrapper');
        const all = $('#page-content-wrapper-all');

        toggle.on('change', function() {
            if ($(this).is(':checked')) {
                // Show all leaves
                current.hide();
                all.fadeIn();
            } else {
                // Show current semester
                all.hide();
                current.fadeIn();
            }
        });
    }
});