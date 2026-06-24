const modalOverlay = document.getElementById('customModal');
const modalTitleEl = document.getElementById('modalTitle');
const modalMessageText = document.getElementById('modalMessageText');
const modalConfirmBtn = document.getElementById('modalConfirmBtn');
const modalCancelBtn = document.getElementById('modalCancelBtn');

// SELECTOR for the close button
const modalCloseBtn = document.querySelector('.custom-notification-close-btn');

let onConfirmCallback = null;
let onCancelCallback = null;

/**
 * showCustomModal function
 */
function showCustomModal({
    message,
    title = 'Notification',
    type = 'alert',
    onConfirm = null,
    onCancel = null
}) {
    modalTitleEl.textContent = title; // Dynamically set the modal title
    modalMessageText.innerHTML = message;
    onConfirmCallback = onConfirm;
    onCancelCallback = onCancel;

    if (type === 'confirm') {
        modalConfirmBtn.style.display = 'inline-block';
        modalCancelBtn.style.display = 'inline-block';
        modalConfirmBtn.textContent = 'OK';
    } else { // 'alert' type
        modalConfirmBtn.style.display = 'inline-block';
        modalCancelBtn.style.display = 'none';
        modalConfirmBtn.textContent = 'OK';
    }

    modalOverlay.classList.add('show');
}

function hideCustomModal() {
    modalOverlay.classList.remove('show');
    onConfirmCallback = null;
    onCancelCallback = null;
}

modalConfirmBtn.addEventListener('click', () => {
    if (typeof onConfirmCallback === 'function') {
        onConfirmCallback();
    }
    hideCustomModal();
});

modalCancelBtn.addEventListener('click', () => {
    if (typeof onCancelCallback === 'function') {
        onCancelCallback();
    }
    hideCustomModal();
});

modalCloseBtn.addEventListener('click', hideCustomModal);
modalOverlay.addEventListener('click', (e) => {
    if (e.target === modalOverlay) {
        hideCustomModal();
    }
});

// Page Logic
const emailFeedback = document.getElementById('email-feedback');
const passwordFeedback = document.getElementById('password-feedback');
const confirmPasswordFeedback = document.getElementById('confirm-password-feedback');
const usernameFeedback = document.getElementById('username-feedback');
const usermobileFeedback = document.getElementById('usermobile-feedback');
const captchaFeedbackDiv = document.getElementById('captcha-feedback');

const eyeIcons = {
   open: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="eye-icon"><path d="M12 15a3 3 0 100-6 3 3 0 000 6z" /><path fill-rule="evenodd" d="M1.323 11.447C2.811 6.976 7.028 3.75 12.001 3.75c4.97 0 9.185 3.223 10.675 7.69.12.362.12.752 0 1.113-1.487 4.471-5.705 7.697-10.677 7.697-4.97 0-9.186-3.223-10.675-7.69a1.762 1.762 0 010-1.113zM17.25 12a5.25 5.25 0 11-10.5 0 5.25 5.25 0 0110.5 0z" clip-rule="evenodd" /></svg>',
   closed: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="eye-icon"><path d="M3.53 2.47a.75.75 0 00-1.06 1.06l18 18a.75.75 0 101.06-1.06l-18-18zM22.676 12.553a11.249 11.249 0 01-2.631 4.31l-3.099-3.099a5.25 5.25 0 00-6.71-6.71L7.759 4.577a11.217 11.217 0 014.242-.827c4.97 0 9.185 3.223 10.675 7.69.12.362.12.752 0 1.113z" /><path d="M15.75 12c0 .18-.013.357-.037.53l-4.244-4.243A3.75 3.75 0 0115.75 12zM12.53 15.713l-4.243-4.244a3.75 3.75 0 004.243 4.243z" /><path d="M6.75 12c0-.619.107-1.213.304-1.764l-3.1-3.1a11.25 11.25 0 00-2.63 4.31c-.12.362-.12.752 0 1.114 1.489 4.467 5.704 7.69 10.675 7.69 1.5 0 2.933-.294 4.242-.827l-2.477-2.477A5.25 5.25 0 016.75 12z" /></svg>'
};

function showFeedback(element, message) {
    if (element) {
        element.textContent = message;
        element.style.display = 'block';
    }
}

function hideFeedback(element) {
    if (element) {
        element.textContent = '';
        element.style.display = 'none';
    }
}

// Field validation
async function checkEmailAvailability() {
    const email = document.getElementById('emailid').value.trim();
    if (!email) { hideFeedback(emailFeedback); return false; }
    try {
        const response = await fetch(`${checkEmail}?email=${encodeURIComponent(email)}`);
        const result = await response.text();
        if (result === "1") { showFeedback(emailFeedback, "This Email ID already exists."); return false; }
        if (result === "2") { showFeedback(emailFeedback, "Invalid email format."); return false; }
        hideFeedback(emailFeedback); return true;
    } catch (error) { showFeedback(emailFeedback, "Server error. Please try again."); return false; }
}
function checkPasswordRequirements() {
    const password = document.getElementById('userpassword').value;
    hideFeedback(passwordFeedback); if (!password) return true;
    const re = /(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}/;
    if (!re.test(password)) { showFeedback(passwordFeedback, "Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a number, and a special character."); return false; }
    return true;
}
function checkConfirmPassword() {
    const password = document.getElementById('userpassword').value; const confirm = document.getElementById('confirmpassword').value;
    hideFeedback(confirmPasswordFeedback); if (!confirm) return true;
    if (password !== confirm) { showFeedback(confirmPasswordFeedback, "Passwords do not match."); return false; }
    return true;
}
function checkUsernameFormat() {
    const username = document.getElementById('username').value.trim();
    hideFeedback(usernameFeedback); if (!username) return true;
    if (!/^[\p{L}]+(?: [\p{L}]+)*$/u.test(username)) { showFeedback(usernameFeedback, "Name should not contain numbers or special characters. Only letters and spaces are allowed."); return false; }
    return true;
}
function checkMobileFormat() {
    const mobile = document.getElementById('usermobile').value.trim();
    hideFeedback(usermobileFeedback); if (!mobile) return true;
    if (!/^\d{10}$/.test(mobile)) { showFeedback(usermobileFeedback, "Please enter a valid 10-digit mobile number."); return false; }
    return true;
}

async function validateFormClientSide() {
    let isValid = true;
    if (!document.getElementById('emailid').value.trim()) {
        showFeedback(emailFeedback, "E-mail ID is required.");
        isValid = false;
    } else if (!await checkEmailAvailability()) isValid = false;

    if (!document.getElementById('userpassword').value) {
        showFeedback(passwordFeedback, "Password is required.");
        isValid = false;
    } else if (!checkPasswordRequirements()) isValid = false;

    if (!document.getElementById('confirmpassword').value) {
        showFeedback(confirmPasswordFeedback, "Please confirm your password.");
        isValid = false;
    } else if (!checkConfirmPassword()) isValid = false;

    if (!document.getElementById('username').value.trim()) {
        showFeedback(usernameFeedback, "Name is required.");
        isValid = false;
    }

    if (!document.getElementById('usermobile').value.trim()) {
        showFeedback(usermobileFeedback, "Mobile number is required.");
        isValid = false;
    } else if (!checkMobileFormat()) isValid = false;

    if (!document.getElementById('userCaptcha').value.trim()) {
        showCaptchaError("Captcha is required.");
        isValid = false;
    } else {
        hideCaptchaError();
    }

    return isValid;
}


document.addEventListener('DOMContentLoaded', () => {
    const registrationForm = document.getElementById('mtuserloginfid');
    const captchaInput = document.getElementById('userCaptcha');

    if (registrationForm) {
        registrationForm.addEventListener('submit', registerParticipant);
    }

    // Captcha reload button
    document.getElementById('captchaReloadBtn')?.addEventListener('click', reloadCaptcha);

    // Field validation on focusout
    document.getElementById('emailid')?.addEventListener('focusout', checkEmailAvailability);
    document.getElementById('userpassword')?.addEventListener('focusout', checkPasswordRequirements);
    document.getElementById('confirmpassword')?.addEventListener('focusout', checkConfirmPassword);
    document.getElementById('username')?.addEventListener('focusout', checkUsernameFormat);
    document.getElementById('usermobile')?.addEventListener('focusout', checkMobileFormat);

    // Hide captcha error when user starts typing in captcha field
    if (captchaInput) {
        captchaInput.addEventListener('input', hideCaptchaError);
    }

    // Password visibility toggle
    document.querySelectorAll(".toggle-button").forEach(button => {
        button.innerHTML = eyeIcons.open;
        button.addEventListener("click", function() {
            const passwordField = this.previousElementSibling;
            const isPassword = passwordField.type === 'password';
            passwordField.type = isPassword ? "text" : "password";
            this.innerHTML = isPassword ? eyeIcons.closed : eyeIcons.open;
        });
    });
});

// This function contains the actual submission logic.
const performRegistration = async () => {
    const userPassword = document.getElementById('userpassword').value;

    const payload = {
        "emailid": document.getElementById('emailid').value.trim(),
        "userpassword": userPassword,
        "username": document.getElementById('username').value.trim(),
        "usermobile": document.getElementById('usermobile').value.trim()
    };

    try {
        const response = await fetch(register, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const resultText = await response.text();

        if (!response.ok) {
           let errorMessage = `Registration failed.`;
           try {
               const errorData = JSON.parse(resultText);
               if (errorData && errorData.message) errorMessage += ` ${errorData.message}`;
           } catch (parseError) {
               errorMessage += ` Server responded with: ${resultText}`;
           }
           // Added title to error modal
           showCustomModal({ message: errorMessage, title: "Registration Failed", type: 'alert' });
           if (response.status === 409) {
                showFeedback(emailFeedback, "This Email ID already exists.");
                document.getElementById('emailid')?.focus();
           }
           await reloadCaptcha();
           return;
        }

        if (resultText === "2") { // SUCCESS
            const successMessage = "Please login using your Email ID.";
            const onOkRedirect = () => { window.location.href = login; };
            showCustomModal({ message: successMessage, title: "Successfully Registered!", type: 'alert', onConfirm: onOkRedirect });
        } else {
            const errorMap = {
                "0": "Invalid Captcha according to server.", "1": "Email ID cannot be empty.",
                "3": "Email ID is too long.", "4": "Email ID already exists.",
                "5": "Password cannot be empty.", "6": "Password is too long.",
                "7": "Mobile number cannot be empty.", "8": "Mobile number must be 10 digits."
            };
            // Added title to error modal
            showCustomModal({
                message: errorMap[resultText] || `An unexpected error occurred: ${resultText}`,
                title: "Registration Failed",
                type: 'alert'
            });
            await reloadCaptcha();
        }
    } catch (error) {
      console.error("Submission error:", error);
      showCustomModal({
          message: "Could not submit registration. Please check your network connection and try again.",
          title: "Submission Error",
          type: 'alert'
      });
      await reloadCaptcha();
    }
};

const registerParticipant = async (e) => {
    e.preventDefault();
    hideCaptchaError(); // Hide any previous errors

    const isClientSideValid = await validateFormClientSide();
    if (!isClientSideValid) return;

    const isCaptchaValid = await validateCaptcha();
    if (!isCaptchaValid) {
        await reloadCaptcha();
        showCaptchaError("Invalid Captcha. Please try again.");
        document.getElementById('userCaptcha')?.focus();
        return;
    }

    showCustomModal({
        message: "Click OK to register. Click Cancel to review your entries.",
        title: "Confirmation",
        type: 'confirm',
        onConfirm: performRegistration
    });
};

const validateCaptcha = async () => {
    let captchaValue = document.getElementById('userCaptcha').value;
    let expectedCaptchaValue = document.getElementById('hiddentCaptcha').value;

    if (!captchaValue || captchaValue.trim().length === 0) {
        return false;
    }

    const response = await fetch(`${captchaValidate}?captcha=${captchaValue}&expected=${expectedCaptchaValue}`);
    return response.ok;
}

const reloadCaptcha = async () => {
    try {
        const response = await fetch(captchaReload);
        const data = await response.json();
        document.getElementById('captchaImage').src = 'data:image/jpg;base64,' + data.realCaptcha;
        document.getElementById('hiddentCaptcha').value = data.hiddentCaptcha;
        document.getElementById('userCaptcha').value = ''; // Clear captcha input
        //hideCaptchaError(); // Hide error when reloading
    } catch(error) {
        console.error("Failed to reload captcha", error);
        showCaptchaError("Error reloading captcha.");
    }
};

// Helper functions for captcha error display
function showCaptchaError(message) {
    if (captchaFeedbackDiv) {
        captchaFeedbackDiv.textContent = message;
        captchaFeedbackDiv.classList.add('show');
    }
}

function hideCaptchaError() {
    if (captchaFeedbackDiv) {
        captchaFeedbackDiv.textContent = '';
        captchaFeedbackDiv.classList.remove('show');
    }
}