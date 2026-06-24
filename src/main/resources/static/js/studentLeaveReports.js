var leaveTable;

function myFunction(x) {
    x.classList.toggle("change");
}


function initializeLeaveTable() {

    return $('#leavelist').DataTable({
        dom: 'Blfrtip',
        pageLength: 5,
        lengthMenu: [[5, 10, 20, 50, -1], [5, 10, 20, 50, "All"]],
        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                title: 'Student Leave Report',
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                },
                className: 'btn btn-sm btn-outline-success'
            }
        ]
    });
}

$(document).ready(function () {
    leaveTable = initializeLeaveTable();

    $('#backtotop').click(function () {
        $("html, body").animate({scrollTop: 0}, 600);
        return false;
    });

    /*
    $("#finyear").change(function () {
        var fy = $("#finyear").val();
        $('#course').empty();
        $('#course').append($('<option></option>').attr("value", "").text("Select"));
        $('#course').append($('<option></option>').attr("value", "ALL").text("All"));
        $('#semester-phase').empty();
        $('#semester-phase').append($('<option></option>').attr("value", "").text("Select"));

        if (fy && fy !== "ALL") {
            var fystart = fy.split("##")[0];
            var fyend = fy.split("##")[1];
            var ajaxUrl = '/nerie/student-leaves/getFYCourseList';

            $.ajax({
                url: ajaxUrl,
                type: 'POST',
                data: "fystart=" + fystart + "&fyend=" + fyend,
                success: function (data) {
                    if (data) {
                        data.forEach(function (x) {
                            if (x.length >= 3) {
                                $('#course').append($('<option></option>').attr("value", x[0]).text(x[1]).attr("data-isshortterm", x[2]).attr("title", x[1]));
                            } else {
                                $('#course').append($('<option></option>').attr("value", x[0]).text(x[1]).attr("title", x[1]));
                            }
                        });
                        $('#course > option').text(function (i, text) {
                            if (text.length > 100) {
                                return text.substr(0, 100) + '...';
                            }
                        });
                    }
                },
                error: function() {
                    Notiflix.Notify.Failure('Failed to load courses for the selected financial year.');
                }
            });
        }
    });
    */
    $('#course').change(function () {
        $('#semester-phase').empty();
        $('#semester-phase').append($('<option></option>').attr("value", "").text("Select"));
        $('#semester-phase').append($('<option></option>').attr("value", "ALL").text("All"));

        const selectedOption = $(this).find(':selected');
        const courseCode = selectedOption.val();
        const isshortterm = selectedOption.data('isshortterm');

        if (courseCode && courseCode !== "ALL") {
            let ajaxUrl;
            let params = { courseCode: courseCode };

            if (isshortterm == '1' || isshortterm === 1) {
                ajaxUrl = API.getPhasesBasedOnCourse;
            } else if (isshortterm == '0' || isshortterm === 0) {
                ajaxUrl = API.getSemestersBasedOnCourse;
            } else {
                 return;
            }

            $.ajax({
                url: ajaxUrl,
                type: "POST",
                data: params,
                success: function (data) {
                    if (data && data.length > 0) {
                        data.forEach(function (item) {
                            let optionElement;
                            if (isshortterm == '0' || isshortterm === 0) {
                                optionElement = $('<option></option>').attr('value', item.semestercode).text(item.semestername);
                            } else if (isshortterm == '1' || isshortterm === 1) {
                                optionElement = $('<option></option>').attr('value', item.sphaseid).text(item.sphasename);
                            }
                            if (optionElement) {
                                $('#semester-phase').append(optionElement);
                            }
                        });
                    } else {
                        Notiflix.Notify.Info('No Semesters/Phases available for the selected course.');
                    }
                },
                error: function (xhr, status, error) {
                    Notiflix.Notify.Failure('Failed to fetch Semesters/Phases. Please try again. Error: ' + error);
                }
            });
        }
    });

    $("#course").click(function () {
        if ($("#finyear").val() === "") {
            Notiflix.Report.warning('Validation Error', 'Please select Financial Year First', 'Okay');
            $("#finyear").focus();
            return false;
        }
    });

    document.querySelectorAll(".btn.btn-info.submitbtn").forEach(function(button) {
        button.addEventListener("click", function(event) {
            event.preventDefault();
            var functionName = button.getAttribute("data-function");
            if (typeof window[functionName] === "function") {
                window[functionName]();
            } else {
                Notiflix.Notify.Failure(`Error: function ${functionName} not found.`);
            }
        });
    });
});

// Function to get list report
function getlistreportfunc() {
    const finYearVal = $("#finyear").val();
    const courseSelectedOption = $('#course').find(':selected');
    const courseCode = courseSelectedOption.val();
    const isshortterm = courseSelectedOption.data('isshortterm');
    const semesterPhaseValue = $('#semester-phase').val();
    const approvedStatus = $('#approvedstatus').val();

    if (!finYearVal) { Notiflix.Notify.Warning('Please select a Financial Year.'); return; }
    if (!courseCode) { Notiflix.Notify.Warning('Please select a Course.'); return; }
    if (!semesterPhaseValue) { Notiflix.Notify.Warning('Please select a Semester/Phase.'); return; }
    if (!approvedStatus) { Notiflix.Notify.Warning('Please select a Status.'); return; }

    let fystart = "all", fyend = "all";
    if (finYearVal !== 'ALL') {
        fystart = finYearVal.split("##")[0];
        fyend = finYearVal.split("##")[1];
    }

    $('#tableTitleType').text(approvedStatus === 'ALL' ? 'All' : approvedStatus.charAt(0).toUpperCase() + approvedStatus.slice(1).toLowerCase());

    let reportUrl = API.reportStudentLeaveList;
    let queryParams = [];

    if (courseCode === 'ALL') {
        queryParams.push("status=all");
    } else {
        if (isshortterm == '1' || isshortterm === 1) {
            queryParams.push("status=shortterm");
            queryParams.push("sphaseid=" + encodeURIComponent(semesterPhaseValue));
        } else if (isshortterm == '0' || isshortterm === 0) {
            queryParams.push("status=longterm");
            queryParams.push("semester=" + encodeURIComponent(semesterPhaseValue));
        }
    }

    queryParams.push("course=" + encodeURIComponent(courseCode));
    queryParams.push("fystart=" + encodeURIComponent(fystart));
    queryParams.push("fyend=" + encodeURIComponent(fyend));
    queryParams.push("approvedstatus=" + encodeURIComponent(approvedStatus));
    reportUrl += "?" + queryParams.join("&");

    Notiflix.Loading.Standard('Fetching leave applications...');
    $.ajax({
        url: reportUrl,
        type: "GET",
        success: function (data) {
            Notiflix.Loading.Remove();
            const validData = (data && Array.isArray(data)) ? data : [];
            updateLeaveTable(validData);
            if (validData.length === 0) {
                 Notiflix.Notify.Info("No data found for the selected criteria.");
            }
        },
        error: function (xhr, status, error) {
            Notiflix.Loading.Remove();
            updateLeaveTable([]); // Clear the table on error
            Notiflix.Notify.Failure("Failed to retrieve leave data. Please try again. Error: " + error);
        }
    });
}

// Function to update the leave table with new data
function updateLeaveTable(data) {
    if ($.fn.DataTable.isDataTable('#leavelist')) {
        leaveTable.destroy();
    }

    const tableBody = $('#leavelist tbody');
    tableBody.empty();

    data.forEach(function (v) {
        const row = $('<tr>');

        let approvalStatusText = "No Action Taken Yet!";
        let statusClass = "text-primary";
        let rejectionReasonHtml = "";

        if (v[4] === "1") { // isapproved
            approvalStatusText = "Approved";
            statusClass = "text-success";
        } else if (v[4] === "0") { // isapproved
            approvalStatusText = "Not Approved";
            statusClass = "text-danger";
            if (v[10]) { // rejectionreason
                rejectionReasonHtml = `<br/><span class="text-primary">Reason: ${v[10]}</span>`;
            }
        }
        row.append($('<td>').addClass(statusClass).html(approvalStatusText + rejectionReasonHtml));

        let studentName = v[7]; // fname
        if (v[11] && v[11].trim() !== "") { // mname
            studentName += " " + v[11];
        }
        studentName += " " + v[8]; // lname
        studentName += " (" + v[6] + ")"; // rollno
        row.append($('<td>').text(studentName));

        const applicationDate = v[5] ? new Date(v[5]).toLocaleDateString('en-GB') : 'N/A';
        row.append($('<td>').text(applicationDate));
        row.append($('<td>').text(v[3])); // Leave Station

        const leaveFrom = v[0] ? new Date(v[0]).toLocaleDateString('en-GB') : 'N/A';
        const leaveTo = v[1] ? new Date(v[1]).toLocaleDateString('en-GB') : 'N/A';
        row.append($('<td>').text(leaveFrom));
        row.append($('<td>').text(leaveTo));
        row.append($('<td>').text(v[2])); // Reason for Leave

        const viewLinkHref = `${API.viewLeaveApplicationDetails}?aid=${v[9]}&origin=report`;
        const viewLink = $('<a>')
            .attr('href', viewLinkHref)
            .attr('target', '_blank')
            .addClass('btn btn-sm btn-primary mb-1 mb-sm-0 mr-sm-1')
            .text('View');
        row.append($('<td>').append(viewLink));

        tableBody.append(row);
    });

    leaveTable = initializeLeaveTable();
}