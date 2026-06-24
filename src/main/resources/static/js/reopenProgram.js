var utable;

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
        '<button type="button" id="modalConfirmOkBtn" class="btn btn-primary btn-modern px-4">Confirm</button>'
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
                searchPlaceholder: "Search programs...",
                emptyTable: "<div class='text-center p-4 text-muted'><i class='fas fa-folder-open fs-2 mb-3 d-block opacity-50'></i>No data available in table</div>"
            },
            buttons: [
                {
                    extend: 'excelHtml5',
                    text: '<i class="fas fa-file-excel me-1"></i> Export Excel',
                    title: title,
                    className: 'btn btn-sm btn-success btn-modern px-3 py-1',
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
  utable = initializeDataTable("closetable", "Un-Closed Programs Exported");

  $("#menu-toggle").click(function (e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
  });

  $("#backtotop").click(function () {
    $("html, body").animate({ scrollTop: 0 }, 600);
    return false;
  });

  $(".sub-menu ul, .sub-sub-menu ul").hide();
  $(".sub-menu a").click(function () {
    $(this).parent(".sub-menu").children("ul").slideToggle("100");
    $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
  });
  $(".sub-sub-menu a").click(function () {
    $(this).parent(".sub-sub-menu").children("ul").slideToggle("100");
    $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
  });

  $("#fyclose").change(getUnClosedPrograms);

  document.addEventListener("click", function (event) {
    let target = event.target.closest(".unclosephasebtn");
    if (target) {
      event.preventDefault();
      let functionName = target.getAttribute("data-function");
      let param1 = target.getAttribute("data-param1");

      if (typeof window[functionName] === "function") {
        window[functionName](param1);
      } else {
        console.error(`Function ${functionName} is not defined`);
      }
    }
  });
});

function getUnClosedPrograms() {
  var fy = $("#fyclose").val();
  if(!fy) return;
  var fystart = fy.split("##")[0];
  var fyend = fy.split("##")[1];

  // Visual feedback during load
  if (utable) utable.clear().draw();
  $("#tablediv").show();
  $("#closetable tbody").html('<tr><td colspan="7" class="text-center p-4 text-muted"><i class="fas fa-spinner fa-spin me-2"></i> Loading...</td></tr>');

  callCustomAjax(
    API.listCloseCourseProgram,
    "fystart=" + fystart + "&fyend=" + fyend,
    function (returndata) {
      if(utable) {
          utable.clear().destroy();
      }
      $('#closetable tbody').empty();
      
      if (returndata && returndata.length > 0) {
        let count = 1;
        returndata.forEach(function (x) {
          var rd =
            `<tr>
              <td class="text-center fw-semibold text-muted">${count++}</td>
              <td class="fw-bold text-dark">${x[2]}</td>
              <td><span class="badge bg-light text-dark border px-2 py-1">${x[4]}</span></td>
              <td class="small text">${x[3]}</td>
              <td class="text-center fw-bold">${x[5]}</td>
              <td class="text-center"><span class="px-2 py-1">${x[6]}</span></td>
              <td class="text-center">
                <button class='btn btn-outline-primary btn-sm btn-modern unclosephasebtn text-nowrap px-3'
                    data-function='uncloseprogram'
                    data-param1='${x[0]}'>
                    <i class='fas fa-folder-open me-1'></i> Reopen
                </button>
              </td>
            </tr>`;
          $("#closetable tbody").append(rd);
        });
      }
      
      utable = initializeDataTable("closetable", "Un-Closed Programs Exported");
    },
    function(jqXHR, textStatus, errorThrown) {
        $('#closetable tbody').empty();
        utable = initializeDataTable("closetable", "Un-Closed Programs Exported");
        handleAjaxError(jqXHR, textStatus, errorThrown);
    }
  );
}

function uncloseprogram(phid) {
    showModalConfirm(
        "Are you sure you want to reopen this program phase?",
        "Confirm Phase Reopening",
        function() {
            callCustomAjax(API.reopenProgramPhase, "phid=" + phid, function (data) {
                if (data === "1") {
                    showModalAlert("Program phase reopened successfully.", "Success", function() {
                        getUnClosedPrograms();
                    });
                } else {
                    showModalAlert("Failed to reopen the phase. Please try again.", "Error");
                }
            }, handleAjaxError);
        }
    );
}

function getdateformate(d) {
    if(!d) return '';
    var date = new Date(d);
    var day = String(date.getDate()).padStart(2, '0');
    var month = String(date.getMonth() + 1).padStart(2, '0');
    var year = date.getFullYear();
    return `${day}/${month}/${year}`;
}

function unclosecoursefunc(ccode) {
    showModalConfirm(
        "Are you sure you want to reopen this program?",
        "Confirm Program Reopening",
        function() {
            $.ajax({
                type: "POST",
                url: API.reopenCourseReport,
                data: "coursecode=" + ccode,
                success: function (data) {
                    if (data == "1") {
                        showModalAlert("Program has been reopened successfully.", "Success", function() {
                            if (typeof getfycloseprogramfunc === 'function') {
                                getfycloseprogramfunc();
                            }
                        });
                    } else {
                        showModalAlert("An error occurred. Please try again.", "Error");
                    }
                },
                error: handleAjaxError
            });
        }
    );
}