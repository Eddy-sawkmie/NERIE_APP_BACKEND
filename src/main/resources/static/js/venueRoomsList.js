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
    const roomTable = document.getElementById('roomtable');
    const roomTableBody = roomTable?.querySelector('tbody');
    const addRoomBtn = document.getElementById('addft');
    const roomModalEl = document.getElementById('modalid-modal');
    const roomForm = document.getElementById('mvenueroomfid');
    const modalTitleText = document.getElementById('modal-title-text');

    // Form Inputs
    const roomCodeInput = document.getElementById('roomcode');
    const roomNameInput = document.getElementById('roomname');
    const capacityInput = document.getElementById('capacity');
    const venueCodeInput = document.getElementById('venuecode');

    // Number validation for capacity
    document.addEventListener('keypress', function (event) {
        if (event.target.classList.contains('numbers')) {
            if (event.which < 48 || event.which > 57) {
                event.preventDefault();
            }
        }
    });

    // Default value for capacity on focusout
    document.addEventListener('focusout', function (event) {
        if (event.target.classList.contains('defaultval')) {
            if (event.target.value.length === 0) {
                event.target.value = '0';
            }
        }
    });

    const resetForm = () => {
        if (roomForm) {
            roomForm.reset();
            roomForm.classList.remove('was-validated');
        }
        if (roomCodeInput) roomCodeInput.value = '';
    };

    // Populate for Edit
    const populateFormForEdit = (button) => {
        resetForm();
        if (modalTitleText) modalTitleText.textContent = 'Edit Venue Room';

        const data = button.dataset;
        if (roomCodeInput) roomCodeInput.value = data.roomcode || '';
        if (roomNameInput) roomNameInput.value = data.roomname || '';
        if (capacityInput) capacityInput.value = data.capacity || '';
        if (venueCodeInput) venueCodeInput.value = data.venuecode || '';
    };

    // Delegate click for edit buttons
    roomTableBody?.addEventListener('click', (event) => {
        const editButton = event.target.closest('.edit-room-btn');
        if (editButton) {
            populateFormForEdit(editButton);
        }
    });

    addRoomBtn?.addEventListener('click', () => {
        resetForm();
        if (modalTitleText) modalTitleText.textContent = 'Add Venue Room';
    });

    // Handle Form Fetch/AJAX
    const handleFormSubmit = async (event) => {
        event.preventDefault();

        if (!roomForm.checkValidity()) {
            event.stopPropagation();
            roomForm.classList.add('was-validated');
            return;
        }

        const submitButton = document.getElementById('submitRoomForm');
        submitButton.disabled = true;
        const originalText = submitButton.innerHTML;
        submitButton.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span> Saving...';

        const urlToUse = (typeof API !== 'undefined' && API.saveVenueRoom) ? API.saveVenueRoom : roomForm.action;

        try {
            const response = await fetch(urlToUse, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams(new FormData(roomForm)).toString()
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
                    const modalInstance = bootstrap.Modal.getInstance(roomModalEl);
                    if (modalInstance) modalInstance.hide();

                    roomModalEl.addEventListener('hidden.bs.modal', function() {
                        showModalAndRedirect("Successfully Saved!", window.location.href);
                    }, { once: true });
                    break;
                case "1":
                    showModalAlert("Room Name Already Exists for this Venue!");
                    if (roomNameInput) roomNameInput.focus();
                    break;
                case "3":
                    showModalAlert("Room Name cannot be Empty!");
                    break;
                case "4":
                    showModalAlert("Room Name should be 1-50 characters long!");
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

    roomForm?.addEventListener('submit', handleFormSubmit);

    // Bootstrap 5 reset modal fields when completely hidden
    roomModalEl?.addEventListener('hidden.bs.modal', resetForm);

    // DataTable Initialization
    if (typeof $ !== 'undefined' && $.fn.DataTable) {
        $('#roomtable').DataTable({
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
                    title: 'Venue Rooms',
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