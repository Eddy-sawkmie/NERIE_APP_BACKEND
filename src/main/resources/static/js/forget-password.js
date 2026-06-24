// Modal Elements
const modalOverlay = document.getElementById('customModal');
const modalTitleEl = document.getElementById('modalTitle');
const modalMessageText = document.getElementById('modalMessageText');
const modalConfirmBtn = document.getElementById('modalConfirmBtn');
const modalCancelBtn = document.getElementById('modalCancelBtn');
const modalCloseBtn = document.querySelector('.custom-notification-close-btn');

let onConfirmCallback = null;
let onCancelCallback = null;

function showCustomModal({
    message,
    title = 'Notification',
    type = 'alert',
    onConfirm = null,
    onCancel = null
}) {
    modalTitleEl.textContent = title;
    modalMessageText.innerHTML = message;
    onConfirmCallback = onConfirm;
    onCancelCallback = onCancel;

    if (type === 'confirm') {
        modalConfirmBtn.style.display = 'inline-block';
        modalCancelBtn.style.display = 'inline-block';
        modalConfirmBtn.textContent = 'OK';
    } else {
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

// Modal Event Listeners
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

// Form Elements
const useridInput = document.getElementById('userid');
const useridFeedback = document.getElementById('userid-feedback');
const forgetPasswordForm = document.getElementById('mtuserloginfid');

// Feedback Functions
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

// Validate User ID
function validateUserId() {
    const userid = useridInput.value.trim();
    hideFeedback(useridFeedback);

    if (!userid) {
        showFeedback(useridFeedback, "User ID is required.");
        return false;
    }

    return true;
}

async function fetchRoleAndConfirm() {
    const userid = useridInput.value.trim();

    try {
        const response = await fetch(`${checkUserRoleUrl}?userid=${encodeURIComponent(userid)}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const result = await response.json();

        if (result.status === "1") {
            // Success: User found, show the second modal with the role
            showCustomModal({
                message: `User <strong>${userid}</strong> has the role of <strong>${result.roleName}</strong>.<br><br>Are you absolutely sure you want to proceed with the password reset?`,
                title: "Confirm User Role",
                type: 'confirm',
                onConfirm: performPasswordReset, // If they click OK here, perform the actual reset
                onCancel: () => {
                    console.log("Password reset cancelled at role verification step.");
                }
            });
        } else {
            // Error: User not found
            showCustomModal({
                message: result.val,
                title: "Verification Failed",
                type: 'alert'
            });
        }

    } catch (error) {
        console.error("Error:", error);
        showCustomModal({
            message: "An error occurred while verifying the user role. Please try again later.",
            title: "Error",
            type: 'alert'
        });
    }
}

// Perform Password Reset
async function performPasswordReset() {
    const userid = useridInput.value.trim();

    try {
        const response = await fetch(`${resetUserPassword}?userid=${encodeURIComponent(userid)}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const result = await response.json();

        if (result.status === "1") {
            // Success
            showCustomModal({
                message: result.val,
                title: "Password Reset Successful",
                type: 'alert',
                onConfirm: () => {
                    useridInput.value = '';
                    hideFeedback(useridFeedback);
                }
            });
        } else {
            // Error
            showCustomModal({
                message: result.val,
                title: "Password Reset Failed",
                type: 'alert'
            });
        }

    } catch (error) {
        console.error("Error:", error);
        showCustomModal({
            message: "An error occurred while processing your request. Please try again later.",
            title: "Error",
            type: 'alert'
        });
    }
}

// Handle Form Submission - Show Confirmation First
async function handleForgetPassword(e) {
    e.preventDefault();

    // Validate form
    if (!validateUserId()) {
        useridInput.focus();
        return;
    }

    const userid = useridInput.value.trim();

    // Show first confirmation modal
    showCustomModal({
        message: `Are you sure you want to reset the password for User ID: <strong>${userid}</strong>?<br><br>A new password will be generated and displayed on the screen.`,
        title: "Confirm Password Reset",
        type: 'confirm',
        onConfirm: fetchRoleAndConfirm,
        onCancel: () => {
            console.log("Password reset cancelled by user");
        }
    });
}

// Event Listeners
document.addEventListener('DOMContentLoaded', () => {
    if (forgetPasswordForm) {
        forgetPasswordForm.addEventListener('submit', handleForgetPassword);
    }

    if (useridInput) {
        useridInput.addEventListener('focusout', validateUserId);
        useridInput.addEventListener('input', () => {
            hideFeedback(useridFeedback);
        });
    }
});
