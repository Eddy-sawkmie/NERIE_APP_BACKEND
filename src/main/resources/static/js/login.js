const eyeIcons = {
    open: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="eye-icon"><path d="M12 15a3 3 0 100-6 3 3 0 000 6z" /><path fill-rule="evenodd" d="M1.323 11.447C2.811 6.976 7.028 3.75 12.001 3.75c4.97 0 9.185 3.223 10.675 7.69.12.362.12.752 0 1.113-1.487 4.471-5.705 7.697-10.677 7.697-4.97 0-9.186-3.223-10.675-7.69a1.762 1.762 0 010-1.113zM17.25 12a5.25 5.25 0 11-10.5 0 5.25 5.25 0 0110.5 0z" clip-rule="evenodd" /></svg>',
    closed: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="eye-icon"><path d="M3.53 2.47a.75.75 0 00-1.06 1.06l18 18a.75.75 0 101.06-1.06l-18-18zM22.676 12.553a11.249 11.249 0 01-2.631 4.31l-3.099-3.099a5.25 5.25 0 00-6.71-6.71L7.759 4.577a11.217 11.217 0 014.242-.827c4.97 0 9.185 3.223 10.675 7.69.12.362.12.752 0 1.113z" /><path d="M15.75 12c0 .18-.013.357-.037.53l-4.244-4.243A3.75 3.75 0 0115.75 12zM12.53 15.713l-4.243-4.244a3.75 3.75 0 004.243 4.243z" /><path d="M6.75 12c0-.619.107-1.213.304-1.764l-3.1-3.1a11.25 11.25 0 00-2.63 4.31c-.12.362-.12.752 0 1.114 1.489 4.467 5.704 7.69 10.675 7.69 1.5 0 2.933-.294 4.242-.827l-2.477-2.477A5.25 5.25 0 016.75 12z" /></svg>'
};

// Helper functions for captcha error display
function showCaptchaError(message) {
    const captchaFeedbackDiv = document.getElementById('captcha-feedback');
    if (captchaFeedbackDiv) {
        captchaFeedbackDiv.textContent = message;
        captchaFeedbackDiv.classList.add('show');
    }
}

function hideCaptchaError() {
    const captchaFeedbackDiv = document.getElementById('captcha-feedback');
    if (captchaFeedbackDiv) {
        captchaFeedbackDiv.textContent = '';
        captchaFeedbackDiv.classList.remove('show');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('userloginsid').addEventListener('submit', async e => await authenticate(e))
    const reloadElements = [document.getElementById('captchaReloadBtn'), document.getElementById('captchaImage')];
    reloadElements.forEach(el => el.addEventListener('click', async () => await reloadCaptcha()));

    // Hide captcha error when user types in captcha field
    const captchaInput = document.getElementById('userCaptcha');
    if(captchaInput) {
        captchaInput.addEventListener('input', hideCaptchaError);
    }

    document.querySelectorAll(".toggle-button").forEach(button => {
        button.innerHTML = eyeIcons.open;
        button.addEventListener("click", function() {
            const passwordField = this.previousElementSibling;
            const isPassword = passwordField.type === 'password';
            passwordField.type = isPassword ? "text" : "password";
            this.innerHTML = isPassword ? eyeIcons.closed : eyeIcons.open;
        });
    });
})

// Display Notiflix notification of Request Param 'msg'
document.addEventListener('DOMContentLoaded', () => {
    const msg = document.getElementById('param-msg').value
    const captchaFeedbackDiv = document.getElementById('captcha-feedback')

    switch (msg) {
        case 'unauthenticated':
            showCaptchaError('You have been logged out')
            break
        default:
            if (msg && msg.trim() !== '') {
                showCaptchaError(msg)
            }
    }
})

const authenticate = async e => {
    e.preventDefault();

    hideCaptchaError();

    // 1. Validate Captcha first
    const captchaInputValue = document.getElementById('userCaptcha').value;

    if (!captchaInputValue || captchaInputValue.trim().length === 0) {
        showCaptchaError("Captcha is required.");
        document.getElementById('userCaptcha')?.focus();
        return;
    }

    let isCaptchaValid = await validateCaptcha();

    if (!isCaptchaValid) {
        await reloadCaptcha();
        showCaptchaError("Invalid Captcha. Please try again.");
        document.getElementById('userCaptcha')?.focus();
        return;
    }

    // 2. If Captcha is valid, attempt to log in
    const payload = {
        "userid": document.getElementById('userid').value,
        "userpassword": document.getElementById('password').value
    };

    try {
        const response = await fetch(loginURL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            window.location.href = homeURL
        } else if (response.status === 401) {
            const msg = await response.text()
            showCaptchaError(msg || "Invalid Credentials")
            await reloadCaptcha()
        } else {
            showCaptchaError("Invalid User Id or Password.")
            await reloadCaptcha()
        }
    } catch (error) {
        console.error('Login request failed:', error);
        showCaptchaError("An unexpected error occurred. Please try again.");
    }
}

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
        document.getElementById('userCaptcha').value = "";
    } catch (error) {
        console.error('Failed to reload captcha:', error);
        showCaptchaError("Error reloading captcha.");
    }
}