/*
 * HELPER FUNCTIONS
 */
function showModalAlert(message, title = 'Message') {
    document.getElementById('feedbackModalLabel').textContent = title;
    document.getElementById('feedbackModalBody').innerHTML = message;

    const feedbackModalEl = document.getElementById('feedbackModal');

    // Move modal to body to prevent backdrop overlapping issues
    if (feedbackModalEl.parentNode !== document.body) {
        document.body.appendChild(feedbackModalEl);
    }

    const modalInstance = bootstrap.Modal.getOrCreateInstance(feedbackModalEl);

    // Update footer button styling to match modern theme
    const footer = feedbackModalEl.querySelector('.modal-footer');
    if (footer) {
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
    const designationTable = document.getElementById('designationtable');
    const designationTableBody = designationTable?.querySelector('tbody');
    const addDesignationBtn = document.getElementById('addDesignationBtn');
    const designationModalEl = document.getElementById('office-modal');
    const designationForm = document.getElementById('mdesignationfid');
    const modalTitleText = document.getElementById('modal-title-text');

    const designationCodeInput = document.getElementById('designationcode');
    const designationNameInput = document.getElementById('designationname');
    const designationTypeSelect = document.getElementById('isparticipantdesignation');

    // Simple alphabet validation
    designationNameInput?.addEventListener('keypress', (event) => {
        if (event.key.length === 1 && !/^[a-zA-Z. ]$/.test(event.key)) {
            event.preventDefault();
        }
    });

    const resetForm = () => {
        if (designationForm) {
            designationForm.reset();
            designationForm.classList.remove('was-validated');
        }
        if (designationCodeInput) designationCodeInput.value = '';
    };

    // Populate for Edit
    const populateFormForEdit = (button) => {
        resetForm();
        if(modalTitleText) {
            modalTitleText.textContent = 'Edit Designation';
        }

        const data = button.dataset;
        if (designationCodeInput) designationCodeInput.value = data.designationcode || '';
        if (designationNameInput) designationNameInput.value = data.designationname || '';
        if (designationTypeSelect) designationTypeSelect.value = data.isparticipantdesignation || '';
    };

    // Delegate click for edit buttons
    designationTableBody?.addEventListener('click', (event) => {
        const editButton = event.target.closest('.designationbtn');
        if (editButton) {
            populateFormForEdit(editButton);
        }
    });

    addDesignationBtn?.addEventListener('click', () => {
        resetForm();
        if(modalTitleText) {
            modalTitleText.textContent = 'Add Designation';
        }
    });

    // Handle Form AJAX
    const handleFormSubmit = async (event) => {
        event.preventDefault();

        if (!designationForm.checkValidity()) {
            event.stopPropagation();
            designationForm.classList.add('was-validated');
            return;
        }

        const submitButton = document.getElementById('submitDesignationForm');
        submitButton.disabled = true;
        const originalText = submitButton.innerHTML;
        submitButton.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span> Saving...';

        try {
            const response = await fetch(designationForm.action, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams(new FormData(designationForm)).toString()
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
                case "1":
                    showModalAlert("Designation Already Exists!", "Validation Error");
                    designationNameInput.focus();
                    break;
                case "2":
                    // Hide Edit Modal first
                    const modalInstance = bootstrap.Modal.getInstance(designationModalEl);
                    if (modalInstance) modalInstance.hide();

                    designationModalEl.addEventListener('hidden.bs.modal', function() {
                         showModalAndRedirect("Successfully Saved!", window.location.href);
                    }, { once: true });
                    break;
                case "3":
                    showModalAlert("Designation cannot be empty!", "Validation Error");
                    break;
                default:
                    showModalAlert("Save Failed! Unexpected response: " + result, "System Error");
                    break;
            }

        } catch (error) {
            showModalAlert(`An error occurred: ${error.message}`, "System Error");
        } finally {
            submitButton.disabled = false;
            submitButton.innerHTML = originalText;
        }
    };

    designationForm?.addEventListener('submit', handleFormSubmit);

    // Bootstrap 5 "hidden" event listener
    designationModalEl?.addEventListener('hidden.bs.modal', resetForm);

    // DataTable Initialization
    if (typeof $ !== 'undefined' && $.fn.DataTable) {
        $('#designationtable').DataTable({
            ordering: false,
            pageLength: 5,
            lengthMenu: [[5, 10, 20, 50, -1], [5, 10, 20, 50, "All"]],
            dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
                 '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
                 'rtip',
            buttons: [
                {
                    extend: 'excelHtml5',
                    text: '<i class="fas fa-file-excel me-1"></i> Excel',
                    title: 'Designations',
                    className: 'btn btn-success btn-modern btn-sm mb-3 shadow-sm',
                    exportOptions: {
                        columns: ':visible:not(.noExport)'
                    }
                }
            ]
        });

        // Style the DataTables native search input to match Bootstrap 5
        $('.dataTables_filter input').addClass('form-control modern-input d-inline-block w-auto ms-2');
    }
});