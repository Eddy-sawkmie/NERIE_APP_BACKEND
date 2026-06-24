$(document).ready(function(){
   /*
       Initialize the DataTable on the table with id="resourcePersonsTable"
   */
   $('#resourcePersonsTable').DataTable({
       processing: true,
       serverSide: true,

       pageLength: 10,
       lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "All"]],

       dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
            '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
            'rtip',

       buttons: [
           {
               extend: 'excelHtml5',
               text: '<i class="fas fa-file-excel me-1"></i> Excel',
               title: 'Resource Persons Data',
               className: 'btn btn-success btn-modern btn-sm mb-3 shadow-sm',
               exportOptions: {
                   columns: ':visible:not(.noExport)'
               }
           }
       ],

       /*
           Custom AJAX function.
       */
       ajax: function(data, callback) {
           const page = data.length === -1 ? 0 : data.start / data.length;
           const size = data.length === -1 ? 1000000 : data.length; // Pass a huge number to Spring if "All" is selected
           const search = data.search.value;

           $.ajax({
               url: API.resourcePersonsData,
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

       /*
           Define how JSON fields map to table columns.
       */
       columns: [
           { data: "name" },
           { data: "email" },
           { data: "designation" },
           { data: "qualification" },
           { data: "office"}
       ]
   });
});