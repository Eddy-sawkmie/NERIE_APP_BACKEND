/*
 * HELPER FUNCTIONS
 */
function showModalAlert(message, title = 'Message') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);

    $('#feedbackModal .modal-footer').html(
        '<button type="button" class="btn btn-primary btn-modern px-4" data-bs-dismiss="modal">OK</button>'
    );

    var modalElement = document.getElementById('feedbackModal');
    
    // FIX: Move modal to the body to prevent backdrop overlapping issues
    if (modalElement.parentNode !== document.body) {
        document.body.appendChild(modalElement);
    }

    // Use getOrCreateInstance to prevent double-backdrop bugs
    var myModal = bootstrap.Modal.getOrCreateInstance(modalElement);
    myModal.show();
}

function showModalAndRedirect(message, url) {
    showModalAlert(message);
    const modalEl = document.getElementById('feedbackModal');

    // Ensure we only attach the listener once
    modalEl.addEventListener('hidden.bs.modal', function (event) {
        window.location.href = url;
    }, { once: true });
}

function showModalConfirm(message, callback, title = 'Confirmation') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message); // use .html() in case of <br> tags

    $('#feedbackModal .modal-footer').html(
        '<button type="button" class="btn btn-light border btn-modern px-4" data-bs-dismiss="modal">Cancel</button>' +
        '<button type="button" id="modalConfirmOkButton" class="btn btn-primary btn-modern px-4">OK</button>'
    );

    // Bind the OK button
    $('#modalConfirmOkButton').one('click', function () {
        const modalEl = document.getElementById('feedbackModal');
        const modalInstance = bootstrap.Modal.getInstance(modalEl);

        // Wait for hidden event to execute callback smoothly
        modalEl.addEventListener('hidden.bs.modal', function () {
            callback();
        }, { once: true });

        if(modalInstance) modalInstance.hide();
    });

    var modalElement = document.getElementById('feedbackModal');
    
    // FIX: Move modal to the body to prevent backdrop overlapping issues
    if (modalElement.parentNode !== document.body) {
        document.body.appendChild(modalElement);
    }

    // Use getOrCreateInstance to prevent double-backdrop bugs
    var myModal = bootstrap.Modal.getOrCreateInstance(modalElement);
    myModal.show();
}

/*
 * RESET LOGIC
 */
document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll("[data-action='reset']").forEach(button => {
        button.addEventListener("click", function () {
            customReset();
        });
    });

    // View/Hide Password Logic
    document.querySelectorAll('.toggle-password').forEach(icon => {
        icon.addEventListener('click', function() {
            const targetId = this.getAttribute('data-target');
            const inputField = document.getElementById(targetId);
            const iconElement = this.querySelector('i');

            if (inputField.type === 'password') {
                inputField.type = 'text';
                iconElement.classList.remove('fa-eye');
                iconElement.classList.add('fa-eye-slash');
            } else {
                inputField.type = 'password';
                iconElement.classList.remove('fa-eye-slash');
                iconElement.classList.add('fa-eye');
            }
        });
    });
});

function customReset() {
    $("#userloginfid")[0].reset();
    $("#usercode").val("");

    // Restore required attribute and show password div
    $("#userpassword").prop("required", true).prop("type", "password");
    $("#confirmpassword").prop("required", true).prop("type", "password");
    $("#pwddiv").show();

    // Reset password icons
    document.querySelectorAll('.toggle-password i').forEach(icon => {
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
    });

    // Clear checkboxes and radio
    $('input[name="role"]').prop('checked', false);
    $('input[name="leaveRole"]').prop('checked', false);

    // Reset specific logic
    const rc = document.getElementById('radio-container');
    if (rc) {
        bootstrap.Collapse.getOrCreateInstance(rc, { toggle: false }).hide();
    }

    $('#dlist').hide();
    $('#toggleLeaveRole').prop('checked', false);

    // Reset Roles default state based on logged in user
    if ($("#principalCheckbox").length) $("#principalCheckbox").prop("checked", true);
    if ($("#localAdminCheckbox").length) $("#localAdminCheckbox").prop("checked", true);
}

/*
 * DOCUMENT READY INIT
 */
$(document).ready(function () {
    $('#backtotop').click(function () {
        $("html, body").animate({scrollTop: 0}, 600);
        return false;
    });

    $("#userdescription").keyup(function () {
        $("#charactercount").html("Characters left: " + (300 - $(this).val().length));
    });

    // Toggle Leave Role Container using Modern Bootstrap Collapse
    $('#toggleLeaveRole').change(function() {
        const rc = document.getElementById('radio-container');
        if (!rc) return;

        const bsCollapse = bootstrap.Collapse.getOrCreateInstance(rc, { toggle: false });

        if($(this).is(':checked')) {
            bsCollapse.show();
        } else {
            bsCollapse.hide();
            // Clear selections after animation finishes (BS5 default is 350ms)
            setTimeout(() => {
                $('input[name="leaveRole"]').prop('checked', false);
            }, 350);
        }
    });

    // Reset form when clicking the "Display Users" tab
    const displayUserTab = document.getElementById('displayuser-tab');
    if(displayUserTab){
        displayUserTab.addEventListener('click', function(){
            customReset();
        });
    }
});

/*
 * MENU TOGGLES
 */
$("#menu-toggle").click(function (e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
});

$('.sub-menu ul').hide();
$('.sub-sub-menu ul').hide();
$(".sub-menu a").click(function () {
    $(this).parent(".sub-menu").children("ul").slideToggle("100");
    $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
});
$(".sub-sub-menu a").click(function () {
    $(this).parent(".sub-sub-menu").children("ul").slideToggle("100");
    $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
});

$(document).ready(function () {
     $('#usertable').DataTable({
        ordering: false,
        pageLength: 5,
        lengthMenu: [[5, 10, 20, 50, -1], [5, 10, 20, 50, "All"]],

        // Updated DOM Layout:
        // 1. First Row: Buttons (B) aligned to the right.
        // 2. Second Row: Length (l) and Search (f). Added 'mb-3' for margin bottom.
        dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
             'rtip',

        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                title: 'Users',
                className: 'btn btn-success btn-sm mb-3',
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                }
            }
        ]
    });

    // Toggle Show More/Less (Existing code)
    $('#usertable tbody').on('click', 'td a.showmore', function () {
        var row = $(this).closest('tr');
        var morespan = row.find('span.more');
        var a = row.find('a.showmore');
        var l = row.find('span.less');
        if (morespan.css('display') === "none") {
            a.text("show less...");
            l.hide();
            morespan.show();
        } else {
            a.text("show more...");
            l.show();
            morespan.hide();
        }
    });
});

/*
 * SAVE USER LOGIC
 */
$(document).ready(async () => {
    $("#dlist").hide()

    $("#userloginfid").submit(async e => {
      e.preventDefault()

      const urole = $("#urole").val();

        // VALIDATION LOGIC
        if (urole == 'A') {
            // If Local Admin creating user, ensure at least one role is checked (Coordinator or Faculty)
            var selectedRoles = $('input[name="role"]:checked').length;
            if (selectedRoles === 0) {
                showModalAlert('Please select at least one Role (Coordinator or Faculty).');
                return false;
            }
        }

        // Leave Role Validation
        if ($('#toggleLeaveRole').is(':checked')) {
            if ($('input[name="leaveRole"]:checked').length === 0) {
                showModalAlert('Please select a specific Authority Role (Warden/Dean) or uncheck the assignment box.');
                return false;
            }
        }

        const formData = new FormData(document.getElementById('userloginfid'))
        await fetch(API.saveUser, {
            method: 'POST',
            body: formData
        })
        .then(async res => {
            const data = await res.text()
            switch (res.status) {
                case 200: return data;
                default: handleFetchError(res.status, data);
            }
        })
        .then(data => {
            if(data) {
                switch (data.trim()) {
                    case '1': showModalAlert('A user with the provided details already exists.'); break;
                    case '2': showModalAndRedirect('User saved successfully.', API.redirectURL); break;
                    case '4': showModalAlert('Office is a required field.'); break;
                    default: showModalAlert('An unexpected error occurred. Please try again.');
                }
            }
        })
        .catch(err => {
            showModalAlert(`Error submitting form. Please try again.<br><b>Error:</b> ${err.message}`);
        })
    });
});

/*
 * FIELD VALIDATIONS
 */
$(document).ready(function () {
    $("#usermobile").focusout(function () {
        var m = $('#usermobile').val();
        if (m.length > 0 && m.length < 10)
        {
            $('#msg1').html("Mobile no.should be 10 digit");
            $("#usermobile").focus();
            return false;
        } else {
            $('#msg1').html("");
        }
    });
    $("#usermobile").keypress(function () {
        var m = $('#usermobile').val();
        if (m.length == 9)
        {
            $('#msg1').html("");
        }
    });

    $(function () {
        $('input.alphabets').keyup(function () {
            if (this.value.match(/[^a-zA-Z. ]/g)) {
                this.value = this.value.replace(/[^a-zA-Z. ]/g, '');
            }
        });
    });

    $(document).on("keypress", ".numbers", function (event) {
        if (event.which < 48 || event.which > 57) {
            event.preventDefault();
        }

    });
    $(document).on("keypress", ".space", function (event) {
        if (event.which == 32) {
            event.preventDefault();
        }

    });

    $("#confirmpassword").keyup(function (e) {
        if ($("#userpassword").val().replace(/\s/g, '').length == 0) {
            showModalAlert("Please enter the primary password first.");
            $("#confirmpassword").val("");
            $("#userpassword").focus();
        }
    });

    $("#confirmpassword").focusout(function (e) {
        if ($("#confirmpassword").val().replace(/\s/g, '').length != 0)
        if ($("#userpassword").val() != $("#confirmpassword").val()) {
            showModalAlert("The passwords do not match.");
            $("#confirmpassword").val("");
            $("#confirmpassword").focus();
            return false;
        }
    });

    $("#designationcode").change(function () {
        if ($("#designationcode").val() === "others") {
            $("#dlist").show();
            $("#dinput").prop("required", true);
        } else {
            $("#dlist").hide();
            $("#dinput").prop("required", false);
        }

    });
});

/*
 * EVENT LISTENERS (EDIT, CHECK EMAIL, ETC)
 */
document.addEventListener("DOMContentLoaded", function () {
    const userInput = document.getElementById('userid');
    const emailInput = document.getElementById('emailid')
    const passwordInput = document.getElementById('userpassword');
    const confirmPasswordInput = document.getElementById('confirmpassword');
    const editLinks = document.querySelectorAll('.edit-link');

    if (userInput)
        userInput.addEventListener('focusout', () => checkuserexistfunc());

    if (emailInput)
        emailInput.addEventListener('focusout', () => checkEmailExist())

    if (passwordInput) {
        passwordInput.addEventListener('input', function () {
            passwordStrength(this.value);
        });

        passwordInput.addEventListener('focusout', function () {
            checkuserpwd();
            checkreq();
        });
    }

    if (confirmPasswordInput) {
        confirmPasswordInput.addEventListener('focusout', function () {
            checkconfirmpwd();
        });
    }

    // Delegate edit link clicks to handle dynamic tables if needed,
    // but here we attach to existing static/server-rendered links
    editLinks.forEach(function (link) {
        link.addEventListener('click', function (event) {
            event.preventDefault();

            const value0 = link.getAttribute('data-value0');        // usercode
            const value1 = link.getAttribute('data-value1');        // username
            const value2 = link.getAttribute('data-value2');        // userdescription
            const value3 = link.getAttribute('data-value3');        // userid
            const value6 = link.getAttribute('data-value6');        // userrole
            const value7 = link.getAttribute('data-value7');        // usermobile
            const value9 = link.getAttribute('data-value9');        // emailid
            const value10 = link.getAttribute('data-value10');      // officecode
            const value11 = link.getAttribute('data-value11');      // officename
            const value12 = link.getAttribute('data-value12');      // designationcode
            const roleType = link.getAttribute('data-roletype');    // isfaculty (for role A only)
            const leaveRole = link.getAttribute('data-leaverole');  // larolecode
            const isCoordinator = link.getAttribute('data-iscoordinator'); // iscoordinator

            editfunc(value10, value11, value0, value1, value3, value2, value12, value6, value7, value9, roleType, leaveRole, isCoordinator);
        });
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const resetLinks = document.querySelectorAll('.reset-link');

    resetLinks.forEach(function (link) {
        link.addEventListener('click', function (event) {
            event.preventDefault();

            const value0 = link.getAttribute('data-value0');
            const value1 = link.getAttribute('data-value1');
            const value3 = link.getAttribute('data-value3');

            resetpwd(value0, value1, value3);
        });
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const changeStatusLinks = document.querySelectorAll('.disablebtn, .enablebtn');

    changeStatusLinks.forEach(function (link) {
        link.addEventListener('click', function (event) {
            event.preventDefault();

            const value0 = link.getAttribute('data-value0');
            const value1 = link.getAttribute('data-value1');
            const value5 = link.getAttribute('data-value5');

            changeUserStatus(value0, value1, value5);
        });
    });
});

function checkEmailExist() {
    var re = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$/;
    const usercode = document.getElementById('usercode')

    if ($("#emailid").val().replace(/\s/g, '').length != 0) {
        if (re.test($('#emailid').val()) == false) {
            showModalAlert("Please enter a valid Email ID.");
            $("#emailid").val("").focus();
        } else if (!usercode.value) {
            $.ajax({
                type: "POST",
                url: API.checkEmail,
                data: "emailid=" + $("#emailid").val(),
                success: function (data) {
                    if (data == "1") {
                        showModalAlert("This Email ID is already registered.");
                        $("#emailid").val("").focus();
                    }
                },
                error: (jqXHR, textStatus, errorThrown) => {
                    // Assuming handleAjaxError exists
                    if(typeof handleAjaxError === 'function') handleAjaxError(jqXHR, textStatus, errorThrown);
                }
            })
        }
    }
}

function checkuserexistfunc() {
    var userid = $("#userid").val();
    const usercode = document.getElementById('usercode')

    if (userid.replace(/\s/g, '').length != 0) {
        if (!usercode.value) {
            $.ajax({
                type: "POST",
                url: API.checkUser,
                data: "userid=" + userid + "&usercode=" + $("#usercode").val(),
                success: function (data) {
                    if (data == "1") {
                        showModalAlert("This User ID already exists. Please enter a different User ID.");
                        $("#userid").val("").focus();
                    } else {
                        var userid1 = $("#userid").val().toUpperCase();
                        var userpassword = $("#userpassword").val().toUpperCase();
                        if (userpassword.replace(/\s/g, '').length != 0) {
                            if (userpassword.indexOf(userid1) > -1)
                            {
                                showModalAlert("Password should not contain the User ID.");
                                $("#userpassword").val("").focus();
                                $("#confirmpassword").val("");
                            }
                        }
                    }
                },
                error: (jqXHR, textStatus, errorThrown) => {
                    if(typeof handleAjaxError === 'function') handleAjaxError(jqXHR, textStatus, errorThrown);
                }
            })
        }
    }
}

function checkconfirmpwd() {
    const password = document.getElementById("userpassword").value.trim();
    const confirmPassword = document.getElementById("confirmpassword").value.trim();

    if (!password) {
        showModalAlert("Please enter the primary password first.");
        document.getElementById("confirmpassword").value = "";
        document.getElementById("userpassword").focus();
        return false;
    }

    if (confirmPassword && (password !== confirmPassword)) {
        showModalAlert("The passwords do not match.");
        document.getElementById("confirmpassword").value = "";
        return false;
    }

    return true;
}

function checkuserpwd() {
    var userid = $('#userid').val().toUpperCase();
    if (userid.replace(/\s/g, '').length != 0) {
        var userpassword = $("#userpassword").val().toUpperCase();
        if (userpassword.indexOf(userid) > -1)
        {
            showModalAlert("For security, the password cannot contain the User ID.");
            $("#userpassword").val("").focus();
        }
    }
}

function checkreq() {
    var re = /(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}/;
    if ($("#userpassword").val().replace(/\s/g, '').length != 0) {
        if (re.test($('#userpassword').val()) == false) {
            showModalAlert("<b>Password does not meet requirements.</b><br><br>It must contain at least one number, one lowercase letter, one uppercase letter, one special character, and be at least 8 characters long.");
            $("#userpassword").val("").focus();
        }
    }
}

/*
 * EDIT FUNCTION
 */
function editfunc(officeCode, officeName, ucode, uname, uid, udesc, udesig, urole, umno, uemail, roleType, leaveRole, isCoordinator) {

    // BS5 Tab Switch Logic
    const tabTriggerEl = document.querySelector('#userdetails-tab')
    const tabInstance = bootstrap.Tab.getOrCreateInstance(tabTriggerEl)
    tabInstance.show();

    $(window).scrollTop(0);

    $("#officecode").val(officeCode);
    $("#usercode").val(ucode);
    $("#username").val(uname);
    $("#userid").val(uid);
    $("#userdescription").val(udesc);
    $("#designationcode").val(udesig).trigger("change");
    $("#usermobile").val(umno);
    $("#emailid").val(uemail);

    // Clear password fields AND remove required attribute
    $("#userpassword").val("").prop("required", false).prop("type", "password");
    $("#confirmpassword").val("").prop("required", false).prop("type", "password");
    $("#pwddiv").hide();

    // Reset password icons
    document.querySelectorAll('.toggle-password i').forEach(icon => {
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
    });

    // Reset UI elements
    $('input[name="role"]').prop('checked', false);
    $('input[name="leaveRole"]').prop('checked', false);

    const rc = document.getElementById('radio-container');
    let bsCollapse = null;
    if (rc) {
        bsCollapse = bootstrap.Collapse.getOrCreateInstance(rc, { toggle: false });
    }

    const currentUserRole = $("#urole").val();

    // 1. SET ROLES
    // If Super Admin or Principal, the child role is fixed
    if(currentUserRole === 'S') $("#principalCheckbox").prop('checked', true);
    if(currentUserRole === 'Z') $("#localAdminCheckbox").prop('checked', true);

    // If Local Admin editing a user (U)
    if (currentUserRole === 'A') {
        if(roleType === '1' || roleType === 1) {
            $("#facultyCheckbox").prop('checked', true);
        }

        if(isCoordinator === '1' || isCoordinator === 1) {
            $("#coordinatorCheckbox").prop('checked', true);
        }
    }

    // 2. SET LEAVE ROLE
    if (leaveRole && leaveRole !== 'null' && leaveRole !== '') {
        $('#toggleLeaveRole').prop('checked', true);
        if (bsCollapse) bsCollapse.show();
        $('input[name="leaveRole"][value="' + leaveRole + '"]').prop('checked', true);
    } else {
        $('#toggleLeaveRole').prop('checked', false);
        if (bsCollapse) bsCollapse.hide();
    }
}

// Updated to ensure generated password meets standard regex requirements:
// 1 Upper, 1 Lower, 1 Number, 8 Chars
function makeid(length) {
    var result = '';
    var upper = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    var lower = 'abcdefghijklmnopqrstuvwxyz';
    var numbers = '0123456789';
    var all = upper + lower + numbers;

    // Ensure at least one of each required type
    result += upper.charAt(Math.floor(Math.random() * upper.length));
    result += lower.charAt(Math.floor(Math.random() * lower.length));
    result += numbers.charAt(Math.floor(Math.random() * numbers.length));

    // Fill the rest
    for (var i = 3; i < length; i++) {
        result += all.charAt(Math.floor(Math.random() * all.length));
    }

    // Shuffle the result to randomize the position of required chars
    result = result.split('').sort(function(){return 0.5-Math.random()}).join('');

    console.log("Generated temp password: " + result);
    return result;
}

function resetpwd(ucode, uname, userid) {
    showModalConfirm("Are you sure you want to reset the password for " + uname + "?", function() {
        var upwd = makeid(8);
        $.ajax({
            type: "POST",
            url: API.resetPassword,
            data: "usercode=" + ucode + "&userpassword=" + upwd,
            success: function (data) {
                if (data === "1") {
                    showModalAlert(
                        'Password reset successfully. <br><br>The new temporary password is: <b>' + upwd + '</b><br><br>Please instruct the user to change this password after their first login.',
                        'Password Reset'
                    );
                } else {
                    showModalAndRedirect("An error occurred while resetting the password. Please try again.", API.redirectURL);
                }
            },
            error: (jqXHR, textStatus, errorThrown) => {
                if(typeof handleAjaxError === 'function') handleAjaxError(jqXHR, textStatus, errorThrown);
            }
        });
    });
}

function changeUserStatus(ucode, uname, status) {
    var smsg = (status == '1')
        ? "Are you sure you want to disable the account for " + uname + "?"
        : "Are you sure you want to enable the account for " + uname + "?";

    showModalConfirm(smsg, function() {
        $.ajax({
            type: "POST",
            url: API.changeUserStatus,
            data: "usercode=" + ucode,
            success: function (data) {
                if (data == "1") {
                    const successMessage = (status == 1)
                        ? "Account disabled successfully."
                        : "Account enabled successfully.";
                    showModalAndRedirect(successMessage, API.redirectURL);
                } else {
                    showModalAndRedirect("An error occurred while changing user status. Please try again.", API.redirectURL);
                }
            },
            error: (jqXHR, textStatus, errorThrown) => {
                if(typeof handleAjaxError === 'function') handleAjaxError(jqXHR, textStatus, errorThrown);
            }
        });
    });
}

/*
 * PASSWORD STRENGTH LOGIC
 * Defined globally so utility.js can trigger it on keyup/input
 */
window.passwordStrength = function(passwordArg) {
    // Fallback to the input value if utility.js doesn't pass the string directly
    var pwd = typeof passwordArg === 'string' ? passwordArg : $('#userpassword').val() || "";

    var colorBar = $('#colorbar');
    var results = $('#results');

    // Reset if empty
    if (pwd.length === 0) {
        colorBar.css({'background-color': '#e9ecef', 'width': '100%'});
        results.text('').css('color', '');
        return;
    }

    // Calculate strength based on conditions
    var strength = 0;
    if (pwd.length >= 8) strength += 1;
    if (pwd.match(/(?=.*[a-z])/)) strength += 1; // Lowercase
    if (pwd.match(/(?=.*[A-Z])/)) strength += 1; // Uppercase
    if (pwd.match(/(?=.*[0-9])/)) strength += 1; // Numbers
    if (pwd.match(/(?=.*[!@#$%^&*()_+={}\[\]|\\:;"'<>,.?/-])/)) strength += 1; // Special Characters

    // Update UI based on score
    switch (strength) {
        case 0:
        case 1:
        case 2:
            colorBar.css({'background-color': '#dc3545', 'width': '33%'}); // Red
            results.text('Weak').css('color', '#dc3545');
            break;
        case 3:
        case 4:
            colorBar.css({'background-color': '#ffc107', 'width': '66%'}); // Yellow
            results.text('Medium').css('color', '#ffc107');
            break;
        case 5:
            colorBar.css({'background-color': '#198754', 'width': '100%'}); // Green
            results.text('Strong').css('color', '#198754');
            break;
    }
};