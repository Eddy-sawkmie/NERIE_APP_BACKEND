var pTable;

function initializeDataTable(tableId, title) {
    if (typeof $ !== 'undefined' && $.fn.DataTable) {
        return $('#' + tableId).DataTable({
            dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
                 '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
                 'rtip',
            retrieve: true,
            pageLength: 10,
            lengthMenu: [[5, 10, 20, 50, -1],[5, 10, 20, 50, "All"],
            ],
            buttons:[
              {
                extend: "excelHtml5",
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                title: title,
                exportOptions: {
                  columns: "thead th:not(.noExport)",
                },
                className: 'btn btn-success btn-sm mb-3'
              },
            ],
        });
    }
    return null;
}

document.addEventListener('DOMContentLoaded', () => pTable = initializeDataTable("programtable", "Program TT Export"));

function showModalAlert(message, title = 'Message') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);
    $('#feedbackModal .modal-footer').html(
        '<button type="button" class="btn btn-primary" data-bs-dismiss="modal">OK</button>'
    );
    var myModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('feedbackModal'));
    myModal.show();
}

function handleAjaxError(jqXHR, textStatus, errorThrown) {
    let errorMessage = `An error occurred: ${textStatus} - ${errorThrown}`;
    if (jqXHR.responseText) {
        errorMessage += `<br><br>Details: ${jqXHR.responseText}`;
    }
    showModalAlert(errorMessage, 'Request Failed');
}

document.addEventListener('DOMContentLoaded', () => {
    $('.select2-search').select2({
        placeholder: "Select Program",
        allowClear: false,
        width: '100%'
    });

    $('#tprogramttform').on('reset', function () {
        setTimeout(function() {
            $('#rpslno').val(null).trigger('change');
            // Ensure Program Select2 visually resets
            $('#programs').val("").trigger('change.select2');
        }, 0);
    });

    // Initialize Select2 on the Resource Person dropdown
    $('#rpslno').select2({
        placeholder: "Select Resource Person",
        allowClear: true,
        width: '100%'
    });

    $("#menu-toggle").click(function (e) {
        e.preventDefault();
        $("#wrapper").toggleClass("toggled");
    });

    $('#backtotop').click(function () {
        $("html, body").animate({
            scrollTop: 0
        }, 600);
        return false;
    });

    $('input[type=radio][name=breakclass]').change(function () {
        if (this.value == 'option1') {
            $('#subinput').hide();
            $('#rpinput').hide();
            $('#rpslno').removeAttr('required');
            $('#subject').removeAttr('required');
            $('#rpslno').val('').trigger('change');
            $('#subject').val("BREAK");

        } else if (this.value == 'option2') {
            $('#subinput').show();
            $('#rpinput').show();
            $('#rpslno').attr('required', 'true');
            $('#subject').attr('required', 'true');
            $('#rpslno').val('').trigger('change');
            $('#subject').val("");
        }
    });

    $('.sub-menu ul, .sub-sub-menu ul').hide();
    $(".sub-menu a").click(function () {
        $(this).parent(".sub-menu").children("ul").slideToggle("100");
        $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
    });

    $(".sub-sub-menu a").click(function () {
        $(this).parent(".sub-sub-menu").children("ul").slideToggle("100");
        $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
    });

    $("#starttime,#endtime").datetimepicker({
        format: 'LT'
    });

    $('#programs').change(function () {
        $('#phaseno').empty().append($('<option></option>').attr("value", "").text("Select"));
        if ($('#programs').val()) {
            callCustomAjax(API.listPhases, "programcode=" + $('#programs').val(), function (data) {
                if (data) {
                    data.forEach(function (x) {
                        $('#phaseno').append($('<option></option>').attr("value", x[0]).text(x[1]));
                    });
                }
            });
        }
    });

    $('#phaseno').change(function () {
        $('#venuecode, #programday').empty().append($("<option>").val("").text("Select"));

        $('#rpslno').empty().trigger('change');

        let selectedPhaseId = $('#phaseno').val();

        if (selectedPhaseId) {
            callCustomAjax(API.listVenues, "phaseid=" + selectedPhaseId, function (data) {
                if (data) {
                    data.forEach(item => $('#venuecode').append($("<option>").val(item[0]).text(item[1])));
                }
            });

            callCustomAjax(API.listProgramDetails, "phaseid=" + selectedPhaseId, function (data) {
                if (data) {
                    data.forEach(item => $('#programday').append($("<option>").val(item[2]).text(item[1])));
                }
            });

            callCustomAjax(API.listCoursePhases, "phaseid=" + selectedPhaseId, function (rpData) {
                $('#rpslno').empty();
                if (rpData) {
                    rpData.forEach(item => {
                         $('#rpslno').append(new Option(item[1], item[0], false, false));
                    });
                }
                $('#rpslno').trigger('change');
            });
        }
    });

    $("#venuecode").change(function () {
        $('#roomcode').empty().append($("<option>").val("").text("Select"));
        callCustomAjax(API.listVenueRooms, "venuecode=" + $("#venuecode").val(), function (data) {
            if (data) {
                data.forEach(item => $('#roomcode').append($("<option>").val(item[0]).text(item[1])));
            }
        });
    });

    $("#programday").change(function () {
        if ($("#phaseno").val() && $("#programday").val()) {
            loadTimeTable();
        }
    });

    $('#tprogramttform').submit(function (e) {
        e.preventDefault();

        const originalStartTime = $("#starttime").val();
        const originalEndTime = $("#endtime").val();

        let stime = originalStartTime.trim();
        let etime = originalEndTime.trim();

        let shh = stime.split(":")[0];
        let ehh = etime.split(":")[0];
        let smm = (stime.split(":")[1]).split(" ")[0];
        let emm = (etime.split(":")[1]).split(" ")[0];
        let sam = stime.split(" ")[1];
        let eam = etime.split(" ")[1];

        if (sam == "PM") {
            if (Number(shh) !== 12) stime = (Number(shh) + 12) + ":" + smm + ":00";
            else stime = "12:" + smm + ":00";
        } else {
            if (Number(shh) === 12) stime = "00:" + smm + ":00";
            else stime = shh + ":" + smm + ":00";
        }

        if (eam === "PM") {
            if (Number(ehh) !== 12) etime = (Number(ehh) + 12) + ":" + emm + ":00";
            else etime = "12:" + emm + ":00";
        } else {
            if (Number(ehh) === 12) etime = "00:" + emm + ":00";
            else etime = ehh + ":" + emm + ":00";
        }

        let cdate = standardToRawdate($("#programday :selected").text().split("-")[0].trim());

        $("#starttime").val(stime);
        $("#endtime").val(etime);
        $("#programdate").val(cdate);

        callCustomAjax(API.saveProgramTimeTable, $('#tprogramttform').serialize(), function (data) {
            $("#starttime").val(originalStartTime);
            $("#endtime").val(originalEndTime);

            const response = data.trim();
            switch (response) {
                case "2":
                    showModalAlert("Successfully Added!!!", "Success");
                    // Vanilla JS Listener
                    document.getElementById('feedbackModal').addEventListener('hidden.bs.modal', function() {
                        $('#rpslno').trigger('change');
                        $('input[name=breakclass][value=option2]').prop('checked', true).trigger('change');
                        loadTimeTable();
                        $('#tprogramttform')[0].reset();
                    }, { once: true });
                    break;
                case "1":
                    showModalAlert("Time Duration cannot be empty.", "Validation Error");
                    break;
                case "3":
                    showModalAlert("Please check the time duration format.", "Validation Error");
                    break;
                case "4":
                    showModalAlert("Subject cannot be empty.", "Validation Error");
                    break;
                case "5":
                    showModalAlert("Subject should be 1-70 characters long.", "Validation Error");
                    break;
                case "6":
                    showModalAlert("There is a clash in the Time Table for the selected slot.", "Scheduling Conflict");
                    break;
                case "7":
                    showModalAlert("The selected Resource Person is engaged with another session.", "Scheduling Conflict");
                    break;
                case "8":
                    showModalAlert("The selected Room/Hall is not available at this time.", "Scheduling Conflict");
                    break;
                default:
                    showModalAlert("Save Failed! Please try again.", "Error");
                    break;
            }
        });
    });
});

function myFunction(x) {
    x.classList.toggle("change");
}

function loadTimeTable() {
    callCustomAjax(API.listProgramTimeTable, "phaseid=" + $("#phaseno").val() + "&programday=" + $("#programday").val(), function (data) {
        if (pTable) pTable.clear().destroy();
        $('#programtable tbody').empty();

        if (data && data.length !== 0) {
            data.forEach(function (x) {
                var rowData = `<tr>
                        <td class="w-5" style="font-size: smaller">${x[13]}<br/>(${x[16]})</td>
                        <td class="w-5" style="font-size: smaller">${x[6]}</td>
                        <td class="w-5" style="font-size: smaller">${x[7]}</td>
                        <td class="w-10" style="font-size: smaller">${x[8]}</td>
                        <td class="w-5" style="font-size: smaller">${x[10]}</td>
                        <td class="w-10" style="font-size: smaller">
                        <a href="#" class="btn btn-sm btn-primary edit-program-btn"
                           data-sttcode="${x[3]}"
                           data-phaseid="${x[0]}"
                           data-programcode="${x[17]}"
                           data-venuecode="${x[14]}"
                           data-roomcode="${x[15]}"
                           data-courseday="${x[5]}"
                           data-starttime="${x[6]}"
                           data-endtime="${x[7]}"
                           data-subject="${x[8]}"
                           data-rpslno="${x[9]}">
                           <i class="fa fa-edit">  Edit</i>
                        </a>
                        </td>
                        </tr>`;
                $('#programtable tbody').append(rowData);
            });

            document.querySelectorAll(".edit-program-btn").forEach(button => {
                button.addEventListener("click", function (event) {
                    event.preventDefault();
                    const ds = this.dataset;
                    editfunc(ds.sttcode, ds.phaseid, ds.venuecode, ds.roomcode, ds.courseday, ds.starttime, ds.endtime, ds.subject, ds.rpslno, ds.programcode);
                });
            });

            $('#tablediv').show();
            $('#errordiv').hide();
        } else {
            $('#tablediv').hide();
            $('#errordiv').show();
            $('#errorspan').html("Schedule not defined for the selected day.");
        }
        pTable = initializeDataTable("programtable", "Program TT Export");
    });
}

function editfunc(sttcode, phaseid, venuecode, roomcode, courseday, starttime, endtime, subject, rpslno, programcode) {
    $('html, body').animate({ scrollTop: 0 }, 'fast');

    $("#programtimetablecode").val(sttcode);

    // Visually update the Select2 component without double-triggering the AJAX call
    $("#programs").val(programcode).trigger('change.select2');

    $("#starttime").val(starttime);
    $("#endtime").val(endtime);

    // Handle Radio Buttons & Visibility
    if (subject.toUpperCase() === 'BREAK') {
        document.getElementById('inlineRadio1').checked = true;
        $('input[name=breakclass][value=option1]').trigger('change');
    } else {
        document.getElementById('inlineRadio2').checked = true;
        $('input[name=breakclass][value=option2]').trigger('change');
    }

    // Set the subject
    $("#subject").val(subject);

    // Ajax calls for Dropdowns (Phase, Day, Venue, Resource Persons)
    callCustomAjax(API.listPhases, "programcode=" + programcode, function (data) {

        // Populate Phase Dropdown
        $('#phaseno').empty().append($('<option></option>').attr("value", "").text("Select"));
        if (data) {
            data.forEach(function (x) {
                $('#phaseno').append($('<option></option>').attr("value", x[0]).text(x[1]));
            });
        }

        // Set Phase ID
        $("#phaseno").val(phaseid);

        // Load Days
        callCustomAjax(API.listProgramDetails, "phaseid=" + phaseid, function (dayData) {
            $('#programday').empty().append($("<option>").val("").text("Select"));
            if (dayData) {
                dayData.forEach(item => $('#programday').append($("<option>").val(item[2]).text(item[1])));
            }
            $("#programday").val(courseday);
        });

        callCustomAjax(API.listCoursePhases, "phaseid=" + phaseid, function (rpData) {
            $('#rpslno').empty();
            if (rpData) {
                rpData.forEach(item => {
                    $('#rpslno').append(new Option(item[1], item[0], false, false));
                });

                var rpslnos = rpslno ? String(rpslno).split(',') :[];
                $("#rpslno").val(rpslnos);
            }
            $('#rpslno').trigger('change');
        });

        // Load Venues
        callCustomAjax(API.listVenues, "phaseid=" + phaseid, function (venueData) {
            $('#venuecode').empty().append($("<option>").val("").text("Select"));
            if (venueData) {
                venueData.forEach(item => $('#venuecode').append($("<option>").val(item[0]).text(item[1])));
            }
            $("#venuecode").val(venuecode);

            // Load Rooms
            callCustomAjax(API.listVenueRooms, "venuecode=" + venuecode, function (roomData) {
                $('#roomcode').empty().append($("<option>").val("").text("Select"));
                if (roomData) {
                    roomData.forEach(item => $('#roomcode').append($("<option>").val(item[0]).text(item[1])));
                }
                $("#roomcode").val(roomcode);
            });
        });
    });
}