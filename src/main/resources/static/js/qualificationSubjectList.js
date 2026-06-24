/*
 * HELPER FUNCTIONS
 */
function showModalAlert(message, title = 'Message') {
    document.getElementById('feedbackModalLabel').textContent = title;
    document.getElementById('feedbackModalBody').innerHTML = message;

    const feedbackModalEl = document.getElementById('feedbackModal');

    // Move modal to the body to prevent backdrop overlapping issues
    if (feedbackModalEl.parentNode !== document.body) {
        document.body.appendChild(feedbackModalEl);
    }

    const modalInstance = bootstrap.Modal.getOrCreateInstance(feedbackModalEl);

    // Update footer button styling to match modern theme
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
    const subjectTable = document.getElementById('qsubjecttable');
    const subjectTableBody = subjectTable?.querySelector('tbody');
    const addSubjectBtn = document.getElementById('addft');
    const subjectModalEl = document.getElementById('modalid-modal');
    const subjectForm = document.getElementById('mqsubjectfid');
    const modalTitleText = document.getElementById('modal-title-text');

    // Form Inputs
    const subjectCodeInput = document.getElementById('qualificationsubjectcode');
    const subjectNameInput = document.getElementById('qualificationsubjectname');

    // Alphabet validation allowing space, dot, and hyphen
    subjectNameInput?.addEventListener('input', function() {
        if (this.value.match(/[^a-zA-Z.\- ]/g)) {
            this.value = this.value.replace(/[^a-zA-Z.\- ]/g, '');
        }
    });

    const resetForm = () => {
        if (subjectForm) {
            subjectForm.reset();
            subjectForm.classList.remove('was-validated');
        }
        if (subjectCodeInput) subjectCodeInput.value = '';
    };

    // Populate for Edit
    const populateFormForEdit = (button) => {
        resetForm();
        if (modalTitleText) modalTitleText.textContent = 'Edit Qualification Subject';

        const data = button.dataset;
        subjectCodeInput.value = data.subjectcode || '';
        subjectNameInput.value = data.subjectname || '';
    };

    // Delegate click for edit buttons
    subjectTableBody?.addEventListener('click', (event) => {
        const editButton = event.target.closest('.edit-subject-btn');
        if (editButton) {
            populateFormForEdit(editButton);
        }
    });

    addSubjectBtn?.addEventListener('click', () => {
        resetForm();
        if (modalTitleText) modalTitleText.textContent = 'Add Qualification Subject';
    });

    // Handle Form Fetch/AJAX
    const handleFormSubmit = async (event) => {
        event.preventDefault();

        if (!subjectForm.checkValidity()) {
            event.stopPropagation();
            subjectForm.classList.add('was-validated');
            return;
        }

        const submitButton = document.getElementById('submitSubjectForm');
        submitButton.disabled = true;
        const originalText = submitButton.innerHTML;
        submitButton.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Saving...';

        const urlToUse = (typeof API !== 'undefined' && API.saveQualificationSubject) ? API.saveQualificationSubject : subjectForm.action;

        try {
            const response = await fetch(urlToUse, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams(new FormData(subjectForm)).toString()
            });

            if (!response.ok) {
                if (response.status === 401) {
                    showModalAndRedirect("Authentication error. Please log in again.", API.redirectErrorURL);
                } else {
                    throw new Error(`Server error! Status: ${response.status}`);
                }
                return;
            }

            const responseBody = await response.text();
            const result = responseBody.trim();

            switch (result) {
                case "2":
                    const modalInstance = bootstrap.Modal.getInstance(subjectModalEl);
                    if (modalInstance) modalInstance.hide();

                    subjectModalEl.addEventListener('hidden.bs.modal', function() {
                        showModalAndRedirect("Successfully Saved!", window.location.href);
                    }, { once: true });
                    break;
                case "1":
                    showModalAlert("Subject Name Already Exists!");
                    subjectNameInput.focus();
                    break;
                case "3":
                    showModalAlert("Subject Name cannot be empty!");
                    break;
                case "4":
                    showModalAlert("Subject Name should be 1-200 characters long!");
                    break;
                default:
                    showModalAlert("Save Failed! An unexpected error occurred.");
                    break;
            }

        } catch (error) {
            showModalAlert(`An error occurred: ${error.message}`);
        } finally {
            submitButton.disabled = false;
            submitButton.innerHTML = originalText;
        }
    };

    subjectForm?.addEventListener('submit', handleFormSubmit);

    // Bootstrap 5 reset modal fields when completely hidden
    subjectModalEl?.addEventListener('hidden.bs.modal', resetForm);

    // DataTable Initialization
    if (typeof $ !== 'undefined' && $.fn.DataTable) {
        $('#qsubjecttable').DataTable({
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
                    title: 'Qualifications Subjects',
                    className: 'btn btn-success btn-modern btn-sm mb-3 shadow-sm',
                    exportOptions: {
                        columns: ':visible:not(.noExport)'
                    }
                }
            ]
        });
    }

    // Back to top behavior
    document.getElementById('backtotop')?.addEventListener('click', (e) => {
        e.preventDefault();
        window.scrollTo({ top: 0, behavior: 'smooth' });
    });
});

function customReset() {
    window.location.reload();
}