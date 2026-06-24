function validateFacultyForm() {
    // Check required dropdowns
    if ($("#usercode").val() === "-1") {
        showModalAlert("Please select a User.");
        return false;
    }
    const fname = $("#fname").val().trim();
    if (fname === "") {
        showModalAlert("First Name is required.");
        return false;
    }
    if (/[^a-zA-Z. ]/.test(fname)) {
        showModalAlert('First Name can only contain letters, periods, and spaces.');
        return false;
    }
    const lname = $("#lname").val().trim();
    if (lname === "") {
        showModalAlert("Last Name is required.");
        return false;
    }
     if (/[^a-zA-Z. ]/.test(lname)) {
        showModalAlert('Last Name can only contain letters, periods, and spaces.');
        return false;
    }
    const mname = $("#mname").val().trim();
    if (mname && /[^a-zA-Z. ]/.test(mname)) {
        showModalAlert('Middle Name can only contain letters, periods, and spaces.');
        return false;
    }
    if ($("#designationcode").val() === "-1") {
        showModalAlert("Please select a Designation.");
        return false;
    }
    if ($("#departmentcode").val() === "-1") {
        showModalAlert("Please select a Department.");
        return false;
    }
    // Check multi-selects
    if (!$("#coursecode").val() || $("#coursecode").val().length === 0) {
        showModalAlert("Please select at least one Course.");
        return false;
    }
    if (!$("#subject").val() || $("#subject").val().length === 0) {
        showModalAlert("Please select at least one Subject.");
        return false;
    }
    return true;
}

$(document).ready(function () {

    $('.select2').select2({
         width: '100%'
    });

    $('#coursecode').on('change', function () {
        const selectedCourses = $(this).val();
        const $subjectSelect = $('#subject');

        const previouslySelectedSubjects = $subjectSelect.val();

        $subjectSelect.empty();

        if (selectedCourses && selectedCourses.length > 0) {
            $subjectSelect.append($('<option></option>').prop('disabled', true).text('Loading subjects...'));
            $subjectSelect.trigger('change.select2'); // Update select2 display

            $.ajax({
                type: 'GET',
                url: API.getFacultyByCourse,
                data: {
                    courseCodes: selectedCourses
                },
                traditional: true,
                success: function (subjects) {
                    $subjectSelect.empty();

                    if (subjects && subjects.length > 0) {
                        subjects.forEach(function (subject) {
                            $subjectSelect.append($('<option></option>').val(subject.subjectcode).text(subject.subjectname));
                        });

                        if(previouslySelectedSubjects) {
                            const validSelections = previouslySelectedSubjects.filter(id =>
                                subjects.some(s => s.subjectcode == id)
                            );
                            $subjectSelect.val(validSelections);
                        }

                    } else {
                         $subjectSelect.append($('<option></option>').prop('disabled', true).text('No subjects found for selected course(s)'));
                    }
                    $subjectSelect.trigger('change.select2');
                },
                error: function () {
                    $subjectSelect.empty();
                    $subjectSelect.append($('<option></option>').prop('disabled', true).text('Error loading subjects'));
                    $subjectSelect.trigger('change.select2');
                }
            });
        } else {
            $subjectSelect.val(null).trigger('change.select2');
        }
    });


    // Form submission with validation
    $("#userloginfid").submit(function (e) {
        e.preventDefault();

        if (!validateFacultyForm()) {
            return;
        }

        $.ajax({
            type: "POST",
            url: API.createEditFaculty,
            data: $("#userloginfid").serialize(),
            success: function (data) {
                if (data === "1" || data === 1) {
                    showModalAlert('Faculty successfully saved.', 'Message');

                    $('#feedbackModal').one('hidden.bs.modal', function () {
                        location.reload();
                    });

                } else {
                    showModalAlert("Error saving faculty details: " + data, "Save Failed");
                }
            },
            error: (jqXHR, textStatus, errorThrown) => showModalAlert(`AJAX Error: ${textStatus} - ${errorThrown}`, 'Error')
        });
    });

    $("#usercode").change(function () {
        const selectedUserCode = $(this).val();
        if (selectedUserCode && selectedUserCode !== '-1') {
             editfaculty(selectedUserCode);
        } else {
            cleardata();
        }
    });

    $('#departmentcode').change(function () {
        const departmentCode = $(this).val();

        if (departmentCode && departmentCode !== '-1') {
            $('#coursecode').empty().append($('<option></option>').attr('value', '-1').text('Loading...')).val('-1').trigger('change');
             $.ajax({
                type: "GET",
                url: API.getCoursesBasedOnDepartmentFaculty,
                data: { departmentcode: departmentCode },
                success: function (data) {
                    $('#coursecode').empty();
                    if (data && data.length > 0) {
                        data.forEach(function (item) {
                            $('#coursecode').append($('<option></option>').attr('value', item[0]).text(item[1]));
                        });
                        $('#coursecode').val(null).trigger('change');
                    } else {
                         $('#coursecode').append($('<option></option>').attr('value', '-1').text('No courses found')).val('-1').trigger('change');
                    }
                },
                error: function() {
                    $('#coursecode').empty().append($('<option></option>').attr('value', '-1').text('Error loading courses')).val('-1').trigger('change');
                }
            });
        } else {
             $('#coursecode').empty();
             $('#coursecode').append($('<option></option>').attr('value', '-1').text('Select'));
             $('#coursecode').val(null).trigger('change');
        }
    });

    $('#usertable').DataTable({
        ordering: false,
        pageLength: 10,
        lengthMenu: [[10, 20, 50, -1],[10, 20, 50, "All"]],
        dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
             'rtip',
        buttons:[
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                title: 'Faculties List',
                className: 'btn btn-success btn-sm mb-3',
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                }
            }
        ]
    });

    $(document).on('click', '.edit-faculty-btn', function() {
        const facultyUserCode = $(this).data('id');
        if (facultyUserCode) {
            $("#usercode").val(facultyUserCode).trigger('change');
        }
    });

    $('#userloginfid').on('reset', function() {
        setTimeout(function() {
            $('#usercode, #designationcode, #departmentcode, #coursecode, #subject').val(null).trigger('change');
            $('#usercode, #designationcode, #departmentcode').val('-1').trigger('change');
        }, 0);
    });

});

function showModalAlert(message, title = 'Message') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);
    $('#feedbackModal').modal('show');
}

function cleardata() {
    $("#facultyid").val('');
    $("#fname").val('');
    $("#mname").val('');
    $("#lname").val('');
    $('#designationcode, #departmentcode, #coursecode, #subject').val(null).trigger('change');
    $('#designationcode, #departmentcode').val('-1').trigger('change');
}

function editfaculty(usercode) {
    $('html, body').animate({ scrollTop: 0 }, 'fast');

    cleardata();

    $.when(
        $.ajax({ type: "GET", url: API.facultyDetails, data: { usercode: usercode } }),
        $.ajax({ type: "GET", url: API.facultySubjects, data: { usercode: usercode } })
    ).done(function (detailsResponse, subjectsResponse) {

        const facultyDataArray = detailsResponse[0];
        const subjectData = subjectsResponse[0];

        if (!facultyDataArray || facultyDataArray.length === 0) {
            showModalAlert('No faculty details found for this user. You can enter new details.', 'Info');
            cleardata();
            return;
        }

        const faculty = facultyDataArray[0];
        const subjectsToSelect = subjectData.map(item => String(item[0]));

        $("#facultyid").val(faculty[1]);
        $("#fname").val(faculty[2]);
        $("#mname").val(faculty[3]);
        $("#lname").val(faculty[4]);
        $("#designationcode").val(faculty[5]).trigger('change');

        const departmentCodeToSet = faculty[7];
        const coursesToSelect = faculty[9] ? String(faculty[9]).split(',').map(s => s.trim()).filter(s => s) : [];

        $("#departmentcode").val(departmentCodeToSet).trigger('change');

        if (departmentCodeToSet && departmentCodeToSet !== '-1') {
            $.ajax({
                type: "GET",
                url: API.getCoursesBasedOnDepartmentFaculty,
                data: { departmentcode: departmentCodeToSet },
                success: function(courseOptions) {
                    const $courseSelect = $('#coursecode');
                    $courseSelect.empty();
                    if (courseOptions && courseOptions.length > 0) {
                        courseOptions.forEach(item => $courseSelect.append($('<option></option>').val(item[0]).text(item[1])));
                    }

                    $courseSelect.val(coursesToSelect);

                    $.ajax({
                        type: 'GET',
                        url: API.getFacultyByCourse,
                        data: { courseCodes: coursesToSelect },
                        traditional: true,
                        success: function (subjectOptions) {
                            const $subjectSelect = $('#subject');
                            $subjectSelect.empty();
                            if(subjectOptions && subjectOptions.length > 0) {
                                subjectOptions.forEach(function (subject) {
                                    $subjectSelect.append($('<option></option>').val(subject.subjectcode).text(subject.subjectname));
                                });
                            }

                            $subjectSelect.val(subjectsToSelect);

                            $('#coursecode, #subject').trigger('change.select2');
                        }
                    });
                }
            });
        }
    }).fail(function() {
        showModalAlert('An error occurred while fetching faculty data. Please try again.', 'Error');
    });
}