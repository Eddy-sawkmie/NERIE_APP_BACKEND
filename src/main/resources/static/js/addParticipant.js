var ptable;
var programcode;
var ifemailexist = "N";

function showModalAlert(message, title = "Message", reloadOnClose = false) {
    $("#feedbackModalLabel").text(title);
    $("#feedbackModalBody").html(message);

    const modalEl = document.getElementById('feedbackModal');
    let myModal = bootstrap.Modal.getInstance(modalEl);
    if (!myModal) {
        myModal = new bootstrap.Modal(modalEl);
    }

    if (reloadOnClose) {
        modalEl.addEventListener('hidden.bs.modal', function () {
            window.location.reload();
        }, { once: true });
    }

    myModal.show();
}

function showConfirmationModal(message, title = "Confirm Action", onConfirm) {
    $("#confirmationModalLabel").text(title);
    $("#confirmationModalBody").html(message);

    $("#confirmActionButton").off('click').on('click', function() {
        const modalEl = document.getElementById('confirmationModal');
        const myModal = bootstrap.Modal.getInstance(modalEl);
        if (myModal) myModal.hide();

        if (typeof onConfirm === 'function') {
            onConfirm();
        }
    });

    const modalEl = document.getElementById('confirmationModal');
    let myModal = bootstrap.Modal.getInstance(modalEl);
    if (!myModal) {
        myModal = new bootstrap.Modal(modalEl);
    }

    myModal.show();
}

// DataTable Initialization Helper
function initParticipantTable() {
    if ($.fn.DataTable.isDataTable('#addparticipanttable')) {
        $('#addparticipanttable').DataTable().clear().destroy();
    }

    return $('#addparticipanttable').DataTable({
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
                title: 'Participant List',
                className: 'btn btn-success btn-sm mb-3',
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                }
            }
        ]
    });
}

document.addEventListener("DOMContentLoaded", () => {
    $("#menu-toggle").click(function (e) {
        e.preventDefault();
        $("#wrapper").toggleClass("toggled");
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

    // Button initializations
    var addButton = document.getElementById("addft");
    if (addButton) {
        addButton.addEventListener("click", function () {
            addparticipantfunc();
        });
    }

    var resetButtons = document.querySelectorAll(".resetformbtn");
    resetButtons.forEach(function (button) {
        button.addEventListener("click", function () {
            resetform();
        });
    });

    // Init empty datatable on load
    ptable = initParticipantTable();
    $("#uploadbtn").hide();

    // Dropdown Logic
    $("#financialyear").change(function () {
        var fy = $("#financialyear").val();
        $("#programs,#phaseno").empty().append($("<option></option>").attr("value", "").text("Select"));

        if (fy) {
            var fystart = fy.split("##")[0];
            var fyend = fy.split("##")[1];

            callCustomAjax(
                API.listFinancialYear,
                "fystart=" + fystart + "&fyend=" + fyend,
                function (data) {
                    if (data) {
                        data.forEach(function (x) {
                            $("#programs").append(
                                $("<option></option>").attr("value", x[0]).text(x[1]).attr("title", x[1])
                            );
                        });
                        $("#programs > option").text(function (i, text) {
                            return text.length > 100 ? text.substr(0, 100) + "..." : text;
                        });
                    }
                }
            );
        }
    });

    $("#programs").change(function () {
        $("#phaseno").empty().append($("<option></option>").attr("value", "").text("Select"));
        if ($("#programs").val()) {
            callCustomAjax(
                API.listPhases,
                "programcode=" + $("#programs").val(),
                function (data) {
                    if (data) {
                        data.forEach(function (x) {
                            $("#phaseno").append($("<option></option>").attr("value", x[0]).text(x[1]));
                        });
                    }
                }
            );
        }
    });

    $("#phaseno").change(function () {
        if ($("#phaseno").val()) {
            $("#phaseid").val($("#phaseno").val());
            getparticipantsfunc();
        }
    });
});

$(document).ready(function () {
    $(document).on("keypress", ".numbers", function (event) {
        if (event.which < 48 || event.which > 57) event.preventDefault();
    });

    $("input.alphabets").keyup(function () {
        if (this.value.match(/[^a-zA-Z. ]/g)) {
            this.value = this.value.replace(/[^a-zA-Z. ]/g, "");
        }
    });

    $("#usermobile").focusout(function () {
        var m = $("#usermobile").val();
        if (m.length > 0 && m.length < 10) {
            $("#msg1").html("Mobile no.should be 10 digit");
            return false;
        } else {
            $("#msg1").html("");
        }
    });

    $("#usermobile").keypress(function () {
        var m = $("#usermobile").val();
        if (m.length == 9) $("#msg1").html("");
    });

    // FIXED: Form populate before Alert + Safe "change" event
    $("#emailid").on("change", function (e) {
        var emailInput = $(this).val().trim();
        var re = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$/;

        if (emailInput.length !== 0) {
            if (re.test(emailInput) == false) {
                $(this).val("");
                showModalAlert("Please enter valid Email ID", "Invalid Email");
            } else {
                $.ajax({
                    type: "POST",
                    url: API.getParticipant,
                    data: "userid=" + emailInput,
                    success: function (data) {
                        if (data.length > 0) {
                            $("#username").prop("readonly", true);
                            $("#usermobile").prop("readonly", true);
                            $("#usercode").val(data[0][0]);
                            $("#username").val(data[0][1]);
                            $("#usermobile").val(data[0][3]);
                            $("#statecode").val(data[0][4]);
                            ifemailexist = "Y";
                            showModalAlert("Email ID already registered. Kindly add participant to the Program", "Email Already Exists");
                        } else {
                            $("#username").prop("readonly", false);
                            $("#usermobile").prop("readonly", false);
                            $("#usercode").val("");
                            $("#username").val("");
                            $("#usermobile").val("");
                            $("#statecode").val("");
                            ifemailexist = "N";
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        showModalAlert("error:" + textStatus + " - exception:" + errorThrown, "Error");
                    },
                });
            }
        }
    });
});

document.addEventListener("DOMContentLoaded", () => {
    $('.select2-search').select2({
        placeholder: "Select Program",
        allowClear: false,
        width: '100%'
    });

    $("#mtuserloginfid").submit(function (e) {
        e.preventDefault();
        $.ajax({
            type: "POST",
            url: API.createParticipant,
            data: $("#mtuserloginfid").serialize(),
            success: function (data) {
                const subjModalEl = document.getElementById('modalsubj-modal');
                const subjModal = bootstrap.Modal.getInstance(subjModalEl);

                if (data[0] === "2") {
                    let message = "Successfully Added!!!";
                    if (data[1]) message += "<br>" + data[1];
                    if (subjModal) subjModal.hide();
                    subjModalEl.addEventListener('hidden.bs.modal', function () {
                        showModalAlert(message, "Success", true);
                    }, { once: true });
                } else if (data[0] == "1") {
                    if (subjModal) subjModal.hide();
                    subjModalEl.addEventListener('hidden.bs.modal', function () {
                        showModalAlert("Successfully Added", "Success", true);
                    }, { once: true });
                } else {
                    showModalAlert("Failed to add participant. Please try again.", "Error");
                }
            },
            error: (jqXHR, textStatus, errorThrown) => handleAjaxError(jqXHR, textStatus, errorThrown)
        });
    });
});

function getparticipantsfunc() {
    callCustomAjax(
        API.listParticipant,
        "phaseid=" + $("#phaseno").val(),
        function (data) {
            if (ptable) ptable.clear().destroy();
            $("#addparticipanttable tbody").empty();

            if (data) {
                let count = 1;
                data.forEach(function (x) {
                    var rd =
                        "<tr>" +
                        "<td>" + count++ + "</td>" +
                        "<td><a class='getappdetails-btn' target='_blank' href='" + API.getParticipantDetails + "?fid=" + x[4] + "'>" + x[4] + "</a></td>" +
                        "<td>" + x[2] + "</td>" +
                        "<td>" + x[1] + "</td>" +
                        "<td>" + x[3] + "</td>" +
                        "<td>" + x[7] + "</td>" +
                        "<td>";

                    if (x[5] === "P") rd += "Pending With Participant";
                    else if (x[5] === "A") rd += "Accepted";
                    else if (x[5] === "R") rd += "Rejected";
                    else if (x[5] === 'T') rd += 'Participant Applied';

                    rd += "</td><td class='noExport'>";

                    if(x[5]=='A'){
                        rd += '<a class="btn-sm clickme enablebtn deletebtn pt-2 pb-2" data-x4="' + x[4] + '" data-x1="' + x[1] + '"><span style="color: #fff"><i class="fa fa-trash"> &nbsp;Delete</span></a>';
                    }
                    if (x[5] === "P") {
                        rd +=
                            '<a class="clickme danger11 editbtn btn btn-sm btn-primary text-white" data-bs-toggle="modal" data-bs-target="#modalsubj-modal" data-bs-dismiss="modal" ' +
                            'data-x0="' + x[0] + '" data-x1="' + x[1] + '" data-x2="' + x[2] + '" data-x3="' + x[3] + '" ' +
                            'data-x6="' + x[6] + '" data-x8="' + x[8] + '"><i class="fa fa-edit"> &nbsp;Edit</i></a><br><br>' +
                            '<a class="btn-sm clickme enablebtn deletebtn pt-2 pb-2" data-x4="' + x[4] + '" data-x1="' + x[1] + '"><span style="color: #fff"><i class="fa fa-trash"> &nbsp;Delete</span></a>';
                    } else if (x[5] === 'T') {
                        rd += '<a class="accept-btn clickme enablebtn btn btn-sm btn-success text-white" data-x4="' + x[4] + '" data-x1="' + x[1] + '"><i class="fa fa-check"> &nbsp;Accept</i></a><br><br>' +
                              '<a class="reject-btn clickme enablebtn btn btn-sm btn-danger text-white" data-x4="' + x[4] + '" data-x1="' + x[1] + '"><i class="fa fa-times"> &nbsp;Reject</i></a>';
                    }

                    rd += "</td></tr>";
                    $("#addparticipanttable tbody").append(rd);
                });

                $("#tablediv").removeClass("d-none");
                $("#uploadbtn").show();
            } else {
                $("#tablediv").addClass("d-none");
                showModalAlert("No data found", "Information");
            }

            if (Array.isArray(data) && data.length > 0) {
                let compiledbtn = "<a target='_blank' href='"+API.participantListDetails+"?fid="+ $('#phaseno').val()+"' ><button class='btn btn-success'>Get Compiled Participant List</button></a>";
                $('#compiled-div').html(compiledbtn);
            }

            // Re-initialize table
            ptable = initParticipantTable();

            // Re-bind click events
            $(document).off('click', '.accept-btn').on('click', '.accept-btn', function () {
                acceptrejectparticipantfunc($(this).data('x4'), $(this).data('x1'), "accept");
            });

            $(document).off('click', '.reject-btn').on('click', '.reject-btn', function () {
                acceptrejectparticipantfunc($(this).data('x4'), $(this).data('x1'), "reject");
            });

            $(document).off('click', '.editbtn').on('click', '.editbtn', function () {
                editfunc($(this).data('x0'), $(this).data('x1'), $(this).data('x2'), $(this).data('x3'), $(this).data('x6'), $(this).data('x8'));
            });

            $(document).off('click', '.deletebtn').on('click', '.deletebtn', function () {
                removeparticipantfunc($(this).data('x4'), $(this).data('x1'));
            });
        }
    );
}

function resetform() {
    $("#username").prop("readonly", false);
    $("#usermobile").prop("readonly", false);
    $("#statecode").prop("disabled", false);
    $("#emailid").val("");
    $("#usercode").val("");
    $("#username").val("");
    $("#usermobile").val("");
    $("#statecode").val("");
}

function addparticipantfunc() {
    $("#phaseid").val($("#phaseno").val());
}

function editfunc(ucode, pname, email, mno, scode, phid) {
    $("#emailid").val(email);
    $("#usercode").val(ucode);
    $("#username").val(pname);
    $("#usermobile").val(mno);
    $("#statecode").val(scode);
    $("#phaseid").val(phid);
}

function removeparticipantfunc(acode, pname) {
    showConfirmationModal("Are you sure you want to remove <strong>" + pname + "</strong> from the Program application list?", "Confirm Removal", function() {
        $.ajax({
            type: "POST",
            url: API.removeParticipant,
            data: "applicationcode=" + acode,
            success: function (data) {
                if (data == "1") {
                    showModalAlert("Participant removed successfully", "Success");
                    getparticipantsfunc();
                } else {
                    showModalAlert("Error Occurred!!! Please try again", "Error", true);
                }
            },
            error: (jqXHR, textStatus, errorThrown) => handleAjaxError(jqXHR, textStatus, errorThrown)
        });
    });
}

function acceptrejectparticipantfunc(acode, pname, action) {
    const actionText = action.charAt(0).toUpperCase() + action.slice(1);
    showConfirmationModal("Are you sure you want to <strong>" + action + "</strong> <strong>" + pname + "'s</strong> application for the program?", "Confirm " + actionText, function() {
        $.ajax({
            method: "POST",
            url: API.acceptOrRejectCourseParticipant,
            data: { applicationcode: acode, action: action },
            success: function (data) {
                if (data == "1") {
                    showModalAlert("Participant " + action + "ed successfully", "Success");
                    getparticipantsfunc();
                } else {
                    showModalAlert("Error Occurred!!! Please try again", "Error");
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                showModalAlert("error:" + textStatus + " - exception:" + errorThrown, "Error");
            }
        });
    });
}