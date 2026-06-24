function showModalAlert(message, title = 'Message') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);
    $('#feedbackModal').modal('show');
}

function showModalAndRedirect(message, url, title = 'Success') {
    showModalAlert(message, title);
    $('#feedbackModal').one('hidden.bs.modal', function () {
        window.location.href = url;
    });
}

document.addEventListener('DOMContentLoaded', () => {

    const form = document.getElementById('userloginfid');
    const oldPasswordInput = document.getElementById('olduserpassword');
    const newPasswordInput = document.getElementById('userpassword');
    const confirmPasswordInput = document.getElementById('confirmpassword');
    const submitButton = document.getElementById('submitBtn');
    const resetButton = document.getElementById('customResetBtn');

    const strengthPercent = document.getElementById('percent');
    const strengthResults = document.getElementById('results');
    const strengthColorbar = document.getElementById('colorbar');

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

    async function checkOldPasswordMatch() {
        const oldPassword = oldPasswordInput.value;
        if (oldPassword.trim().length === 0) return true;

        const checkUrl = API.checkOldPassword;
        try {
            const formData = new URLSearchParams({ 'olduserpassword': oldPassword });
            const response = await fetch(checkUrl, { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: formData });
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

            const result = await response.text();
            if (result === "0") {
                showModalAlert("The old password you entered is incorrect.", "Message");
                oldPasswordInput.value = "";
                oldPasswordInput.focus();
                return false;
            }
            return true;
        } catch (error) {
            console.error("Error checking old password:", error);
            showModalAlert("Could not verify your old password due to a server error. Please try again.", "Server Error");
            return false;
        }
    }

    function checkPasswordRequirements() {
        const password = newPasswordInput.value;
        if (password.trim().length === 0) return true;

        const re = /(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}/;
        if (!re.test(password)) {
            const requirementMessage =
                '<div class="alert alert-warning border-0 text-dark mb-0">' +
                '<p class="mb-2 fw-bold"><i class="fas fa-exclamation-triangle me-2"></i>Password does not meet the requirements.</p>' +
                '<p class="mb-2">It must contain:</p>' +
                '<ul class="text-start mb-0" style="padding-left: 1.5rem;">' +
                '<li>At least one number (0-9)</li>' +
                '<li>At least one lowercase letter (a-z)</li>' +
                '<li>At least one uppercase letter (A-Z)</li>' +
                '<li>Be at least 8 characters long</li>' +
                '</ul>' +
                '</div>';

            showModalAlert(requirementMessage, "Requirement Not Met");
            newPasswordInput.value = "";
            updatePasswordStrengthUI(0, "Awaiting Input");
            newPasswordInput.focus();
            return false;
        }
        return true;
    }

    function checkUserIdInPassword() {
        const userId = document.getElementById('userid').value?.toUpperCase() || '';
        const password = newPasswordInput.value?.toUpperCase() || '';
        if (password.trim().length === 0 || userId.trim().length === 0) return true;

        if (password.includes(userId)) {
            showModalAlert("For security, your password cannot contain your User ID.", "Security Warning");
            newPasswordInput.value = "";
            updatePasswordStrengthUI(0, "Awaiting Input");
            newPasswordInput.focus();
            return false;
        }
        return true;
    }

    function checkOldNewPassword() {
        const oldPassword = oldPasswordInput.value;
        const newPassword = newPasswordInput.value;
        if (newPassword.trim().length === 0 || oldPassword.trim().length === 0) return true;

        if (oldPassword === newPassword) {
            showModalAlert("Your new password must be different from your old password.", "Validation Error");
            newPasswordInput.value = "";
            updatePasswordStrengthUI(0, "Awaiting Input");
            newPasswordInput.focus();
            return false;
        }
        return true;
    }

    function checkConfirmPassword() {
        const newPassword = newPasswordInput.value;
        const confirmPassword = confirmPasswordInput.value;
        if (confirmPassword.trim().length === 0) return true;

        if (newPassword.trim().length === 0 && confirmPassword.trim().length > 0) {
            showModalAlert("Please enter your new password before confirming it.", "Validation Error");
            confirmPasswordInput.value = "";
            newPasswordInput.focus();
            return false;
        }

        if (newPassword !== confirmPassword) {
            showModalAlert("The confirmation password does not match your new password.", "Validation Error");
            confirmPasswordInput.value = "";
            confirmPasswordInput.focus();
            return false;
        }
        return true;
    }

    function calculatePasswordStrength(password) {
        if (!password || password.length === 0) return { score: 0, text: "Awaiting Input" };
        let score = 0;
        if (password.length >= 8) score += 25; else score += password.length * 3;
        if (password.match(/[a-z]/)) score += 15;
        if (password.match(/[A-Z]/)) score += 20;
        if (password.match(/\d/)) score += 20;
        if (password.match(/[^a-zA-Z0-9]/)) score += 20;
        score = Math.min(score, 100);
        let text = "Very Weak";
        if (score >= 85) text = "Very Strong";
        else if (score >= 70) text = "Strong";
        else if (score >= 50) text = "Moderate";
        else if (score >= 25) text = "Weak";
        return { score, text };
    }

    function updatePasswordStrengthUI(score, text) {
        strengthPercent.textContent = `${score}%`;
        strengthResults.textContent = text;

        // Define Bootstrap 5 Background Utility Classes
        let colorClass = "bg-danger";
        if (score >= 85) colorClass = "bg-success";
        else if (score >= 70) colorClass = "bg-primary";
        else if (score >= 50) colorClass = "bg-warning";

        // Apply classes and adjust width to the progress bar
        strengthColorbar.className = `progress-bar transition-all ${colorClass}`;
        strengthColorbar.style.width = `${score}%`;
        strengthColorbar.setAttribute('aria-valuenow', score);

        // Update text color dynamically
        strengthResults.className = `fw-bold text-uppercase text-${colorClass.replace('bg-', '')}`;
        if (score === 0) strengthResults.className = "fw-bold text-uppercase text-muted";
    }

    oldPasswordInput.addEventListener('blur', checkOldPasswordMatch);

    newPasswordInput.addEventListener('keyup', (e) => {
        const { score, text } = calculatePasswordStrength(e.target.value);
        updatePasswordStrengthUI(score, text);
    });

    newPasswordInput.addEventListener('blur', () => {
        if (!checkUserIdInPassword()) return;
        if (!checkPasswordRequirements()) return;
        if (!checkOldNewPassword()) return;
    });

    confirmPasswordInput.addEventListener('blur', checkConfirmPassword);

    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        if (oldPasswordInput.value.trim() === '' || newPasswordInput.value.trim() === '' || confirmPasswordInput.value.trim() === '') {
            showModalAlert("Please fill in all required fields.", "Validation Error");
            return;
        }

        let isValid = true;
        if (!checkUserIdInPassword() || !checkPasswordRequirements() || !checkOldNewPassword() || !checkConfirmPassword()) {
            isValid = false;
        }

        if (isValid && !(await checkOldPasswordMatch())) {
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        submitButton.disabled = true;
        submitButton.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i> Saving...';

        try {
            const response = await fetch(form.action, { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: new URLSearchParams(new FormData(form)) });
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

            const result = await response.text();

            switch (result) {
                case "2":
                    showModalAndRedirect("Password updated successfully!", API.redirectURL, 'Success');
                    break;
                case "1":
                    showModalAlert("Save Failed: The old password you entered is incorrect.", "Error");
                    oldPasswordInput.focus();
                    break;
                case "3":
                    showModalAlert("Save Failed: Your new password must be different from your old one.", "Error");
                    newPasswordInput.focus();
                    break;
                case "5":
                    showModalAlert("Save Failed: Your password cannot contain your User ID.", "Error");
                    newPasswordInput.focus();
                    break;
                default:
                    showModalAlert("Save Failed: An unknown error occurred. Please contact support.", "Error");
                    break;
            }

        } catch (error) {
            console.error("Error submitting form:", error);
            showModalAlert("Submission failed due to a network or server error. Please try again.", "Submission Error");
        } finally {
            submitButton.disabled = false;
            submitButton.innerHTML = '<i class="fas fa-save me-2"></i> Update Password';
        }
    });

    resetButton.addEventListener('click', () => {
        form.reset();
        updatePasswordStrengthUI(0, "Awaiting Input");

        document.querySelectorAll('.toggle-password i').forEach(icon => {
            icon.classList.remove('fa-eye-slash');
            icon.classList.add('fa-eye');
        });
        document.querySelectorAll('input[type="text"]').forEach(input => {
            if(input.id !== 'userid') input.type = 'password';
        });
    });

    updatePasswordStrengthUI(0, "Awaiting Input");
});