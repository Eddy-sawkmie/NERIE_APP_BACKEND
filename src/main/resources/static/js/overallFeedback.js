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
    const phid = /*[[${phaseid}]]*/ null;
    const viewmode = /*[[${view}]]*/ false;
    const loader = document.getElementById('loader');

    if (viewmode === true) {
        // Retrieve values from the model. Thymeleaf handles nulls and quotes.
        const q8Value = /*[[${tpfeedback.q8}]]*/ '';
        const q9Value = /*[[${tpfeedback.q9}]]*/ '';
        const q10Value = /*[[${tpfeedback.q10}]]*/ '';
        const q11Value = /*[[${tpfeedback.q11}]]*/ '';
        const q12Value = /*[[${tpfeedback.q12}]]*/ '';

        $('#q8').val(q8Value);
        $('#q9').val(q9Value);
        $('#q10').val(q10Value);
        $('#q11').val(q11Value);
        $('#q12').val(q12Value);

        $('#tpfeedback').find('input, textarea, select').prop('disabled', true);

        $('.btn-submit').hide();
    }

    $("#tpfeedback").submit(function (event) {
        event.preventDefault();

        if (loader) loader.style.display = 'block';

        const formData = new FormData(this);
        const postUrl =  '/participant/feedback/save-overall-feedback';
        const redirectUrl = '/program/my-programs';

        $.ajax({
            type: "POST",
            url: postUrl + "?phid=" + phid,
            data: formData,
            processData: false,
            contentType: false,
            success: function (data) {
                if (loader) loader.style.display = 'none';
                if (data === '-1') {
                    showModalAlert("Error. Something went wrong. Please try again.", "Error");
                } else {
                    showModalAlert(
                        "Feedback Successfully Uploaded.",
                        "Success",
                        function() {
                            window.location.href = redirectUrl;
                        }
                    );
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                if (loader) loader.style.display = 'none';
                const errorMessage = "An error occurred: " + textStatus + " - " + errorThrown;
                showModalAlert(errorMessage, "Request Failed");
                console.error("AJAX Error:", textStatus, errorThrown);
            }
        });
    });
});