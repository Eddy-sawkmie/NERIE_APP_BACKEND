function showModalAlert(message, title = 'Message') {
    if (typeof $ !== 'undefined' && $.fn.modal) {
        $('#feedbackModalLabel').text(title);
        $('#feedbackModalBody').html(message);

        // Updated for Bootstrap 5
        $('#feedbackModal .modal-footer').html(
            '<button type="button" class="btn btn-primary btn-modern px-4" data-bs-dismiss="modal">OK</button>'
        );

        $('#feedbackModal').modal('show');
    } else {
        alert(`${title}: ${message.replace(/<br\/?>/gi, '\n')}`);
    }
}

function showModalAndRedirect(message, url) {
    showModalAlert(message, 'Success');
    $('#feedbackModal').one('hidden.bs.modal', function () {
        if (url) {
            window.location.href = url;
        } else {
            window.location.reload();
        }
    });
}

const createOption = (value, text, isSelected = false) => {
    const option = document.createElement('option');
    option.value = value;
    option.textContent = text;
    if (isSelected) {
        option.selected = true;
    }
    return option;
};

$(document).ready(function() {
    // Attach blur event to both fields
    $('#shorttermcoursecode, #longtermcoursecode').on('blur', function() {
        const $input = $(this);
        const value = $input.val().trim();
        const id = $input.attr('id');
        const officeId = $('#officecode').val(); // Get the ID of the record being edited
        const requiredLength = (id === 'shorttermcoursecode') ? 2 : 3;

        if (value.length === requiredLength) {
            $.ajax({
                url: '/offices/check-coursecodes-exists',
                type: 'GET',
                data: { 
                    code: value,
                    type: id,
                    excludeOfficeCode: officeId // Pass this to the backend
                },
                success: function(exists) {
                    if (exists === true) {
                        alert('This course code is already taken by another office.');
                        $input.val(''); 
                    }
                }
            });
        }
    });
});

document.addEventListener('DOMContentLoaded', () => {
    // --- DOM Elements ---
    const officeTable = document.getElementById('officetable');
    const officeTableBody = document.getElementById('officeTableBody');
    const addOfficeBtn = document.getElementById('addOfficeBtn');
    const officeModal = document.getElementById('office-modal');
    const officeForm = document.getElementById('mofficefid');
    const stateDropdown = document.getElementById('statecode');
    const districtDropdown = document.getElementById('districtcode');
    const mobileInput = document.getElementById('mobileno');
    const emailInput = document.getElementById('emailid');
    const pincodeInput = document.getElementById('officepincode');
    const contactPersonInput = document.getElementById('contactpersonname');
    const messageContainer = document.getElementById('message-container');
    const mobileMsg = document.getElementById('mobileno-msg');
    const pincodeMsg = document.getElementById('pincode-msg');

    // --- Input Validation Helpers ---
    const allowNumbersOnly = (event) => {
        if (event.key.length === 1 && (event.key < '0' || event.key > '9')) {
            event.preventDefault();
        }
    };

    const allowAlphabetsOnly = (event) => {
        if (event.key.length === 1 && !/^[a-zA-Z. ]$/.test(event.key)) {
            event.preventDefault();
        }
    };

    const validateEmail = (email) => {
        const re = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$/;
        return re.test(String(email).toLowerCase());
    };

    // --- Attach Input Validators ---
    mobileInput?.addEventListener('keypress', allowNumbersOnly);
    pincodeInput?.addEventListener('keypress', allowNumbersOnly);
    contactPersonInput?.addEventListener('keypress', allowAlphabetsOnly);

    mobileInput?.addEventListener('input', () => {
       if (mobileInput.value.length > 0 && mobileInput.value.length < 10) {
           mobileMsg.innerHTML = '<i class="fas fa-exclamation-circle me-1"></i> Mobile no. should be 10 digits';
           mobileInput.setCustomValidity("Mobile no. should be 10 digits");
       } else {
           mobileMsg.textContent = "";
           mobileInput.setCustomValidity("");
       }
    });

    pincodeInput?.addEventListener('input', () => {
        if (pincodeInput.value.length > 0 && pincodeInput.value.length < 6) {
            pincodeMsg.innerHTML = '<i class="fas fa-exclamation-circle me-1"></i> Pincode should be 6 digits';
            pincodeInput.setCustomValidity("Pincode should be 6 digits");
        } else {
            pincodeMsg.textContent = "";
            pincodeInput.setCustomValidity("");
        }
    });

    emailInput?.addEventListener('blur', () => {
        if (emailInput.value && !validateEmail(emailInput.value)) {
            emailInput.classList.add('is-invalid');
        } else {
            emailInput.classList.remove('is-invalid');
        }
    });

    // --- Event Listener for State Dropdown Change ---
    stateDropdown?.addEventListener('change', async (event) => {
        const stateCode = event.target.value;
        let districtToSelectAfterLoad = districtDropdown.value;
        if (districtDropdown.hasAttribute('data-select-after-load')) {
            districtToSelectAfterLoad = districtDropdown.getAttribute('data-select-after-load');
            districtDropdown.removeAttribute('data-select-after-load');
        }

        districtDropdown.innerHTML = '';
        districtDropdown.disabled = true;

        if (!stateCode) {
            districtDropdown.appendChild(createOption("", "-- Select State First --"));
            districtDropdown.disabled = false;
            return;
        }

        districtDropdown.appendChild(createOption("", "Loading..."));

        try {
            const response = await fetch(API.getDistrict, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `statecode=${encodeURIComponent(stateCode)}`
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const districts = await response.json();

            districtDropdown.innerHTML = '';
            districtDropdown.appendChild(createOption("", "-- Select District --"));

            if (districts && districts.length > 0) {
                districts.forEach(district => {
                    const isSelected = String(district.districtcode) === String(districtToSelectAfterLoad);
                    districtDropdown.appendChild(createOption(district.districtcode, district.districtname, isSelected));
                });
            } else {
                districtDropdown.appendChild(createOption("", "No districts found"));
            }

        } catch (error) {
            districtDropdown.innerHTML = '';
            districtDropdown.appendChild(createOption("", "-- Error Loading Districts --"));
            showModalAlert(`Failed to load districts: ${error.message}`, 'Error');
        } finally {
            districtDropdown.disabled = false;
        }
    });

    // --- Function to Populate Form for Editing ---
    const populateFormForEdit = (button) => {
        officeForm.reset();
        officeForm.classList.remove('was-validated');
        clearMessages();

        const data = button.dataset;
        document.getElementById('officecode').value = data.officecode || '';
        document.getElementById('officename').value = data.officename || '';
        document.getElementById('shorttermcoursecode').value = data.shorttermcoursecode || '';
        document.getElementById('longtermcoursecode').value = data.longtermcoursecode || '';
        document.getElementById('officeshortname').value = data.officeshortname || '';
        document.getElementById('officeaddress').value = data.officeaddress || '';
        document.getElementById('officepincode').value = data.officepincode || '';
        document.getElementById('contactpersonname').value = data.contactpersonname || '';
        document.getElementById('mobileno').value = data.mobileno || '';
        document.getElementById('landlineno').value = data.landlineno || '';
        document.getElementById('emailid').value = data.emailid || '';

        const stateCode = data.statecode;
        const districtCodeToSelect = data.districtcode;

        districtDropdown.innerHTML = '';
        districtDropdown.disabled = true;

        if (stateCode) {
            stateDropdown.value = stateCode;

            if(districtCodeToSelect) {
                districtDropdown.setAttribute('data-select-after-load', districtCodeToSelect);
            }

            stateDropdown.dispatchEvent(new Event('change'));
        } else {
            stateDropdown.value = '';
            districtDropdown.appendChild(createOption("", "-- Select State First --"));
            districtDropdown.disabled = false;
        }
        mobileMsg.textContent = '';
        pincodeMsg.textContent = '';
    };

    // --- Event Listener for Edit Buttons ---
    officeTableBody?.addEventListener('click', (event) => {
        const editButton = event.target.closest('.officebtn');
        if (editButton) {
            populateFormForEdit(editButton);
        }
    });

    // --- Event Listener for "Add New" Button ---
    addOfficeBtn?.addEventListener('click', () => {
        officeForm.reset();
        officeForm.classList.remove('was-validated');
        clearMessages();
        document.getElementById('officecode').value = '';
        stateDropdown.value = '';
        stateDropdown.dispatchEvent(new Event('change'));
        mobileMsg.textContent = '';
        pincodeMsg.textContent = '';
    });

    const clearMessages = () => {
        const messageContainer = document.getElementById('message-container');
        if(messageContainer) messageContainer.innerHTML = '';
    };

    // --- Function to Handle Form Submission ---
    const handleFormSubmit = async (event) => {
        event.preventDefault();
        clearMessages();

        if (!officeForm.checkValidity()) {
            event.stopPropagation();
            officeForm.classList.add('was-validated');
            showModalAlert('Please fill all required fields correctly.', 'Validation Error');
            return;
        }
        officeForm.classList.add('was-validated');

        const formData = new FormData(officeForm);
        const urlEncodedData = new URLSearchParams();
        for (const pair of formData.entries()) {
            if (pair[0] === 'mstates.statecode') {
                urlEncodedData.append('mstates.statecode', pair[1]);
            } else if (pair[0] === 'mdistricts.districtcode') {
                urlEncodedData.append('mdistricts.districtcode', pair[1]);
            } else {
                urlEncodedData.append(pair[0], pair[1]);
            }
        }

        const headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
        const submitButton = document.getElementById('submitOfficeForm');
        submitButton.disabled = true;
        submitButton.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i> Saving...';

        try {
            const httpResponse = await fetch(officeForm.action, {
                method: 'POST',
                headers: headers,
                body: urlEncodedData.toString()
            });

            const responseBody = await httpResponse.text();

            if (httpResponse.ok && responseBody === "2") {
                $(officeModal).modal('hide');
                $(officeModal).one('hidden.bs.modal', function () {
                    showModalAndRedirect('Office details saved successfully!', window.location.href);
                });

            } else {
                let errorMsg = "Save Failed!";
                if (!httpResponse.ok) {
                    if(httpResponse.status === 401) errorMsg = "Save Failed: Authentication error.";
                    else if(httpResponse.status === 403) errorMsg = "Save Failed: Permission denied.";
                    else if(httpResponse.status === 404) errorMsg = "Save Failed: Endpoint not found.";
                    else errorMsg = `Save Failed! Server returned status ${httpResponse.status}.`;
                } else if(responseBody === "1"){
                    errorMsg = "An office with the same name or short name already exists.";
                } else {
                    errorMsg = "Save Failed! Unexpected server response.";
                }
                showModalAlert(errorMsg, 'Save Failed');
            }
        } catch (error) {
            showModalAlert(`An error occurred: ${error.message || 'Please check connection and try again.'}`, 'Error');
        } finally {
            submitButton.disabled = false;
            submitButton.innerHTML = '<i class="fas fa-save me-2"></i> Save Details';
        }
    };

    // --- Attach Submit Listener to Form ---
    officeForm?.addEventListener('submit', handleFormSubmit);

    // --- Modal Reset on Hidden ---
    if (typeof $ !== 'undefined' && $.fn.modal) {
        $(officeModal).on('hidden.bs.modal', function () {
            officeForm.reset();
            officeForm.classList.remove('was-validated');
            clearMessages();
            stateDropdown.value = '';
            stateDropdown.dispatchEvent(new Event('change'));
            mobileMsg.textContent = '';
            pincodeMsg.textContent = '';
        });
    }

    // --- Initialize DataTables ---
    if (typeof $ !== 'undefined' && $.fn.DataTable && $(officeTable).length) {
        try {
            $(officeTable).DataTable({
                dom: '<"d-flex justify-content-between align-items-center mb-3"Bf>rt<"d-flex justify-content-between align-items-center mt-3"ip>',
                pageLength: 10,
                lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "All"]],
                
                // Add this block to handle the empty state natively!
                language: {
                    search: "_INPUT_",
                    searchPlaceholder: "Search offices...",
                    emptyTable: $('#emptyTableMessage').html()
                },
                
                buttons: [
                    {
                        extend: 'excelHtml5',
                        text: '<i class="fas fa-file-excel me-1"></i> Export Excel',
                        title: 'Offices List',
                        exportOptions: {
                            columns: ':visible:not(.noExport)'
                        },
                        className: 'btn btn-sm btn-success btn-modern'
                    }
                ]
            });
            
            // Style the DataTables search input
            $('.dataTables_filter input').addClass('form-control form-control-sm modern-select d-inline-block w-auto');
            
        } catch (e) {
            showModalAlert('Could not initialize the office list table.', 'Initialization Error');
        }
    }
});