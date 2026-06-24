var sTable;

function getdepartments(departmentcode) {
    callCustomAjaxasync(API.getCoursesBasedOnDepartment2, "departmentcode=" + departmentcode, function (data) {
        var $courseSelect = $('#coursecode');
        $courseSelect.empty().append($('<option selected disabled></option>').attr('value', '').text('Select Course'));
        var temp = '';
        if (data) {
            for (var i = 0; i < data.length; i++) {
                temp += '<option isstc="' + data[i][3] + '" value="' + data[i][0] + '">' + data[i][1] + '</option>';
            }
        }
        $courseSelect.append(temp);
    });
}

function getsemphase(sp_isstc_value) {
    callCustomAjaxasync(API.getSemPhaseBasedOnCourses, "sp=" + sp_isstc_value, function (data) {
        var $semphaseSelect = $('#semphase');
        $semphaseSelect.empty();
        var temp = '';
        if (String(sp_isstc_value) === '0') {
            temp += '<option selected disabled value="">Select Semester</option>';
        } else {
            temp += '<option selected disabled value="">Select Phase</option>';
        }
        if (data) {
            for (var i = 0; i < data.length; i++) {
                temp += '<option value="' + data[i][0] + '">' + data[i][1] + '</option>';
            }
        }
        $semphaseSelect.html(temp);
    });
}

function loadStudents() {
    const departmentCode = $('#departmentcode').val();
    const courseCode = $('#coursecode').val();
    const semPhase = $('#semphase').val();
    const isShortTerm = $('#isshortterm').val();

    if (!departmentCode) {
        Notiflix.Notify.Warning('Please select a Department.');
        return;
    }
    if (!courseCode) {
        Notiflix.Notify.Warning('Please select a Course.');
        return;
    }
    if (!semPhase) {
        Notiflix.Notify.Warning('Please select a Semester/Phase.');
        return;
    }
    if (isShortTerm === '' || isShortTerm === null) {
        Notiflix.Notify.Warning('Course type not determined. Please reselect course.');
        return;
    }

    Notiflix.Loading.Standard('Loading Students...');
    callCustomAjax(API.getListOfStudents,
        "departmentcode=" + departmentCode + "&coursecode=" + courseCode + "&semphase=" + semPhase + "&isshortterm=" + isShortTerm,
        function (data) {
            if (sTable) {
                sTable.clear().draw();
                if (data && data.length !== 0) {
                    var count = 1;
                    data.forEach(function (x) {
                        sTable.row.add([
                            count++,
                            x[5] || 'N/A',
                            x[1] || 'N/A',
                            x[2] || 'N/A',
                            x[3] || 'N/A',
                            x[4] || 'N/A',
                            '<button type="button" class="btn btn-sm btn-primary promote-btn" data-student-id="' + x[0] + '">PROMOTE</button>'
                        ]).draw(false);
                    });
                } else {
                    Notiflix.Notify.Info('No students found for selected criteria.');
                }
            }
            Notiflix.Loading.Remove();
        }, function (jqXHR, textStatus, errorThrown) {
            Notiflix.Loading.Remove();
            Notiflix.Report.Failure('Error', 'Failed to load students: ' + textStatus + ' - ' + errorThrown, 'Okay');
        });
}

function loadStudentToPromote(studentid) {
    Notiflix.Loading.Standard('Loading Student Details...');
    $.ajax({
        type: "GET",
        url: API.getStudentOnStudentId,
        data: {studentid: studentid},
        dataType: "json",
        success: function (studentData) {
            Notiflix.Loading.Remove();
            if (studentData) {
                var depcode = studentData.departmentcode ? studentData.departmentcode.departmentcode : '';
                var isstc = String(studentData.isshortterm);
                var spcode = "";
                var spname = "";

                if (isstc === '0') {
                    $("#inputSPLabel").html('Current Semester:');
                    $("#inputNextSPLabel").html('Semester Promoted To:');
                    if (studentData.semestercode && typeof studentData.semestercode.semestercode !== 'undefined') {
                        spcode = studentData.semestercode.semestercode;
                        spname = studentData.semestercode.semestername || 'N/A';
                    }
                } else if (isstc === '1') {
                    $("#inputSPLabel").html('Current Phase:');
                    $("#inputNextSPLabel").html('Phase Promoted To:');
                    if (studentData.sphaseid && typeof studentData.sphaseid.sphaseid !== 'undefined') {
                        spcode = studentData.sphaseid.sphaseid;
                        spname = studentData.sphaseid.sphasename || 'N/A';
                    }
                } else {
                    $("#inputSPLabel").html('Current Semester/Phase:');
                    $("#inputNextSPLabel").html('Promoted To:');
                    spname = 'N/A (Invalid Type)';
                }
                $("#inputSP").val(spname);

                if (spcode === "") {
                    Notiflix.Report.Failure('Error', 'Could not determine current semester/phase code for the student.', 'Okay');
                    $("#inputNextSP").val('Error');
                    $("#inputYearid").val(studentData.academicyear || '');
                    $("#inputStudentid").val(studentData.studentid || '');
                    $("#inputFname").val(studentData.fname || '');
                    $("#inputMname").val(studentData.mname || '');
                    $("#inputLname").val(studentData.lname || '');
                    $("#inputDepartment").val(studentData.departmentcode ? studentData.departmentcode.departmentname : '');
                    $("#inputCourse").val(studentData.coursecode ? studentData.coursecode.coursename : '');
                    $("#electiveSub").hide();
                    $("#optionalSubs").val(0);
                    $("#promotionModal").modal('show');
                    return;
                }

                Notiflix.Loading.Standard('Loading Promotion Data...');
                $.ajax({
                    type: "GET",
                    url: API.getStudentPromotionData,
                    data: {depcode: depcode, spcode: spcode, isstc: isstc},
                    dataType: "json",
                    success: function (promoData) {
                        Notiflix.Loading.Remove();
                        if (promoData) {
                            $("#inputYearid").val(studentData.academicyear || '');
                            $("#inputStudentid").val(studentData.studentid || '');
                            $("#inputFname").val(studentData.fname || '');
                            $("#inputMname").val(studentData.mname || '');
                            $("#inputLname").val(studentData.lname || '');
                            $("#inputDepartment").val(studentData.departmentcode ? studentData.departmentcode.departmentname : '');
                            $("#inputCourse").val(studentData.coursecode ? studentData.coursecode.coursename : '');

                            var $optionalSPSelect = $("#inputOptionalSP");
                            $optionalSPSelect.empty();

                            if (promoData.subjects && promoData.subjects.length > 0) {
                                var subsHtml = '';
                                promoData.subjects.forEach(function (subject) {
                                    subsHtml += '<option value="' + subject.subjectcode + '">' + subject.subjectname + '</option>';
                                });
                                $optionalSPSelect.append(subsHtml);
                                $("#optionalSubs").val(promoData.subjects.length);
                            } else {
                                $("#optionalSubs").val(0);
                            }

                            var currentSpCodeInt = parseInt(spcode);
                            if (promoData.semphases && promoData.semphases.length > 0) {
                                if (isNaN(currentSpCodeInt)) {
                                    let cSPIPA = -1;
                                    promoData.semphases.forEach(function (phase, index) {
                                        if (String(phase.semphasecode) == String(spcode)) cSPIPA = index;
                                    });
                                    if (cSPIPA !== -1 && cSPIPA + 1 < promoData.semphases.length) {
                                        $("#inputNextSP").val(promoData.semphases[cSPIPA + 1].semphasename);
                                    } else {
                                        $("#inputNextSP").val('Graduation');
                                    }
                                } else if (currentSpCodeInt < promoData.semphases.length && currentSpCodeInt >= 0) {
                                    if (promoData.semphases[currentSpCodeInt] && typeof promoData.semphases[currentSpCodeInt].semphasename !== 'undefined') {
                                        $("#inputNextSP").val(promoData.semphases[currentSpCodeInt].semphasename);
                                    } else {
                                        $("#inputNextSP").val('Graduation');
                                    }
                                } else {
                                    $("#inputNextSP").val('Graduation');
                                }
                            } else {
                                $("#inputNextSP").val('Graduation');
                            }

                            $("#promotionModal").modal('show');
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        Notiflix.Loading.Remove();
                        Notiflix.Report.Failure('AJAX Error', "Error fetching promotion data: " + textStatus + " - " + errorThrown, 'Okay');
                    }
                });
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            Notiflix.Loading.Remove();
            Notiflix.Report.Failure('AJAX Error', "Error fetching student: " + textStatus + " - " + errorThrown, 'Okay');
        }
    });
}

$(document).ready(function () {

    sTable = $('#s-table').DataTable({
        retrieve: true,
        bDestroy: true,
        ordering: false,
        pageLength: 10,
        lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
        dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
             'rtip',
        buttons:[
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                title: 'Students List',
                exportOptions: { columns: ':visible:not(.noExport)' },
                className: 'btn btn-success btn-sm mb-3'
            }
        ]
    });

    var $promotionModal = $('#promotionModal');

    $promotionModal.on('shown.bs.modal', function (e) {
        var $modal = $(this);
        var $optionalSPSelect = $modal.find("#inputOptionalSP");
        var $electiveSubContainer = $modal.find("#electiveSub");

        if (parseInt($modal.find("#optionalSubs").val()) > 0 || $optionalSPSelect.find('option').length > 0) {
            // Replaced selectpicker with select2 inside the modal
            $optionalSPSelect.select2({
                placeholder: "Select Optional Subject",
                allowClear: true,
                width: '100%',
                dropdownParent: $modal
            });
            $electiveSubContainer.show();
        } else {
            if ($optionalSPSelect.hasClass("select2-hidden-accessible")) {
                $optionalSPSelect.select2('destroy');
            }
            $electiveSubContainer.hide();
        }
    });

    $promotionModal.on('hidden.bs.modal', function () {
        var $modal = $(this);
        $modal.find('input[type="text"], input[type="hidden"]').val('');

        $modal.find('select:not(#inputOptionalSP)').each(function () {
            $(this).val($(this).find('option:first').val());
        });

        var $optionalSPSelect = $modal.find("#inputOptionalSP");
        if ($optionalSPSelect.hasClass("select2-hidden-accessible")) {
            $optionalSPSelect.select2('destroy');
        }
        $optionalSPSelect.empty();
        $modal.find("#electiveSub").hide();
    });

    $('input.alphabets').keyup(function () {
        if (this.value.match(/[^a-zA-Z. ]/g)) {
            this.value = this.value.replace(/[^a-zA-Z. ]/g, '');
        }
    });

    $('#departmentcode').change(function () {
        var deptCode = $(this).val();
        $('#coursecode').empty().append($('<option selected disabled value="">Select Course</option>')).val('');
        $('#semphase').empty().append($('<option selected disabled value="">Select Semester/Phase</option>')).val('');
        $("#isshortterm").val('');
        if (deptCode && deptCode !== '') {
            getdepartments(deptCode);
        }
    });

    $('#coursecode').change(function () {
        var selOpt = $('option:selected', this), courseCode = selOpt.val(), isstcVal = selOpt.attr('isstc');
        $('#semphase').empty().append($('<option selected disabled value="">Select Semester/Phase</option>')).val('');
        $("#isshortterm").val('');
        if (courseCode && courseCode !== '' && typeof isstcVal !== 'undefined' && isstcVal !== '-1') {
            $("#isshortterm").val(isstcVal);
            getsemphase(isstcVal);
        }
    });

    $("#promoteBTN").click(function () {
        var studentid = $("#inputStudentid").val(),
            elective = $("#inputOptionalSP").val(),
            scount = $("#optionalSubs").val(),
            fname = $("#inputFname").val() || "",
            mname = $("#inputMname").val() || "",
            lname = $("#inputLname").val() || "",
            nameParts = [fname, mname, lname].filter(Boolean),
            name = nameParts.join(" "),
            nowsp = $("#inputSP").val(),
            nexsp = $("#inputNextSP").val();

        if (parseInt(scount) > 0 && (!elective || elective.length === 0)) {
            Notiflix.Report.Warning('Validation Error', 'Please Select Elective Subject(s)', 'Okay');
            return false;
        }

        $.confirm({
            title: 'Confirm Promotion',
            content: 'Promote <b>' + name + '</b> (Roll: ' + studentid + ') from <b>' + nowsp + ' to ' + nexsp + '</b>?',
            theme: 'modern',
            draggable: false,
            buttons: {
                promote: {
                    text: 'Yes, Promote',
                    btnClass: 'btn-primary',
                    action: function () {
                        Notiflix.Loading.Standard('Promoting Student...');
                        $.ajax({
                            url: API.promoteStudent,
                            data: {studentid: studentid, elective: elective ? elective.join(',') : ''},
                            method: 'POST',
                            success: function (response) {
                                Notiflix.Loading.Remove();
                                $('#promotionModal').modal('hide');
                                var messageContent = 'Student <b>' + name + '</b> has been promoted.',
                                    errorContent = 'Student <b>' + name + '</b> could not be promoted.';

                                if (String(response) === '1') {
                                    $.confirm({
                                        title: 'Success!',
                                        content: messageContent,
                                        theme: 'modern',
                                        draggable: false,
                                        buttons: {
                                            ok: {text: 'OK', btnClass: 'btn-primary'},
                                            close: {text: 'Close', btnClass: 'btn-secondary'}
                                        },
                                        onClose: function () {
                                            loadStudents();
                                        }
                                    });
                                } else {
                                    $.confirm({
                                        title: 'Error!',
                                        content: errorContent,
                                        theme: 'modern',
                                        draggable: false,
                                        buttons: {
                                            ok: {text: 'OK', btnClass: 'btn-primary'},
                                            close: {text: 'Close', btnClass: 'btn-secondary'}
                                        },
                                        onClose: function () {
                                            loadStudents();
                                        }
                                    });
                                }
                            },
                            error: function (jqXHR, textStatus, errorThrown) {
                                Notiflix.Loading.Remove();
                                $('#promotionModal').modal('hide');
                                $.confirm({
                                    title: 'AJAX Error!',
                                    content: 'An error occurred: ' + textStatus + ' - ' + errorThrown,
                                    theme: 'modern',
                                    draggable: false,
                                    buttons: {
                                        ok: {text: 'OK', btnClass: 'btn-primary'},
                                        close: {text: 'Close', btnClass: 'btn-secondary'}
                                    },
                                    onClose: function () {
                                        loadStudents();
                                    }
                                });
                            }
                        });
                    }
                },
                cancel: {
                    text: 'Cancel',
                    btnClass: 'btn-secondary'
                }
            }
        });
    });

    $("#loadStudentsBTN").on("click", function (event) {
        event.preventDefault();
        loadStudents();
    });

    $('#s-table tbody').on('click', '.promote-btn', function (event) {
        event.preventDefault();
        var studentId = $(this).data("student-id");
        if (studentId) loadStudentToPromote(studentId);
    });
});