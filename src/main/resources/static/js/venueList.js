/*
 * HELPER FUNCTIONS
 */
function showModalAlert(message, title = 'Message') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);

    $('#feedbackModal .modal-footer').html(
        '<button type="button" class="btn btn-primary btn-modern px-4" data-bs-dismiss="modal">OK</button>'
    );

    var modalElement = document.getElementById('feedbackModal');

    // Move modal to the body to prevent backdrop overlapping issues
    if (modalElement.parentNode !== document.body) {
        document.body.appendChild(modalElement);
    }

    // Use getOrCreateInstance to prevent double-backdrop bugs
    var myModal = bootstrap.Modal.getOrCreateInstance(modalElement);
    myModal.show();
}

function showModalAndRedirect(message, url) {
    showModalAlert(message);
    const modalEl = document.getElementById('feedbackModal');

    // Ensure we only attach the listener once
    modalEl.addEventListener('hidden.bs.modal', function (event) {
        window.location.href = url;
    }, { once: true });
}

/*
 * MAIN LOGIC
 */
$(document).ready(function () {
    $('#venuetable').DataTable({
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
                title: 'Venues',
                className: 'btn btn-success btn-modern btn-sm mb-3 shadow-sm',
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                }
            }
        ]
    });

    // Add New Button Logic
    $('#addft').on('click', function() {
        $('#mvenuefid')[0].reset();
        $('#venuecode').val('');
    });

    // Form Submit Logic
    $("#mvenuefid").submit(function (e) {
        e.preventDefault();
        $.ajax({
            type: "POST",
            url: API.saveVenue,
            data: $(this).serialize(),
            success: function (data) {
                const response = data.trim();

                const venueModalEl = document.getElementById('venue-modal');
                const venueModal = bootstrap.Modal.getInstance(venueModalEl);

                switch (response) {
                    case "2":
                        if (venueModal) venueModal.hide();

                        venueModalEl.addEventListener('hidden.bs.modal', function () {
                            showModalAndRedirect('Successfully Saved!', window.location.href);
                        }, { once: true });
                        break;
                    case "1":
                        showModalAlert("Venue Already Exists!");
                        $("#venuename").focus().val("");
                        break;
                    case "3":
                        showModalAlert("Venue Name cannot be Empty!");
                        break;
                    case "4":
                        showModalAlert("Venue Name should be 1-100 characters long!");
                        break;
                    default:
                        showModalAlert("Save Failed! An unexpected error occurred.");
                        break;
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                showModalAlert(`An error occurred while saving. Please try again.<br><b>Error:</b> ${errorThrown}`);
            }
        });
    });

    $('input.alphabets').keyup(function () {
        if (this.value.match(/[^0-9a-zA-Z.\- ]/g)) {
            this.value = this.value.replace(/[^0-9a-zA-Z.\- ]/g, '');
        }
    });
});

/*
 * EVENT LISTENERS
 */
document.addEventListener("DOMContentLoaded", function () {
    const editVenueButtons = document.querySelectorAll('.editvenuebtn');

    editVenueButtons.forEach(function (button) {
        button.addEventListener('click', function (event) {
            const venueCode = button.getAttribute('data-param1');
            const venueName = button.getAttribute('data-param2');
            editfunc(venueCode, venueName);
        });
    });
});

function editfunc(code, name) {
    $('#mvenuefid')[0].reset();
    $("#venuecode").val(code);
    $("#venuename").val(name);
}

function customReset() {
    window.location.reload();
}