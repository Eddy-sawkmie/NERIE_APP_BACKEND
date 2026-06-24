// --- MODAL HELPER FUNCTIONS ---
function showModalAlert(message, title = 'Message') {
    document.getElementById('feedbackModalLabel').textContent = title;
    document.getElementById('feedbackModalBody').innerHTML = message;

    document.getElementById('feedbackModalFooter').innerHTML =
        '<button type="button" class="btn btn-primary btn-modern px-4" data-bs-dismiss="modal">OK</button>';

    const feedbackModalEl = document.getElementById('feedbackModal');

    if (feedbackModalEl.parentNode !== document.body) {
        document.body.appendChild(feedbackModalEl);
    }

    const modalInstance = bootstrap.Modal.getOrCreateInstance(feedbackModalEl);
    modalInstance.show();
}

function showModalAndRedirect(message, url) {
    showModalAlert(message);
    const feedbackModalEl = document.getElementById('feedbackModal');
    feedbackModalEl.addEventListener('hidden.bs.modal', function () {
        window.location.href = url;
    }, { once: true });
}

function showModalConfirm(message, callback, title = 'Confirmation') {
    document.getElementById('feedbackModalLabel').textContent = title;
    document.getElementById('feedbackModalBody').innerHTML = message;

    document.getElementById('feedbackModalFooter').innerHTML =
        '<button type="button" class="btn btn-light border btn-modern px-4" data-bs-dismiss="modal">Cancel</button>' +
        '<button type="button" id="modalConfirmOkButton" class="btn btn-danger btn-modern px-4">Delete</button>';

    const feedbackModalEl = document.getElementById('feedbackModal');

    if (feedbackModalEl.parentNode !== document.body) {
        document.body.appendChild(feedbackModalEl);
    }

    const modalInstance = bootstrap.Modal.getOrCreateInstance(feedbackModalEl);
    modalInstance.show();

    const confirmBtn = document.getElementById('modalConfirmOkButton');
    const newConfirmBtn = confirmBtn.cloneNode(true);
    confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);

    newConfirmBtn.addEventListener('click', function() {
        modalInstance.hide();
        callback();
    }, { once: true });
}

// --- CORE LOGIC ---
document.addEventListener('DOMContentLoaded', () => {

    // Generate Financial Years dynamically
    const d = new Date();
    const m = d.getMonth() + 1;
    const y = d.getFullYear();
    const fySelect = document.getElementById('financialyear');

    for (let i = 0; i < 5; i++) {
        let finyear = "", finyearstart = "", finyearend = "";
        if (m > 3) {
            finyear = `${y + i}-${String(y + i + 1).substring(2)}`;
            finyearstart = `${y + i}-04`;
            finyearend = `${y + i + 1}-03`;
        } else {
            finyear = `${y + i - 1}-${String(y + i).substring(2)}`;
            finyearstart = `${y + i - 1}-04`;
            finyearend = `${y + i}-03`;
        }
        const option = document.createElement('option');
        option.value = `${finyearstart}##${finyearend}`;
        option.textContent = finyear;
        fySelect.appendChild(option);
    }

    // Handle Financial Year Change
    fySelect.addEventListener('change', function () {
        document.getElementById('holidaydate').value = "";
        document.getElementById('holidayreason').value = "";
        const fy = this.value;

        if (fy !== "-1") {
            const [finyearstart, finyearend] = fy.split("##");

            // jQuery Datepicker Initialization based on Financial Year
            if (typeof $ !== 'undefined' && $.fn.datepicker) {
                const datepickerInput = $('#holidaydate');
                if (datepickerInput.hasClass('hasDatepicker')) {
                    datepickerInput.datepicker('destroy');
                }

                datepickerInput.datepicker({
                    minDate: new Date(Number(finyearstart.split("-")[0]), 3, 1),
                    maxDate: new Date(Number(finyearend.split("-")[0]), 2, 31),
                    dateFormat: 'dd/mm/yy',
                    changeMonth: true,
                    changeYear: true
                });
            }

            getholidaylist(finyearstart, finyearend);
        } else {
            document.getElementById('tablediv').innerHTML = "";
        }
    });

    // Handle Form Submit (Fetch API)
    const holidayForm = document.getElementById('mholidayfid');
    holidayForm.addEventListener('submit', async function (e) {
        e.preventDefault();

        if (!holidayForm.checkValidity()) {
            e.stopPropagation();
            holidayForm.classList.add('was-validated');
            return;
        }

        const holidayDateVal = document.getElementById('holidaydate').value;
        if (!holidayDateVal) {
            showModalAlert("Please select the holiday date.", "Validation Error");
            document.getElementById('holidaydate').focus();
            return;
        }

        const submitBtn = document.getElementById('submitBtn');
        const originalText = submitBtn.innerHTML;
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span> Saving...';

        try {
            const response = await fetch(API.saveHolidays, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams(new FormData(holidayForm)).toString()
            });

            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const data = await response.text();

            if (data.trim() === "2") {
                showModalAlert("Successfully Saved!", "Success");
                customReset(); // reset inputs

                const fy = document.getElementById('financialyear').value;
                if (fy !== "-1") {
                    const [finyearstart, finyearend] = fy.split("##");
                    getholidaylist(finyearstart, finyearend);
                }
            } else {
                showModalAlert("Save Failed! Please try again.", "Error");
            }
        } catch (error) {
            showModalAlert(`An error occurred: ${error.message}`, "Error");
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalText;
            holidayForm.classList.remove('was-validated');
        }
    });

    // Handle Reset Button
    document.getElementById('resetBtn')?.addEventListener('click', customReset);

    // Event Delegation for Edit & Delete buttons inside the dynamic table
    document.getElementById('tablediv').addEventListener('click', function(event) {
        // Edit
        const editBtn = event.target.closest('.editfunc-btn');
        if (editBtn) {
            event.preventDefault();
            const date = editBtn.dataset.param1;
            const reason = editBtn.dataset.param2;
            editfunc(date, reason);
            window.scrollTo({ top: 0, behavior: 'smooth' }); // Scroll up to form
        }

        // Delete
        const deleteBtn = event.target.closest('.deletesubjectbtn');
        if (deleteBtn) {
            event.preventDefault();
            const date = deleteBtn.dataset.param1;
            deletefunc(date);
        }
    });
});

// --- HELPER FUNCTIONS ---
async function getholidaylist(finyearstart, finyearend) {
    const tableDiv = document.getElementById('tablediv');

    // Show loading state
    tableDiv.innerHTML = `
        <div class="text-center text-primary py-4">
            <div class="spinner-border me-2" role="status"></div>
            <span>Loading holidays...</span>
        </div>
    `;

    try {
        const response = await fetch(API.listHolidays, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ finyearstart, finyearend }).toString()
        });

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

        const data = await response.json();

        // Build Modern Table structure
        let tableHtml = `
            <div class="table-responsive table-wrapper border-top pt-4">
                <h5 class="mb-3 fw-bold text-dark"><i class="fas fa-list me-2"></i> Holiday List</h5>
                <table class="table modern-table align-middle mb-0 w-100" id="htable">
                    <thead>
                        <tr>
                            <th style="width: 10%;" class="text-center">Sl No.</th>
                            <th style="width: 25%;">Holiday Date</th>
                            <th style="width: 45%;">Description</th>
                            <th style="width: 10%;" class="text-center noExport">Edit</th>
                            <th style="width: 10%;" class="text-center noExport">Delete</th>
                        </tr>
                    </thead>
                    <tbody>
        `;

        data.forEach((row, index) => {
            const [date, reason] = row;
            tableHtml += `
                <tr>
                    <td class="text-center fw-semibold text-muted">${index + 1}</td>
                    <td><span class="badge bg-light text-dark border d-inline-block px-2 py-1"><i class="far fa-calendar-alt me-1 p-2"></i> ${date}</span></td>
                    <td><span class="fw-bold text-dark">${reason}</span></td>
                    <td class="text-center">
                        <button class="btn btn-sm btn-primary btn-modern editfunc-btn" data-param1="${date}" data-param2="${reason}">
                            <i class="fas fa-edit"></i>
                        </button>
                    </td>
                    <td class="text-center">
                        <button class="btn btn-sm btn-danger btn-modern deletesubjectbtn" data-param1="${date}">
                            <i class="fas fa-trash-alt"></i>
                        </button>
                    </td>
                </tr>
            `;
        });

        tableHtml += '</tbody></table></div>';
        tableDiv.innerHTML = tableHtml;

        // Re-initialize DataTable using jQuery
        if (typeof $ !== 'undefined' && $.fn.DataTable) {
            $('#htable').DataTable({
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
                        title: 'Holiday List',
                        className: 'btn btn-success btn-modern btn-sm mb-3 shadow-sm',
                        exportOptions: {
                            columns: ':visible:not(.noExport)'
                        }
                    }
                ]
            });
        }
    } catch (error) {
        showModalAlert(`Failed to fetch holiday list: ${error.message}`, "Error");
        tableDiv.innerHTML = "";
    }
}

function editfunc(hdate, hreason) {
    // Handling standard DB date format parsing to dd/mm/yyyy
    const parts = hdate.split('-');
    if (parts.length === 3) {
        document.getElementById('holidaydate').value = `${parts[2]}/${parts[1]}/${parts[0]}`;
        document.getElementById('oldholidaydate').value = hdate;
    } else {
        document.getElementById('holidaydate').value = hdate;
    }

    const reasonInput = document.getElementById('holidayreason');
    reasonInput.value = hreason;
    reasonInput.focus();
}

function deletefunc(hdate) {
    showModalConfirm(`Are you sure you want to delete the holiday on <b>${hdate}</b>?`, async function() {
        try {
            const response = await fetch(API.removeHolidays, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({ holidaydate: hdate }).toString()
            });

            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const data = await response.text();

            if (data.trim() === "1") {
                showModalAlert("Holiday deleted successfully!", "Success");
                const fy = document.getElementById('financialyear').value;
                if (fy !== "-1") {
                    const [finyearstart, finyearend] = fy.split("##");
                    getholidaylist(finyearstart, finyearend);
                }
            } else {
                showModalAndRedirect("Error Occurred while deleting! Please try again.", API.redirectURL);
            }
        } catch (error) {
            showModalAlert(`An error occurred: ${error.message}`, "Error");
        }
    });
}

function customReset() {
    document.getElementById('holidaydate').value = "";
    document.getElementById('oldholidaydate').value = "";
    document.getElementById('holidayreason').value = "";
    document.getElementById('mholidayfid').classList.remove('was-validated');
}

// --- UI Toggle Handlers ---
document.getElementById('menu-toggle')?.addEventListener('click', function(e) {
    e.preventDefault();
    document.getElementById('wrapper')?.classList.toggle('toggled');
});

document.getElementById('backtotop')?.addEventListener('click', function(e) {
    e.preventDefault();
    window.scrollTo({ top: 0, behavior: 'smooth' });
});