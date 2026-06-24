// Helper Function for Modal
function showModalAlert(message, title = 'Message') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);
    
    // Updated to use Bootstrap 5 data-bs-dismiss
    $('#feedbackModal .modal-footer').html(
        '<button type="button" class="btn btn-primary btn-modern px-4" data-bs-dismiss="modal">OK</button>'
    );
    
    $('#feedbackModal').modal('show');
}

// Ensure these variables are handled correctly from inline script
const initialImageSrc = initialProfilePicBase64 ? 'data:image/jpeg;base64,' + initialProfilePicBase64 : defaultProfilePicUrl;

document.addEventListener('DOMContentLoaded', () => {
    const userloginForm = document.getElementById('userloginfid');
    const usermobileInput = document.getElementById('usermobile');
    const usernameInput = document.getElementById('username');
    const emailidInput = document.getElementById('emailid');
    const useridInput = document.getElementById('userid');
    const msg1Span = document.getElementById('msg1');
    const submitButton = document.getElementById('submit');
    const file1Input = document.getElementById('file1');

    // Input Restrictions
    usermobileInput.addEventListener('input', () => {
        msg1Span.textContent = "";
        const val = usermobileInput.value;
        usermobileInput.value = val.replace(/[^\d]/g, '');
    });

    usermobileInput.addEventListener('blur', () => {
        const m = usermobileInput.value;
        if (m.length > 0 && m.length !== 10) {
            msg1Span.innerHTML = '<i class="fas fa-exclamation-circle me-1"></i> Mobile no. should be exactly 10 digits';
        } else {
            msg1Span.textContent = "";
        }
    });

    usernameInput.addEventListener('input', () => {
        usernameInput.value = usernameInput.value.replace(/[^a-zA-Z.\s]/g, '');
    });

    // Validation Helpers
    function isValidEmail(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    }

    function isValidFile(file) {
        const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png'];
        const maxSizeKB = 500;
        
        if (!allowedTypes.includes(file.type)) {
            showModalAlert("Invalid file type. Only JPG, JPEG, or PNG allowed.", "Validation Error");
            return false;
        }
        if ((file.size / 1024) > maxSizeKB) {
            showModalAlert(`File is too large. Max allowed size is ${maxSizeKB} KB.`, "Validation Error");
            return false;
        }
        return true;
    }

    // Image Preview on Selection
    file1Input.addEventListener('change', function() {
        if (this.files && this.files[0]) {
            if(isValidFile(this.files[0])) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    document.getElementById('previewfile1').src = e.target.result;
                }
                reader.readAsDataURL(this.files[0]);
            } else {
                this.value = '';
                document.getElementById('previewfile1').src = initialImageSrc;
            }
        } else {
            // Revert back to original image if selection is cancelled
            document.getElementById('previewfile1').src = initialImageSrc;
        }
    });

    // Form Submission
    userloginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const m = usermobileInput.value;
        const name = usernameInput.value.trim();
        const emailVal = emailidInput.value.trim();

        // Validate inputs
        if (!m || m.length !== 10) {
            msg1Span.innerHTML = '<i class="fas fa-exclamation-circle me-1"></i> Mobile number must be 10 digits.';
            usermobileInput.focus();
            return;
        }

        if (!name) {
            showModalAlert("Name is required.", "Validation Error");
            usernameInput.focus();
            return;
        }

        if (!emailVal) {
            showModalAlert("E-mail id is required.", "Validation Error");
            emailidInput.focus();
            return;
        } else if (!isValidEmail(emailVal)) {
            showModalAlert("Please enter a valid e-mail address.", "Validation Error");
            emailidInput.focus();
            return;
        }

        // Validate file input
        if (file1Input.files.length > 0) {
            const file = file1Input.files[0];
            if (!isValidFile(file)) {
                file1Input.value = '';
                return;
            }
        }

        const formData = new FormData(userloginForm);
        const submitUrl = API.updateProfile;

        // Visual Loading State
        submitButton.disabled = true;
        submitButton.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i> Saving...';

        try {
            const response = await fetch(submitUrl, {
                method: 'POST',
                body: formData
            });

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            const data = await response.text();
            switch (data.trim()) {
                case "2":
                    showModalAlert("Successfully Saved!", "Success");
                    $('#feedbackModal').one('hidden.bs.modal', function () {
                        window.location.reload();
                    });
                    break;
                case "1":
                    showModalAlert("Email ID already exists", "Warning");
                    break;
                case "4":
                    showModalAlert("Uploaded file is not allowed. Kindly check file type or filename.", "Error");
                    break;
                default:
                    showModalAlert(`Save Failed! Server response: ${data}`, "Error");
            }
        } catch (error) {
            console.error("Fetch Error:", error);
            showModalAlert("An error occurred while saving. Please try again later.", "System Error");
        } finally {
            submitButton.disabled = false;
            submitButton.innerHTML = '<i class="fas fa-save me-2"></i> Save Changes';
        }
    });

    // Email existence check
    emailidInput.addEventListener('focusout', async () => {
        const email = emailidInput.value.trim();
        if (!email || !useridInput) return;

        // Basic Regex check before calling server
        const re = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$/;
        if (!re.test(email)) {
            return;
        }

        const checkEmailUrl = API.checkEmail;
        const currentUserId = useridInput.value;

        try {
            const response = await fetch(checkEmailUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: `emailid=${encodeURIComponent(email)}&userid=${encodeURIComponent(currentUserId)}`
            });

            if (!response.ok) {
                console.error('HTTP error during email check!', response);
                return;
            }

            const data = await response.text();
            if (data === "1") {
                showModalAlert("This Email ID is already registered to another user.", "Duplicate Email");
                emailidInput.value = "";
                emailidInput.focus();
            }
        } catch (error) {
            console.error("Fetch Error during email check:", error);
        }
    });
});