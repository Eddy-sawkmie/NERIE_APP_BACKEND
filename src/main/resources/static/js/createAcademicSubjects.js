var sTable;

$(document).ready(function () {
    // Event listener for the short-term course toggle switch
    $('#isshortterm_toggle').on('change', function() {
        if ($(this).is(':checked')) {
            $('#isshortterm').val('1');
            $('#isshortterm_label').text('Yes');
        } else {
            $('#isshortterm').val('0');
            $('#isshortterm_label').text('No');
        }
        $('#isshortterm').trigger('change');
    });

    sTable = $('#s-table').DataTable({
        ordering: false,
        pageLength: 10,
        lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "All"]],

        dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
             'rtip',

        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                title: 'Venues',
                className: 'btn btn-success btn-sm mb-3',
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                }
            }
        ]
    });

    $('input.alphabets').keyup(function () {
        if (this.value.match(/[^a-zA-Z. ]/g)) {
            this.value = this.value.replace(/[^a-zA-Z. ]/g, '');
        }
    });

    $('#s-table tbody').on('click', 'button.edit-subject-btn', function () {
        const button = $(this);
        editSubject(
            button.data('subjectname'), button.data('subjectcode'),
            button.data('departmentcode'), button.data('semestercode'),
            button.data('sphase'), button.data('isshortterm'),
            button.data('coursecode'), button.data('isoptional')
        );
    });

    $('#isshortterm_toggle').on('change', function() {
        toggleViz();
        if ($('#departmentcode').val()) {
             getdepartments($('#departmentcode').val(), $('#isshortterm').val());
        }
        loadSubjects();
    });

    $('#departmentcode, #coursecode, #semestercode, #sphase').on('change', function() {
        loadSubjects();
    });

    $('#newsubjectform').submit(function (e) {
        e.preventDefault();

        if (!$('#departmentcode').val()) {
            showModalAlert('Please select a Department.');
            return;
        }
        if (!$('#coursecode').val()) {
            showModalAlert('Please select a Course Name.');
            return;
        }
        if ($('#isshortterm').val() === '0' && !$('#semestercode').val()) {
            showModalAlert('Please select a Semester.');
            return;
        }
        if ($('#isshortterm').val() === '1' && !$('#sphase').val()) {
            showModalAlert('Please select a Phase.');
            return;
        }
        const subjectName = $('#subjectname').val().trim();
        if (subjectName === '') {
            showModalAlert('Please enter a Subject Name.');
            return;
        }

        callCustomAjax(API.saveNewSubject, $('#newsubjectform').serialize(), function (data) {
            if (data === "2") {
                showModalAlert('Successfully Saved!', 'Message', true);
            } else if (data === "3") {
                showModalAlert('Subject already exists for the selected department and course.', 'Save Failed');
            } else {
                showModalAlert('An unknown error occurred while saving the subject.', 'Save Failed');
            }
        });
    });

    $('#departmentcode').change(function () {
        if ($(this).val()) {
            getdepartments($(this).val(), $('#isshortterm').val());
        } else {
            $('#coursecode').empty().append('<option value="">Select Course</option>');
        }
        loadSubjects();
    });

    loadSubjects();

    // Department Modal
    const departmentModal = document.getElementById('department-modal');
    const departmentForm = document.getElementById('departmentForm');
    const addDepartmentBtn = document.getElementById('adddept');
    const departmentNameInput = document.getElementById('departmentname_modal');

    const resetDepartmentForm = () => {
        departmentForm.reset();
    };

    addDepartmentBtn.addEventListener('click', resetDepartmentForm);

    departmentForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const departmentName = departmentNameInput.value.trim();
        if (departmentName === '') {
            showModalAlert('Please enter a Department Name.');
            return;
        }
        if (/[^a-zA-Z. ]/.test(departmentName)) {
            showModalAlert('Department Name can only contain letters, periods, and spaces.');
            return;
        }

        const submitButton = document.getElementById('submitDepartmentForm');
        submitButton.disabled = true;
        submitButton.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Saving...';

        try {
            const response = await fetch(departmentForm.action, {
                method: 'POST',
                body: new URLSearchParams(new FormData(departmentForm))
            });
            const responseBody = await response.text();

            if (!response.ok) {
                 throw new Error(`Server error! Status: ${response.status}`);
            }

            let feedbackCallback;
            switch (responseBody.trim()) {
                case "1":
                    feedbackCallback = () => showModalAlert('Department Name Already Exists!', 'Save Failed');
                    break;
                case "2":
                    feedbackCallback = () => showModalAlert('Successfully Saved!', 'Message', true);
                    break;
                default:
                    feedbackCallback = () => showModalAlert('Save failed. Please check the details and try again.', 'Save Failed');
                    break;
            }

            departmentModal.addEventListener('hidden.bs.modal', feedbackCallback, { once: true });

            let deptModalInstance = bootstrap.Modal.getInstance(departmentModal);
            if (!deptModalInstance) {
                deptModalInstance = new bootstrap.Modal(departmentModal);
            }
            deptModalInstance.hide();

        } catch (error) {
            showModalAlert(`An error occurred: ${error.message}`, 'Error');
        } finally {
            submitButton.disabled = false;
            submitButton.textContent = 'SUBMIT';
        }
    });

    departmentModal.addEventListener('hidden.bs.modal', resetDepartmentForm);
});


function showModalAlert(message, title = 'Message', reloadOnClose = false) {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);

    const modalEl = document.getElementById('feedbackModal');
    let myModal = bootstrap.Modal.getInstance(modalEl);
    if (!myModal) {
        myModal = new bootstrap.Modal(modalEl);
    }

    if (reloadOnClose) {
        modalEl.addEventListener('hidden.bs.modal', function () {
            window.location.reload();
        }, { once: true });
    }

    myModal.show();
}


function loadSubjects() {
    var deptCode = $('#departmentcode').val();
    var courseCode = $('#coursecode').val();
    var semCode = $('#semestercode').val();
    var sphaseVal = $('#sphase').val();
    var isShortTerm = $('#isshortterm').val();

    if (!deptCode || !courseCode || (isShortTerm === '0' && !semCode) || (isShortTerm === '1' && !sphaseVal)) {
        if(sTable) sTable.clear().draw();
        return;
    }

    $.ajax({
        type: "POST",
        url: API.getListOfSubjects,
        data: `departmentcode=${deptCode}&semestercode=${isShortTerm === '0' ? semCode : ""}&sphase=${isShortTerm === '1' ? sphaseVal : ""}&coursecode=${courseCode}&isshortterm=${isShortTerm}`,
        success: function (data) {
            sTable.clear();
            if (data && data.length > 0) {
                let tableData =[];
                data.forEach((x, index) => {
                    const buttonHtml = `<button class='btn btn-sm btn-primary edit-subject-btn text-white'
                        data-subjectname="${x[1]}" data-subjectcode="${x[0]}"
                        data-departmentcode="${x[4]}" data-semestercode="${x[5]}"
                        data-sphase="${x[7]}" data-isshortterm="${x[8]}"
                        data-coursecode="${x[9]}" data-isoptional="${x[11]}">Edit</button>`;

                    tableData.push([
                        index + 1, x[1], x[2], x[10], (x[11] === '1' ? 'Optional' : 'Compulsory'), (x[8] === '1' ? x[6] : x[3]), buttonHtml
                    ]);
                });
                sTable.rows.add(tableData).draw();
            } else {
                sTable.draw();
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            showModalAlert(`Failed to load subjects: ${textStatus} - ${errorThrown}`, 'Load Error');
            sTable.clear().draw();
        }
    });
}

function getdepartments(departmentcode, isshortterm) {
    callCustomAjaxasync(API.getCoursesBasedOnDepartment, `departmentcode=${departmentcode}&isshortterm=${isshortterm}`, function (data) {
        $('#coursecode').empty().append('<option value="">Select Course</option>');
        data.forEach(function (item) {
            $('#coursecode').append(`<option data-duration="${item[5]}" value="${item[0]}">${item[1]}</option>`);
        });
        loadSubjects();
    });
}

function editSubject(sname, subcode, dcode, scode, sphase, ist, ccode, isopt) {
    $("#subjectcode").val(subcode);
    $("#departmentcode").val(dcode);
    const isShortTerm = (ist == 1 || ist === true);
    $("#isshortterm_toggle").prop('checked', isShortTerm);
    $("#isshortterm").val(isShortTerm ? '1' : '0');
    $("#isshortterm_label").text(isShortTerm ? 'Yes' : 'No');
    toggleViz();
    const isShortTermValueForAjax = isShortTerm ? '1' : '0';
    callCustomAjaxasync(API.getCoursesBasedOnDepartment, `departmentcode=${dcode}&isshortterm=${isShortTermValueForAjax}`, function (data) {
        $('#coursecode').empty().append('<option value="">Select Course</option>');
        data.forEach(function (item) {
            $('#coursecode').append(`<option data-duration="${item[5]}" value="${item[0]}">${item[1]}</option>`);
        });
        $("#coursecode").val(ccode);
        if (isShortTerm) {
            $("#sphase").val(sphase);
        } else {
            $("#semestercode").val(scode);
        }
        $("#subjectname").val(sname);
        if (isopt !== null && isopt !== undefined) {
            $("input[name=isoptional][value='" + isopt + "']").prop('checked', true);
        }
        $('html, body').animate({ scrollTop: 0 }, 'fast');
    });
}

function toggleViz() {
    if ($("#isshortterm_toggle").is(":checked")) {
        $('#semesterdiv').hide();
        $('#phasediv').show();
        $('#sphase').prop('required', true);
        $('#semestercode').prop('required', false).val('');
    } else {
        $('#semesterdiv').show();
        $('#phasediv').hide();
        $('#sphase').prop('required', false).val('');
        $('#semestercode').prop('required', true);
    }
    $('#coursecode').empty().append('<option value="">Select Course</option>');
}

function callCustomAjax(url, data, successCallback) {
    $.ajax({
        type: 'POST', url: url, data: data, success: successCallback,
        error: function(jqXHR, textStatus, errorThrown) {
            showModalAlert(`${textStatus} - ${errorThrown}`, 'AJAX Error');
        }
    });
}

function callCustomAjaxasync(url, data, successCallback) {
    $.ajax({
        type: 'POST', url: url, data: data, success: successCallback,
        error: function(jqXHR, textStatus, errorThrown) {
             showModalAlert(`${textStatus} - ${errorThrown}`, 'AJAX Error');
        }
    });
}