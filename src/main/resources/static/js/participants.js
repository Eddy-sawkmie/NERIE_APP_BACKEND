$(document).ready(function(){
   /*
       Initialize the DataTable on the table with id="participantsTable"
   */
   $('#participantsTable').DataTable({
       processing: true,
       serverSide: true,

       pageLength: 10,
       lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "All"]],

       /* DOM & BUTTONS CONFIGURATION */
       dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
            '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
            'rtip',

       buttons: [
           {
               extend: 'excelHtml5',
               text: '<i class="fas fa-file-excel me-1"></i> Excel',
               title: 'Participants Data',
               className: 'btn btn-success btn-modern btn-sm mb-3 shadow-sm',
               exportOptions: {
                   columns: ':visible:not(.noExport)'
               }
           }
       ],

       /* Custom AJAX function */
       ajax: function(data, callback) {
           const page = data.length === -1 ? 0 : data.start / data.length;
           const size = data.length === -1 ? 1000000 : data.length; // Pass a huge number to Spring if "All" is selected
           const search = data.search.value;

           $.ajax({
               url: API.participantsData,
               data: {
                   page: page,
                   size: size,
                   search: search
               },
               success: function(response) {
                   callback({
                       recordsTotal: response.totalElements,
                       recordsFiltered: response.totalElements,
                       data: response.content
                   });
               }
           });
       },

       columns: [
           { data: "username" },
           { data: "gender" },
           { data: "category" },
           { data: "state" }
       ]
   });
});