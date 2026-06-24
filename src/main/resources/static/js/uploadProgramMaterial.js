// Modal Helper Functions
function showModalAlert(message, title = 'Message') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);
    $('#feedbackModal .modal-footer').html(
        '<button type="button" class="btn btn-primary" data-bs-dismiss="modal">OK</button>'
    );
    var myModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('feedbackModal'));
    myModal.show();
}

function showModalConfirm(message, title, callback) {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);

    const footer = `
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
        <button type="button" id="modalConfirmOkButton" class="btn btn-danger">OK</button>
    `;
    $('#feedbackModal .modal-footer').html(footer);

    var myModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('feedbackModal'));
    myModal.show();

    $('#modalConfirmOkButton').off('click').on('click', function() {
        myModal.hide();
        if (typeof callback === 'function') {
            callback();
        }
    });
}

function handleAjaxError(jqXHR, textStatus, errorThrown) {
    let errorMessage = `An error occurred: ${textStatus} - ${errorThrown}`;
    if (jqXHR.responseText) {
        errorMessage += `<br><br>Details: ${jqXHR.responseText}`;
    }
    showModalAlert(errorMessage, 'Request Failed');
}

function initializeDataTable() {
    if (typeof $ !== 'undefined' && $.fn.DataTable) {
        return $('#programmaterialtable').DataTable({
            ordering: false,
            pageLength: 10,
            lengthMenu: [[10, 20, 50, -1],[10, 20, 50, "All"]],
            dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
                 '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
                 'rtip',
            buttons:[
                {
                    extend: 'excelHtml5',
                    text: '<i class="fa fa-file-excel-o"></i> Excel',
                    title: 'Program Materials List',
                    className: 'btn btn-success btn-sm mb-3',
                    exportOptions: {
                        columns: ':visible:not(.noExport)'
                    }
                }
            ]
        });
    }
    return null;
}

// Main Logic
var mutable;
var programcode;

document.addEventListener('DOMContentLoaded', () => {
    $(".select2-search").select2({
        placeholder: "Select",
        allowClear: false,
        width: '100%'
    });

    mutable = initializeDataTable();

    $("#menu-toggle").click(function(e) {
        e.preventDefault();
        $("#wrapper").toggleClass("toggled");
    });

    $('#backtotop').click(function() {
        $("html, body").animate({ scrollTop: 0 }, 600);
        return false;
    });

    $('.sub-menu ul').hide();
    $('.sub-sub-menu ul').hide();
    $(".sub-menu a").click(function() {
        $(this).parent(".sub-menu").children("ul").slideToggle("100");
        $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
    });
    $(".sub-sub-menu a").click(function() {
        $(this).parent(".sub-sub-menu").children("ul").slideToggle("100");
        $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
    });

    document.querySelectorAll('.resetformbtn').forEach(button => {
        button.addEventListener('click', resetform);
    });

    const addBtn = document.getElementById('addft');
    if (addBtn) {
        addBtn.addEventListener('click', setcoursecodefunc);
    }

    $("#financialyear").change(function() {
        var fy = $("#financialyear").val();

        $('#programs, #phaseno').empty().append($('<option></option>').attr("value", "").text("Select")).trigger('change');

        if (fy) {
            var fystart = fy.split("##")[0];
            var fyend = fy.split("##")[1];
            callCustomAjax(API.listFinancialYear, "fystart=" + fystart + "&fyend=" + fyend, function(data) {
                if (data) {
                    data.forEach(x => {
                        $('#programs').append($('<option></option>').attr("value", x[0]).text(x[1]).attr('title', x[1]));
                    });
                    $('#programs > option').text((i, text) => (text.length > 100) ? text.substr(0, 100) + '...' : text);
                    $('#programs').trigger('change');
                }
            });
        }
    });

    $('#programs').change(function() {
        $('#phaseno').empty().append($('<option></option>').attr("value", "").text("Select")).trigger('change');

        if ($('#programs').val()) {
            callCustomAjax(API.listPhases, "programcode=" + $('#programs').val(), function(data) {
                if (data) {
                    data.forEach(x => $('#phaseno').append($('<option></option>').attr("value", x[0]).text(x[1])));
                    $('#phaseno').trigger('change');
                }
            });
        }
    });

    $('#phaseno').change(function() {
        if ($('#phaseno').val()) {
            $('#phaseid').val($('#phaseno').val());
            getmaterialuploaddatafunc();
        } else {
             $("#uploadbtn").hide();
        }
    });

    $("#tprogrammaterialform").submit(function(e) {
        e.preventDefault();
        if (!checkmaterialdoctype()) return;

        var formData = new FormData($(this)[0]);
        $.ajax({
            type: "POST",
            url: API.saveProgramMaterial,
            data: formData,
            processData: false,
            contentType: false,
            success: function(data) {
                bootstrap.Modal.getOrCreateInstance(document.getElementById('modalsubj-modal')).hide();
                if (data == "2") {
                    showModalAlert("Successfully Saved!!!", "Success");
                    document.getElementById('feedbackModal').addEventListener('hidden.bs.modal', function() {
                        getmaterialuploaddatafunc();
                        resetform();
                    }, { once: true });
                } else if (data == "1") {
                    showModalAlert("Please Upload Program Material.", "Warning");
                } else {
                    showModalAlert("Save Failed!!!", "Error");
                }
            },
            error: handleAjaxError
        });
    });
});

function resetform() {
    $("#tprogrammaterialform")[0].reset();
    $("#reportormaterial").val("").trigger("change");
}

function checkmaterialdoctype() {
    if (document.getElementById("file1").files.length !== 0) {
        var mext = $("#file1").val().split('.').pop().toLowerCase();
        if (mext !== "jpg" && mext !== "jpeg" && mext !== "pdf") {
            showModalAlert("File must be of type PDF, JPG, or JPEG.", "Invalid File Type");
            $("#file1").val("");
            return false;
        }
    }
    return true;
}

function getmaterialuploaddatafunc() {
    callCustomAjax(API.listProgramMaterial, "phaseid=" + $('#phaseno').val(), function(data) {

        if(mutable) {
            mutable.clear().destroy();
        }

        $('#programmaterialtable tbody').empty();

        if (data && data.length > 0) {
            let count = 1;
            data.forEach(x => {
                var udate = getdateformate(x[2]);
                var rd =
                        "<tr>" +
                            "<td>" + (count++) + "</td>" +
                            "<td>" + x[1] + "</td>" +
                            "<td style='white-space: nowrap'>" + udate + "</td>" +
                            "<td><a href='" + API.viewProgramMaterial + "?programmaterialid=" + x[0] + "' target='_blank' class='btn btn-outline-primary btn-sm'>View File</a></td>" +
                            "<td>" +
                                "<a href='#' class='btn btn-danger btn-sm clickme enablebtn delete-material-btn pt-2 pb-2' data-programmaterialid='" + x[0] + "'>" +
                                    "<i class='fa fa-trash'>&nbsp;Delete</i>" +
                                "</a>" +
                            "</td>" +
                        "</tr>";

                    $('#programmaterialtable tbody').append(rd);
            });
            $('#tablediv').show();
            $("#uploadbtn").show();

            var deleteButtons = document.querySelectorAll('.delete-material-btn');
            deleteButtons.forEach(function(button) {
                button.addEventListener('click', function(event) {
                    event.preventDefault();
                    var programMaterialId = this.getAttribute('data-programmaterialid');
                    deletematerialfunc(programMaterialId);
                });
            });

        } else {
            $('#tablediv').hide();
            $("#uploadbtn").show();
        }
        mutable = initializeDataTable();
    });
}

function deletematerialfunc(cmid) {
    showModalConfirm(
        "Are you sure you want to delete this program material?",
        "Confirm Delete",
        function() {
            $.ajax({
                type: "POST",
                url: API.deleteProgramMaterial,
                data: { programmaterialid: cmid },
                success: function (data) {
                    if (data == "1") {
                        showModalAlert("Program Material deleted successfully", "Success");
                        document.getElementById('feedbackModal').addEventListener('hidden.bs.modal', function() {
                            getmaterialuploaddatafunc();
                        }, { once: true });
                    } else {
                        showModalAlert("Error Occured! Please try again", "Error");
                    }
                },
                error: handleAjaxError
           });
        }
    );
}

function setcoursecodefunc() {
    $("#programcode").val($("#programs").val());
}

function getdateformate(d) {
    if (!d) return '';
    var dateObj = new Date(d);
    var day = String(dateObj.getDate()).padStart(2, '0');
    var month = String(dateObj.getMonth() + 1).padStart(2, '0');
    var year = dateObj.getFullYear();
    return `${day}-${month}-${year}`;
}

function myFunction(x) {
    x.classList.toggle("change");
}