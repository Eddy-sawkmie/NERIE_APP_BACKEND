function showModalAlert(message, title = 'Message') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);
    $('#feedbackModal .modal-footer').html(
        '<button type="button" class="btn btn-primary" data-bs-dismiss="modal">OK</button>'
    );
    var myModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('feedbackModal'));
    myModal.show();
}

function showModalAndReload(message, title = 'Message') {
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
    showModalAlert(errorMessage, 'Request Failed');
}

function customReset() {
    window.location.reload();
}

function initializeDataTable() {
    if (typeof $ !== 'undefined' && $.fn.DataTable) {
        return $('#resoursepersontable').DataTable({
            ordering: false,
            pageLength: 10,
            lengthMenu: [[10, 20, 50, -1],[10, 20, 50, "All"]],
            dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
                 '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
                 'rtip',
            buttons:[
                {
                    extend: 'excelHtml5',
                    text: '<i class="fa fa-file-excel-o"></i> Excel',
                    title: 'Resource Persons List Exported',
                    className: 'btn btn-success btn-sm mb-3',
                    exportOptions: {
                        columns: ':visible:not(.noExport)'
                    }
                }
            ]
        });
    }
    return null;
}

$(document).ready(function () {
    var rptable = initializeDataTable();

    $('.select2-search').select2({
        placeholder: "Select Program",
        allowClear: false,
        width: '100%'
    });

    $("#resetButton").on("click", customReset);

    $("#menu-toggle").click(function (e) {
        e.preventDefault();
        $("#wrapper").toggleClass("toggled");
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

    $("#financialyear").change(function () {
        var fy = $("#financialyear").val();
        $('#programs, #phaseno').empty().append($('<option></option>').attr("value", "").text("Select"));
        if (fy) {
            var fystart = fy.split("##")[0];
            var fyend = fy.split("##")[1];

            $.ajax({
                type: "POST",
                url: API.listFinancialYear,
                data: { fystart: fystart, fyend: fyend },
                success: function (data) {
                    if (data) {
                        data.forEach(function (x) {
                            $('#programs').append($('<option></option>').attr("value", x[0]).text(x[1]));
                        });
                    }
                },
                error: handleAjaxError
            });
        }
    });

    $('#programs').change(function () {
        $('#phaseno').empty().append($('<option></option>').attr("value", "").text("Select"));
        if ($('#programs').val()) {
            $.ajax({
                type: "POST",
                url: API.listPhases,
                data: { programcode: $('#programs').val() },
                success: function (data) {
                    if (data) {
                        data.forEach(function (x) {
                            $('#phaseno').append($('<option></option>').attr("value", x[0]).text(x[1]));
                        });
                    }
                },
                error: handleAjaxError
            });
        }
    });

    $('#phaseno').change(function () {
        if ($('#phaseno').val()) {
            $.ajax({
                type: "POST",
                url: API.listResourcePersonPhase,
                data: { phaseid: $('#phaseno').val() },
                success: function (data) {

                    // Clear and destroy existing DataTable instance before repopulating
                    if (rptable !== null) {
                        rptable.clear().destroy();
                    }

                    if (data && data.length > 0) {
                        let tableBody = $('#resoursepersontable tbody');
                        tableBody.empty();
                        data.forEach(function (x) {
                            var rd = "<tr>" +
                                "<td><input type='checkbox' class='form-check-input' value='" + x[0] + "' name='resourceperson'" + (x[8] ? " checked" : "") + " /></td>" +
                                "<td>" + x[2] + "</td>" +
                                "<td>" + x[1] + "</td>" +
                                "<td>" + x[3] + "</td>" +
                                "<td>" + x[4] + '<br>(' + x[5] + ')' + "</td>" +
                                "<td>" + x[6] + "</td>" +
                                "<td>" + x[7] + "</td>" +
                                "</tr>";
                            tableBody.append(rd);
                        });
                        $('#tablediv').show();
                    } else {
                        $('#tablediv').hide();
                        showModalAlert('No data found');
                    }

                    // Re-initialize DataTable with newly appended rows
                    rptable = initializeDataTable();
                },
                error: handleAjaxError
            });
        }
    });

    $("#mprogramfid").submit(function (e) {
        e.preventDefault();

        // Get the value of the dropdowns (Financial Year, Program, Phase)
        var formData =[
            { name: 'financialyear', value: $('#financialyear').val() },
            { name: 'programcode', value: $('#programs').val() },
            { name: 'phaseid', value: $('#phaseno').val() }
        ];

        var checkedBoxes = rptable.$('input[name="resourceperson"]:checked');

        if (checkedBoxes.length === 0) {
            showModalAlert("Please select at least one resource person");
            return false;
        }

        // Push the checked values into the formData array
        checkedBoxes.each(function () {
            formData.push({
                name: 'resourceperson',
                value: $(this).val()
            });
        });

        // Submit via Ajax
        $.ajax({
            type: "POST",
            url: API.mapResourcePerson,
            data: formData,
            success: function (data) {
                if (data === "2") {
                    showModalAndReload("Successfully Saved!!!");
                } else {
                    showModalAlert("Save Failed!!!");
                }
            },
            error: handleAjaxError
        });
    });
});