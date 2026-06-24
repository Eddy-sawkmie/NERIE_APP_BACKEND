$(document).ready(function () {
    const assignmentTable = $('#submittedassignmenttable').DataTable({
        retrieve: true,
        bDestroy: true,
        ordering: false,
        pageLength: 10,
        lengthMenu: [[10, 20, 50, 100, -1], [10, 20, 50, 100, "All"]],
        language: {
            emptyTable: "No students have submitted their assignment yet."
        },
        dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
             'rtip',
        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                title: 'Student Assignment Marks',
                className: 'btn btn-success btn-sm mb-3',
                exportOptions: {
                    columns: ':visible:not(.noExport)',
                    format: {
                        body: function (data, row, column, node) {
                            if (column === 4) {
                                var input = $(node).find('input[name="assignmentmarks"]');
                                return input.length ? input.val() : '';
                            }
                            return data;
                        }
                    }
                }
            }
        ]
    });

    $("#stuasstform").submit(function (e) {
        e.preventDefault();

        let isValid = true;
        const formData = new FormData();
        const firstInput = $('input[name="assignmentmarks"]').first();
        const fullMark = parseFloat(firstInput.attr('max')) || 100;

        assignmentTable.rows().every(function () {
            const rowNode = this.node();
            const $row = $(rowNode);
            const markInput = $row.find('input[name="assignmentmarks"]');
            const enteredMark = markInput.val().trim();
            const studentAssignmentId = $row.find('input[name="stdid"]').val();

            if (enteredMark !== '') {
                const enteredMarkNum = parseFloat(enteredMark);

                if (isNaN(enteredMarkNum) || enteredMarkNum > fullMark || enteredMarkNum < 0) {
                    Notiflix.Notify.Failure(`Invalid mark for student. Must be 0 - ${fullMark}.`);
                    markInput.addClass('is-invalid').focus();
                    isValid = false;
                    return false;
                }
                markInput.removeClass('is-invalid');
                formData.append('stdids', studentAssignmentId);
                formData.append('assignmentmarks', enteredMark);

            } else if (studentAssignmentId && studentAssignmentId !== '') {
                formData.append('idsToDelete', studentAssignmentId);
            }
        });

        if (!isValid) return;

        if (!formData.has('stdids') && !formData.has('idsToDelete')) {
            Notiflix.Notify.Info("No changes detected to save.");
            return;
        }

        Notiflix.Loading.Standard('Saving marks...');
        $.ajax({
            type: "POST",
            url: API.saveStudentAssignmentMarks,
            data: formData,
            processData: false,
            contentType: false,
            success: function (data) {
                Notiflix.Loading.Remove();
                if (data === '-1') {
                    Notiflix.Notify.Failure("Unable to save. Please try again.");
                } else {
                    Notiflix.Notify.Success("Marks saved successfully.");
                    setTimeout(() => window.location.reload(), 1200);
                }
            },
            error: function (jqXHR, textStatus) {
                Notiflix.Loading.Remove();
                Notiflix.Notify.Failure("Server error: " + textStatus);
            }
        });
    });

    $('#submittedassignmenttable').on('input', 'input[name="assignmentmarks"]', function() {
        const $input = $(this);
        const max = parseFloat($input.attr('max'));
        const val = parseFloat($input.val());
        if (!isNaN(val) && val > max) {
            Notiflix.Notify.Warning('Mark cannot exceed full mark of ' + max);
            $input.val(max);
        }
    });
});