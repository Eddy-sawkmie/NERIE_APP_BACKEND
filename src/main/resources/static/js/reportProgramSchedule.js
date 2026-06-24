// =================================================================
// MODAL HELPER FUNCTIONS (BS5 Stacking Fix included)
// =================================================================
function showModalAlert(message, title = 'Message') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);
    
    // Sleek pill button design for modal
    $('#feedbackModalFooter').html(
        '<button type="button" class="btn btn-sleek btn-sleek-primary px-4" data-bs-dismiss="modal">Got it</button>'
    );

    const feedbackModalEl = document.getElementById('feedbackModal');
    if (feedbackModalEl.parentNode !== document.body) {
        document.body.appendChild(feedbackModalEl);
    }

    const myModal = bootstrap.Modal.getOrCreateInstance(feedbackModalEl);
    myModal.show();
}

// =================================================================
// MAIN LOGIC
// =================================================================

$(document).ready(function () {

    // Cascade: Financial Year -> Programs
    $("#finyear").change(function () {
        var fy = $("#finyear").val();
        $('#programs, #phaseid').empty();
        $('#programs').append($('<option></option>').attr("value", "-1").text("-- Select Program --").prop('disabled', true).prop('selected', true));
        $('#phaseid').append($('<option></option>').attr("value", "").text("-- Select Phase --").prop('disabled', true).prop('selected', true));

        if (fy) {
            var fystart = fy.split("##")[0];
            var fyend = fy.split("##")[1];

            callCustomAjax(API.getFYCourseList, "fystart=" + fystart + "&fyend=" + fyend, function (data) {
                if (data) {
                    data.forEach(function (x) {
                        $('#programs').append($('<option></option>').attr("value", x[0]).text(x[1]).attr("title", x[1]));
                    });
                    // Truncate text to keep UI sleek
                    $('#programs > option').text(function (i, text) {
                        if (text.length > 80) {
                            return text.substr(0, 80) + '...';
                        }
                        return text;
                    });
                }
            });
        }
    });

    // Cascade: Programs -> Phases
    $('#programs').change(function () {
        $('#phaseid').empty();
        $('#phaseid').append($('<option></option>').attr("value", "").text("-- Select Phase --").prop('disabled', true).prop('selected', true));

        var programVal = $('#programs').val();
        if (programVal && programVal !== "-1") {
            callCustomAjax(API.getPhasesBasedOnProgram, "programcode=" + programVal, function (data) {
                if (data) {
                    data.forEach(function (x) {
                        $('#phaseid').append($('<option></option>').attr("value", x[0]).text(x[1]));
                    });
                }
            });
        }
    });

    // Prevent program click before FY selection
    $("#programs").on('mousedown', function (e) {
        if (!$("#finyear").val()) {
            e.preventDefault();
            showModalAlert("Please select a Financial Year first.", "Validation Error");
            $("#finyear").focus();
        }
    });
});

// =================================================================
// FORM SUBMISSION
// =================================================================

document.addEventListener('DOMContentLoaded', function () {
    const submitButton = document.getElementById('submitBtn');
    if (submitButton) {
        submitButton.addEventListener('click', function (e) {
            e.preventDefault();
            getlistreportfunc();
        });
    }
});

function getlistreportfunc() {
    var programVal = $("#programs").val();
    var phaseVal = $("#phaseid").val();

    // Validation
    if (!$("#finyear").val()) {
        showModalAlert("Please select a Financial Year.", "Missing Information");
        $("#finyear").focus();
        return false;
    }
    if (!programVal || programVal === "-1") {
        showModalAlert("Please select a Program.", "Missing Information");
        $("#programs").focus();
        return false;
    }
    if (!phaseVal || phaseVal === "") {
        showModalAlert("Please select the Phase of the program.", "Missing Information");
        $("#phaseid").focus();
        return false;
    }

    // Add visual loading state to the button
    const submitBtn = $('#submitBtn');
    const originalText = submitBtn.html();
    submitBtn.prop('disabled', true).html('<i class="fas fa-circle-notch fa-spin me-2"></i> Generating...');

    // Generate Report URL
    var url = API.printScheduleReport + "?phaseid=" + phaseVal;
    
    // Timeout to allow button to re-render
    setTimeout(() => {
        window.open(url, '_blank');
        submitBtn.prop('disabled', false).html(originalText);
    }, 500);
}