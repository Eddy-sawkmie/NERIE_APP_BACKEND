document.addEventListener('DOMContentLoaded', function () {
    const fyButtons = document.querySelectorAll('.fy-filter-btn');
    const container = document.getElementById('program-cards-container');
    const displayElement = document.getElementById('current-fy-display');

    function resetButton(btn) {
        btn.classList.remove('border-success', 'border-2', 'shadow', 'opacity-100', 'active');
        btn.classList.add('opacity-75', 'border-light');

        const yearSpan = btn.querySelector('.fy-year-text');
        if (yearSpan) {
            yearSpan.classList.remove('text-success');
            yearSpan.classList.add('text-dark');
        }

        const statusSpan = btn.querySelector('.status-label');
        if (statusSpan) {
            statusSpan.classList.remove('text-success');
            statusSpan.classList.add('text-muted');
        }
    }

    function activateButton(btn) {
        btn.classList.remove('opacity-75', 'border-light');
        btn.classList.add('border-success', 'border-2', 'shadow', 'opacity-100', 'active');

        const yearSpan = btn.querySelector('.fy-year-text');
        if (yearSpan) {
            yearSpan.classList.remove('text-dark');
            yearSpan.classList.add('text-success');
        }

        const statusSpan = btn.querySelector('.status-label');
        if (statusSpan) {
            statusSpan.classList.remove('text-muted');
            statusSpan.classList.add('text-success');
        }
    }

    fyButtons.forEach(button => {
        button.addEventListener('click', function (e) {
            e.preventDefault();

            const fyText = this.dataset.fytext;
            const fyValueData = this.dataset.fyvalue;
            if(!fyValueData) return;
            const [fystart, fyend] = fyValueData.split("##");

            fyButtons.forEach(btn => resetButton(btn));
            activateButton(this);

            if (displayElement) {
                displayElement.innerText = fyText;
            }

            container.innerHTML = `
                <div class="col-12 d-flex flex-column align-items-center justify-content-center py-5">
                    <div class="spinner-border text-success" role="status"></div>
                    <span class="mt-3 text-muted fw-medium">
                        Fetching completed programs for ${fyText}...
                    </span>
                </div>
            `;

            const url = `/programs/completed-by-fy?fystart=${encodeURIComponent(fystart)}&fyend=${encodeURIComponent(fyend)}`;

            fetch(url)
                .then(res => {
                    if (!res.ok) throw new Error('Network response was not ok');
                    return res.json();
                })
                .then(data => {
                    if (!data || data.length === 0) {
                        container.innerHTML = `
                            <div class="col-12 text-center py-5">
                                <p class="text-muted fw-bold mb-0">
                                    There are no completed programs available for ${fyText}.
                                </p>
                            </div>`;
                        return;
                    }

                    container.innerHTML = data.map(program => {
                        const [progCode, progIdRaw, progName, progDescRaw] = program;

                        const progId = progIdRaw || 'N/A';
                        const progDesc = progDescRaw || 'No description provided.';

                        return `
                            <div class="col-12 col-lg-6 program-card">
                                <div class="bg-white rounded-3 border shadow-sm p-4 d-flex flex-column justify-content-between h-100">
                                    <div>
                                        <div class="d-flex justify-content-between align-items-start mb-3">
                                            <span class="badge text-success bg-success bg-opacity-10 rounded-pill px-2 py-1 fw-bold">${progId}</span>
                                            <span class="text-muted" style="font-size: 0.75rem;">Completed</span>
                                        </div>
                                        <h4 class="font-display fw-bold fs-5 text-dark mb-2 lh-sm">${progName}</h4>
                                        <p class="small line-clamp-2 mb-4">${progDesc}</p>
                                    </div>
                                    <div class="pt-3 border-top d-flex justify-content-end">
                                        <button class="btn bg-success text-white small fw-medium d-flex align-items-center gap-2 px-3 py-2 closedbtn"
                                                data-program-code="${progCode}">
                                            <span>View Details</span>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        `;
                    }).join('');
                })
                .catch(err => {
                    console.error('Fetch error:', err);
                    container.innerHTML = `
                        <div class="col-12 text-center py-5">
                            <p class="text-danger fw-bold mb-0">Error loading data.</p>
                        </div>`;
                });
        });
    });

    $('#modalclosedlist-modal').on('show.bs.modal', function () {
        let $modal = $(this);

        let $targetBtn = $modal.find('.is-current-fy').first();

        if($targetBtn.length === 0) {
            $targetBtn = $modal.find('.fy-filter-btn').first();
        }

        let isLoaded = $modal.data('initial-load-done');
        if($targetBtn.length && !isLoaded) {
            $targetBtn.trigger('click');
            $modal.data('initial-load-done', true);
        }
    });
});

$(document).ready(function(){
    console.log("Document ready. jQuery version: " + ($.fn.jquery || 'Not loaded'));
    if (typeof $.fn.slick === 'function') {
        console.log("Slick function IS available.");
    }
});

/* ============================================================
   SHARED HELPERS — inline-styled HTML builders
   Used by all three "get more" functions so styles are
   guaranteed to render regardless of CSS load order.
   ============================================================ */

function buildProgramItem(url, code, name, meta) {
    return '<li style="' +
               'display:flex;align-items:flex-start;gap:10px;' +
               'padding:10px 14px;margin-bottom:0;' +
               'background:#fff;border:1px solid #e2e8f0;border-radius:10px;' +
               'transition:background 0.15s,border-color 0.15s;' +
           '" onmouseover="this.style.background=\'#eff6ff\';this.style.borderColor=\'#bfdbfe\';" ' +
               'onmouseout="this.style.background=\'#fff\';this.style.borderColor=\'#e2e8f0\';">' +
               '<i class="fa fa-arrow-right" aria-hidden="true" style="' +
                   'color:#3b82f6;font-size:11px;margin-top:4px;flex-shrink:0;' +
               '"></i>' +
               '<a href="' + url + '" target="_blank" style="' +
                   'display:flex;flex-direction:column;gap:3px;' +
                   'text-decoration:none;flex:1;' +
               '">' +
                   '<span style="' +
                       'font-size:0.87rem;font-weight:600;color:#1e293b;line-height:1.4;' +
                   '">' + code + ': ' + name + '</span>' +
                   '<span style="' +
                       'font-size:0.75rem;color:#64748b;font-weight:400;' +
                   '">' + meta + '</span>' +
               '</a>' +
           '</li>';
}

function buildEmptyState(msg) {
    return '<div style="' +
               'display:flex;flex-direction:column;align-items:center;' +
               'padding:2.5rem 1rem;color:#94a3b8;text-align:center;gap:10px;' +
           '">' +
               '<i class="fas fa-inbox" style="font-size:2rem;color:#cbd5e1;"></i>' +
               '<p style="margin:0;font-size:0.87rem;font-style:italic;">' + msg + '</p>' +
           '</div>';
}

function buildErrorState() {
    return '<div style="' +
               'background:#fef2f2;border:1px solid #fecaca;border-radius:10px;' +
               'color:#991b1b;padding:12px 16px;font-size:0.87rem;text-align:center;' +
           '">Error loading data. Please try again.</div>';
}

/* GET MORE ONGOING PROGRAMS */
function getmoreongoingfunc() {
    $.ajax({
        type: "POST",
        url: getMoreOngoingProgramList,
        success: function (data) {
            var lst = '';
            if (Array.isArray(data) && data.length > 0) {
                lst = '<ul style="list-style:none;padding:0;margin:0;display:flex;flex-direction:column;gap:8px;">';
                $.each(data, function (k, v) {
                    lst += buildProgramItem('./reports/publicReport?phaseid=' + v[0], v[11], v[2], 'Started on ' + v[8] + ' \u2022 ' + v[9]);
                });
                lst += '</ul>';
            } else {
                lst = buildEmptyState('No ongoing programs available.');
            }
            $("#titlespan").html("Ongoing Programs");
            $("#programlistdiv").html(lst);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Error: " + textStatus);
            $("#programlistdiv").html(buildErrorState());
        }
    });
}

/* GET MORE UPCOMING PROGRAMS */
function getmoreupcomingfunc() {
    $.ajax({
        type: "POST",
        url: getMoreUpcomingProgramList,
        success: function (data) {
            var lst = '';
            if (Array.isArray(data) && data.length > 0) {
                lst = '<ul style="list-style:none;padding:0;margin:0;display:flex;flex-direction:column;gap:8px;">';
                $.each(data, function (k, v) {
                    lst += buildProgramItem('./reports/publicReport?phaseid=' + v[0], v[11], v[2], 'Starting from ' + v[9] + ' \u2022 ' + v[10]);
                });
                lst += '</ul>';
            } else {
                lst = buildEmptyState('No upcoming programs available.');
            }
            $("#titlespan").html("Upcoming Programs");
            $("#programlistdiv").html(lst);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Error: " + textStatus);
            $("#programlistdiv").html(buildErrorState());
        }
    });
}

/* GET MORE COMPLETED PROGRAMS */
function getmorecompletedfunc() {
    $.ajax({
        type: "POST",
        url: getMoreCompletedProgramList,
        success: function (data) {
            var lst = '';
            if (Array.isArray(data) && data.length > 0) {
                lst = '<ul style="list-style:none;padding:0;margin:0;display:flex;flex-direction:column;gap:8px;">';
                $.each(data, function (k, v) {
                    lst += buildProgramItem('./reports/publicReport?phaseid=' + v[0], v[11], v[2], 'Started on ' + v[9] + ' \u2022 ' + v[10]);
                });
                lst += '</ul>';
            } else {
                lst = buildEmptyState('No completed programs available.');
            }
            $("#titlespan").html("Completed Programs");
            $("#programlistdiv").html(lst);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Error: " + textStatus);
            $("#programlistdiv").html(buildErrorState());
        }
    });
}

/* EVENT LISTENERS */
document.addEventListener('DOMContentLoaded', function () {

    // "View More" — Ongoing
    // Selector updated: old `.btn-view-more.moreongoingbtn` → `.moreongoingbtn`
    document.querySelectorAll(".moreongoingbtn").forEach(function(link) {
        link.addEventListener("click", function(event) {
            event.preventDefault();
            var functionName = link.getAttribute("data-function");
            if (functionName && typeof window[functionName] === "function") window[functionName]();
        });
    });

    // "View More" — Upcoming
    // Selector updated: old `.btn-view-more.upcomingProgramsBtn` → `.upcomingProgramsBtn`
    document.querySelectorAll(".upcomingProgramsBtn").forEach(function(btn) {
        btn.addEventListener("click", function(event) {
            event.preventDefault();
            var functionName = btn.getAttribute("data-function");
            if (functionName && typeof window[functionName] === "function") window[functionName]();
        });
    });

    // "View More" — Completed
    // Selector updated: old `.btn.btn-view-more.view-more-completed` → `.view-more-completed`
    document.querySelectorAll(".view-more-completed").forEach(function(link) {
        link.addEventListener("click", function(event) {
            event.preventDefault();
            if (typeof getmorecompletedfunc === "function") getmorecompletedfunc();
        });
    });

    // Program details (Ongoing / Upcoming / Closed "View Details" buttons in modals)
    $(document).on('click', '.ongoingbtn, .upcomingbtn, .closedbtn', function(event) {
        event.preventDefault();
        var programCode = $(this).attr("data-program-code");
        if (programCode) {
            getPhasesListDetails(programCode);
        }
    });

    // Phase more details
    $(document).on('click', '.getmoredetails-btn', function(event) {
        event.preventDefault();
        var phaseId = $(this).data("phaseid");
        if (phaseId) {
            getPhaseMoreDetails(phaseId);
        }
    });

    // Activity details
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

/* GET PHASES LIST DETAILS (Populates MyProgramDetailsModal) */
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
                      '<td>' + two[i].startDate + ' to ' + two[i].endDate + '</td>';

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

                tc += '<td>' + two[i].coordinator + '</td>' +
                      '<td>' + stat + '</td>' +
                      '<td>' +
                          '<button class="btn getmoredetails-btn btn-info btn-sm mb-2" data-phaseid="' + two[i].phase + '">More Details</button><br>' +
                          '<button class="btn getactivity-btn btn-warning btn-sm mb-2" data-phaseid="' + two[i].phase + '">Activities</button>' +
                          '<button class="btn getprogrammaterials-btn btn-success btn-sm" data-phaseid="' + two[i].phase + '">Program Materials</button>' +
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

/* GET PHASE MORE DETAILS */
function getPhaseMoreDetails(phaseid) {
    $.ajax({
        url: getPhaseMoreDetailsBasedOnPhaseId,
        data: { phaseid: phaseid },
        method: 'GET',
        success: function (response) {
            $('#PhaseMoreDetailsprogramname').text(response.programcode.programname || 'N/A');
            $('#PhaseMoreDetailsofficeNameDetailsModal').text(response.programcode.moffices.officename || 'N/A');
            $('#PhaseMoreDetailsprogramID').text(response.programcode.programid || 'N/A');
            $('#PhaseMoreDetailsprogramStatusDetailsModal').text(response.programcode.closed || 'N/A');
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

            //$('#MyProgramDetailsModal').modal('hide');
            $('#PhaseMoreDetailsModal').modal('show');
        },
        error: function (xhr) {
            var alertMsg = "Something went wrong. Please try again.";
            if (xhr.status === 404) alertMsg = "Phase details not found.";
            $.alert(alertMsg);
            $('#PhaseMoreDetailsModal').modal('hide');
        }
    });
}

/* GET ACTIVITIES */
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
        //$('#MyProgramDetailsModal').modal('hide');
        $('#ActivitiesModal').modal('show');

    }).fail(function (xhr) {
        alert("Something went wrong fetching activities.");
    });
}

/* GET Program Materials */
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
                            'View' +
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

function viewProgramMaterialFile(programmaterialid) {

    window.open(
        '/program-materials/view-file?programmaterialid=' + programmaterialid,
        '_blank'
    );

}

/* HELPERS */
function formatDate(dateString) {
    if (!dateString) return "N/A";
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

//nd-hero slider (replaces lily slider for the hero section)
(function () {
            var slides = document.querySelectorAll('.nd-hero__slide');
            var dots   = document.querySelectorAll('.nd-hero__dot');
            var prev   = document.getElementById('heroPrev');
            var next   = document.getElementById('heroNext');
            var current = 0;
            var timer;

            function goTo(index) {
                slides[current].classList.remove('nd-hero__slide--active');
                dots[current].classList.remove('nd-hero__dot--active');
                current = (index + slides.length) % slides.length;
                slides[current].classList.add('nd-hero__slide--active');
                dots[current].classList.add('nd-hero__dot--active');
            }

            function startAuto() {
                timer = setInterval(function () { goTo(current + 1); }, 6000);
            }

            function resetAuto() {
                clearInterval(timer);
                startAuto();
            }

            if (prev) prev.addEventListener('click', function () { goTo(current - 1); resetAuto(); });
            if (next) next.addEventListener('click', function () { goTo(current + 1); resetAuto(); });

            dots.forEach(function (dot) {
                dot.addEventListener('click', function () {
                    goTo(parseInt(this.getAttribute('data-slide')));
                    resetAuto();
                });
            });

            startAuto();
        })();