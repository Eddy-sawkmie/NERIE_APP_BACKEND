// Initialize DataTable globally
var pTable;

// Modal Helper Function (Bootstrap 5)
function showModalAlert(message, title = 'Message', reloadOnClose = false) {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);

    const modalEl = document.getElementById('feedbackModal');
    let myModal = bootstrap.Modal.getInstance(modalEl);

    if (!myModal) {
        myModal = new bootstrap.Modal(modalEl);
    }

    if (reloadOnClose) {
        modalEl.addEventListener('hidden.bs.modal', function () {
            window.location.reload();
        }, { once: true });
    } else {
        $(modalEl).off('hidden.bs.modal');
    }

    myModal.show();
}

function myFunction(x) {
    x.classList.toggle("change");
}

document.addEventListener("DOMContentLoaded", () => {
    $('.select2-search').select2({
        placeholder: "Select Program",
        allowClear: false,
        width: '100%'
    });

    $("#backtotop").click(function () {
        $("html, body").animate({ scrollTop: 0 }, 600);
        return false;
    });

    $(".sub-menu ul").hide();
    $(".sub-sub-menu ul").hide();

    $(".sub-menu a").click(function () {
        $(this).parent(".sub-menu").children("ul").slideToggle("100");
        $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
    });

    $(".sub-sub-menu a").click(function () {
        $(this).parent(".sub-sub-menu").children("ul").slideToggle("100");
        $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
    });
});

$(document).ready(function () {
    // Hide buttons initially
    $("#submit").hide();
    $("#cancel").hide();

    if ($.fn.DataTable.isDataTable('#participantatt')) {
        $('#participantatt').DataTable().clear().destroy();
    }

    pTable = $('#participantatt').DataTable({
        ordering: false,
        pageLength: 10,
        lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "All"]],
        dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
             'rtip',
        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                title: 'Participant Attendance',
                className: 'btn btn-success btn-sm mb-3',
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                }
            }
        ]
    });

    // Input validations
    $(document).on("keypress", ".numbers", function (event) {
        if (event.which < 48 || event.which > 57) {
            event.preventDefault();
        }
    });

    $("input.alphabets").keyup(function () {
        if (this.value.match(/[^a-zA-Z. ]/g)) {
            this.value = this.value.replace(/[^a-zA-Z. ]/g, "");
        }
    });

    // Truncate long program names in dropdown
    $("#programs > option").text(function (i, text) {
        if (text.length > 100) {
            return text.substr(0, 100) + "...";
        }
    });

    // CASCADE DROPDOWNS

    // Program Change -> Load Phases
    $("#programs").change(function () {
        $("#phaseno").empty().append($("<option></option>").attr("value", "").text("Select")).val("").trigger("change");

        var programCode = $(this).val();
        if (programCode) {
            callCustomAjax(API.listPhases, "programcode=" + programCode, function (data) {
                if (data) {
                    data.forEach(function (x) {
                        $("#phaseno").append($("<option></option>").attr("value", x[0]).text(x[1]));
                    });
                }
            });
        }
    });

    // Phase Change -> Load Days
    $("#phaseno").change(function () {
        $("#programday").empty().append($("<option></option>").attr("value", "").text("Select")).val("").trigger("change");

        var phaseId = $(this).val();
        if (phaseId) {
            callCustomAjax(API.listProgramDetails, "phaseid=" + phaseId, function (data) {
                if (data) {
                    for (var i = 0; i < data.length; i++) {
                        $("#programday").append($("<option>").val(data[i][2]).text(data[i][1]));
                    }
                }
            });
        }
    });

    // Day Change -> Load Sessions (Timings)
    $("#programday").change(function () {
        $("#timings").empty().append($("<option></option>").attr("value", "").text("Select")).val("").trigger("change");

        var phaseId = $("#phaseno").val();
        var programDay = $(this).val();

        if (programDay && phaseId) {
            var datatosend = "phaseid=" + phaseId + "&programday=" + programDay;
            callCustomAjax(API.getProgramTimeTable, datatosend, function (data) {
                if (data) {
                    for (var i = 0; i < data.length; i++) {
                        $("#timings").append($("<option>").val(data[i][0]).text(data[i][1]));
                    }
                }
            });
        }
    });

    // Session Change -> Load Participants
    $("#timings").change(function () {
        if ($(this).val()) {
            getParticipantsList();
        } else {
            // Hide table and buttons if session is deselected
            if(pTable) pTable.clear().draw();
            $("#tablediv").addClass("d-none");
            $("#submit").hide();
            $("#cancel").hide();
        }
    });

    $("#cancel").click(function() {
        if(pTable) pTable.clear().draw();
        $("#tablediv").addClass("d-none");
        $("#submit").hide();
        $("#cancel").hide();
        $("#timings").val("").trigger("change");
    });

    // SUBMIT ATTENDANCE
    $("#tparticipantattendanceid").submit(function (e) {
        e.preventDefault();

        const phaseId = $("#phaseno").val();
        const timetableCode = $("#timings").val();

        // Collect all checked application codes
        const applicationCodes = [];
        $('input[name="p_applicationcode"]:checked').each(function() {
            applicationCodes.push($(this).val());
        });

        $.ajax({
            type: "POST",
            url: API.saveParticipantAttendance,
            data: {
                phaseid: phaseId,
                programtimetablecode: timetableCode,
                p_applicationcode: applicationCodes
            },
            success: function (response) {
                if (response === "2" || response === 2) {
                    showModalAlert("Attendance saved successfully!", "Message");

                    const modalEl = document.getElementById('feedbackModal');
                    modalEl.addEventListener('hidden.bs.modal', function () {
                        getParticipantsList(); // Refresh list after closing modal
                    }, { once: true });
                } else {
                    showModalAlert("Save Failed! Please try again.", "Error");
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("Error saving attendance:", textStatus, errorThrown);
                showModalAlert("An error occurred: " + (jqXHR.responseText || "Please check the console for details."), "Error");
            }
        });
    });
});

function getParticipantsList() {
    var phaseId = $("#phaseno").val();
    var timetableCode = $("#timings").val();

    if (!phaseId || !timetableCode) return;

    callCustomAjax(
        API.listSessionParticipant,
        "phaseid=" + phaseId + "&programtimetablecode=" + timetableCode,
        function (data) {
            // Clear existing DataTables rows securely without destroying the instance
            if (pTable) {
                pTable.clear().draw();
            }

            if (data && data.length > 0) {
                var count = 1;

                // Loop through and add rows via DataTables API
                data.forEach(function (x) {
                    // Check if participant was previously marked present ("P")
                    var isChecked = (x[3] === "P") ? "checked" : "";

                    // Generate Checkbox HTML safely using template literals
                    var checkbox = `<input type="checkbox" name="p_applicationcode" value="${x[2]}" ${isChecked}>`;

                    // Add row
                    pTable.row.add([
                        count++,
                        x[2],
                        x[1],
                        checkbox
                    ]);
                });

                pTable.draw();

                $("#tablediv").removeClass("d-none");
                $("#submit").show();
                $("#cancel").show();

            } else {
                $("#tablediv").addClass("d-none");
                $("#submit").hide();
                $("#cancel").hide();
            }
        }
    );
}