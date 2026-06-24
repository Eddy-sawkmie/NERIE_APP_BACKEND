var atableList = getdatatable("approveTableList", "Approved List Exported");
var rtableList = getdatatable("rejectTableList", "Rejected List Exported");

function getdatatable(tname, rname) {
    return $("#" + tname).DataTable({
        retrieve: true,
        ordering: false,
        pageLength: 5,
        lengthMenu: [[5, 10, 20, 50, -1],[5, 10, 20, 50, "All"]],
        dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
             'rtip',
        buttons:[
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                title: rname,
                className: 'btn btn-success btn-sm mb-3',
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                }
            }
        ]
    });
}

function getApproveProgramList() {
    var fy = $("#approvefinancialyear").val();

    if (fy) {
        $("#approvelistdiv").show();
        var fystart = fy.split("##")[0];
        var fyend = fy.split("##")[1];
        callCustomAjax(
            API.listApproveProgram,
            "fystart=" + fystart + "&fyend=" + fyend,
            function (data) {
                atableList.clear();
                atableList.destroy();
                $("#atttablebody").empty();

                if (data.length !== 0) {
                    var count = 1;
                    data.forEach(function (item) {
                        // 1. Prepare Program Name (Show More/Less logic)
                        var programname = "";
                        if (item[0].length > 20) {
                            programname =
                                "<span class='less'>" +
                                item[0].substring(0, 20) +
                                "...</span>" +
                                "<span class='more' style='display:none'>" +
                                item[0] +
                                "</span>" +
                                '</br><a href="#more" class="showmore">show more</a></br>';
                        } else {
                            programname = item[0];
                        }

                        // 2. Prepare Friendly Messages for Null values
                        // Checks if value is null, the string "null", or empty.
                        var category   = (item[3] && item[3] !== 'null') ? item[3] : 'N/A';
                        var phaseDesc  = (item[9] && item[9] !== 'null') ? item[9] : 'N/A';
                        var lastDate   = (item[6] && item[6] !== 'null') ? item[6] : 'N/A';
                        var venues     = (item[10] && item[10] !== 'null') ? item[10] : 'N/A';

                        // 3. Build the Row
                        var rowString =
                            "<tr>" +
                            "<td>" + count++ + "</td>" +
                            "<td>" + category + "</td>" +
                            "<td>" + programname + "(" + getdateformate(item[4]) + " to " + getdateformate(item[5]) + ")<hr>(Program ID:"+item[2]+")</td>" +
                            // "<td>" + item[2] + "</td>" +
                            "<td>" + item[1] + "</td>" +
                            // "<td>" + item[8] + "</td>" +
                            "<td> Phase No. "+ item[8] +"<hr>"+ phaseDesc + "</td>" +
                            "<td>" + lastDate + "</td>" +
                            "<td>" + venues + "</td>" +
                            "<td>" + item[11] + "</td>";

                        // 4. Role Based Column
                        if ($("#rolecode").val() === "U") {
                            rowString +=
                                "<td>" +
                                    "<a href='" + API.listParticipantOverallFeedback + "?aid=" + item[7] + "'>View Overall Feedback</a> <hr> " +
                                    "<a href='" + API.listParticipantDailyFeedback + "?phaseid=" + item[7] + "'>View Day Feedback</a> " +
                                "</td>" +
                                "<td>" +
                                    "Finalized/Open<br>" +
                                    "<a href='" + API.viewApproval + "?pdid=" + item[19] + "' target='_blank'>View Letter</a><br>" +
                                    "approved on:<br>" +
                                    getdateformate(item[15]) +
                                "</td>" +
                                "</tr>";
                        } else {
                            rowString +=
                                "<td>" +
                                    "Finalized/Open<br>" +
                                    "<a href='" + API.viewApproval + "?pdid=" + item[19] + "' target='_blank'>View Letter</a><br>" +
                                    "approved on:<br>" +
                                    getdateformate(item[15]) +
                                "</td>" +
                                "</tr>";
                        }
                        $("#atttablebody").append(rowString);
                    });
                } else {
                    showModalAlert("No records found", "Information");
                }
                atableList = getdatatable("approveTableList", "Approved List");
            }
        );
    } else {
        $("#approvelistdiv").hide();
    }
}

function getRejectProgramList() {
    var fy = $("#rejectfinancialyear").val();
    if (fy) {
        $("#rejectlistdiv").show();
        var fystart = fy.split("##")[0];
        var fyend = fy.split("##")[1];
        callCustomAjax(
            API.listRejectedPrograms,
            "fystart=" + fystart + "&fyend=" + fyend,
            function (data) {
                rtableList.clear();
                rtableList.destroy();
                $("#rtttablebody").empty();

                if (data.length !== 0) {
                    var count = 1;
                    data.forEach(function (item) {
                        var programdescriptionr = "";
                        if (item[1].length > 10) {
                            programdescriptionr =
                                "<span class='less'>" +
                                item[1].substring(0, 10) +
                                "...</span>" +
                                "<span class='more' style='display:none'>" +
                                item[1] +
                                "</span>" +
                                '</br><a href="#more" class="showmore">show more</a>';
                        } else {
                            programdescriptionr = item[1];
                        }

                        var rowString =
                            "<tr>" +
                            "<td>" + count++ + "</td>" +
                            "<td>" + item[3] + "</td>" +
                            "<td>" + item[0] + "(" + getdateformate(item[4]) + " to " + getdateformate(item[5]) + ")</td>" +
                            "<td>" + item[2] + "</td>" +
                            "<td>" + programdescriptionr + "</td>" +
                            "<td>" + item[8] + "</td>" +
                            "<td>" + item[9] + "</td>" +
                            "<td>" + item[6] + "</td>" +
                            "<td>" + item[10] + "</td>" +
                            "<td>" + item[11] + "</td>" +
                            "<td>Program Rejection Details<br>" +
                                "<a href='" + API.viewRejectionPrograms + "?pdid=" + item[19] + "' target='_blank'>View Letter</a><br>" +
                                "rejected on:<br>" + getdateformate(item[22]) +
                            "</td>" +
                            "<td>" + item[20] + "</td>" +
                            "</tr>";
                        $("#rtttablebody").append(rowString);
                    });
                } else {
                    showModalAlert("No records found", "Information");
                }
                rtableList = getdatatable("rejectTableList", "Rejected List Exported");
            }
        );
    } else {
        $("#rejectlistdiv").hide();
    }
}

function showModalAlert(message, title = "Message") {
    $("#feedbackModalLabel").text(title);
    $("#feedbackModalBody").html(message);
    $("#feedbackModal .modal-footer").html(
        '<button type="button" class="btn btn-primary" data-bs-dismiss="modal">OK</button>'
    );
    var myModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('feedbackModal'));
    myModal.show();
}

function showModalAndReload(message, title = "Success") {
    showModalAlert(message, title);
    const modalEl = document.getElementById('feedbackModal');
    modalEl.addEventListener("hidden.bs.modal", function () {
        window.location.reload();
    }, { once: true });
}

function handleAjaxError(jqXHR, textStatus, errorThrown) {
    let errorMessage = `An error occurred: ${textStatus} - ${errorThrown}`;
    if (jqXHR.responseText) {
        errorMessage += `<br><br>Details: ${jqXHR.responseText}`;
    }
    showModalAlert(errorMessage, "Request Failed");
}

function getdateformate(d) {
    if (!d) return "";
    var dateObj = new Date(d);
    var day = dateObj.getDate();
    var month = dateObj.getMonth() + 1;
    var year = dateObj.getFullYear();
    if (day < 10) day = "0" + day;
    if (month < 10) month = "0" + month;
    return day + "/" + month + "/" + year;
}

$(document).ready(function () {
    $(".datepicker").datepicker({
        dateFormat: "dd-mm-yy",
        orientation: "bottom",
    });

    // Updated #usertable to match the new configuration
    $("#usertable").DataTable({
        ordering: false,
        pageLength: 5,
        lengthMenu: [[5, 10, 20, 50, -1],[5, 10, 20, 50, "All"]],
        dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
             'rtip',
        buttons:[
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                title: 'Programs',
                className: 'btn btn-success btn-sm mb-3',
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                }
            }
        ]
    });

    var demo1 = $("[name=coordinators]").bootstrapDualListbox({
        infoText: "All {0} Coordinators",
        infoTextEmpty: "Selected Coordinators",
        filterPlaceHolder: "Search Coordinator",
    });

//    var demo2 = $("[name=newcoordinators]").bootstrapDualListbox({
//        infoText: "All {0} Coordinators",
//        infoTextEmpty: "Selected Coordinators",
//        filterPlaceHolder: "Search Coordinator",
//    });

    var demo3 = $("[name=ecoordinators]").bootstrapDualListbox({
        infoText: "All {0} Coordinators",
        infoTextEmpty: "Selected Coordinators",
        filterPlaceHolder: "Search Coordinator",
        eventMoveOverride: true,
    });

    $("#venues").select2({
        placeholder: "Select Venues",
        allowClear: true,
        closeOnSelect: false,
        width: '100%'
    });

    $("#ongoingprograms").select2();

    $(".select2-search").select2({
        placeholder: "Select Option",
        allowClear: false,
        width: '100%'
    });

    function initializeFormSubmitted(selectId, displayId) {
        var $select = $(selectId);

        $select.select2({
            //dropdownParent: $('#programEditModalSubmitted'), // Ensures z-index correctness
            placeholder: "Select Coordinators",
            allowClear: true,
            closeOnSelect: true,
            width: '100%'
        });

        var selectedCoordinators = $select.val() ||[];
        $select.data('ordered-selection', selectedCoordinators);

        function updateDisplay() {
            var orderedIds = $select.data('ordered-selection') ||[];
            var $display = $(displayId);

            if (orderedIds.length === 0) {
                $display.html("Select Head Coordinator");
                return;
            }

            var names = orderedIds.map(function(id) {
                var $option = $select.find('option[value="' + id + '"]');
                return $option.length ? $option.text() : id;
            });

            var displayText = "Head Coordinator: " + names[0];
            if (names.length > 1) {
                displayText += "<br><span class='text-danger'>Co-coordinators: " + names.slice(1).join(", ") + "</span>";
            }
            $display.html(displayText);
        }

        $select.on('select2:select', function(e) {
            var id = e.params.data.id;
            var current = $select.data('ordered-selection') ||[];
            if (!current.includes(id)) {
                current.push(id);
                $select.data('ordered-selection', current);
            }
            updateDisplay();
        });

        $select.on('select2:unselect', function(e) {
            var id = e.params.data.id;
            var current = $select.data('ordered-selection') ||[];
            var index = current.indexOf(id);
            if (index > -1) {
                current.splice(index, 1);
                $select.data('ordered-selection', current);
            }
            updateDisplay();
        });

        updateDisplay();
    }

    initializeFormSubmitted('#coorsSubmitted', '#selectionOrderDisplaySubmitted');

    window.editfuncsubmitted = function(pname, pdesc, pid, phid, phdesc, pcode, ucodes) {
        $("#editprogramformsubmitted")[0].reset();

        $('#ephasedescdivsubmitted').hide();
        $('#ephasedescriptionsubmitted').prop('required', false);

        $('#eprogramnamesubmitted').val(pname);
        $('#ephaseidsubmitted').val(phid);
        $('#eprogramdescriptionsubmitted').val(pdesc);
        $('#eprogramidsubmitted').val(pid);
        $('#eprogramcodesubmitted').val(pcode);

        if (phdesc && phdesc !== 'null' && phdesc !== '') {
            $('#ephasedescdivsubmitted').show();
            $('#ephasedescriptionsubmitted').val(phdesc);
            $('#ephasedescriptionsubmitted').prop('required', true);
        }

        var $select = $('#coorsSubmitted');

        var cuserarray = ucodes ? ucodes.toString().split(',') :[];

        $select.val(cuserarray).trigger('change');

        $select.data('ordered-selection', cuserarray);

        initializeFormSubmitted('#coorsSubmitted', '#selectionOrderDisplaySubmitted');

        var myModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('programEditModalSubmitted'));
        myModal.show();
    };

    $("#usertable").on("click", ".editfuncsubmitted-btn", function (event) {
        event.preventDefault();
        const btn = $(this);

        editfuncsubmitted(
            btn.data("param1"),   // pname
            btn.data("param2"),   // pdesc
            btn.data("param3"),   // pid
            btn.data("param7"),   // phid
            btn.data("param8"),   // phdesc
            btn.data("param11"),  // pcode
            btn.data("param13")   // ucodes (coordinators)
        );
    });

    // SUBMIT HANDLER
    $('#editprogramformsubmitted').on('submit', function (e) {
        e.preventDefault();

        // Get the ordered coordinators to ensure Head Coordinator is first
        var orderedCoordinators = $('#coorsSubmitted').data('ordered-selection') ||[];

        if (orderedCoordinators.length === 0) {
            showModalAlert("Please select at least one Program Coordinator", "Warning");
            return false;
        }

        var formParams = $(this).serializeArray();

        formParams = formParams.filter(function(item) {
            return item.name !== 'ecoordinators111';
        });

        orderedCoordinators.forEach(function(val) {
            formParams.push({ name: 'ecoordinators111', value: val });
        });

        $.ajax({
            type: "POST",
            url: API.updateProgramDetails,
            data: $.param(formParams),
            success: function (data) {
                if (data === "1") {
                    $('#programEditModalSubmitted').modal('hide');
                    showModalAndReload('Successfully Updated!');
                }
                else if (data === "-99") {
                    showModalAlert('Please Reset and Re-Select the Coordinators', "Warning");
                } else {
                    showModalAlert('Failed to update! ' + data, "Error");
                }
            },
            error: function(xhr) {
                showModalAlert("An error occurred: " + xhr.statusText, "Error");
            }
        });
    });

    window.editfunc2 = function(pname, pdesc, pid, sdate, edate, ldate, phid, phdesc, cccode, cdate, pcode, vcodes, ucodes) {
        $("#editprogramform")[0].reset();
        $("#ephasedescdiv").hide();
        $("#ephasedescription").prop("required", false);

        $("#eprogramname").val(pname);
        $("#ephaseid").val(phid);
        $("#eprogramdescription").val(pdesc);
        $("#eprogramid").val(pid);
        $("#estartdate").val(sdate);
        $("#eenddate").val(edate);
        $("#elastdate").val(ldate);
        $("#ecourseclosedate").val(cdate);
        $("#eprogramcode").val(pcode);
        $("#ecoursecategorycode").val(cccode); // Program Category

        if (phdesc) {
            $("#ephasedescdiv").show();
            $("#ephasedescdiv").removeClass("display-none");
            $("#ephasedescription").val(phdesc).prop("required", true);
        }

        $('input[name="evenues"]').prop("checked", false);
        if (vcodes) {
            var venuearray = vcodes.toString().split(",");
            venuearray.forEach(function (v) {
                $("#ev_" + v).prop("checked", true);
            });
        }

        var cuserarray = ucodes ? ucodes.toString().split(",") :[];
        $("#coors3").val(cuserarray);

        if (typeof demo3 !== 'undefined') {
            demo3.bootstrapDualListbox("refresh", true);
        }

        bootstrap.Modal.getOrCreateInstance(document.getElementById('programEditModal')).show();
    };

    $("#editprogramform").on("submit", function (e) {
        e.preventDefault();
        var s_val = $("#estartdate").val(), e_val = $("#eenddate").val(), l_val = $("#elastdate").val(), c_val = $("#ecourseclosedate").val();
        var s = new Date(s_val.replace(/(\d+)-(\d+)-(\d+)/, "$2/$1/$3")), e_date = new Date(e_val.replace(/(\d+)-(\d+)-(\d+)/, "$2/$1/$3")), l = new Date(l_val.replace(/(\d+)-(\d+)-(\d+)/, "$2/$1/$3")), c = new Date(c_val.replace(/(\d+)-(\d+)-(\d+)/, "$2/$1/$3"));

        if (s > e_date) {
            showModalAlert("Startdate cannot be after Enddate!");
            bootstrap.Modal.getOrCreateInstance(document.getElementById('programEditModal')).hide();
            return false;
        }

        if (l > e_date) {
            showModalAlert("Lastdate cannot be after Enddate!");
            bootstrap.Modal.getOrCreateInstance(document.getElementById('programEditModal')).hide();
            return false;
        }

        if (e_date > c) {
            showModalAlert("Program closedate cannot be before Enddate!");
            bootstrap.Modal.getOrCreateInstance(document.getElementById('programEditModal')).hide();
            return false;
        }

        $.ajax({
            type: "POST",
            url: API.updateProgramDetails,
            data: $("#editprogramform").serialize(),
            success: function (data) {
                if (data === "1") {
                    bootstrap.Modal.getOrCreateInstance(document.getElementById('programEditModal')).hide();
                    showModalAndReload("Successfully Updated!");
                } else {
                    showModalAlert("Failed to update! " + (data || ""));
                }
            },
            error: handleAjaxError,
        });
    });

    let formCount = 1;
    function initializeBatchFormCoordinator(selectId, displayId) {
        const $selectElement = $(selectId);

        $selectElement.select2({
            placeholder: "Select Coordinators",
            allowClear: true,
            closeOnSelect: true
        });

        $selectElement.data('ordered-selection', $selectElement.val() ||[]);

        function updateDisplay() {
            const orderedIds = $selectElement.data('ordered-selection') ||[];
            const $displayElement = $(displayId);

            if (orderedIds.length === 0) {
                $displayElement.html("");
                return;
            }

            const headCoordinatorName = $selectElement.find(`option[value="${orderedIds[0]}"]`).text();
            const coCoordinatorNames = orderedIds.slice(1).map(id => {
                return $selectElement.find(`option[value="${id}"]`).text();
            });

            let displayText = `<span style="color: blue;">Head Coordinator: ${headCoordinatorName}</span>`;
            if (coCoordinatorNames.length > 0) {
                displayText += `<br><span class='text-danger' style='margin-bottom: 0;'>Co-coordinators: ${coCoordinatorNames.join(", ")}</span>`;
            }
            $displayElement.html(displayText);
        }

        // Use .off() to clear existing listeners, and check for duplicates before pushing
        $selectElement.off("select2:select").on("select2:select", function (e) {
            let currentOrderedIds = $selectElement.data('ordered-selection') ||[];
            let newId = e.params.data.id;

            // Check if it already exists before adding
            if (!currentOrderedIds.includes(newId)) {
                currentOrderedIds.push(newId);
                $selectElement.data('ordered-selection', currentOrderedIds);
            }
            updateDisplay();
        });

        $selectElement.off("select2:unselect").on("select2:unselect", function (e) {
            let currentOrderedIds = $selectElement.data('ordered-selection') ||[];
            const unselectedId = e.params.data.id;
            currentOrderedIds = currentOrderedIds.filter(id => id !== unselectedId);
            $selectElement.data('ordered-selection', currentOrderedIds);
            updateDisplay();
        });

        updateDisplay();
    }

    function initializePhaseToggle(form) {
        const phasesSelect = form.find(".phases-select"), phasedisContainer = form.find(".phasedis-container"), phasedescriptionInput = form.find(".phasedescription-input");
        phasesSelect.change(function () {
            if ($(this).val() === "Yes") {
                phasedisContainer.show();
                phasedescriptionInput.prop("required", true);
            } else {
                phasedisContainer.hide();
                phasedescriptionInput.prop("required", false);
            }
        });
    }

    initializeBatchFormCoordinator("#coorsTEST1", "#selectionOrderDisplay1");
    initializePhaseToggle($("#formsContainer .form-container:first"));
    initializeBatchFormCoordinator("#coors2", "#selectionOrderDisplay2");

    // ADD FORM BUTTON LOGIC ---
    $("#addFormButton").click(function (e) {
        e.preventDefault();
        formCount++;

        const originalFormContainer = $("#formsContainer .form-container:first");
        const newFormContainer = originalFormContainer.clone();

        newFormContainer.find(".close-batch-btn").show();

        newFormContainer.find(".close-batch-btn").click(function() {
            $(this).closest(".form-container").remove();
        });

        // -> THE ERRONEOUS LINE WAS REMOVED FROM HERE <-

        newFormContainer.find("form").attr("id", "programdetADDFORM" + formCount);

        newFormContainer.find("input, textarea, select").not('.coordinatorSelect').val("");

        newFormContainer.find(".select2-container").remove();
        newFormContainer.find(".coordinatorSelect")
            .removeClass("select2-hidden-accessible")
            .removeAttr("data-select2-id")
            .find("option").removeAttr("data-select2-id");
        newFormContainer.find(".coordinatorSelect").val(null);

        newFormContainer.find("span.charactercount-phase").text("");
        newFormContainer.find(".phasedis-container").hide();

        const newSelectId = "coorsTEST" + formCount;
        const newDisplayId = "selectionOrderDisplay" + formCount;

        newFormContainer.find(".coordinatorSelect").attr("id", newSelectId);
        newFormContainer.find('p[id^="selectionOrderDisplay"]').attr("id", newDisplayId).html("Select Head Coordinator");

        newFormContainer.appendTo("#formsContainer");

        initializePhaseToggle(newFormContainer);
        initializeBatchFormCoordinator("#" + newSelectId, "#" + newDisplayId);

        newFormContainer.find('button[type="reset"]').click(function () {
            const form = $(this).closest("form");
            form.find("input, textarea, select").val("");
            form.find("span.charactercount-phase").text("");
            $("#" + newDisplayId).html("Select Head Coordinator");
            form.find(".coordinatorSelect").val(null).trigger("change");
        });
    });

    $("#submitAllForms").click(function () {
        var forms = $("#formsContainer .form-container form"),
            allFormsValid = true;

        forms.each(function (index, form) {
            var $form = $(form);
            if ($form.data("submitted")) {
                return;
            }

            var orderedCoordinators = $form.find(".coordinatorSelect").data('ordered-selection') ||[];

            if (!orderedCoordinators || orderedCoordinators.length === 0) {
                allFormsValid = false;
                showModalAlert("Form " + (index + 1) + ": Please select at least one Program Coordinator.");
                return false;
            }

            $.ajax({
                type: "POST",
                url: API.saveBatchPrograms,
                data: $form.serialize() + "&coordinators=" + orderedCoordinators.join(","),
                async: false,
                success: function (data) {
                    if (data !== "2") {
                        allFormsValid = false;
                        showModalAlert("Form " + (index + 1) + " failed to save. " + (data === "1" ? "Program Name already exists." : "An error occurred."));
                    } else {
                        $form.data("submitted", true);
                    }
                },
                error: (jqXHR, textStatus, errorThrown) => {
                    allFormsValid = false;
                    handleAjaxError(jqXHR, textStatus, errorThrown);
                },
            });

            if (!allFormsValid) {
                return false;
            }
        });

        if (allFormsValid) {
            showModalAndReload("All valid forms submitted successfully!");
        }
    });

    $("#financialyear-details").change(function () {
        const fy = $(this).val(), $programsDropdown = $("#programs"), $phaseDropdown = $("#phaseno");
        $programsDropdown.empty().append($("<option></option>").attr("value", "").text("Select"));
        $phaseDropdown.empty().append($("<option></option>").attr("value", "").text("Select"));
        $("#newFormContainer").hide();
        if (fy) {
            const fystart = fy.split("##")[0], fyend = fy.split("##")[1];
            $.ajax({
                type: "POST",
                url: API.listAcceptedPrograms,
                data: { fystart: fystart, fyend: fyend },
                success: function (data) {
                    if (data && data.length > 0) {
                        data.forEach((program) => $programsDropdown.append($("<option></option>").attr("value", program[0]).text(program[1])));
                    } else {
                        $programsDropdown.empty().append($("<option></option>").attr("value", "").text("No programs found"));
                    }
                },
                error: handleAjaxError,
            });
        }
    });

    $("#programs").change(function () {
        const programCode = $(this).val(), $phaseDropdown = $("#phaseno");
        $phaseDropdown.empty().append($("<option></option>").attr("value", "").text("Select"));
        $("#newFormContainer").hide();
        if (programCode) {
            $.ajax({
                type: "POST",
                url: API.listPhases,
                data: { programcode: programCode },
                success: function (data) {
                    if (data && data.length > 0) {
                        data.forEach((phase) => $phaseDropdown.append($("<option></option>").attr("value", phase[0]).text(phase[1])));
                    } else {
                        $phaseDropdown.empty().append($("<option></option>").attr("value", "").text("No phases found"));
                    }
                },
                error: handleAjaxError,
            });
        }
    });

    $("#phaseno").change(function() {
        const phaseId = $(this).val();
        const programCode = $('#programs').val();

        if (phaseId) {
            $('#DetailsForm')[0].reset();
            $("#programDetailProgcode").val(programCode);
            $("#programDetailPhaseID").val(phaseId);

            $.ajax({
                type: "POST",
                url: API.programToPopulate,
                data: { programcode: programCode },
                success: function(data) {
                    if (data && data.length > 0) {
                        $("#programdescriptiondisplayDET").val(data[0][0]);
                        $("#programiddisplayDET").val(data[0][1]);
                    }

                    $.ajax({
                        type: "POST",
                        url: API.getProgramMembers,
                        data: { programcode: programCode, phaseid: phaseId },
                        success: function(memberData) {
                            const $programMembers = $('#programMembers');

                            $('#programMembers').select2({
                                placeholder: "Select Local Coordinators",
                                allowClear: true,
                                width: '100%'
                            });
                            if (memberData && memberData.length > 0) {
                                memberData.forEach(function(member) {
                                    $programMembers.append('<option value="' + member.pmid + '">' + member.username + '</option>');
                                });
                            } else {
                                showModalAlert('No Member Details Available', "Information");
                            }
                        },
                        error: handleAjaxError
                    });
                },
                error: handleAjaxError
            });

            $('#newFormContainer').show();

            $("#focusTEST, #stageTEST, #targetTEST, #venuesDET").select2({
                placeholder: "Select Options",
                allowClear: true,
                closeOnSelect: true,
                width: '100%'
            });

        } else {
            $('#newFormContainer').hide();
        }
    });

    $("#DetailsForm").submit(function (e) {
        e.preventDefault();
        $.ajax({
            type: "POST",
            url: API.saveProgramDetails,
            data: $(this).serialize(),
            success: function (data) {
                if (data === "1" || data === "2") {
                    showModalAndReload("Program details saved successfully!");
                } else {
                    showModalAlert("Failed to save program details: " + (data || ""), "Error");
                }
            },
            error: handleAjaxError,
        });
    });

    $("#activityfinancialyear").change(function () {
        var fy = $(this).val();
        $("#activityprograms, #activityphaseno").empty().append($("<option></option>").val("").text("Select"));
        $("#activityContainer").hide();
        if (fy) {
            var fystart = fy.split("##")[0], fyend = fy.split("##")[1];
            $.ajax({
                url: API.listFinancialYear,
                type: "POST",
                data: {
                    fystart: fystart,
                    fyend: fyend
                },
                success: function (data) {
                    if (data) {
                        data.forEach(x =>
                            $("#activityprograms")
                                .append($("<option></option>").val(x[0]).text(x[1]))
                        );
                    }
                },
                error: handleAjaxError
            });
        }
    });

    $("#activityprograms").change(function () {
        var programcode = $(this).val();
        $("#activityphaseno").empty().append($("<option></option>").val("").text("Select"));
        $("#activityContainer").hide();
        if (programcode) {
            $.ajax({
                url: API.listPhases,
                type: "POST",
                data: {
                    programcode: programcode
                },
                success: function (data) {
                    if (data) {
                        data.forEach(x =>
                            $("#activityphaseno")
                                .append($("<option></option>").val(x[0]).text(x[1]))
                        );
                    }
                },
                error: handleAjaxError
            });
        }
    });

    $("#activityphaseno").change(function () {
        var phaseId = $(this).val(), programcode = $("#activityprograms").val();
        if (phaseId) {
            $('#activityListDetails').html('');
            getActivities($('#activityphaseno').val());
            $("#activityForm")[0].reset();
            $("#activityContainer").show();
            $("#activityProgcode").val(programcode);
            $("#activityPhaseID").val(phaseId);
            $.ajax({
                url: API.programToPopulate, type: "POST", data: { programcode: programcode },
                success: function (data) {
                    if (data.length !== 0) {
                        $("#activityprogramdescriptiondisplayDET").val(data[0][0]);
                        $("#activityprogramiddisplayDET").val(data[0][1]);
                    }
                },
                error: handleAjaxError,
            });
        } else {
            $("#activityContainer").hide();
        }
    });

    $("#activityForm").submit(function (e) {
        e.preventDefault();
        $.ajax({
            type: "POST", url: API.saveActivities, data: $("#activityForm").serialize(),
            success: function (data) {
                if (data === "2") { showModalAndReload("Activity saved successfully!"); }
                else { showModalAlert("Failed to save activity!", "Error"); }
            },
            error: handleAjaxError,
        });
    });

    const userRole = $("#userrole").val();
    let requestData = userRole !== "A" ? { usercode: $("#usercode").val() } : {};
    $.ajax({
        type: "POST", url: API.listOngoingPrograms, data: requestData,
        success: function (data) {
            $("#ongoingprograms").empty().append($("<option></option>").val("").text("Select"));
            if (data) { data.forEach(function (item) { $("#ongoingprograms").append($("<option></option>").val(item[0]).text(item[1]).attr("title", item[1])); }); }
        },
        error: handleAjaxError,
    });

    $("#ongoingprograms").on("change", function () {
        var programcode = $(this).val();
        if (programcode) {
            $.ajax({
                url: API.getProgramDetails, type: "POST", data: { programcode: programcode },
                success: function (data) {
                    if (data.length !== 0) {
                        $("#programdescriptiondisplay").val(data[0][0]);
                        $("#programiddisplay").val(data[0][1]);
                        $("#categorytypedisplay").val(data[0][2]);
                        $("#nextphase").val(data[0][3]);
                        $("#addphasediv").show();
                    } else {
                        showModalAlert("Kindly close the previous phase and submit phase report before creating new phase", "Warning");
                        $("#addphasediv").hide();
                    }
                },
                error: handleAjaxError,
            });
        } else {
            $("#addphasediv").hide();
        }
    });

    $("#ongoingform").on("submit", function (e) {
        e.preventDefault();

        var orderedCoordinators = $("#coors2").data('ordered-selection') || [];

        if (!orderedCoordinators || orderedCoordinators.length === 0) {
            showModalAlert("Please select at least one Program Coordinator.", "Error");
            return false;
        }

        var formData = $(this).serializeArray();
        formData = formData.filter(item => item.name !== 'newcoordinators');
        var serializedData = $.param(formData) + "&newcoordinators=" + orderedCoordinators.join(",");

        $.ajax({
            url: API.saveProgramPhases,
            type: "POST",
            data: serializedData,
            success: function (data) {
                if (data === "1") {
                    showModalAndReload("New phase added successfully!");
                } else {
                    showModalAlert("Failed to add new phase. Please try again!", "Error");
                }
            },
            error: handleAjaxError,
        });
    });

    $("#usertable, #approveTableList, #rejectTableList").on("click", "tbody td a.showmore", function (e) {
        e.preventDefault();
        var row = $(this).closest("tr"), morespan = row.find("span.more"), lessspan = row.find("span.less");
        if (morespan.is(":hidden")) {
            $(this).text("show less");
            lessspan.hide();
            morespan.show();
        } else {
            $(this).text("show more");
            lessspan.show();
            morespan.hide();
        }
    });

    $("#programdescription, #phasedescription, #newphasedescription").keyup(function () {
        var maxLength = parseInt($(this).attr("maxlength")), currentLength = $(this).val().length, charLeft = maxLength - currentLength;
        var counterSpan = $(this).parent().next('span[id^="charactercount"]');
        if (counterSpan.length) { counterSpan.text("Characters left: " + charLeft); }
    });

    $("#programid").focusout(function () {
        var programid = $(this).val().trim().toUpperCase();
        if (programid && !["NON PAC", "NON PAC PROGRAM", "NON PAC PROGRAMME"].includes(programid)) {
            $.ajax({
                type: "POST", url: API.checkProgramExistByProgramID, data: { programid: programid },
                success: function (data) {
                    if (data === "1") {
                        showModalAlert("Program ID already exists. To add another part to this program, please use the 'Add New Phase' tab.", "Warning");
                        $("#programid").val("").focus();
                    }
                },
                error: handleAjaxError,
            });
        }
    });
});

 // Helper function to format dates
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    var date = new Date(dateString);
    return date.toLocaleDateString('en-GB'); // Format as DD/MM/YYYY
}

// Format expenditure with commas
function formatIndianExpenditure(expenditureString) {
    if (!expenditureString) return 'N/A';
    if (expenditureString.includes(',')) return expenditureString; // Return as is if already formatted
    return expenditureString.replace(/(\d)(?=(\d\d)+\d$)/g, '$1,');
}

function getActivities(phaseid) {
    $.ajax({
        url: API.getActivitiesList,
        data: { phaseid: phaseid },
        method: 'GET'
    }).done(function (response) {
        var tableContent = '<h3 class="text-center">Activities</h3><br>';
        tableContent += '<table class="table"><thead><tr><td>Sl No.</td><td>Name</td><td>Description</td><td>Dates</td><td>Expenditure.</td></tr></thead><tbody>';
        // Check if response is valid and contains activities
        if (Array.isArray(response) && response.length > 0) {
            var activities = response; // Response is now a direct list of activities

            // Build table rows
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
            // Display a message when no activities are found
            tableContent = '<tr><td colspan="5" class="text-center">No activities found</td></tr>';
        }
        tableContent += '</tbody></table>';
        // Populate the modal
        $('#activityListDetails').html(tableContent);
    }).fail(function (xhr) {
        if (xhr.status === 404) {
            showModalAlert("No activities added yet for this program phase!", "Information");
        } else {
            showModalAlert("Something went wrong.", "Error");
        }
    });
}