$(document).ready(function() {

    // Event Listeners
    $(document).on('click', '.view-program-btn', function(event) {
        event.preventDefault();
        var programCode = $(this).attr("data-program-code");
        if (programCode) {
            getPhasesListDetails(programCode);
        } else {
            alert("Program code is unavailable. Please check the backend data.");
        }
    });

    $(document).on('click', '.getmoredetails-btn', function(event) {
        event.preventDefault();
        var phaseId = $(this).data("phaseid");
        if (phaseId) {
            getPhaseMoreDetails(phaseId);
        }
    });

    $(document).on('click', '.getactivity-btn', function(event) {
        event.preventDefault();
        var phaseId = $(this).data("phaseid");
        if (phaseId) {
            getActivities(phaseId);
        }
    });

    $(document).on('click', '.getprogrammaterials-btn', function(event) {
        event.preventDefault();
        var phaseId = $(this).data("phaseid");
        if (phaseId) {
            getProgramMaterials(phaseId);
        }
    });

});

/* AJAX Data Fetch Functions */

function getPhasesListDetails(programcode) {
    $.ajax({
        url: getAllProgramDetailsBasedOnProgramCode,
        data: { programcode: programcode },
        method: 'GET'
    }).done(function (r) {
        var modalId = '#MyProgramDetailsModal';

        if (r && r.length > 0 && r[0]) {
            $("#officeNameDetailsModal").html(r[0].moffices ? r[0].moffices.officename : 'N/A');
            $("#programTitleDetailsModal").html(r[0].programname);
            $("#programDescDetailsModal").html(r[0].programdescription);
            $(".programIDDetailsModal").html(r[0].programid);
            $("#programStatusDetailsModal").html(r[0].closed === 'N' ? 'Not Closed' : 'Closed');
            $("#programCategoryDetailsModal").html(
                r[0].mcoursecategories && r[0].mcoursecategories.coursecategoryname
                    ? r[0].mcoursecategories.coursecategoryname
                    : 'No Course Category'
            );
        }

        var two = r[1];
        var tc = '';

        if (Array.isArray(two) && two.length > 0) {
            for (var i = 0; i < two.length; i++) {
                var stat = '';
                if (two[i].finalized === 'Y') {
                    stat = two[i].closed === 'Y' ? 'Phase is Finalized and it is Closed' : 'Phase is Finalized and it is Ongoing';
                } else {
                    stat = 'Phase is Not Finalized';
                }

                tc += '<tr>' +
                    '<td>' + (two[i].phase.phaseno || (i + 1)) + '</td>' +
                    '<td>' + (two[i].phase.phasedescription || 'No Description') + '</td>' +
                    '<td>' + formatDate(two[i].startDate) + ' to ' + formatDate(two[i].endDate) + '</td>';

                if (two[i].VenuesAndRP && two[i].VenuesAndRP.length > 0) {
                    let venueNames = '', RPNames = '';
                    for (let v = 0; v < two[i].VenuesAndRP.length; v++) {
                        RPNames   += two[i].VenuesAndRP[v].RPNames    + '<br>';
                        venueNames += two[i].VenuesAndRP[v].venueNames + '<br>';
                    }
                    tc += '<td>' + venueNames + '</td><td>' + RPNames + '</td>';
                } else {
                    tc += '<td>No Venues</td><td>No Resource Persons</td>';
                }

                tc += '<td>' + (two[i].coordinator || 'N/A') + '</td>' +
                        '<td>' + stat + '</td>' +
                        '<td>' +
                            '<button class="btn getmoredetails-btn btn-info btn-sm mb-2 w-100" data-phaseid="' + two[i].phase + '">More Details</button><br>' +
                            '<button class="btn getactivity-btn btn-warning btn-sm mb-2 w-100" data-phaseid="' + two[i].phase + '">Activities</button><br>' +
                            '<button class="btn getprogrammaterials-btn btn-success btn-sm w-100" data-phaseid="' + two[i].phase + '">Program Materials</button>' +
                        '</td>' +
                        '</tr>';
            }
        } else {
            tc = '<tr><td colspan="8" class="text-center fw-bold py-5 text-muted" style="font-size: 1.1em;">There are no phases available for this program.</td></tr>';
        }

        $("#phaseListDetailsModal").html(tc);
        $(modalId).modal('show');

    }).fail(function () {
        alert("Something went wrong fetching program details.");
    });
}

function getPhaseMoreDetails(phaseid) {
    $.ajax({
        url: getPhaseMoreDetailsBasedOnPhaseId,
        data: { phaseid: phaseid },
        method: 'GET',
        success: function (response) {
            $('#PhaseMoreDetailsprogramname').text(response.programcode.programname || 'N/A');
            $('#PhaseMoreDetailsofficeNameDetailsModal').text(response.programcode.moffices.officename || 'N/A');
            $('#PhaseMoreDetailsprogramID').text(response.programcode.programid || 'N/A');
            $('#PhaseMoreDetailsprogramStatusDetailsModal').text(response.programcode.closed === 'Y' ? 'Closed' : 'Not Closed');
            $('#PhaseMoreDetailsprogramCategoryDetailsModal').text(
                response && response.programcode.mcoursecategories && response.programcode.mcoursecategories.coursecategoryname
                    ? response.programcode.mcoursecategories.coursecategoryname
                    : 'No Course Category'
            );

            $("#PhaseMoreDetailsFocusAreas").text(response.focusareas || "N/A");
            $("#PhaseMoreDetailsBudgetProposed").text(response.budgetproposed || "N/A");
            $("#PhaseMoreDetailsTargetGroup").text(response.targetgroup || "N/A");
            $("#PhaseMoreDetailsStage").text(response.stage || "N/A");
            $("#PhaseMoreDetailsObjectives").text(response.objectives || "N/A");
            $("#PhaseMoreDetailsMethodology").text(response.methodology || "N/A");
            $("#PhaseMoreDetailsTools").text(response.tools || "N/A");
            $("#PhaseMoreDetailsKPIndicators").text(response.kpindicators || "N/A");
            $("#PhaseMoreDetailsOutcomes").text(response.outcomes || "N/A");

            $('#PhaseMoreDetailsModal').modal('show');
        },
        error: function (xhr) {
            var alertMsg = "Something went wrong. Please try again.";
            if (xhr.status === 404) alertMsg = "Phase details not found.";
            if(typeof $.alert !== "undefined") {
                $.alert(alertMsg);
            } else {
                alert(alertMsg);
            }
        }
    });
}

function getActivities(phaseid) {
    $.ajax({
        url: getActivityBasedOnPhaseId,
        data: { phaseid: phaseid },
        method: 'GET'
    }).done(function (response) {
        var tableContent = '';
        var activities = response;

        if (Array.isArray(activities) && activities.length > 0 && activities[0].programcode) {
            $('#activityprogramname').text(activities[0].programcode.programname);
            $("#activityofficeNameDetailsModal").text(activities[0].programcode.moffices.officename);
            $("#activityprogramDescDetailsModal").text(activities[0].programcode.programdescription);
            $(".activityprogramIDDetailsModal").text(activities[0].programcode.programid);
            $("#activityprogramStatusDetailsModal").text(activities[0].programcode.closed === 'N' ? 'Not Closed' : 'Closed');
            $("#activityprogramCategoryDetailsModal").text(
                activities[0].programcode.mcoursecategories && activities[0].programcode.mcoursecategories.coursecategoryname
                    ? activities[0].programcode.mcoursecategories.coursecategoryname
                    : 'No Course Category'
            );
        }

        if (Array.isArray(activities) && activities.length > 0) {
            activities.forEach(function (activity, index) {
                tableContent +=
                    '<tr>' +
                        '<td>' + (index + 1) + '</td>' +
                        '<td>' + (activity.activityname || 'N/A') + '</td>' +
                        '<td>' + (activity.activitydescription || 'N/A') + '</td>' +
                        '<td>' + (formatDate(activity.activitystartdate) + ' - ' + formatDate(activity.activityenddate)) + '</td>' +
                        '<td>' + formatIndianExpenditure(activity.expenditure) + '/-</td>' +
                    '</tr>';
            });
        } else {
            tableContent = '<tr><td colspan="5" class="text-center fw-bold py-5 text-muted" style="font-size: 1.1em;">There are no activities available.</td></tr>';
        }

        $('#activityListDetailsModal').html(tableContent);
        $('#ActivitiesModal').modal('show');

    }).fail(function (xhr) {
        alert("Something went wrong fetching activities.");
    });
}

function getProgramMaterials(phaseid) {
    $.ajax({
        url: getProgramMaterialsBasedOnPhaseId,
        method: 'POST',
        data: { phaseid: phaseid }
    }).done(function (response) {
        var tableContent = '';
        var programMaterials = response;

        if (Array.isArray(programMaterials) && programMaterials.length > 0) {
            programMaterials.forEach(function (material, index) {
                var programmaterialid = material[0];
                var materialdesc = material[1];
                var uploaddate = material[2];

                tableContent +=
                    '<tr>' +
                        '<td>' + (index + 1) + '</td>' +
                        '<td>' + (materialdesc || 'N/A') + '</td>' +
                        '<td>' + formatDate(uploaddate) + '</td>' +
                        '<td>' +
                            '<button class="btn btn-sm btn-success" ' +
                            'onclick="viewProgramMaterialFile(\'' + programmaterialid + '\')">' +
                            '<i class="fas fa-eye"></i> View' +
                            '</button>' +
                        '</td>' +
                    '</tr>';
            });
        } else {
            tableContent =
                '<tr>' +
                    '<td colspan="4" class="text-center fw-bold py-5 text-muted">' +
                    'No program materials available.' +
                    '</td>' +
                '</tr>';
        }

        $('#programMaterialsTableBody').html(tableContent);
        $('#ProgramMaterialsModal').modal('show');

    }).fail(function () {
        alert("Something went wrong fetching program materials.");
    });
}

/* Helpers */
function viewProgramMaterialFile(programmaterialid) {
    window.open(
        '/program-materials/view-file?programmaterialid=' + programmaterialid,
        '_blank'
    );
}

function formatDate(dateString) {
    if (!dateString) return "N/A";
    // If the dateString is already formatted like "15 March 2026", just return it
    if(isNaN(Date.parse(dateString)) && typeof dateString === 'string' && dateString.includes('-')) {
        return dateString;
    }
    var date = new Date(dateString);
    if (isNaN(date.getTime())) return dateString;
    return date.toLocaleDateString('en-GB', { day: 'numeric', month: 'long', year: 'numeric' });
}

function formatIndianExpenditure(amount) {
    if (amount === null || amount === undefined) return "0";
    return new Intl.NumberFormat('en-IN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(amount);
}