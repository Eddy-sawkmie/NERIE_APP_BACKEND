/*
 * HELPER FUNCTIONS
 */
function showModalAlert(message, title = 'Message') {
    document.getElementById('feedbackModalLabel').textContent = title;
    document.getElementById('feedbackModalBody').innerHTML = message;

    const feedbackModalEl = document.getElementById('feedbackModal');

    if (feedbackModalEl.parentNode !== document.body) {
        document.body.appendChild(feedbackModalEl);
    }

    const modalInstance = bootstrap.Modal.getOrCreateInstance(feedbackModalEl);

    const footer = feedbackModalEl.querySelector('.modal-footer');
    if(footer) {
        footer.innerHTML = '<button type="button" class="btn btn-primary btn-modern px-4" data-bs-dismiss="modal">OK</button>';
    }

    modalInstance.show();
}

function showModalAndRedirect(message, url) {
    showModalAlert(message);
    const feedbackModalEl = document.getElementById('feedbackModal');

    feedbackModalEl.addEventListener('hidden.bs.modal', function () {
        window.location.href = url;
    }, { once: true });
}

/*
 * MAIN LOGIC
 */
document.addEventListener('DOMContentLoaded', () => {
    const qualificationTable = document.getElementById('qualificationtable');
    const qualificationTableBody = qualificationTable?.querySelector('tbody');
    const addQualificationBtn = document.getElementById('addft');
    const qualificationModalEl = document.getElementById('modalid-modal');
    const qualificationForm = document.getElementById('mqualificationfid');
    const modalTitleText = document.getElementById('modal-title-text');

    // Form Inputs
    const qualificationCodeInput = document.getElementById('qualificationcode');
    const qualificationNameInput = document.getElementById('qualificationname');
    const qualificationCategorySelect = document.getElementById('qualificationcategorycode');

    // Simple alphabet validation
    qualificationNameInput?.addEventListener('keypress', (event) => {
        if (event.key.length === 1 && !/^[a-zA-Z. \-]$/.test(event.key)) {
            event.preventDefault();
        }
    });

    const resetForm = () => {
        if(qualificationForm) {
            qualificationForm.reset();
            qualificationForm.classList.remove('was-validated');
        }
        if(qualificationCodeInput) qualificationCodeInput.value = '';
    };

    // Populate for Edit
    const populateFormForEdit = (button) => {
        resetForm();
        if (modalTitleText) modalTitleText.textContent = 'Edit Qualification';

        const data = button.dataset;
        qualificationCodeInput.value = data.qcode || '';
        qualificationNameInput.value = data.qname || '';
        qualificationCategorySelect.value = data.qcategory || '';
    };

    // Delegate click for edit buttons
    qualificationTableBody?.addEventListener('click', (event) => {
        const editButton = event.target.closest('.qualification-edit-btn');
        if (editButton) {
            populateFormForEdit(editButton);
        }
    });

    addQualificationBtn?.addEventListener('click', () => {
        resetForm();
        if (modalTitleText) modalTitleText.textContent = 'Add Qualification';
    });

    // Handle Form Fetch/AJAX
    const handleFormSubmit = async (event) => {
        event.preventDefault();

        if (!qualificationForm.checkValidity()) {
            event.stopPropagation();
            qualificationForm.classList.add('was-validated');
            return;
        }

        const submitButton = document.getElementById('submitQualificationForm');
        submitButton.disabled = true;
        const originalText = submitButton.innerHTML;
        submitButton.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Saving...';

        // Use the API endpoint defined in the inline script, fallback to form action
        const urlToUse = (typeof API !== 'undefined' && API.saveQualification) ? API.saveQualification : qualificationForm.action;

        try {
            const response = await fetch(urlToUse, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams(new FormData(qualificationForm)).toString()
            });

            if (!response.ok) {
                if (response.status === 401) {
                    showModalAndRedirect("Authentication error. Please log in again.", API.redirectURL);
                } else {
                    throw new Error(`Server error! Status: ${response.status}`);
                }
                return;
            }

            const responseBody = await response.text();
            const result = responseBody.trim();

            switch (result) {
                case "2":
                    // Hide Edit Modal first
                    const modalInstance = bootstrap.Modal.getInstance(qualificationModalEl);
                    if (modalInstance) modalInstance.hide();

                    qualificationModalEl.addEventListener('hidden.bs.modal', function() {
                        showModalAndRedirect("Successfully Saved!", window.location.href);
                    }, { once: true });
                    break;
                case "1":
                    showModalAlert("Qualification Name Already Exists!");
                    qualificationNameInput.focus();
                    break;
                case "3":
                    showModalAlert("Qualification Name cannot be empty!");
                    break;
                case "4":
                    showModalAlert("Qualification Name should be 1-100 characters long!");
                    break;
                default:
                    showModalAlert("Save Failed! Unexpected response: " + result);
                    break;
            }

        } catch (error) {
            showModalAlert(`An error occurred while saving: ${error.message}`);
        } finally {
            submitButton.disabled = false;
            submitButton.innerHTML = originalText;
        }
    };

    qualificationForm?.addEventListener('submit', handleFormSubmit);

    // Bootstrap 5 reset modal fields when completely hidden
    qualificationModalEl?.addEventListener('hidden.bs.modal', resetForm);

    // DataTable Initialization
    if (typeof $ !== 'undefined' && $.fn.DataTable) {
        $('#qualificationtable').DataTable({
            ordering: false,
            pageLength: 10,
            lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "All"]],
            dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
                 '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
                 'rtip',
            buttons: [
                {
                    extend: 'excelHtml5',
                    text: '<i class="fas fa-file-excel me-1"></i> Excel',
                    title: 'Qualifications',
                    className: 'btn btn-success btn-modern btn-sm mb-3 shadow-sm',
                    exportOptions: {
                        columns: ':visible:not(.noExport)'
                    }
                }
            ]
        });
    }

    // Optional Sidebar Toggle from original script
    const menuToggle = document.getElementById('menu-toggle');
    menuToggle?.addEventListener('click', (e) => {
        e.preventDefault();
        document.getElementById('wrapper')?.classList.toggle('toggled');
    });
});