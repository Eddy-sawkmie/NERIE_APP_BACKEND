document.addEventListener('DOMContentLoaded', function() {

    const modalRefreshBtn = document.getElementById('modalRefreshCaptcha');
    const captchaFeedbackDiv = document.getElementById('captcha-feedback');
    const modalFeedbackEl = document.getElementById('modal-form-feedback');

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

    // Reload Captcha Function
    const reloadCaptcha = async () => {
        try {
            const response = await fetch(API.reloadCaptcha);
            const data = await response.json();

            const img = document.getElementById('modalCaptchaImage');
            const hidden = document.getElementById('modalHiddenCaptcha');
            const input = document.getElementById('modalUserCaptcha');

            if(img) img.src = 'data:image/jpg;base64,' + data.realCaptcha;
            if(hidden) hidden.value = data.hiddentCaptcha;
            if(input) input.value = '';

            //hideCaptchaError(); // Hide error when reloading
        } catch (error) {
            console.error('Failed to reload captcha:', error);
            showCaptchaError("Error reloading captcha.");
        }
    };

    // Captcha reload button
    if(modalRefreshBtn) {
        modalRefreshBtn.addEventListener('click', reloadCaptcha);
    }

    // Validate Captcha Function
    const validateCaptchaOnServer = async () => {
        let captchaValue = document.getElementById('modalUserCaptcha').value;
        let expectedCaptchaValue = document.getElementById('modalHiddenCaptcha').value;

        if (!captchaValue || captchaValue.trim().length === 0) {
            return false;
        }

        const response = await fetch(
            `${API.validateCaptcha}?captcha=${encodeURIComponent(captchaValue)}&expected=${encodeURIComponent(expectedCaptchaValue)}`
        );

        return response.ok;
    };

    // Handle Password Visibility Toggle
    const toggleBtn = document.querySelector('.toggle-modal-password');
    if (toggleBtn) {
        toggleBtn.addEventListener('click', function() {
            const input = document.getElementById('modalPassword');
            const icon = this.querySelector('i');

            if (input && input.type === 'password') {
                input.type = 'text';
                if(icon) { icon.classList.remove('fa-eye'); icon.classList.add('fa-eye-slash'); }
            } else if (input) {
                input.type = 'password';
                if(icon) { icon.classList.remove('fa-eye-slash'); icon.classList.add('fa-eye'); }
            }
        });
    }

    // Hide captcha error when user starts typing in captcha field
    const captchaInput = document.getElementById('modalUserCaptcha');
    if(captchaInput) {
        captchaInput.addEventListener('input', hideCaptchaError);
    }

    // Perform Login - Actual API Call
    const performLogin = async () => {
        try {
            const payload = {
                "userid": document.getElementById('modalUserId').value,
                "userpassword": document.getElementById('modalPassword').value
            };

            const loginResponse = await fetch(API.login, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (loginResponse.ok) {
                window.location.href = API.redirectURL;
            } else {
                const msg = await loginResponse.text();
                showCaptchaError(msg || "Invalid Credentials");
                await reloadCaptcha();
            }
        } catch (error) {
            console.error('Login error:', error);
            showCaptchaError("An error occurred. Please try again.");
        }
    };

    // Handle Form Submission
    const modalForm = document.getElementById('modalLoginForm');
    if(modalForm) {
        modalForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            hideCaptchaError(); // Hide any previous errors

            // Validate Captcha locally first
            const captchaInputValue = document.getElementById('modalUserCaptcha').value;

            if (!captchaInputValue || captchaInputValue.trim().length === 0) {
                showCaptchaError("Captcha is required.");
                document.getElementById('modalUserCaptcha')?.focus();
                return;
            }

            // Validate captcha on server
            const isCaptchaValid = await validateCaptchaOnServer();
            if (!isCaptchaValid) {
                await reloadCaptcha();
                showCaptchaError("Invalid Captcha. Please try again.");
                document.getElementById('modalUserCaptcha')?.focus();
                return;
            }

            // If captcha is valid, proceed with login
            await performLogin();
        });
    }
});
