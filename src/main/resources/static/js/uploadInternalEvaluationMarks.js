var iatable;

$(document).ready(function () {
    function getDtConfig() {
        return {
            retrieve: true,
            bDestroy: true,
            ordering: false,
            pageLength: 10,
            lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
            language: { emptyTable: "No Students Available." },
            dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
                 '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
                 'rtip',
            buttons: [
                {
                    extend: 'excelHtml5',
                    text: '<i class="fa fa-file-excel-o"></i> Excel',
                    title: 'Internal Evaluation Marks',
                    className: 'btn btn-success btn-sm mb-3',
                    exportOptions: {
                        columns: ':visible',
                        format: {
                            body: function (data, row, column, node) {
                                if (column === 2) {
                                    var input = $(node).find('.studentmarkinput');
                                    return input.length ? input.val() : '';
                                }
                                return data;
                            }
                        }
                    }
                }
            ]
        };
    }

    iatable = $('#internalevaluationtable').DataTable(getDtConfig());

    $('#subjectcode').on('change', function() {
        const subjectCode = $(this).val();
        const testDropdown = $('#testid');

        testDropdown.html('<option value="-1">Loading tests...</option>').prop('disabled', true);
        iatable.clear().draw();
        $("#tabletitle").html('<i class="fa fa-list"></i> Internal Evaluation');
        $("#fullmark").empty();

        if (subjectCode === '-1' || subjectCode === null) {
            testDropdown.html('<option value="-1">Select Subject First</option>').prop('disabled', true);
            return;
        }

        $.ajax({
            type: "GET",
            url: API.getInternalEvaluationBySubject,
            data: { subjectcode: subjectCode },
            success: function (tests) {
                testDropdown.empty();
                if (tests && tests.length > 0) {
                    testDropdown.append('<option value="-1">Select Test</option>');
                    tests.forEach(test => {
                        const optionText = `${test[3]} - ${test[8]}`;
                        const fullMark = test[5];
                        testDropdown.append(`<option value="${test[0]}" data-fullmark="${fullMark}">${optionText}</option>`);
                    });
                    testDropdown.prop('disabled', false);
                } else {
                    testDropdown.append('<option value="-1">No tests found</option>');
                }
            },
            error: function (jqXHR, textStatus) {
                testDropdown.html('<option value="-1">Error loading tests</option>');
                showModalAlert("Error loading tests: " + textStatus, "Error");
            }
        });
    });

    $('#testid').on('change', function() {
        iatable.clear().draw();
        $("#tabletitle").html('<i class="fa fa-list"></i> Internal Evaluation');
        $("#fullmark").empty();
    });

    // Marks Submission
    $("#marksform").submit(function (e) {
        e.preventDefault();

        if ($("#subjectcode").val() === '-1' || $("#testid").val() === '-1') {
            showModalAlert("Please select both a Subject and a Test.");
            return;
        }

        let marksEnteredCount = 0;
        let isValid = true;
        const formData = new FormData();
        formData.append('subjectcode', $("#subjectcode").val());
        formData.append('testid', $("#testid").val());

        // Process all rows from the table
        iatable.rows().every(function () {
            const row = $(this.node());
            const markInput = row.find('.studentmarkinput');
            const enteredMark = markInput.val().trim();
            const studentId = row.find('input[name="studentids"]').val();
            const internalEvalId = row.find('input[name="internalevaluationids"]').val();

            if (enteredMark !== '') {
                marksEnteredCount++;
                const enteredMarkNum = parseInt(enteredMark, 10);
                const maxMark = parseInt(markInput.attr('max'), 10);

                if (isNaN(enteredMarkNum) || enteredMarkNum > maxMark) {
                    showModalAlert(`Invalid mark for student ${studentId}. Marks cannot exceed ${maxMark}.`, 'Validation Error');
                    markInput.addClass('is-invalid').focus();
                    isValid = false;
                    return false;
                }
                markInput.removeClass('is-invalid');

                formData.append('studentids', studentId);
                formData.append('studentmarks', enteredMark);
                formData.append('internalevaluationids', internalEvalId);

            } else if (internalEvalId && internalEvalId !== '' && internalEvalId !== 'null') {
                formData.append('idsToDelete', internalEvalId);
            }
        });

        if (!isValid) return;

        if (marksEnteredCount === 0 && !formData.has('idsToDelete')) {
            showModalAlert("Please enter marks for at least one student");
            return;
        }

        Notiflix.Loading.Standard('Saving marks...');
        $.ajax({
            type: "POST",
            url: API.saveInternalEvaluation,
            data: formData,
            processData: false,
            contentType: false,
            success: function (data) {
                Notiflix.Loading.Remove();
                if (data === '-1') {
                    showModalAlert("Unable to save. Please try again.", "Save Failed");
                } else {
                    showModalAlert("Marks saved successfully.", "Success", true);
                }
            },
            error: function (jqXHR, textStatus) {
                Notiflix.Loading.Remove();
                showModalAlert("Error saving: " + textStatus, "Error");
            }
        });
    });

    $(".loadstudentsbtn").on("click", loadStudents);

    $('#studentslist').on('input', '.studentmarkinput', function() {
        const markInput = $(this);
        const enteredMark = parseInt(markInput.val(), 10);
        const maxMark = parseInt(markInput.attr('max'), 10);
        if (!isNaN(enteredMark) && enteredMark > maxMark) {
            Notiflix.Notify.Warning(`Marks cannot exceed ${maxMark}.`);
            markInput.val(maxMark);
        }
    });

    function loadStudents() {
        const subjectCodeVal = $('#subjectcode').val();
        const testIdVal = $('#testid').val();
        const selectedTestOption = $('#testid option:selected');
        const fullMark = selectedTestOption.data('fullmark');

        if (subjectCodeVal == "-1" || testIdVal == "-1") {
            showModalAlert("Please select both a subject and a test.");
            return;
        }

        const subjectText = $("#subjectcode option:selected").text();
        const testText = selectedTestOption.text();
        const dynamicTitle = `Internal Evaluation for ${subjectText} (${testText})`;

        $("#tabletitle").html(`<i class="fa fa-list"></i> ${dynamicTitle}`);
        $("#fullmark").html(`<span class="badge bg-info p-2">Full Marks: ${fullMark}</span>`);

        Notiflix.Loading.Standard('Loading students...');
        $.ajax({
            type: "GET",
            url: API.getStudentsList,
            data: { subjectcode: subjectCodeVal, testid: testIdVal },
            success: function (data) {
                Notiflix.Loading.Remove();
                iatable.clear().destroy();
                $('#studentslist').empty();

                if (!data || data.length === 0) {
                     $('#studentslist').html('<tr><td colspan="3" class="text-center text-muted">No Students Available.</td></tr>');
                } else {
                    let studentsHtml = '';
                    data.forEach(student => {
                        const studentName = `${student[1] || ''} ${student[2] || ''} ${student[3] || ''}`.trim();
                        const marks = student[11] !== null ? student[11] : '';
                        const internalEvalId = (student[9] !== null && student[9] !== undefined) ? student[9] : '';

                        studentsHtml += `<tr data-studentid='${student[0]}'>
                              <td>${student[0]}</td>
                              <td>${studentName}</td>
                              <td>
                                  <input type='number' min='0' max='${fullMark}' class='form-control studentmarkinput' value='${marks}' />
                                  <input type='hidden' value='${student[0]}' name='studentids' />
                                  <input type='hidden' value='${internalEvalId}' name='internalevaluationids'>
                              </td>
                           </tr>`;
                    });
                    $('#studentslist').html(studentsHtml);
                }

                // Re-initialize DataTable with config
                iatable = $('#internalevaluationtable').DataTable(getDtConfig());
            },
            error: function (jqXHR, textStatus) {
                Notiflix.Loading.Remove();
                showModalAlert("Error loading students: " + textStatus, "Error");
            }
        });
    }
});

function showModalAlert(message, title = 'Message', reloadOnClose = false) {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);
    $('#feedbackModal').off('hidden.bs.modal');
    if (reloadOnClose) {
        $('#feedbackModal').on('hidden.bs.modal', function() { window.location.reload(); });
    }
    $('#feedbackModal').modal('show');
}