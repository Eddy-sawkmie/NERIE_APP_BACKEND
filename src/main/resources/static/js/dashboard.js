$(document).ready(function () {
    if ($("#calendar").length && $.fn.MEC) {
        $("#calendar").MEC();
    }
    $('#backtotop').click(function () {
        $("html, body").animate({scrollTop: 0}, 600);
        return false;
    });

    $("#one-item-row").on("click", function () { $(".b-customize").removeClass("col-lg-3 col-lg-4 col-lg-6").addClass("col-lg-12"); });
    $("#two-item-row").on("click", function () { $(".b-customize").removeClass("col-lg-3 col-lg-4 col-lg-12").addClass("col-lg-6"); });
    $("#three-item-row").on("click", function () { $(".b-customize").removeClass("col-lg-3 col-lg-6 col-lg-12").addClass("col-lg-4"); });

    function createCardHTML(progCode, progId, progName, progDesc, statusBadgeHtml) {
        return `
            <div class="col-12 col-lg-6 program-card">
                <div class="bg-white rounded-3 border shadow-sm p-4 d-flex flex-column justify-content-between h-100">
                    <div>
                        <div class="d-flex justify-content-between align-items-start mb-3">
                            <span class="badge text-success bg-success bg-opacity-10 rounded-pill px-2 py-1 fw-bold">${progId || 'N/A'}</span>
                            ${statusBadgeHtml}
                        </div>
                        <h4 class="fw-bold fs-5 text-dark mb-2 lh-sm">${progName || 'Unnamed Program'}</h4>
                        <p class="small text-muted line-clamp-2 mb-4">${progDesc || 'No description provided.'}</p>
                    </div>
                    <div class="pt-3 border-top d-flex justify-content-end">
                        <button class="btn btn-primary small fw-medium d-flex align-items-center gap-2 px-3 py-2" data-program-code="${progCode}">
                            <span>View Details</span>
                        </button>
                    </div>
                </div>
            </div>`;
    }

    function renderLoader(message) {
        return `
            <div class="col-12 d-flex flex-column align-items-center justify-content-center py-5">
                <div class="spinner-border text-secondary" role="status"></div>
                <span class="mt-3 text-muted fw-medium">${message}</span>
            </div>`;
    }

    function renderEmpty(message) {
        return `
            <div class="col-12 text-center py-5 border rounded-3 bg-white" style="border-style: dashed !important;">
                <p class="text-muted fw-bold mb-0">${message}</p>
            </div>`;
    }

    function renderError() {
        return `
            <div class="col-12 text-center py-5 border rounded-3 border-danger bg-white">
                <p class="text-danger fw-bold mb-0">Error loading data. Please try again later.</p>
            </div>`;
    }

    function loadModalData(modalId, fystart, fyend, fyText) {
        const roleCheck = (typeof CURRENT_USER_ROLE !== 'undefined' && CURRENT_USER_ROLE === "A");

        if (modalId === 'my-programs') {
            const containerAcc = document.getElementById('my-accepted-container');
            const containerRej = document.getElementById('my-rejected-container');
            const headingAcc = document.getElementById('accepted-heading');
            const headingRej = document.getElementById('rejected-heading');

            if (headingAcc) headingAcc.style.display = 'none';
            if (headingRej) headingRej.style.display = 'none';
            containerRej.style.display = 'none';

            containerAcc.innerHTML = renderLoader(`Fetching programs for ${fyText}...`);

            const urlAcc = roleCheck ? API.getMyProgramsAcceptedAdminByFy : API.getMyProgramsAcceptedCoorByFy;
            const urlRej = roleCheck ? API.getMyProgramsRejectedAdminByFy : API.getMyProgramsRejectedCoorByFy;

            $.when(
                $.post(urlAcc, { fystart, fyend }),
                $.post(urlRej, { fystart, fyend })
            ).done(function(accResponse, rejResponse) {
                const accData = accResponse[0];
                const rejData = rejResponse[0];

                if (!accData || accData.length === 0) {
                    containerAcc.innerHTML = renderEmpty('No Accepted programs found.');
                } else {
                    let html = '';
                    accData.forEach(p => {
                        html += createCardHTML(p[0], p[4], p[2], p[3], `<span class="text-success fw-bold" style="font-size: 0.75rem;">Accepted</span>`);
                    });
                    containerAcc.innerHTML = html;
                }

                if (!rejData || rejData.length === 0) {
                    containerRej.innerHTML = renderEmpty('No Rejected programs found.');
                } else {
                    let html = '';
                    rejData.forEach(p => {
                        html += createCardHTML(p[0], p[4], p[2], p[3], `<span class="text-danger fw-bold" style="font-size: 0.75rem;">Rejected</span>`);
                    });
                    containerRej.innerHTML = html;
                }

                if (headingAcc) headingAcc.style.display = '';
                if (headingRej) headingRej.style.display = '';
                containerRej.style.display = ''; // Restores standard Bootstrap flex layout

            }).fail(function() {
                containerAcc.innerHTML = renderError();
                if (headingAcc) headingAcc.style.display = '';
            });
        }
        else if (modalId === 'ongoing') {
            const container = document.getElementById('ongoing-container');
            container.innerHTML = renderLoader(`Fetching Ongoing for ${fyText}...`);
            const url = roleCheck ? API.getOngoingAdminByFy : API.getOngoingCoorByFy;

            $.post(url, { fystart, fyend }).done(function(data) {
                if (!data || data.length === 0) container.innerHTML = renderEmpty('No Ongoing programs found.');
                else {
                    let html = '';
                    data.forEach(p => {
                        html += createCardHTML(p[0], p[1], p[2], p[3], `<span class="text-warning fw-bold" style="font-size: 0.75rem;">Active</span>`);
                    });
                    container.innerHTML = html;
                }
            }).fail(() => container.innerHTML = renderError());
        }
        else if (modalId === 'upcoming') {
            const container = document.getElementById('upcoming-container');
            container.innerHTML = renderLoader(`Fetching Upcoming for ${fyText}...`);
            const url = roleCheck ? API.getUpcomingAdminByFy : API.getUpcomingCoorByFy;

            $.post(url, { fystart, fyend }).done(function(data) {
                if (!data || data.length === 0) container.innerHTML = renderEmpty('No Upcoming programs found.');
                else {
                    let html = '';
                    data.forEach(p => {
                        html += createCardHTML(p[0], p[1], p[2], p[3], `<span class="text-success fw-bold" style="font-size: 0.75rem;">Planned</span>`);
                    });
                    container.innerHTML = html;
                }
            }).fail(() => container.innerHTML = renderError());
        }
        else if (modalId === 'closed') {
            const container = document.getElementById('closed-container');
            container.innerHTML = renderLoader(`Fetching Closed for ${fyText}...`);
            const url = roleCheck ? API.getClosedAdminByFy : API.getClosedCoorByFy;

            $.post(url, { fystart, fyend }).done(function(data) {
                if (!data || data.length === 0) container.innerHTML = renderEmpty('No Closed programs found.');
                else {
                    let html = '';
                    data.forEach(p => {
                        html += createCardHTML(p[0], p[1], p[2], p[3], `<span class="text-muted fw-bold" style="font-size: 0.75rem;">Closed</span>`);
                    });
                    container.innerHTML = html;
                }
            }).fail(() => container.innerHTML = renderError());
        }
    }

    $(document).on('click', '.fy-filter-btn', function(e) {
        e.preventDefault();
        const $btn = $(this);
        const modalId = $btn.data('target-modal');
        const fyText = $btn.data('fytext');
        const fyValueData = $btn.data('fyvalue');
        if(!fyValueData) return;
        const [fystart, fyend] = fyValueData.split("##");

        const $modal = $btn.closest('.modal');

        $modal.find('.fy-filter-btn').each(function() {
            $(this).removeClass('active border-success').addClass('opacity-75 border-light');
            $(this).find('.fy-year-text').removeClass('text-success').addClass('text-dark');
            $(this).find('.fy-status-text').removeClass('text-success').addClass('text-muted');
        });

        $btn.removeClass('opacity-75 border-light').addClass('active border-success');
        $btn.find('.fy-year-text').removeClass('text-dark').addClass('text-success');
        $btn.find('.fy-status-text').removeClass('text-muted').addClass('text-success');

        $modal.find('.modal-fy-display').text(fyText);

        loadModalData(modalId, fystart, fyend, fyText);
    });

    $('.modal').on('show.bs.modal', function () {
        let $targetBtn = $(this).find('.is-current-fy').first();

        if($targetBtn.length === 0) {
            $targetBtn = $(this).find('.fy-filter-btn').first();
        }

        if($targetBtn.length) {
            const targetModalId = $targetBtn.data('target-modal');
            let needsLoad = false;

            if(targetModalId === 'my-programs') {
                needsLoad = $('#my-accepted-container').children().length === 0;
            } else if (targetModalId === 'ongoing') {
                needsLoad = $('#ongoing-container').children().length === 0;
            } else if (targetModalId === 'upcoming') {
                needsLoad = $('#upcoming-container').children().length === 0;
            } else if (targetModalId === 'closed') {
                 needsLoad = $('#closed-container').children().length === 0;
            }

            if(needsLoad) {
                $targetBtn.trigger('click');
            }
        }
    });

    $(document).on("click", 'button[data-program-code]', function(event) {
        if($(this).attr('data-phaseid')) return;
        event.preventDefault();
        var programDbId = $(this).data("program-code");
        if (programDbId) {
            getPhasesListDetails(programDbId);
        } else {
            console.error("Program DB ID is missing from the button.");
        }
    });

});

function getPhasesListDetails(programDbId) {
    if (!programDbId) { $.alert("Error: Program DB ID is missing."); return; }
    $.confirm({
        content: function () {
            var self = this;
            return $.ajax({
                url: './program/getAllProgramDetails',
                data: 'programcode=' + programDbId,
                method: 'GET'
            }).done(function (r) {
                if(self && typeof self.close === 'function') self.close();
                $('#MyProgramDetailsModal').modal('show');
                if (r && r[0]) {
                    var programData = r[0];
                    $("#officeNameDetailsModal").html((programData.moffices && typeof programData.moffices.officename !== 'undefined') ? programData.moffices.officename : "N/A");
                    $("#programTitleDetailsModal").html(programData.programname || "N/A");
                    $("#programDescDetailsModal").html(programData.programdescription || "N/A");
                    $(".programIDDetailsModal").html(programData.programid || "N/A");
                    $("#programStatusDetailsModal").html(typeof programData.closed !== 'undefined' ? (programData.closed === 'N' ? 'Not Closed' : (programData.closed === 'Y' ? 'Closed' : 'N/A')) : "N/A");
                    $("#programCategoryDetailsModal").html((programData.mcoursecategories && programData.mcoursecategories.coursecategoryname) ? programData.mcoursecategories.coursecategoryname : "N/A");
                    var phaseListData = r[1];
                    var tc = '';
                    if (phaseListData && Array.isArray(phaseListData) && phaseListData.length > 0) {
                        phaseListData.forEach(function(phaseItem) {
                            var stat = 'Phase is Not Finalized';
                            if (phaseItem.finalized === 'Y') stat = phaseItem.closed === 'Y' ? 'Phase Finalized & Closed' : 'Phase Finalized & Ongoing';
                            var phaseIdForButton = phaseItem.phase ? (phaseItem.phase.phaseid || '') : '';
                            tc += '<tr><td>' + (phaseItem.phase ? (phaseItem.phase.phaseno || 'N/A') : 'N/A') + '</td>' +
                                  '<td>' + (phaseItem.phase ? (phaseItem.phase.phasedescription || 'N/A') : 'N/A') + '</td>' +
                                  '<td>' + (phaseItem.startDate || 'N/A') + ' to ' + (phaseItem.endDate || 'N/A') + '</td>';
                            let venueNames = 'No Venues', RPNames = 'No Resource Persons';
                            if (phaseItem.VenuesAndRP && phaseItem.VenuesAndRP.length > 0) {
                                venueNames = ''; RPNames = '';
                                phaseItem.VenuesAndRP.forEach(function(vrp) { RPNames += (vrp.RPNames || 'N/A') + '<br>'; venueNames += (vrp.venueNames || 'N/A') + '<br>'; });
                            }
                            tc += '<td>' + venueNames + '</td><td>' + RPNames + '</td><td>' + (phaseItem.coordinator || 'N/A') + '</td><td>' + stat + '</td>' +
                                  '<td><button class="btn getmoredetails-btn btn-primary" data-phaseid="'+phaseIdForButton+'" data-programdbid="'+programDbId+'">More Details</button><br>' +
                                  '<button class="mt-3 btn getactivity-btn btn-primary" data-phaseid="'+phaseIdForButton+'" data-programdbid="'+programDbId+'">Activity Details</button></td></tr>';
                        });
                    } else { tc = '<tr><td colspan="8">No phases found.</td></tr>'; }
                    $("#phaseListDetailsModal").html(tc);
                    $("#MyProgramDetailsModal #printBTNDetailsModal").attr('href', API.printProgramDetails + '?programcode=' + programDbId);

                    $('#phaseListDetailsModal .getmoredetails-btn').off('click').on('click', function(event) {
                        event.preventDefault(); var phaseid = $(this).data("phaseid"); var progDbId = $(this).data("programdbid");
                        if(phaseid && progDbId) getPhaseMoreDetails(phaseid, progDbId); else console.warn('Phase/ProgDB ID missing for More Details.');
                    });
                    $('#phaseListDetailsModal .getactivity-btn').off('click').on('click', function(event) {
                        event.preventDefault(); var phaseid = $(this).data("phaseid"); var progDbId = $(this).data("programdbid");
                        if(phaseid && progDbId) getActivities(phaseid, progDbId); else console.warn('Phase/ProgDB ID missing for Activity Details.');
                    });
                } else { $('#MyProgramDetailsModal').modal('hide'); $.alert("Error: Could not display program details."); }
            }).fail(function (jqXHR, textStatus, errorThrown) {
                if(self && typeof self.close === 'function') self.close();
                $('#MyProgramDetailsModal').modal('hide');
                $.alert("AJAX Error: " + textStatus);
            });
        }
    });
}

function getActivities(phaseid, programDbIdForPrint) {
    if (!phaseid) { $.alert("Error: Phase ID missing."); return; }
    $('#loader').show();
    $.ajax({
        url: './activities/getActivityBasedOnPhaseId', data: { phaseid: phaseid }, method: 'GET',
        success: function (response) {
            $('#loader').hide();
            var tableContent = '', programInfoFromServer = null;
            if (Array.isArray(response) && response.length > 0 && response[0] && response[0].programcode) {
                programInfoFromServer = response[0].programcode;
                $('#activityprogramname').text(programInfoFromServer.programname || 'N/A');
                $("#activityofficeNameDetailsModal").text((programInfoFromServer.moffices && programInfoFromServer.moffices.officename) ? programInfoFromServer.moffices.officename : 'N/A');
                $("#activityprogramID").text(programInfoFromServer.programid || 'N/A');
                let programStatusText = 'N/A';
                if (typeof programInfoFromServer.closed !== 'undefined') {
                    programStatusText = (programInfoFromServer.closed === 'Y' || programInfoFromServer.closed === true) ? 'Closed' : ((programInfoFromServer.closed === 'N' || programInfoFromServer.closed === false) ? 'Active' : 'N/A');
                }
                $("#activityprogramStatusDetailsModal").text(programStatusText);
                $("#activityprogramCategoryDetailsModal").text((programInfoFromServer.mcoursecategories && programInfoFromServer.mcoursecategories.coursecategoryname) ? programInfoFromServer.mcoursecategories.coursecategoryname : 'N/A');
            } else {
                $('#activityprogramname').text('N/A'); $("#activityofficeNameDetailsModal").text('N/A');
                $("#activityprogramID").text('N/A'); $("#activityprogramStatusDetailsModal").text('N/A');
                $("#activityprogramCategoryDetailsModal").text('N/A');
            }
            if (Array.isArray(response) && response.length > 0) {
                response.forEach(function (activity, index) {
                    tableContent += '<tr><td>' + (index + 1) + '</td>' +
                                    '<td>' + (activity.activityname || 'N/A') + '</td>' +
                                    '<td>' + (activity.activitydescription || 'N/A') + '</td>' +
                                    '<td>' + (formatDate(activity.activitystartdate) + (activity.activityenddate ? (' - ' + formatDate(activity.activityenddate)) : '')) + '</td>' +
                                    '<td>' + formatIndianExpenditure(activity.expenditure) + (activity.expenditure ? '/-' : '') + '</td></tr>';
                });
            } else { tableContent = '<tr><td colspan="5" class="text-center">No activities found for this phase.</td></tr>'; }
            $('#activityListDetailsModal').html(tableContent);
            $('#ActivitiesModal').modal('show');
            var printButtonInActivities = $('#ActivitiesModal').find('#printBTNDetailsModal');
            if(printButtonInActivities.length > 0) {
                 var printLink = programDbIdForPrint ? API.printProgramDetails + '?programcode=' + programDbIdForPrint + '&phaseid=' + phaseid : API.printProgramDetails + '?phaseid=' + phaseid;
                 printButtonInActivities.attr('href', printLink);
            }
        },
        error: function (xhr) { $('#loader').hide(); $.alert(xhr.status === 404 ? "No activities found." : "Error fetching activities."); }
    });
}

function getPhaseMoreDetails(phaseid, programDbIdForPrint) {
    if (!phaseid) { $.alert("Error: Phase ID missing."); return; }
    $('#loader').show();
    $.ajax({
        url: './phase-more-details/getPhaseMoreDetailsBasedOnPhaseId', data: { phaseid: phaseid }, method: 'GET',
        success: function (response) {
            $('#loader').hide();
            if (!response) { $.alert("Empty data for phase details."); $('#PhaseMoreDetailsModal').modal('hide'); return; }
            var programInfoFromServer = response.programcode;
            if (programInfoFromServer) {
                $('#PhaseMoreDetailsprogramname').text(programInfoFromServer.programname || 'N/A');
                $("#PhaseMoreDetailsofficeNameDetailsModal").text((programInfoFromServer.moffices && programInfoFromServer.moffices.officename) ? programInfoFromServer.moffices.officename : 'N/A');
                $('#PhaseMoreDetailsprogramID').text(programInfoFromServer.programid || 'N/A');
                let programStatusText = 'N/A';
                if (typeof programInfoFromServer.closed !== 'undefined') {
                    programStatusText = (programInfoFromServer.closed === 'Y' || programInfoFromServer.closed === true) ? 'Program Closed' : ((programInfoFromServer.closed === 'N' || programInfoFromServer.closed === false) ? 'Program Active' : 'N/A');
                }
                $('#PhaseMoreDetailsprogramStatusDetailsModal').text(programStatusText);
                $("#PhaseMoreDetailsprogramCategoryDetailsModal").text((programInfoFromServer.mcoursecategories && programInfoFromServer.mcoursecategories.coursecategoryname) ? programInfoFromServer.mcoursecategories.coursecategoryname : 'N/A');
            } else {
                $('#PhaseMoreDetailsprogramname').text('N/A'); $("#PhaseMoreDetailsofficeNameDetailsModal").text('N/A');
                $('#PhaseMoreDetailsprogramID').text('N/A'); $('#PhaseMoreDetailsprogramStatusDetailsModal').text('N/A');
                $('#PhaseMoreDetailsprogramCategoryDetailsModal').text('N/A');
            }
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
            var printButtonInPhaseMore = $('#PhaseMoreDetailsModal').find('#printBTNDetailsModal');
            if(printButtonInPhaseMore.length > 0) {
                 var printLink = programDbIdForPrint ? API.printProgramDetails + '?programcode=' + programDbIdForPrint + '&phaseid=' + phaseid : API.printProgramDetails + '?phaseid=' + phaseid;
                 printButtonInPhaseMore.attr('href', printLink);
            }
        },
        error: function (xhr) {
            $('#loader').hide();
            var alertMsg = "Error fetching phase details.";
            if (xhr.status === 404) { alertMsg = "Phase details not found."; }
            else if (xhr.status === 500) { alertMsg = "Server error fetching phase details."; }
            $.alert(alertMsg); $('#PhaseMoreDetailsModal').modal('hide');
        }
    });
}

function formatDate(dateString) {
    if (!dateString) return 'N/A';
    try {
        var date = new Date(dateString);
        return isNaN(date.getTime()) ? 'Invalid Date' : date.toLocaleDateString('en-GB');
    } catch (e) {
        return 'Invalid Date String';
    }
}

function formatIndianExpenditure(expenditureString) {
    if (expenditureString === null || typeof expenditureString === 'undefined') return 'N/A';
    let numStr = String(expenditureString);
    if (numStr.includes(',')) return numStr;
    return numStr.replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,');
}