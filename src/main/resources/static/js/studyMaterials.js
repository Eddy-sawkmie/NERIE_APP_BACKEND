var studyMaterialTable;
var rowclicktddata;

function initializeDataTable() {
    return $('#assignmentlist').DataTable({
        dom: '<"d-flex justify-content-between align-items-center mb-3"Bf>rt<"d-flex justify-content-between align-items-center mt-3"ip>', // Modern layout for DataTables controls
        pageLength: 10,
        lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
        language: {
            emptyTable: "<div class='text-center p-4'><i class='fas fa-folder-open fs-1 text-muted mb-3'></i><br>No Study Material Available for the selected subject</div>",
            search: "_INPUT_",
            searchPlaceholder: "Search materials..."
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
}

$(document).ready(function () {
    studyMaterialTable = initializeDataTable();

    // Style the default DataTables search input to match Bootstrap 5
    $('.dataTables_filter input').addClass('form-control form-control-sm modern-select d-inline-block w-auto');

    $("#menu-toggle").click(function (e) {
        e.preventDefault();
        $("#wrapper").toggleClass("toggled");
    });

    $('#backtotop').click(function () {
        $("html, body").animate({scrollTop: 0}, 600);
        return false;
    });

    $('.sub-menu ul').hide();
    $('.sub-sub-menu ul').hide();

    $(".sub-menu a").click(function () {
        $(this).parent(".sub-menu").children("ul").slideToggle("100");
        $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
    });

    $(".sub-sub-menu a").click(function () {
        $(this).parent(".sub-sub-menu").children("ul").slideToggle("100");
        $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
    });

    $("#subcode").change(function () {
        var subcode = $("#subcode").val();

        studyMaterialTable.clear().draw();

        if (subcode === '-1') {
            alert('Select Subject');
            $("#subcode").focus();
            return false;
        }

        $.ajax({
            type: "GET",
            url: API.getStudyMaterial,
            data: "subjectcode=" + subcode,
            success: function (data) {
                if (data && data.length > 0) {
                    var tableData = data.map(function(item, index) {
                        var uploadDate = new Date(item.uploaddate).toLocaleDateString("en-US", { year: 'numeric', month: 'short', day: 'numeric' }); // Made date formatting a bit cleaner
                        
                        // Updated to match the modern Bootstrap 5 button styling
                        var viewLink = `<a href='${API.viewStudyMaterial}?sid=${item.studymaterialid}' target='_blank' class='btn btn-primary btn-sm btn-modern'>
                                            <i class='fas fa-external-link-alt me-1'></i> View Material
                                        </a>`;

                        return [
                            `<span class="fw-semibold text-muted">${index + 1}</span>`, // Styled index
                            `<span class="fw-medium text-dark">${item.title}</span>`, // Styled title
                            uploadDate,
                            `<div class="text-center">${viewLink}</div>` // Centered action button
                        ];
                    });

                    studyMaterialTable.rows.add(tableData).draw();
                }
            },
            error: (jqXHR, textStatus, errorThrown) => handleAjaxError(jqXHR, textStatus, errorThrown)
        });
    });

    $("#subjectlist").change(function () {
        $.ajax({
            type: "GET",
            url: "./getstudentassinmentlist.htm",
            data: "subcode=" + $("#subjectlist").val(),
            success: function (data) {
                $("#asgnmntid").html("");
                // Changed from 'form-control' to 'form-select modern-select'
                var temp = "<select class='form-select modern-select'>"; 
                for (let i = 0; i < data.length; i++) {
                    temp += "<option>" + data[i][1] + "</option>";
                }
                temp += "</select>"
                $("#asgnmntid").append(temp);
            },
            error: (jqXHR, textStatus, errorThrown) => handleAjaxError(jqXHR, textStatus, errorThrown)
        });
    });

    $("#afile").change(function () {
        if (document.getElementById("afile").files.length !== 0) {
            var mext = $("#afile").val().split('.').pop().toUpperCase();
            if (!((mext === "JPG") || (mext === "JPEG") || (mext === "PDF") || (mext === "TXT"))) {
                $("#afile").val("").focus();
                alert("Assignment should be of: .jpg, .pdf, .txt formats only.");
                return false;
            }
        }
    });

    $("#uploadformid").submit(function (e) {
        e.preventDefault();
        if (document.getElementById("afile").files.length === 0) {
            alert("Please Upload Assignment File");
            return false;
        }
        var formData = new FormData($(this)[0]);
        $.ajax({
            type: "POST",
            url: "./uploadstudentassignment.htm",
            data: formData,
            processData: false,
            contentType: false,
            success: function (data) {
                // Updated injected success button to match modern styling
                var anchorstring = `<a class="btn btn-success btn-sm btn-modern" href="viewassignmentsubmission.htm?fid=${data.toString()}" target="_blank">
                                        <i class="far fa-eye me-1"></i> View My Assignment
                                    </a>`;
                rowclicktddata.html(anchorstring);
                $("#exampleModal").modal('hide');
                Notiflix.Notify.Success('Assignment Successfully Submitted');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Notiflix.Notify.Failure('There was a failure while uploading assignment');
            }
        });
    });

    var date = new Date();
    var today = new Date(date.getFullYear(), date.getMonth(), date.getDate());

    $('#assignmentdate').datepicker({
        format: 'yyyy-mm-dd',
        orientation: 'bottom'
    });
    $('#assignmentdate').datepicker('setDate', today);

    $("#lastdate").datepicker({
        dateFormat: "dd-mm-yy",
        minDate: new Date()
    });

    // Kept your important functional classes completely intact
    $('#assignmentlist tbody').on('click', 'td button.this_is_upload_button_class', function () {
        rowclicktddata = $(this).closest('td');
    });
});

function showModal(aid) {
    $("#subname").html($("#subn").val());
    $("#assignmenttitle").html($("#testn").val());
    $("#assignmentid").val(aid.id);
    $("#exampleModal").modal('show');
}