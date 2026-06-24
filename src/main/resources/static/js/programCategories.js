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

    modalEl.addEventListener('hidden.bs.modal', function (event) {
        window.location.href = url;
    }, { once: true });
}

/*
 * MAIN LOGIC
 */
$(document).ready(function () {
    $('#categorytable').DataTable({
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
                title: 'Program Categories',
                className: 'btn btn-success btn-modern btn-sm mb-3 shadow-sm',
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                }
            }
        ]
    });

    // Add New Button Logic
    $('#addft').on('click', function() {
        $('#mcoursecategoriesfid')[0].reset();
        $('#coursecategorycode').val('');
    });

    // Form Submit Logic
    $("#mcoursecategoriesfid").submit(function (e) {
        e.preventDefault();
        $.ajax({
            type: "POST",
            url: API.saveCourseCategory,
            data: $(this).serialize(),
            success: function (data) {
                const response = data.trim();

                const categoryModalEl = document.getElementById('category-modal');
                const categoryModal = bootstrap.Modal.getInstance(categoryModalEl);

                switch (response) {
                    case "2":
                        if (categoryModal) categoryModal.hide();

                        categoryModalEl.addEventListener('hidden.bs.modal', function () {
                           showModalAndRedirect('Successfully Saved!', window.location.href);
                        }, { once: true });
                        break;
                    case "1":
                        showModalAlert("Category Name Already Exists!");
                        $("#coursecategoryname").focus().val("");
                        break;
                    case "3":
                        showModalAlert("Category Name cannot be Empty!");
                        break;
                    case "4":
                        showModalAlert("Category Name should be 1-50 characters long!");
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
        if (this.value.match(/[^a-zA-Z.\- ]/g)) {
            this.value = this.value.replace(/[^a-zA-Z.\- ]/g, '');
        }
    });
});

/*
 * EVENT LISTENERS
 */
document.addEventListener('DOMContentLoaded', function () {
    const editButtons = document.querySelectorAll(".editbtn");

    editButtons.forEach(function (button) {
        button.addEventListener("click", function (event) {
            const courseCategoryCode = button.getAttribute("data-coursecategorycode");
            const courseCategoryName = button.getAttribute("data-coursecategoryname");
            const courseType = button.getAttribute("data-coursetype");

            editfunc(courseCategoryCode, courseCategoryName, courseType);
        });
    });
});

function editfunc(code, name, type) {
    $('#mcoursecategoriesfid')[0].reset();
    $("#coursecategorycode").val(code);
    $("#coursecategoryname").val(name);
    $("#coursetype").val(type);
}

function customReset() {
    window.location.reload();
}

/*
 * UTILITIES
 */
$("#menu-toggle").click(function (e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
});

$(document).ready(function () {
    $('#backtotop').click(function () {
        $("html, body").animate({scrollTop: 0}, 600);
        return false;
    });
});

$('.sub-menu ul').hide();
$('.sub-sub-menu ul').hide();
$(".sub-menu a").click(function () {
    $(this).parent(".sub-menu").children("ul").slideToggle("100");
    $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
});
$(".sub-sub-menu a").click(function () {
    $(this).parent(".sub-sub-menu").children("ul").slideToggle("100");
    $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
});