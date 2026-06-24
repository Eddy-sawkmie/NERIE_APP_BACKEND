var utable;
var ptable;
var programcode;

// =================================================================
// MODAL HELPER FUNCTIONS (BS5 Stacking Fix included)
// =================================================================

function showModalAlert(message, title = 'Message', callback = null) {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);
    
    $('#feedbackModalFooter').html(
        '<button type="button" id="modalAlertOkBtn" class="btn btn-primary btn-modern px-4" data-bs-dismiss="modal">OK</button>'
    );

    const feedbackModalEl = document.getElementById('feedbackModal');
    if (feedbackModalEl.parentNode !== document.body) {
        document.body.appendChild(feedbackModalEl);
    }

    if (callback && typeof callback === 'function') {
        $(feedbackModalEl).off('hidden.bs.modal').one('hidden.bs.modal', callback);
    } else {
        $(feedbackModalEl).off('hidden.bs.modal');
    }

    var myModal = bootstrap.Modal.getOrCreateInstance(feedbackModalEl);
    myModal.show();
}

function showModalConfirm(message, title = 'Confirmation', onConfirmCallback) {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);
    
    $('#feedbackModalFooter').html(
        '<button type="button" class="btn btn-light border btn-modern px-4" data-bs-dismiss="modal">Cancel</button>' +
        '<button type="button" id="modalConfirmOkBtn" class="btn btn-danger btn-modern px-4">Confirm</button>'
    );

    const feedbackModalEl = document.getElementById('feedbackModal');
    if (feedbackModalEl.parentNode !== document.body) {
        document.body.appendChild(feedbackModalEl);
    }

    const myModal = bootstrap.Modal.getOrCreateInstance(feedbackModalEl);

    $('#modalConfirmOkBtn').off('click').one('click', function () {
        $(feedbackModalEl).off('hidden.bs.modal').one('hidden.bs.modal', function () {
            if (typeof onConfirmCallback === 'function') {
                onConfirmCallback();
            }
        });
        myModal.hide();
    });

    myModal.show();
}

function handleAjaxError(jqXHR, textStatus, errorThrown) {
    let errorMessage = `An error occurred: ${textStatus} - ${errorThrown}`;
    if (jqXHR.responseText) {
        errorMessage += `<br><br>Details: ${jqXHR.responseText}`;
    }
    showModalAlert(errorMessage, 'Request Failed');
}

// =================================================================
// INITIALIZATION
// =================================================================

function initializeDataTable(tableId, title) {
    if (typeof $ !== 'undefined' && $.fn.DataTable) {
        let table = $('#' + tableId).DataTable({
            ordering: false,
            pageLength: 10,
            retrieve: true,
            lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "All"]],
            dom: '<"d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-3"Bf>rt<"d-flex flex-column flex-md-row justify-content-between align-items-md-center mt-3"ip>',
            language: {
                search: "_INPUT_",
                searchPlaceholder: "Search...",
                emptyTable: "<div class='text-center p-4 text-muted'><i class='fas fa-folder-open fs-2 mb-3 d-block opacity-50'></i>No data available</div>"
            },
            buttons: [
                {
                    extend: 'excelHtml5',
                    text: '<i class="fas fa-file-excel me-1"></i> Export Excel',
                    title: title,
                    className: 'btn btn-success btn-sm btn-modern px-3 py-1',
                    exportOptions: {
                        columns: ':visible:not(.noExport)'
                    }
                }
            ]
        });

        // Style DataTables native inputs
        $('.dataTables_filter input').addClass('form-control form-control-sm modern-input d-inline-block w-auto');
        $('.dataTables_length select').addClass('form-select form-select-sm modern-select d-inline-block w-auto');
        
        return table;
    }
    return null;
}

document.addEventListener("DOMContentLoaded", () => {
    utable = initializeDataTable("unclosetable", "Unclosed Programs Exported");
    ptable = initializeDataTable("phasestable", "Unclosed Phases Exported");

    $('.sub-menu ul, .sub-sub-menu ul').hide();

    $("#menu-toggle").click(function (e) {
        e.preventDefault();
        $("#wrapper").toggleClass("toggled");
    });

    $(".sub-menu a").click(function () {
        $(this).parent(".sub-menu").children("ul").slideToggle("100");
        $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
    });

    $(".sub-sub-menu a").click(function () {
        $(this).parent(".sub-sub-menu").children("ul").slideToggle("100");
        $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
    });

    $("#fyunclose").change(function() {
        var fy = $(this).val();
        if (!fy) return;
        var fystart = fy.split("##")[0];
        var fyend = fy.split("##")[1];
        getClosedPrograms(fystart, fyend);
    });

    // Event Delegation for action buttons
    document.getElementById('unclosetable').addEventListener('click', function(e) {
        const viewBtn = e.target.closest('.viewphasesbtn');
        if (viewBtn) {
            e.preventDefault();
            const pcode = viewBtn.getAttribute('data-pcode');
            viewphasesofprogram(pcode);
        }

        const closeProgBtn = e.target.closest('.closeprogrambtn');
        if (closeProgBtn) {
            e.preventDefault();
            const pcode = closeProgBtn.getAttribute('data-pcode');
            closeprogram(pcode);
        }
    });

    document.getElementById('phasestable').addEventListener('click', function(e) {
        const closePhaseBtn = e.target.closest('.closephasebtn');
        if (closePhaseBtn) {
            e.preventDefault();
            const phaseid = closePhaseBtn.getAttribute('data-phaseid');
            closephase(phaseid);
        }
    });


    // =================================================================
    // FORM SUBMISSIONS
    // =================================================================

    $('#closephaseform').submit(function (e) {
        e.preventDefault();

        // Hide Report Modal
        bootstrap.Modal.getOrCreateInstance(document.getElementById('phasesModalCloseReport')).hide();

        showModalConfirm(
            "Are you sure you want to close this phase? This action cannot be undone.",
            "Confirm Phase Closure",
            function() {
                // Loading State
                const $btn = $('#submitClosePhaseBtn');
                const originalText = $btn.html();
                $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-2"></i> Closing...');

                callCustomAjax(
                    API.closePhase,
                    "phaseid=" + $('#phaseid').val() + "&closingreport=" + encodeURIComponent($('#phaseclosingreport').val()),
                    function (data) {
                        $btn.prop('disabled', false).html(originalText);
                        if (data === '1') {
                            showModalAlert('Phase closed successfully.', 'Success', function() {
                                $('#phaseclosingreport').val('');
                                viewphasesofprogram(programcode);
                            });
                        } else {
                            showModalAlert('Failed to close the phase. Please try again.', 'Error');
                        }
                    },
                    function(jqXHR, textStatus, errorThrown) {
                        $btn.prop('disabled', false).html(originalText);
                        handleAjaxError(jqXHR, textStatus, errorThrown);
                    }
                );
            }
        );
    });

    $('#closeprogramform').submit(function (e) {
        e.preventDefault();

        // Hide Report Modal
        bootstrap.Modal.getOrCreateInstance(document.getElementById('programModalCloseReport')).hide();

        showModalConfirm(
            "Are you sure you want to close this program and all its phases? This action cannot be undone.",
            "Confirm Program Closure",
            function () {
                // Loading State
                const $btn = $('#submitCloseProgramBtn');
                const originalText = $btn.html();
                $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-2"></i> Closing...');

                callCustomAjax(
                    API.closeProgram,
                    "pcode=" + $('#programcode').val() + "&closingreport=" + encodeURIComponent($('#programclosingreport').val()),
                    function (data) {
                        $btn.prop('disabled', false).html(originalText);
                        if (data === '1') {
                            showModalAlert('Program closed successfully.', 'Success', function() {
                                $('#programclosingreport').val('');
                                $("#fyunclose").trigger('change');
                            });
                        } else {
                            showModalAlert('Failed to close the program. Please try again.', 'Error');
                        }
                    },
                    function(jqXHR, textStatus, errorThrown) {
                        $btn.prop('disabled', false).html(originalText);
                        handleAjaxError(jqXHR, textStatus, errorThrown);
                    }
                );
            }
        );
    });

    $('#backtotop').click(function () {
        $("html, body").animate({ scrollTop: 0 }, 600);
        return false;
    });

    $('#unclosetable tbody').on('click', 'td a.showmore', function (e) {
        e.preventDefault();
        var row = $(this).closest('tr');
        var morespan = row.find('span.more');
        var l = row.find('span.less');
        if (morespan.is(':hidden')) {
            $(this).html("<i class='fas fa-chevron-up me-1'></i> show less");
            l.hide();
            morespan.show();
        } else {
            $(this).html("<i class='fas fa-chevron-down me-1'></i> show more");
            l.show();
            morespan.hide();
        }
    });
});

// =================================================================
// AJAX DATA FETCHING
// =================================================================

function getdateformate(d) {
    if (!d) return '';
    var date = new Date(d);
    var day = String(date.getDate()).padStart(2, '0');
    var month = String(date.getMonth() + 1).padStart(2, '0');
    var year = date.getFullYear();
    return `${day}/${month}/${year}`;
}

function getClosedPrograms(fystart, fyend) {
    // Show loading spinner
    if(utable) utable.clear().draw();
    $('#tablediv').show();
    $('#unclosetable tbody').html('<tr><td colspan="7" class="text-center p-4 text-muted"><i class="fas fa-spinner fa-spin me-2"></i> Loading...</td></tr>');

    callCustomAjax(API.listProgramOpenCourse, "fystart=" + fystart + "&fyend=" + fyend,
        function (returndata) {
            if(utable) {
                utable.clear().destroy();
            }
            $('#unclosetable tbody').empty();

            if (returndata && returndata.length > 0) {
                let count = 1;
                returndata.forEach(function (x) {
                    var programdescriptionr = x[3].length > 20
                        ? `<span class='less'>${x[3].substring(0, 20)}...</span><span class='more' style='display:none'>${x[3]}</span><br><a href="#" class="showmore small text-decoration-none mt-1 d-inline-block"><i class='fas fa-chevron-down me-1'></i> show more</a>`
                        : x[3];

                    var rd = `<tr>
                        <td class="text-center fw-semibold text-muted">${count++}</td>
                        <td class="fw-bold text-dark">${x[1]}</td>
                        <td><span class="badge bg-light text-dark border px-2 py-1">${x[2]}</span></td>
                        <td class="small text-secondary">${programdescriptionr}</td>
                        <td class="text-center fw-medium">${x[4]}</td>
                        <td class="text-center">
                            <button class='btn btn-primary btn-sm btn-modern viewphasesbtn text-nowrap px-3' data-pcode='${x[0]}'>
                                <i class='fas fa-eye me-1'></i> View Phases
                            </button>
                        </td>
                        <td class="text-center">
                            <button class='btn btn-danger btn-sm btn-modern closeprogrambtn text-nowrap px-3' data-pcode='${x[0]}'>
                                <i class='fas fa-times me-1'></i> Close Program
                            </button>
                        </td>
                        </tr>`;
                    $('#unclosetable tbody').append(rd);
                });
                $('#tablediv').show();
            } else {
                $('#tablediv').hide();
                showModalAlert('No open programs found for the selected financial year.', 'Information');
            }
            utable = initializeDataTable("unclosetable", "Unclosed Programs Exported");
        },
        function(jqXHR, textStatus, errorThrown) {
            $('#unclosetable tbody').empty();
            utable = initializeDataTable("unclosetable", "Unclosed Programs Exported");
            handleAjaxError(jqXHR, textStatus, errorThrown);
        }
    );
}

function viewphasesofprogram(pcode) {
    programcode = pcode;
    
    // UI indicator
    $('#phasestable tbody').html('<tr><td colspan="7" class="text-center p-4 text-muted"><i class="fas fa-spinner fa-spin me-2"></i> Loading phases...</td></tr>');
    var phasesModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('phasesModal'));
    phasesModal.show();

    callCustomAjax(API.listUnclosePhase, "pcode=" + pcode,
        function (returndata) {
            if(ptable) {
                ptable.clear().destroy();
            }
            $('#phasestable tbody').empty();

            if (returndata && returndata.length > 0) {
                let count = 1;
                returndata.forEach(function (x) {

                    var closebtn = (x[6] === 'N')
                        ? `<button class='btn btn-danger btn-sm btn-modern closephasebtn text-nowrap px-3' data-phaseid='${x[0]}'><i class='fas fa-times-circle me-1'></i> Close Phase</button>`
                        : "<span class='badge bg-secondary bg-opacity-10 text-dark border border-secondary border-opacity-25 px-3 py-1'>CLOSED</span>";

                    var sdate = getdateformate(x[3]);
                    var edate = getdateformate(x[4]);
                    var cdate = getdateformate(x[5]);

                    var rd = `<tr>
                        <td class="text-center fw-semibold text-muted">${count++}</td>
                        <td class="text-center"><span class="badge bg-light text-dark border px-2 py-1">${x[1]}</span></td>
                        <td class="small text-dark">${x[2]}</td>
                        <td class="small fw-medium"><i class="far fa-calendar-check me-1"></i> ${sdate}</td>
                        <td class="small fw-medium"><i class="far fa-calendar-times me-1"></i> ${edate}</td>
                        <td class="small fw-medium"><i class="far fa-calendar-alt me-1"></i> ${cdate}</td>
                        <td class="text-center">${closebtn}</td>
                        </tr>`;
                    $('#phasestable tbody').append(rd);
                });

                ptable = initializeDataTable("phasestable", "Unclosed Phases Exported");
            } else {
                phasesModal.hide();
                showModalAlert('No unclosed phases found for this program.', 'Information');
            }
        },
        function(jqXHR, textStatus, errorThrown) {
            phasesModal.hide();
            handleAjaxError(jqXHR, textStatus, errorThrown);
        }
    );
}

function closephase(pid) {
    bootstrap.Modal.getOrCreateInstance(document.getElementById('phasesModal')).hide();
    $('#phaseid').val(pid);
    var reportModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('phasesModalCloseReport'));
    reportModal.show();
}

function closeprogram(pcode) {
    $('#programcode').val(pcode);
    var reportModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('programModalCloseReport'));
    reportModal.show();
}