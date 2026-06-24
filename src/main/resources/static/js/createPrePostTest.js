$(document).ready(function () {
    $('.select2-search').select2({
        placeholder: "Select",
        allowClear: false,
        width: '100%'
    });

    function showFeedbackModal(message, title = 'Message', onHiddenCallback = null) {
        $('#feedbackModalLabel').text(title);
        $('#feedbackModalBody').html(message);
        $('#feedbackModal .modal-footer').html(
            '<button type="button" class="btn btn-primary" data-bs-dismiss="modal">OK</button>'
        );

        var myModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('feedbackModal'));

        var modalEl = document.getElementById('feedbackModal');
        var newModalEl = modalEl.cloneNode(true);
        modalEl.parentNode.replaceChild(newModalEl, modalEl);

        if (onHiddenCallback && typeof onHiddenCallback === 'function') {
            newModalEl.addEventListener('hidden.bs.modal', function() {
                onHiddenCallback();
            }, { once: true });
        }
        myModal = bootstrap.Modal.getOrCreateInstance(newModalEl);
        myModal.show();
    }

    function showConfirmModal(message, title, onConfirm, onCancel) {
        $('#feedbackModalLabel').text(title);
        $('#feedbackModalBody').html(message);
        $('#feedbackModal .modal-footer').html(
            '<button type="button" id="modalCancelButton" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>' +
            '<button type="button" id="modalConfirmButton" class="btn btn-success">Proceed</button>'
        );

        var myModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('feedbackModal'));
        myModal.show();

        $('#modalCancelButton').off('click').on('click', function() {
            if (onCancel && typeof onCancel === 'function') {
                onCancel();
            }
        });

        $('#modalConfirmButton').off('click').on('click', function() {
            myModal.hide();
            document.getElementById('feedbackModal').addEventListener('hidden.bs.modal', function() {
                if (onConfirm && typeof onConfirm === 'function') {
                    onConfirm();
                }
            }, { once: true });
        });
    }

    function showPromptModal(message, title, onConfirm, availableQuestions = null) {
        $('#feedbackModalLabel').text(title);

        let modalBodyHtml = `<p>${message}</p>`;

        if (availableQuestions) {
            modalBodyHtml += `<p class="text-muted small">Enter a number between 1 and ${availableQuestions}.</p>`;
        }

        modalBodyHtml +=
            `<input type="number" id="modalPromptInput" class="form-control"
                    placeholder="Enter a positive number" min="1"
                    ${availableQuestions ? `max="${availableQuestions}"` : ''}>` +
            '<div id="modalPromptError" class="text-danger mt-2" style="display:none;"></div>';

        $('#feedbackModalBody').html(modalBodyHtml);

        $('#feedbackModal .modal-footer').html(
            '<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>' +
            '<button type="button" id="modalPromptConfirmButton" class="btn btn-primary">OK</button>'
        );

        var myModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('feedbackModal'));

        document.getElementById('feedbackModal').addEventListener('shown.bs.modal', function () {
            $('#modalPromptInput').focus();
        }, { once: true });

        myModal.show();

        $('#modalPromptConfirmButton').off('click').on('click', function() {
            const value = $('#modalPromptInput').val();
            const numValue = parseInt(value, 10);
            const errorDiv = $('#modalPromptError');

            if (!value || isNaN(numValue) || numValue <= 0) {
                errorDiv.text('Please enter a valid positive number.').show();
                return;
            }
            if (availableQuestions && numValue > availableQuestions) {
                errorDiv.text(`Only ${availableQuestions} questions are available. Please enter a smaller number.`).show();
                return;
            }

            myModal.hide();

            document.getElementById('feedbackModal').addEventListener('hidden.bs.modal', function() {
                if (onConfirm && typeof onConfirm === 'function') {
                    onConfirm(value);
                }
            }, { once: true });
        });

        // Reset on close
        document.getElementById('feedbackModal').addEventListener('hidden.bs.modal', function() {
            $('#modalPromptError').hide().text('');
            $("#randomizeFlag").val("false");
        });
    }

    function validateProgramSelection() {
        if (!$("#activityfinancialyear").val() || $("#activityfinancialyear").val() === "Select") {
            showFeedbackModal("Please select a Financial Year first.", "Message");
            return false;
        }
        if (!$("#activityprograms").val()) {
            showFeedbackModal("Please select a Program Name first.", "Message");
            return false;
        }
        if (!$("#activityphaseno").val()) {
            showFeedbackModal("Please select a Phase No. first.", "Message");
            return false;
        }
        return true; // All fields are valid
    }

    function validateLengthOnBlur(inputId, maxLength, fieldName) {
        const input = document.getElementById(inputId);
        const errorDiv = document.getElementById(inputId + "-error");
        if (!input || !errorDiv) return;
        input.addEventListener("blur", function () {
            const value = input.value.trim();
            if (value.length > maxLength) {
                errorDiv.innerText = `${fieldName} must not exceed ${maxLength} characters.`;
                errorDiv.style.display = "block";
                input.classList.add("is-invalid");
            } else {
                errorDiv.innerText = "";
                errorDiv.style.display = "none";
                input.classList.remove("is-invalid");
            }
        });
    }

    validateLengthOnBlur("questionname", 100, "Question");
    validateLengthOnBlur("newCategory", 30, "Category");
    for (let i = 1; i <= 6; i++) {
        validateLengthOnBlur(`option${i}`, 50, `Option ${i}`);
    }

    const questionTypeSelect = document.getElementById('questiontype');
    if (questionTypeSelect) {
        questionTypeSelect.addEventListener("change", function () {
            const newCategoryWrapper = document.getElementById('newCategoryWrapper');
            const newCategoryInput = document.getElementById('newCategory');
            if (this.value === "Other") {
                newCategoryWrapper.style.display = "flex";
                newCategoryInput.setAttribute("required", "true");
            } else {
                newCategoryWrapper.style.display = "none";
                newCategoryInput.removeAttribute("required");
                newCategoryInput.value = "";
            }
        });
    }

    function toggleGoogleFormLink() {
        $("#formActionButtons").show();

        if ($("#testtypelink").is(":checked")) {
            $("#googleFormLinkDiv").show();
            $("#testlink").attr("required", "required");

            $("#questionsTableDiv").hide();
            $("#randomButton").hide();

            $('#phasepreposttestform')[0].reset();

        } else {
            $("#googleFormLinkDiv").hide();
            $("#testlink").removeAttr("required");

            $("#questionsTableDiv").show();
            $("#randomButton").show();
        }
    }
    $("input[name='testtype']").change(toggleGoogleFormLink);
    toggleGoogleFormLink();

    $("#clearFormButton").on("click", function() {
        $("#testlink").val("");
    });

    const maxQuestions = 25;
    document.querySelectorAll(".question-checkbox").forEach(checkbox => {
        checkbox.addEventListener("change", function () {
            const selectedCount = document.querySelectorAll(".question-checkbox:checked").length;
            if (selectedCount > maxQuestions) {
                showFeedbackModal(`You can only select up to ${maxQuestions} questions.`, "Selection Limit Reached");
                this.checked = false;
            }
        });
    });

    $("#submitButton").on("click", function() {
        $("#randomizeFlag").val("false");
    });


    $("#randomButton").on("click", function () {
        $("#randomizeFlag").val("true");

        if (validateProgramSelection()) {
            showConfirmModal(
                "The questions selected will be randomized. Do you want to proceed?",
                "Confirm Randomization",
                () => {
                    $("#phasepreposttestform").trigger("submit");
                },
                () => {
                    $("#randomizeFlag").val("false");
                }
            );
        } else {
            $("#randomizeFlag").val("false");
        }
    });

    $("#t_preposttestquestions").on("submit", function(e) {

    e.preventDefault();

    let firstErrorElement = null;

    // clear previous errors
    $(".invalid-feedback").hide().text("");
    $(".form-control").removeClass("is-invalid");

    // check checked options
    $("input[name='correctans']:checked").each(function () {

        let optionNumber = $(this).val();
        let inputField = $("#option" + optionNumber);
        let errorField = $("#option" + optionNumber + "-error");

        if (!inputField.val().trim()) {

            errorField
                .text("Selected correct answer cannot be blank")
                .show();

            inputField.addClass("is-invalid");

            if (!firstErrorElement) {
                firstErrorElement = inputField;
            }
        }
    });

    // OPTIONAL: ensure at least one correct answer selected
    if ($("input[name='correctans']:checked").length === 0) {

        showFeedbackModal(
            "Please select at least one correct answer.",
            "Validation Error"
        );

        return;
    }

    // if error found → stop submit
    if (firstErrorElement) {

        showFeedbackModal(
            "Please fill text for selected correct answer.",
            "Validation Error",
            () => {

                $('html, body').animate({
                    scrollTop: firstErrorElement.offset().top - 150
                }, 400);

                firstErrorElement.focus();

            }
        );

        return;
    }

    // existing error check
    let existingError = null;

    $(this).find(".invalid-feedback").each(function () {

        if ($(this).css("display") !== "none" &&
            $(this).text().trim() !== "") {

            existingError = $(this);
            return false;
        }
    });

    if (existingError) {

        showFeedbackModal(
            "Please correct the highlighted field(s).",
            "Message"
        );

        return;
    }

    // submit via ajax
    $.ajax({

        type: "POST",
        url: API.savePrePostTestQuestions,
        data: $(this).serialize(),

        success: (data) =>
            showFeedbackModal(data, "Success",
                () => window.location.reload()),

        error: (xhr) => {

            const errorMsg =
                xhr.status === 400
                ? xhr.responseText
                : "Unexpected error occurred";

            showFeedbackModal(errorMsg, "Error");
        }
    });

});

    // === UPDATED AJAX CALLS TO TRIGGER SELECT2 ===
    $("#activityfinancialyear").change(function () {
        var fy = $(this).val();

        // Clear visually using trigger
        $('#activityprograms, #activityphaseno').empty().append($('<option></option>').attr("value", "").text("Select")).trigger('change.select2');

        if (fy && typeof callCustomAjax === 'function') {
            var [fystart, fyend] = fy.split("##");
            callCustomAjax(API.listFinancialYear, `fystart=${fystart}&fyend=${fyend}`, data => {
                if (data) {
                    data.forEach(x => $('#activityprograms').append($('<option></option>').attr("value", x[0]).text(x[1])));
                    // Update Select2 visually with new data
                    $('#activityprograms').trigger('change.select2');
                }
            });
        }
    });

    $('#activityprograms').change(function() {

        // Clear visually using trigger
        $('#activityphaseno').empty().append($('<option></option>').attr("value", "").text("Select")).trigger('change.select2');

        if ($(this).val() && typeof callCustomAjax === 'function') {
            callCustomAjax(API.phasesList, `programcode=${$(this).val()}`, data => {
                if (data) {
                    data.forEach(x => $('#activityphaseno').append($('<option></option>').attr("value", x[0]).text(x[1])));
                    // Update Select2 visually with new data
                    $('#activityphaseno').trigger('change.select2');
                }
            });
        }
    });

    $("#phasepreposttestform").submit(function(e) {
        e.preventDefault();
        $("#activityProgcode").val($('#activityprograms').val());
        $("#activityPhaseID").val($('#activityphaseno').val());

        if (!validateProgramSelection()) {
            return;
        }

        const randomizeFlag = $("#randomizeFlag").val();
        const selectedTestType = $("input[name='testtype']:checked").val();
        const googleFormLink = $("#testlink").val();

        if (selectedTestType === "LINK") {
            if (googleFormLink === "") {
                showFeedbackModal("Please enter a valid Google Form link.", "Message");
                return;
            }
            submitForm();
            return;
        }

        if (selectedTestType === "APP" && randomizeFlag === "false") {
            const selectedQuestionsCount = document.querySelectorAll(".question-checkbox:checked").length;
            if (selectedQuestionsCount === 0) {
                showFeedbackModal("Please select at least one question from the list.", "Selection Required");
                return;
            }
        }

        if (randomizeFlag === "true") {
            const availableQuestions = $('#questionsTable tbody tr').length;

            if (availableQuestions === 0) {
                showFeedbackModal("There are no questions available to randomize.", "Error");
                $("#randomizeFlag").val("false");
                return;
            }

            showPromptModal(
                "Enter the number of random questions you want:",
                "Random Questions",
                (numQuestions) => {
                    $("#randomizeCount").val(numQuestions);
                    submitForm(true);
                },
                availableQuestions
            );
        } else {
            submitForm(false);
        }
    });

    function submitForm(isRandom = false) {
        const selectedTestType = $("input[name='testtype']:checked").val();
        const googleFormLink = $("#testlink").val();

        const randomizeFlagValue = isRandom ? 'true' : 'false';

        const submitUrl = `${API.savePrePostTest}?testtype=${selectedTestType}&testlink=${encodeURIComponent(googleFormLink)}&randomizeFlag=${randomizeFlagValue}`;

        $.ajax({
            type: "POST",
            url: submitUrl,
            data: $("#phasepreposttestform").serialize(),

            success: (data) => {
                showFeedbackModal(data, "Success", () => window.location.reload());
            },

            error: (jqXHR, textStatus, errorThrown) => {
                if (jqXHR.status === 409) {
                    showFeedbackModal(jqXHR.responseText, "Message");
                } else {
                    const errorMessage = jqXHR.responseText || `An unexpected error occurred. Please try again later.`;
                    const errorTitle = `Error: ${jqXHR.status}`;
                    showFeedbackModal(errorMessage, errorTitle);
                }
            }
        });
    }
});