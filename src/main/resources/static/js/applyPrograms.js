$(document).ready(function() {
    console.log("Apply Programs JS Loaded");

    // =================================================================
    // MODAL HELPER FUNCTION (With BS5 Z-Index Fix)
    // =================================================================
    function showFeedbackModal(message, title = 'Message', onOkCallback = null) {
        $('#feedbackModalLabel').text(title);
        $('#feedbackModalBody').html(message);

        // Set modern button style
        $('#feedbackModal .modal-footer').html(
            '<button type="button" id="modalOkButton" class="btn btn-primary btn-modern px-4" data-bs-dismiss="modal">OK</button>'
        );

        const okButton = $('#modalOkButton');
        const feedbackModalEl = document.getElementById('feedbackModal');

        // Move to body to prevent dimming/z-index issues
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
    // POPULATE APPLY MODAL
    // =================================================================
    const upcomingTable = document.getElementById('upcomingProgramsTable');
    const applyForm = document.getElementById('applyProgramForm');

    if (upcomingTable) {
        upcomingTable.addEventListener('click', (event) => {
            const target = event.target;
            const applyButton = target.closest('.apply-program-btn');

            if (applyButton && applyForm) {
                const programName = applyButton.dataset.programname;
                const phaseId = applyButton.dataset.phaseid;

                document.getElementById('acceptprogramname').textContent = programName;
                document.getElementById('acceptprogramcode').value = phaseId;
                document.getElementById('acceptremarks').value = '';

                applyForm.dataset.programname = programName;
            }
        });
    }

    // =================================================================
    // FORM SUBMISSION (With Loading State)
    // =================================================================
    if (applyForm) {
        applyForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const programName = event.target.dataset.programname || 'this program';
            const phaseid = applyForm.querySelector('input[name="phaseid"]').value;
            const remarks = applyForm.querySelector('textarea[name="remarks"]').value;

            if (!phaseid) {
                showFeedbackModal('Error: Program Code is missing.', 'Error');
                return;
            }

            const formData = new URLSearchParams();
            formData.append('phaseid', phaseid);
            formData.append('remarks', remarks);

            // Visual loading state
            const submitBtn = document.getElementById('submitApply');
            const originalText = submitBtn.innerHTML;
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i> Submitting...';

            try {
                const response = await fetch(applyForm.getAttribute('action'), {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: formData.toString(),
                });

                const responseBody = await response.text();

                // Safely hide the apply modal via BS5
                const parentModal = bootstrap.Modal.getInstance(document.getElementById('modalapply-modal'));
                if (parentModal) parentModal.hide();

                if (response.ok && responseBody === "2") {
                    showFeedbackModal('Application submitted successfully!', 'Success', () => {
                        window.location.reload();
                    });
                } else {
                    console.error(`Apply failed. Status: ${response.status}, Body: ${responseBody}`);
                    showFeedbackModal(`Submission Failed. Please try again.`, 'Error');
                }
            } catch (error) {
                console.error('Network error during form submission:', error);
                showFeedbackModal('A network error occurred. Please check your connection.', 'Error');
            } finally {
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalText;
            }
        });
    }

    // =================================================================
    // INITIALIZE DATATABLES (Modern UI)
    // =================================================================
    if ($.fn.DataTable) {
        
        // Define common modern DOM layout for DataTables
        const modernDomLayout = '<"d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-3"lf>rt<"d-flex flex-column flex-md-row justify-content-between align-items-md-center mt-3"ip>';

        if ($('#upcomingProgramsTable').length) {
            try {
                $('#upcomingProgramsTable').DataTable({
                    dom: modernDomLayout,
                    pageLength: 5,
                    lengthMenu: [[5, 10, 20, 50, -1], [5, 10, 20, 50, "All"]],
                    language: {
                        search: "_INPUT_",
                        searchPlaceholder: "Search programs...",
                        emptyTable: $('#upcomingEmptyMsg').html()
                    }
                });
            } catch (e) {
                console.error("Error initializing DataTables for upcoming programs:", e);
            }
        }

        if ($('#appliedProgramsTable').length) {
            try {
                $('#appliedProgramsTable').DataTable({
                    dom: modernDomLayout,
                    pageLength: 5,
                    lengthMenu: [[5, 10, 20, 50, -1], [5, 10, 20, 50, "All"]],
                    language: {
                        search: "_INPUT_",
                        searchPlaceholder: "Search pending...",
                        emptyTable: $('#appliedEmptyMsg').html()
                    }
                });
            } catch (e) {
                console.error("Error initializing DataTables for applied programs:", e);
            }
        }

        // Apply Bootstrap 5 styling to DataTables native inputs globally
        $('.dataTables_filter input').addClass('form-control form-control-sm modern-input d-inline-block w-auto');
        $('.dataTables_length select').addClass('form-select form-select-sm modern-select d-inline-block w-auto');
    }
});