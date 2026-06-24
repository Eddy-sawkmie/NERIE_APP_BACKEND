function showMessageModal(message, title = 'Message') {
    $('#messageModalLabel').text(title);
    $('#messageModalBody').html(message);
    $('#messageModal').modal('show');
}

function showMessageModalAndReload(message, title = 'Success') {
    showMessageModal(message, title);
    $('#messageModal').one('hidden.bs.modal', function () {
        window.location.reload();
    });
}

$(document).ready(function() {
    $('.feedback-btn').on('click', function() {
        const subcode = $(this).data('scode');
        const fcode = $(this).data('fcode');

        $('#feedbacksubjectcode').val(subcode);
        $('#feedbackfacultyid').val(fcode);
    });

    $('#studentfeedbackform').on('submit', function(e) {
        handleSubmit(e);
    });
});


const handleSubmit = (e) => {
    e.preventDefault();

    let subjectcode = $('#feedbacksubjectcode').val();
    let studentid = $('#feedbackstudentid').val();
    let facultyid = $('#feedbackfacultyid').val();
    let feedback = $('#formfeedbackinput').val();
    let entrydate = new Date().toISOString();
    const payload = JSON.stringify({ feedback, subjectcode, studentid, facultyid, entrydate });

    fetch(API.saveSubjectFeedback, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: payload
    })
    .then(res => {
        $('#feedbackModal').modal('hide');

        $('#feedbackModal').one('hidden.bs.modal', function() {
            if (res.ok) {
                showMessageModalAndReload('Feedback submitted successfully!');
            } else {
                res.text().then(text => {
                    const errorMessage = text || 'An unknown error occurred.';
                    showMessageModal(`Submission Failed: ${errorMessage}`, 'Error');
                });
            }
        });
    })
    .catch(err => {
        $('#feedbackModal').modal('hide');

        $('#feedbackModal').one('hidden.bs.modal', function() {
            showMessageModal(`An unexpected error occurred: ${err.message}`, 'Error');
        });
    });
};