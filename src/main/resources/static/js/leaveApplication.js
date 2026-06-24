function showModalAlert(message, title = 'Message') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);
    $('#feedbackModal').modal('show');
}

function handleAjaxError(jqXHR, textStatus, errorThrown) {
    if (jqXHR.status === 400 && jqXHR.responseText) {
        showModalAlert(jqXHR.responseText, 'Message');
    } else {
        let errorMessage = `An unexpected error occurred: ${textStatus} - ${errorThrown}`;
        if (jqXHR.responseText) {
            errorMessage += `<br><br><strong>Details:</strong> ${jqXHR.responseText}`;
        }
        showModalAlert(errorMessage, 'Request Failed');
    }
}

$(document).ready(function () {
    
    // Optional: Initialize DataTable for the leave history if it isn't initialized elsewhere
    if ($.fn.DataTable && !$.fn.DataTable.isDataTable('#leavelist')) {
        $('#leavelist').DataTable({
            dom: '<"d-flex justify-content-between align-items-center mb-3"Bf>rt<"d-flex justify-content-between align-items-center mt-3"ip>', // Modern layout for DataTables controls
        pageLength: 10,
        lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
        language: {
            emptyTable: "<div class='text-center p-4'><i class='fas fa-folder-open fs-1 text-muted mb-3'></i><br>No Prior Student Leaves Available </div>",
            search: "_INPUT_",
            searchPlaceholder: "Search Leaves..."
        },
        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fas fa-file-excel me-1"></i> Export Excel',
                title: 'Student Leaves',
                exportOptions: { columns: ':visible:not(.noExport)' },
                className: 'btn btn-sm btn-success btn-modern' // Added modern styling
            }
        ],
            order: [[0, 'desc']] // Orders by the "Applied On" date descending by default
        });
        $('.dataTables_filter input').addClass('form-control form-control-sm modern-select d-inline-block w-auto');
    }

    const $isDayScholarCheckbox = $("#isDayScholar");
    const $nonDayScholarInfo = $("#nondayscholar-info");
    const $buildingNoInput = $("#buildingno");
    const $roomNoInput = $("#roomno");

    function toggleHostelInfo() {
        if ($isDayScholarCheckbox.is(":checked")) {
            $nonDayScholarInfo.slideUp(300); // Smoother transition
            $buildingNoInput.prop('required', false).val('');
            $roomNoInput.prop('required', false).val('');
        } else {
            $nonDayScholarInfo.slideDown(300); // Smoother transition
            $buildingNoInput.prop('required', true);
            $roomNoInput.prop('required', true);
        }
    }

    $isDayScholarCheckbox.on('change', toggleHostelInfo);

    toggleHostelInfo();

    $("#studentleaveid").submit(function (ed) {
        ed.preventDefault();

        const phno = $("#guardian-phno").val();
        if (phno.length !== 10) {
            showModalAlert("Please enter a valid 10-digit phone number.", "Validation Error");
            return false;
        }

        const leaveFromDateStr = $("#requestedfromdate").val();
        const leaveToDateStr = $("#requestedtodate").val();

        function parseDate(dateStr) {
            if (!dateStr) return null;
            const parts = dateStr.split("-");
            if (parts.length !== 3) return null;
            const [day, month, year] = parts.map(Number);
            return new Date(year, month - 1, day);
        }

        const leaveFromDate = parseDate(leaveFromDateStr);
        const leaveToDate = parseDate(leaveToDateStr);

        if (!leaveFromDate || !leaveToDate) {
             showModalAlert("Please select both 'Leave From' and 'Leave To' dates.", "Validation Error");
             return false;
        }

        if (leaveToDate < leaveFromDate) {
            showModalAlert("'Leave To' date cannot be earlier than 'Leave From' date.", "Validation Error");
            return false;
        }

        const isDayScholar = $("#isDayScholar").is(":checked");
        var formDataed = new FormData($(this)[0]);

        // Update button state visually
        const $submitBtn = $("#subbtn");
        $submitBtn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-2"></i> Submitting...');

        $.ajax({
            type: "POST",
            url: API.submitLeaveApplication + "?ds=" + isDayScholar,
            data: formDataed,
            processData: false,
            contentType: false,
            success: function (data) {
                if (data === "-1") {
                    showModalAlert("An error occurred while submitting the application. Please try again.", "Error");
                    $submitBtn.prop('disabled', false).html('<i class="fas fa-paper-plane me-2"></i> Submit Application');
                } else {
                    showModalAlert("Application submitted successfully.", "Success");
                    $('#feedbackModal').one('hidden.bs.modal', function () {
                        window.location.reload();
                    });
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                 handleAjaxError(jqXHR, textStatus, errorThrown);
                 $submitBtn.prop('disabled', false).html('<i class="fas fa-paper-plane me-2"></i> Submit Application');
            }
        });
    });

    $(".datepicker").datepicker({
        dateFormat: "dd-mm-yy",
        minDate: new Date(),
    });

    $("#leave-reason").keyup(function () {
        const maxLength = 500;
        const currentLength = $(this).val().length;
        const charsLeft = maxLength - currentLength;
        $("#charactercountdesc").html("Characters left: " + charsLeft);
    });

    $("#declarationCheckbox").change(function () {
        $("#subbtn").prop('disabled', !$(this).is(":checked"));
    });
});

function validateFile(fileInput) {
    if (fileInput.files.length > 0) {
        const fileName = fileInput.files[0].name;
        const fileExtension = fileName.split('.').pop().toLowerCase();

        if (fileExtension !== "pdf") {
            showModalAlert("Only PDF files are allowed.", "Invalid File Type");
            fileInput.value = "";
            return false;
        }
    }
    return true;
}