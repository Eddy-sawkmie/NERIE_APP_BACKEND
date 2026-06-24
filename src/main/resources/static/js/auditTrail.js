$(document).ready(function () {
    try {
        var table = $('#audittable').DataTable({
            "paging": false,
            "searching": false,
            "ordering": false,
            "info": false,

            dom: 'Brt',

            buttons: [
                {
                    extend: 'excelHtml5',
                    text: '<i class="fas fa-file-excel me-1"></i> Export Excel',
                    title: 'System Audit Trail Report',
                    exportOptions: {
                        columns: ':visible:not(.noExport)'
                    },
                    className: 'btn btn-sm btn-success btn-modern px-3 py-1' // Added modern styling classes
                }
            ],
            responsive: true
        });

        // Appends the button smoothly into our new modern top bar container
        table.buttons().container().appendTo('#export-buttons-container');

        console.log("DataTables initialized for #audittable with custom controls.");
    } catch(e) {
         console.error("Error initializing DataTables:", e);
    }
});