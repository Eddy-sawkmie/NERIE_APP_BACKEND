function showModalAlert(message, title = 'Message') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);
    // Updated to use Bootstrap 5's data-bs-dismiss
    $('#feedbackModal .modal-footer').html(
        '<button type="button" class="btn btn-primary btn-modern px-4" data-bs-dismiss="modal">OK</button>'
    );
    $('#feedbackModal').modal('show');
}

function showModalAndReload(message, title = 'Success') {
    showModalAlert(message, title);
    $('#feedbackModal').one('hidden.bs.modal', function () {
        window.location.reload();
    });
}

$(document).ready(function () {
    // Fetch Profile Data
    $.ajax({
        type: "GET",
        url: API.studentInfo,
        data: "",
        success: function (data) {
            if (data && data.student) {
                const studentArr = JSON.parse(data.student);
                $("#inputAcademicYear").val(studentArr.academicyear);
                $("#inputStudentid").val(studentArr.studentid);
                studentArr.dateofbirth ? $("#inputDOB").val(studentArr.dateofbirth) : $("#inputDOB").val("Not Provided");
                $("#inputName").val(studentArr.fname + ' ' + (studentArr.mname ? studentArr.mname + ' ' : '') + studentArr.lname);
                $("#inputMobileno").val(studentArr.mobileno);
                $("#inputEmail").val(studentArr.email);
                
                if (studentArr.departmentcode && studentArr.departmentcode.departmentname) {
                    $("#inputDepartment").val(studentArr.departmentcode.departmentname);
                } else {
                    $("#inputDepartment").val("N/A");
                }
                
                if (studentArr.coursecode && studentArr.coursecode.coursename) {
                    $("#inputCourse").val(studentArr.coursecode.coursename);
                } else {
                    $("#inputCourse").val("N/A");
                }

                if (studentArr.sphaseid && studentArr.sphaseid.sphasename) {
                    $("#inputSemphase").val(studentArr.sphaseid.sphasename);
                } else if (studentArr.semestercode && studentArr.semestercode.semestername) {
                    $("#inputSemphase").val(studentArr.semestercode.semestername);
                } else {
                    $("#inputSemphase").val("N/A");
                }
            } else {
                showModalAlert("An error occurred while fetching your profile data. Please try again.", "Error");
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            showModalAlert(`A network error occurred while fetching your data. Please check your connection and try again.<br><br><b>Error:</b> ${errorThrown}`, 'Network Error');
        }
    });

    // Handle Upload Button Click
    $("#uploadbtn").click(function (event) {
        event.preventDefault();
        
        const fileInput = $('#file1').get(0);

        if (fileInput.files.length === 0) {
            showModalAlert("Please select a profile picture before uploading.", "Message");
            return;
        }

        const $btn = $(this);
        const originalBtnText = $btn.html();
        
        // Visual Loading State
        $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-2"></i> Uploading...');

        $.ajax({
            type: "POST",
            url: API.savePhoto,
            data: new FormData($("#uploadphotoform")[0]),
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            success: function (data) {
                $btn.prop('disabled', false).html(originalBtnText);
                if (data === '1') {
                    showModalAndReload('Profile photo has been uploaded successfully!', 'Success');
                } else {
                    showModalAlert('Failed to Upload Photo. Please ensure the file is a valid image (jpg/png) and under 500KB.', 'Upload Failed');
                }
            },
            error: function (jqXHR) {
                $btn.prop('disabled', false).html(originalBtnText);
                if (jqXHR.status === 401) {
                    window.location.href = API.redirectErrorURL;
                } else {
                    showModalAlert('Something went wrong. An unexpected error occurred. Please try again.', 'Server Error');
                }
            }
        });
    });

    // Real-time file size validation
    $(document).on('change', 'input[type="file"]#file1', function () {
        if (this.files.length > 0) {
            const filesize = this.files[0].size / 1024; // KB
            if (filesize > 700) {
                showModalAlert('Your file size is ' + filesize.toFixed(2) + ' KB. <br> Filesize cannot be greater than 500 KB.', 'File Too Large');
                this.form.reset();
                $("#previewfile1").attr("src", "/tempscripts/images/participant_photo.png");
            }
        }
    });
});

function previewFile1image() {
    const fileInput = $("#file1");
    if (fileInput[0].files.length !== 0) {
        const mext = fileInput.val().split('.').pop().toLowerCase();

        if (["jpg", "jpeg", "png"].includes(mext)) {
            const file = fileInput[0].files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function () {
                    $("#previewfile1").attr("src", reader.result);
                }
                reader.readAsDataURL(file);
            }
        } else {
            showModalAlert("Invalid file type. Please select a photo with a .jpg, .jpeg, or .png extension.", "Invalid File Type");
            fileInput.val("");
            $("#previewfile1").attr("src", "/tempscripts/images/participant_photo.png");
            fileInput.focus();
            return false;
        }
    }
}