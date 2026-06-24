$(document).ready(function() {
    console.log("Participant Action JS Loaded");

    // =================================================================
    // MODAL HELPER FUNCTION (With BS5 Z-Index Fix)
    // =================================================================
    function showFeedbackModal(message, title = 'Message', onOkCallback = null) {
        $('#feedbackModalLabel').text(title);
        $('#feedbackModalBody').html(message);

        // Set modern button
        $('#feedbackModal .modal-footer').html(
            '<button type="button" id="modalOkButton" class="btn btn-primary btn-modern px-4" data-bs-dismiss="modal">OK</button>'
        );

        const okButton = $('#modalOkButton');
        const feedbackModalEl = document.getElementById('feedbackModal');

        // Move to body to prevent dimming issue
        if (feedbackModalEl.parentNode !== document.body) {
            document.body.appendChild(feedbackModalEl);
        }

        // Clean up previous event listeners
        $(feedbackModalEl).off('hidden.bs.modal');

        if (onOkCallback && typeof onOkCallback === 'function') {
            $(feedbackModalEl).on('hidden.bs.modal', function () {
                onOkCallback();
                $(this).off('hidden.bs.modal');
            });
        }

        const myModal = bootstrap.Modal.getOrCreateInstance(feedbackModalEl);
        myModal.show();
    }

    // =================================================================
    // MAIN LOGIC
    // =================================================================

    const approveProgramTable = document.getElementById('approveprogram');
    const acceptForm = document.getElementById('acceptProgramForm');
    const rejectForm = document.getElementById('rejectProgramForm');

    if (approveProgramTable) {
        approveProgramTable.addEventListener('click', (event) => {
            const target = event.target;

            const acceptButton = target.closest('.acceptbtn');
            if (acceptButton && acceptForm) {
                const programName = acceptButton.dataset.programname;
                const programCode = acceptButton.dataset.programcode;

                document.getElementById('acceptprogramname').textContent = programName;
                document.getElementById('acceptprogramcode').value = programCode;
                document.getElementById('acceptremarks').value = '';

                acceptForm.dataset.programname = programName;
            }

            const rejectButton = target.closest('.rejectbtn');
            if (rejectButton && rejectForm) {
                const programName = rejectButton.dataset.programname;
                const programCode = rejectButton.dataset.programcode;

                document.getElementById('reject_program_name_display').textContent = programName;
                document.getElementById('reject_program_code').value = programCode;
                document.getElementById('reject_remarks').value = '';

                rejectForm.dataset.programname = programName;
            }
        });
    }

    const handleFormSubmit = async (form, url, actionType, programName, submitButtonId) => {
        if (!url) {
            showFeedbackModal("Configuration error: Form submission URL is missing.", "Error");
            return;
        }

        // HTML5 Validation check (specifically for Reject form which has required 'remarks')
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        const phaseid = form.querySelector('input[name="phaseid"]').value;
        const remarks = form.querySelector('textarea[name="remarks"]').value;

        if (!phaseid) {
            showFeedbackModal(`Error: Program Code is missing.`, "Error");
            return;
        }

        const formData = new URLSearchParams();
        formData.append('phaseid', phaseid);
        formData.append('remarks', remarks);

        // Visual loading state
        const submitBtn = document.getElementById(submitButtonId);
        const originalText = submitBtn.innerHTML;
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i> Processing...';

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString(),
            });

            const responseBody = await response.text();

            // Hide the active form modal safely via BS5
            const parentModal = bootstrap.Modal.getInstance(form.closest('.modal'));
            if(parentModal) parentModal.hide();

            if (response.ok && responseBody === "2") {
                showFeedbackModal("Program status updated successfully!", "Success", () => {
                    window.location.reload();
                });
            } else {
                console.error(`${actionType} failed. Status: ${response.status}, Body: ${responseBody}`);
                showFeedbackModal(`Update Failed! Server responded with: ${responseBody || 'Unknown error'}`, "Error");
            }
        } catch (error) {
            console.error(`Network error during ${actionType} submission:`, error);
            showFeedbackModal(`A network error occurred. Please try again.`, "Network Error");
        } finally {
            // Restore button just in case the modal wasn't hidden
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalText;
        }
    };

    if (acceptForm) {
        acceptForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const programName = event.target.dataset.programname || 'this program';
            await handleFormSubmit(acceptForm, acceptForm.getAttribute('action'), 'Accept', programName, 'submitAccept');
        });
    }

    if (rejectForm) {
        rejectForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const programName = event.target.dataset.programname || 'this program';
            await handleFormSubmit(rejectForm, rejectForm.getAttribute('action'), 'Reject', programName, 'submitRejectBtn');
        });
    }

    // Initialize DataTables with Modern Styling
    if ($.fn.DataTable && $('#approveprogram').length) {
       try {
            $('#approveprogram').DataTable({
                // Modern layout (Search on right, length on left)
                dom: '<"d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-3"lf>rt<"d-flex flex-column flex-md-row justify-content-between align-items-md-center mt-3"ip>',
                pageLength: 10,
                lengthMenu: [[5, 10, 20, 50, -1], [5, 10, 20, 50, "All"]],
                language: {
                    search: "_INPUT_",
                    searchPlaceholder: "Search programs..."
                }
            });
            
            // Style the DataTables native search input to match Bootstrap 5
            $('.dataTables_filter input').addClass('form-control form-control-sm modern-input d-inline-block w-auto');
            $('.dataTables_length select').addClass('form-select form-select-sm modern-select d-inline-block w-auto');

        } catch (e) {
            console.error("Error initializing DataTables:", e);
        }
    }
});