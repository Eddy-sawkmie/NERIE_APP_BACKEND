// =================================================================
// MODAL HELPER FUNCTIONS (BS5 Stacking Fix included)
// =================================================================
function showModalAlert(message, title = 'Message') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);
    
    // Updated to use the sleek pill button
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
    // Populate Programs based on Financial Year
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
                    
                    // Truncate long program names in the dropdown to keep UI clean
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

    // Populate Phases based on selected Program
    $('#programs').change(function () {
        $('#phaseid').empty();
        $('#phaseid').append($('<option></option>').attr("value", "").text("-- Select Phase --").prop('disabled', true).prop('selected', true));

        if ($('#programs').val() && $('#programs').val() !== "-1") {
            callCustomAjax(API.getPhasesBasedOnProgram, "programcode=" + $('#programs').val(), function (data) {
                if (data) {
                    data.forEach(function (x) {
                        $('#phaseid').append($('<option></option>').attr("value", x[0]).text(x[1]));
                    });
                }
            });
        }
    });

    // Validation check before opening Program dropdown
    $("#programs").on('mousedown', function (e) {
        if (!$("#finyear").val()) {
            e.preventDefault();
            showModalAlert("Please select a Financial Year first.", "Validation Error");
            $("#finyear").focus();
        }
    });
});

// =================================================================
// FORM SUBMISSION & VALIDATION
// =================================================================

function getlistreportfunc() {
    
    if (!$("#reporttype").val() || $("#reporttype").val() === "-1") {
        showModalAlert("Please select the type of report you want to generate.", "Missing Information");
        $("#reporttype").focus();
        return false;
    }

    if (!$("#finyear").val()) {
        showModalAlert("Please select a Financial Year.", "Missing Information");
        $("#finyear").focus();
        return false;
    }

    if (!$('#programs').val() || $('#programs').val() === "-1") {
        showModalAlert("Please select a Program.", "Missing Information");
        $("#programs").focus();
        return false;
    }

    if (!$("#phaseid").val() || $("#phaseid").val() === "-1") {
        showModalAlert("Please select a Phase.", "Missing Information");
        $("#phaseid").focus();
        return false;
    }

    // Add visual loading state to the button
    const submitBtn = $('.submitbtn');
    const originalText = submitBtn.html();
    submitBtn.prop('disabled', true).html('<i class="fas fa-circle-notch fa-spin me-2"></i> Generating...');

    var fystart = $("#finyear").val().split("##")[0];
    var fyend = $("#finyear").val().split("##")[1];

    var url = API.printReportLA + "?status=" + $("#reporttype").val() + "&phaseid=" + $("#phaseid").val() + "&fystart=" + fystart + "&fyend=" + fyend;
    
    // Slight delay to allow UI to update, then open report and reset button
    setTimeout(() => {
        window.open(url, '_blank');
        submitBtn.prop('disabled', false).html(originalText);
    }, 500);
}

// Event listener mapping for custom data-functions
document.addEventListener('DOMContentLoaded', function () {
    var submitButtons = document.querySelectorAll(".submitbtn");

    submitButtons.forEach(function(button) {
        button.addEventListener("click", function(event) {
            event.preventDefault();

            var functionName = button.getAttribute("data-function");

            if (typeof window[functionName] === "function") {
                window[functionName]();
            } else {
                console.error(`Function ${functionName} is not defined`);
            }
        });
    });
});