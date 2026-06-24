function showModalAlert(message, title = 'Message', onOkCallback) {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);

    const okButton = $('<button type="button" class="btn btn-primary">OK</button>');

    okButton.on('click', function() {
        $('#feedbackModal').modal('hide');
        if (typeof onOkCallback === 'function') {
            setTimeout(onOkCallback, 200);
        }
    });

    $('#feedbackModal .modal-footer').empty().append(okButton);
    $('#feedbackModal').modal('show');
}

$(document).ready(function () {
    const loader = document.getElementById('loader');

    $("#prePostTestForm").submit(function (ed) {
        ed.preventDefault();
        $('.form-group').removeClass('has-error');

        const unansweredQuestions = [];

        $('.form-group').each(function() {
            const $questionGroup = $(this);
            const hasRadios = $questionGroup.find('input[type="radio"]').length > 0;
            const hasCheckboxes = $questionGroup.find('input[type="checkbox"]').length > 0;
            let isQuestionAnswered = false;

            if (hasRadios) {
                if ($questionGroup.find('input[type="radio"]:checked').length > 0) {
                   isQuestionAnswered = true;
                }
            } else if (hasCheckboxes) {
                if ($questionGroup.find('input[type="checkbox"]:checked').length > 0) {
                    isQuestionAnswered = true;
                }
            }

            if (!isQuestionAnswered) {
                unansweredQuestions.push(this);
            }
        });

        if (unansweredQuestions.length > 0) {
            showModalAlert(
                'Please answer all questions. Unanswered questions will be highlighted in red.',
                'Incomplete Form',
                function() {
                    const $unanswered = $(unansweredQuestions);
                    $unanswered.addClass('has-error');

                    $('html, body').animate({
                        scrollTop: $unanswered.first().offset().top - 20
                    }, 500);
                }
            );
            return;
        }

        if (loader) loader.style.display = 'block';

        const formDataed = new FormData(this);
        const aggregatedData = {};
        formDataed.forEach((value, key) => {
            if (!aggregatedData[key]) {
                aggregatedData[key] = [];
            }
            aggregatedData[key].push(value);
        });
        const jsonData = JSON.stringify(aggregatedData);

        $.ajax({
            type: "POST",
            url: API.submitAnswers + "?testid="+$('#testid').val()+"&testtype="+$('#testtype').val(),
            data: jsonData,
            contentType: "application/json",
            processData: false,
            success: function (data) {
                if (loader) loader.style.display = 'none';
                showModalAlert(data, 'Success', function() {
                   window.location.href = API.myProgramList;
                });
            },
            error: function (xhr, status, error) {
                if (loader) loader.style.display = 'none';
                console.error("Error: " + error);
                showModalAlert("An error occurred while saving the Test. Please try again.", "Error");
            }
        });
    });
});