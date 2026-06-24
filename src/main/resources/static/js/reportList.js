// Helper Function for Modal Alerts (Replaces native alert())
function showModalAlert(message, title = 'Message') {
    if (typeof $ !== 'undefined' && $.fn.modal) {
        $('#feedbackModalLabel').text(title);
        $('#feedbackModalBody').html(message);
        $('#feedbackModal .modal-footer').html(
            '<button type="button" class="btn btn-primary btn-modern px-4" data-bs-dismiss="modal">OK</button>'
        );
        $('#feedbackModal').modal('show');
    } else {
        alert(`${title}: ${message.replace(/<br\/?>/gi, '\n')}`);
    }
}

// Function to fetch financial years based on office code
async function fetchFinancialYears(officeCode) {
    const finYearSelect = document.getElementById('finyear');
    finYearSelect.innerHTML = '';
    finYearSelect.appendChild(new Option("Loading...", ""));
    finYearSelect.disabled = true;

    if (!officeCode || officeCode === "-1") {
        finYearSelect.innerHTML = '';
        finYearSelect.appendChild(new Option("-- Select --", ""));
        finYearSelect.disabled = false;
        return;
    }

    try {
        const url = API.getReportFinancialYear;

        const requestBody = new URLSearchParams();
        requestBody.append('officecode', officeCode);

        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: requestBody
        });

        if (!response.ok) {
            console.error(`HTTP error! status: ${response.status}`);
            if (response.status === 404) {
                 throw new Error("Endpoint not found.");
            } else if (response.status === 405) {
                 throw new Error("HTTP method not allowed. Check server logs or controller annotations.");
            } else {
                 throw new Error(`Request failed with status ${response.status}`);
            }
        }

        const data = await response.json();

        finYearSelect.innerHTML = '';
        finYearSelect.appendChild(new Option("-- Select --", ""));

        if (data && data.length > 0) {
            data.forEach(item => {
                if (item && item.length === 2) {
                    finYearSelect.appendChild(new Option(item[1], item[0]));
                }
            });
        } else {
             finYearSelect.appendChild(new Option("No financial years found", ""));
        }

    } catch (error) {
        console.error("Error fetching financial years:", error);
        finYearSelect.innerHTML = '';
        finYearSelect.appendChild(new Option("Error loading", ""));
        showModalAlert("Failed to load financial years: " + error.message, "Data Error");
    } finally {
         finYearSelect.disabled = false;
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const officeCodeSelect = document.getElementById('officecode');
    const submitButton = document.getElementById('submit');
    const finYearSelect = document.getElementById('finyear');

    officeCodeSelect.addEventListener('change', function() {
        fetchFinancialYears(this.value);
    });

    submitButton.addEventListener('click', function() {
        getOfficewisedetailsreportfunc();
    });

    if (officeCodeSelect.value && officeCodeSelect.value !== '-1' && officeCodeSelect.value !== '') {
        fetchFinancialYears(officeCodeSelect.value);
    } else {
        finYearSelect.innerHTML = '';
        finYearSelect.appendChild(new Option("-- Select --", ""));
        finYearSelect.disabled = false;
    }
});

function getOfficewisedetailsreportfunc() {
    const reportStatusSelect = document.getElementById("reportstatus");
    const officeSelect = document.getElementById("officecode");
    const finYearSelect = document.getElementById("finyear");

    // Input validation (Now using Modals instead of alerts)
    if (reportStatusSelect.value === "-1") {
        showModalAlert('<i class="fas fa-exclamation-triangle text-warning me-2"></i>Please select a report type.', 'Validation Error');
        reportStatusSelect.focus();
        return false;
    }
    if (officeSelect.value === "-1") {
        showModalAlert('<i class="fas fa-exclamation-triangle text-warning me-2"></i>Please select an office.', 'Validation Error');
        officeSelect.focus();
        return false;
    }

    let finyearstart = 0;
    let finyearend = 0;
    let financialyear = "";
    const fyValue = finYearSelect.value;

    if (fyValue !== "") {
        const fyParts = fyValue.split("##");
        if (fyParts.length >= 2) {
            finyearstart = fyParts[0];
            finyearend = fyParts[1];
        } else {
            console.warn("Financial year value does not contain '##':", fyValue);
        }

        if (finYearSelect.selectedIndex >= 0 && finYearSelect.options.length > 0) {
            financialyear = finYearSelect.options[finYearSelect.selectedIndex].text;
        } else {
            console.warn("No option selected in Financial Year dropdown.");
            financialyear = "";
        }
    } else {
        financialyear = "";
        finyearstart = 0;
        finyearend = 0;
    }

    const url = `${API.getReport}?status=${reportStatusSelect.value}` +
               `&finyearstart=${finyearstart}` +
               `&finyearend=${finyearend}` +
               `&financialyear=${encodeURIComponent(financialyear)}` +
               `&officecode=${encodeURIComponent(officeSelect.value)}`;

    // Optional: Visual feedback on button before opening the new tab
    const btn = document.getElementById('submit');
    const originalText = btn.innerHTML;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i> Generating...';
    btn.disabled = true;

    setTimeout(() => {
        window.open(url, '_blank');
        btn.innerHTML = originalText;
        btn.disabled = false;
    }, 500); // 500ms delay just to show user interaction occurred

    return true;
}