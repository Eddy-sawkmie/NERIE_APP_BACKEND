/*
 * HELPER FUNCTIONS
 */
function showModalAlert(message, title = 'Message', onModalClose = null) {
    document.getElementById('feedbackModalLabel').textContent = title;
    document.getElementById('feedbackModalBody').innerHTML = message;

    const feedbackModalEl = document.getElementById('feedbackModal');

    if (feedbackModalEl.parentNode !== document.body) {
        document.body.appendChild(feedbackModalEl);
    }

    const modalInstance = bootstrap.Modal.getOrCreateInstance(feedbackModalEl);

    const newFeedbackModalEl = feedbackModalEl.cloneNode(true);
    feedbackModalEl.parentNode.replaceChild(newFeedbackModalEl, feedbackModalEl);

    const refreshedModalInstance = bootstrap.Modal.getOrCreateInstance(newFeedbackModalEl);

    if (onModalClose && typeof onModalClose === 'function') {
        newFeedbackModalEl.addEventListener('hidden.bs.modal', function () {
            onModalClose();
        }, { once: true });
    }

    refreshedModalInstance.show();
}

function handleAjaxError(jqXHR, textStatus, errorThrown) {
    console.error("AJAX Error:", textStatus, errorThrown);
    showModalAlert(`An error occurred processing your request. Please try again.<br><b>Details:</b> ${errorThrown}`, "Error");
}

/*
 * MAIN LOGIC
 */
$(document).ready(function () {

    $("#add-new-sub").click(function(e) {
        e.preventDefault();
        if (API && API.redirectToQualificationSubjectManageURL) {
            window.location.href = API.redirectToQualificationSubjectManageURL;
        }
    });

    $('#add-new-qualific').click(function(e) {
        e.preventDefault();
        if (API && API.redirectToQualificationsManageURL) {
            window.location.href = API.redirectToQualificationsManageURL;
        }
    });

    $('#resetButton').click(function() {
        $('#mmapqsfid')[0].reset();
        $('#subjectdiv').html(`
            <div class="text-muted text-center mt-3 fst-italic">
                <i class="fas fa-info-circle me-1"></i> Please select a qualification above to load available subjects.
            </div>
        `);
    });

    $("#qualificationcode").change(function (e) {
        const selectedCode = $(this).val();
        const $subjectDiv = $("#subjectdiv");

        if (!selectedCode) {
            $subjectDiv.html(`
                <div class="text-muted text-center mt-3 fst-italic">
                    <i class="fas fa-info-circle me-1"></i> Please select a qualification above to load available subjects.
                </div>
            `);
            return;
        }

        $subjectDiv.html(`
            <div class="text-center text-primary mt-2 mb-2">
                <div class="spinner-border spinner-border-sm me-2" role="status"></div>
                Loading subjects...
            </div>
        `);

        $.ajax({
            type: "GET",
            url: API.getMapQualificationSubject,
            data: { qualificationcode: selectedCode },
            success: function (data) {
                if (!data || data.length === 0) {
                    $subjectDiv.html(`
                        <div class="alert alert-warning mb-0 border-0 shadow-sm text-center">
                            <i class="fas fa-exclamation-triangle me-2"></i> No subjects available for this qualification.
                        </div>
                    `);
                    return;
                }

                let htmlOutput = '<div class="row g-3">';

                for (let i = 0; i < data.length; i++) {
                    const subjectCode = data[i][0];
                    const subjectName = data[i][1];
                    const isChecked = data[i][2] ? 'checked="checked"' : '';

                    htmlOutput += `
                        <div class="col-md-6 col-lg-4">
                            <div class="form-check modern-form-check border rounded p-2 shadow-sm bg-white h-100 d-flex align-items-center">
                                <input class="form-check-input ms-1 me-2" style="cursor: pointer;" type="checkbox" value="${subjectCode}" name="subjects" id="sub_${subjectCode}" ${isChecked}>
                                <label class="form-check-label w-100 mb-0" style="cursor: pointer; user-select: none;" for="sub_${subjectCode}">
                                    ${subjectName}
                                </label>
                            </div>
                        </div>
                    `;
                }

                htmlOutput += '</div>';
                $subjectDiv.html(htmlOutput);
            },
            error: handleAjaxError
        });
    });

    $("#mmapqsfid").submit(function (e) {
        e.preventDefault();

        if (!$("#qualificationcode").val()) {
            showModalAlert("Please select a Qualification.", "Validation Error");
            $("#qualificationcode").focus();
            return false;
        }

        let isSubjectSelected = false;
        $('input[name="subjects"]').each(function () {
            if (this.checked) isSubjectSelected = true;
        });

        if (!isSubjectSelected) {
            showModalAlert("Please select at least one subject to map.", "Validation Error");
            return false;
        }

        const $submitBtn = $("#submit");
        const originalText = $submitBtn.html();
        $submitBtn.prop('disabled', true).html('<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Saving...');

        $.ajax({
            type: "POST",
            url: API.saveMapQualificationSubject,
            data: $(this).serialize(),
            success: function (data) {
                $submitBtn.prop('disabled', false).html(originalText);

                if (data.toString().trim() === "2") {
                    showModalAlert("Mapping Successfully Saved!", "Success", function() {
                        window.location.reload();
                    });
                } else {
                    showModalAlert("Save Failed! An unexpected response was received.", "Error");
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                $submitBtn.prop('disabled', false).html(originalText);
                handleAjaxError(jqXHR, textStatus, errorThrown);
            }
        });
    });
});