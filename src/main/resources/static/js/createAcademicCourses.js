var sTable;

// Initialize DataTable
function initializeDataTable() {
    return $('#c-table').DataTable({
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
                title: 'Academic Courses',
                className: 'btn btn-success btn-sm mb-3',
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                }
            }
        ]
    });
}

// Custom form validation function
function validateCourseForm(formId) {
    const department = $(`#${formId} select[name="departmentcode"]`).val();
    const courseId = $(`#${formId} input[name="courseid"]`).val().trim();
    const courseName = $(`#${formId} input[name="coursename"]`).val().trim();
    const duration = $(`#${formId} input[name="courseduration"]`).val().trim();

    if (!department) {
        showModalAlert('Please select a Department.');
        return false;
    }
    if (!courseId) {
        showModalAlert('Please enter a Course ID.');
        return false;
    }
    if (courseId.length !== 3 || !/^[A-Z]+$/.test(courseId)) {
        showModalAlert('Course ID must be exactly 3 uppercase letters.');
        return false;
    }
    if (!courseName) {
        showModalAlert('Please enter a Course Name.');
        return false;
    }
    if (!duration) {
        showModalAlert('Please enter a Duration.');
        return false;
    }
    if (!/^\d+$/.test(duration) || parseInt(duration, 10) < 1) {
        showModalAlert('Duration must be a positive number.');
        return false;
    }
    return true;
}

$(document).ready(function () {
    $('input.onlyletters').keyup(function () {
        this.value = this.value.replace(/[^a-zA-Z]/g, '').toUpperCase();
    });
    $('input.coursename').keyup(function () {
        this.value = this.value.replace(/[^a-zA-Z0-9\)(\&. ]/g, '');
    });
    $('input.courseduration').keyup(function () {
        this.value = this.value.replace(/[^0-9]/g, '');
    });

    sTable = initializeDataTable();

    $('#departmentcode').on('change', loadCourses);
    $('#clearCourseBtn').on('click', clearCourse);

    // Add New Course Form Submission
    $('#newcourseform').submit(function (e) {
        e.preventDefault();
        if (!validateCourseForm('newcourseform')) {
            return;
        }

        callCustomAjax(API.saveMapDepartmentCourse, $(this).serialize(), function (data) {
            const addModalEl = document.getElementById('course-modal');
            const addModal = bootstrap.Modal.getInstance(addModalEl);
            let feedbackCallback;

            switch (data.trim()) {
                case "2":
                    feedbackCallback = () => showModalAlert('Successfully Saved!', 'Message', true);
                    break;
                case "3":
                    feedbackCallback = () => showModalAlert('Course Name Already Exists!', 'Save Failed');
                    break;
                case "4":
                    feedbackCallback = () => showModalAlert('Course ID already exists!', 'Save Failed');
                    break;
                default:
                    feedbackCallback = () => showModalAlert('Failed to save course. Please try again.', 'Save Failed');
                    break;
            }

            if (addModal) addModal.hide();
            addModalEl.addEventListener('hidden.bs.modal', feedbackCallback, { once: true });
        });
    });

    // Edit Course Form Submission
    $('#editcourseform').submit(function (e) {
        e.preventDefault();
        if (!validateCourseForm('editcourseform')) {
            return;
        }
        callCustomAjax(API.updateDepartmentCourse, $(this).serialize(), function (data) {
            const editModalEl = document.getElementById('edit-course-modal');
            const editModal = bootstrap.Modal.getInstance(editModalEl);
            let feedbackCallback;

            switch (data.trim()) {
                case "2":
                    feedbackCallback = () => showModalAlert('Successfully Updated!', 'Message', true);
                    break;
                case "3":
                    feedbackCallback = () => showModalAlert('Course Name Already Exists!', 'Update Failed');
                    break;
                case "4":
                    feedbackCallback = () => showModalAlert('Course ID already exists!', 'Update Failed');
                    break;
                case "5":
                    feedbackCallback = () => showModalAlert('Course not found.', 'Update Failed');
                    break;
                default:
                    feedbackCallback = () => showModalAlert('Failed to update course. Please try again.', 'Update Failed');
                    break;
            }

            if (editModal) editModal.hide();
            editModalEl.addEventListener('hidden.bs.modal', feedbackCallback, { once: true });
        });
    });

    $('#c-table tbody').on('click', '.editCourseBtn', function() {
        var btn = $(this);
        editCourse(
            btn.data('ccode'),
            btn.data('cname'),
            btn.data('dcode'),
            btn.data('cid'),
            String(btn.data('ist')),
            btn.data('duration')
        );
    });

    function autoCorrectDuration(formId) {
        const durationInput = $(`#${formId} input[name="courseduration"]`);
        const duration = durationInput.val();

        const isShortTerm = $(`#${formId} input[name="isshortterm"]:checked`).val();

        if (duration) {
            const durationValue = parseInt(duration, 10);

            if (isShortTerm === '1' && durationValue > 1) {
                if(typeof Notiflix !== 'undefined') Notiflix.Notify.Warning('Short Term course duration cannot exceed 1 year.');
                durationInput.val('1');
            }
            else if (isShortTerm === '0' && durationValue > 2) {
                if(typeof Notiflix !== 'undefined') Notiflix.Notify.Warning('Long Term course duration cannot exceed 2 year.');
                durationInput.val('2');
            }
        }
    }

    $('#newcourseform input[name="courseduration"], #newcourseform input[name="isshortterm"]').on('change', function() {
        autoCorrectDuration('newcourseform');
    });

    $('#editcourseform input[name="courseduration"], #editcourseform input[name="isshortterm"]').on('change', function() {
        autoCorrectDuration('editcourseform');
    });
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

function clearCourse() {
    $('#newcourseform')[0].reset();
    $('#coursecode').val('');
    $('#departmentcode2').val('');
    $('#isshortterm_no_add').prop('checked', true);
}

function loadCourses() {
    var departmentCode = $('#departmentcode').val();
    if (!departmentCode) {
        sTable.clear().draw();
        return;
    }

    callCustomAjax(API.getListOfCourses, { departmentcode: departmentCode }, function (data) {
        sTable.clear();
        if (data && data.length > 0) {
            const tableData = data.map((x, index) => {
                const courseType = (String(x[5]) === '1') ? "Short Term" : "Long Term";
                const editButton = `<button class='btn btn-sm btn-primary editCourseBtn text-white'
                    data-ccode='${x[0]}'
                    data-cname='${x[1]}'
                    data-dcode='${x[3]}'
                    data-cid='${x[4]}'
                    data-ist='${x[5]}'
                    data-duration='${x[6]}'>Edit</button>`;
                return [index + 1, x[1], x[2], `${x[6]} Years`, courseType, editButton];
            });
            sTable.rows.add(tableData);
        }
        sTable.draw();
    });
}

function editCourse(ccode, cname, dcode, cid, ist, duration) {
    $('#edit_coursecode').val(ccode);
    $('#edit_coursename').val(cname);
    $('#edit_departmentcode').val(dcode);
    $('#edit_courseid').val(cid);
    $('#edit_courseduration').val(duration);

    if (ist === '1') {
        $('#isshortterm_yes_edit').prop('checked', true);
    } else {
        $('#isshortterm_no_edit').prop('checked', true);
    }

    const editModalEl = document.getElementById('edit-course-modal');
    let editModal = bootstrap.Modal.getInstance(editModalEl) || new bootstrap.Modal(editModalEl);
    editModal.show();
}

function callCustomAjax(url, data, successCallback) {
    $.ajax({
        type: 'POST',
        url: url,
        data: data,
        success: successCallback,
        error: function(jqXHR, textStatus, errorThrown) {
            showModalAlert(`An unexpected error occurred: ${textStatus} - ${errorThrown}`, 'AJAX Error');
        }
    });
}