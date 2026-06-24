function showModalAlert(message, title = 'Message') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);
    $('#feedbackModal .modal-footer').html(
        '<button type="button" class="btn btn-primary" data-bs-dismiss="modal">OK</button>'
    );
    var myModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('feedbackModal'));
    myModal.show();
}

function showModalAndReload(message, title = 'Success') {
    showModalAlert(message, title);
    const modalEl = document.getElementById('feedbackModal');
    modalEl.addEventListener("hidden.bs.modal", function () {
        window.location.reload();
    }, { once: true });
}

function handleAjaxError(jqXHR, textStatus, errorThrown) {
    let errorMessage = `An error occurred: ${textStatus} - ${errorThrown}`;
    if (jqXHR.responseText) {
        errorMessage += `<br><br>Details: ${jqXHR.responseText}`;
    }
    showModalAlert(errorMessage, 'Request Failed');
}

$(document).ready(function () {
    $("#rpqualification").select2();
    $("#rpqualificationcategory").select2();
    $("#qualificationsubjectcode").select2();
    $("#designationcodess").select2();

    $(".select2-search").select2({
        placeholder: "Select Option",
        allowClear: false,
        width: '100%'
    });
});

function customReset() {
    window.location.reload();
}

function editfunc(rpslno, rpemailid, rpname, designation, rpqualification, rpspecialization, rpinstitutename, rpresidentialaddress, rpofficephone, rpresidencephone, rpmobileno, rpfax, rpofficeaddress, qualificationcategory, qualificationsubjectcode) {
    $("#rpemailid").focus();
    $("#rpslno").val(rpslno);
    $("#rpemailid").val(rpemailid);
    $("#rpname").val(rpname);
    $("#designationcodess").val(designation).trigger('change');
    $("#rpqualificationcategory").val(qualificationcategory).trigger('change');
    $("#rpqualification").val(rpqualification).trigger('change');
    $("#qualificationsubjectcode").val(qualificationsubjectcode).trigger('change');
    $("#rpspecialization").val(rpspecialization);
    $("#rpinstitutename").val(rpinstitutename);
    $("#rpofficeaddress").val(rpofficeaddress);
    $("#rpresidentialaddress").val(rpresidentialaddress);
    $("#rpofficephone").val(rpofficephone);
    $("#rpresidencephone").val(rpresidencephone);
    $("#rpmobileno").val(rpmobileno);
    $("#rpfax").val(rpfax);

    // Automatically scroll to top
    $("html, body").animate({ scrollTop: 0 }, 600);
}

$(document).ready(function () {
    $("#resetButton").on("click", customReset);

    $('#usertable').on('click', '.clickme.danger11', function(e) {
        e.preventDefault();
        const button = $(this);
        editfunc(
            button.data('rpslno'), button.data('rpemailid'), button.data('rpname'),
            button.data('designationcode'), button.data('qualificationcode'),
            button.data('rpspecialization'), button.data('rpinstitutename'),
            button.data('rpresidentialaddress'), button.data('rpofficephone'),
            button.data('rpresidencephone'), button.data('rpmobileno'),
            button.data('rpfax'), button.data('rpofficeaddress'),
            button.data('qualificationcategorycode'), button.data('qualificationsubjectcode')
        );
    });

    $("#menu-toggle").click(function (e) {
        e.preventDefault();
        $("#wrapper").toggleClass("toggled");
    });

    $('#usertable').DataTable({
        ordering: false,
        pageLength: 5,
        lengthMenu: [[5, 10, 20, 50, -1], [5, 10, 20, 50, "All"]],
        dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
             'rtip',
        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                title: 'Resource Person Data',
                className: 'btn btn-success btn-sm mb-3',
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                }
            }
        ]
    });

    $("#mtresourcepersonfid").submit(function (e) {
        e.preventDefault();
        $.ajax({
            type: "POST",
            url: API.saveResourcePerson,
            data: $("#mtresourcepersonfid").serialize(),
            success: function (data) {
                const response = data.trim();
                switch (response) {
                    case "2":
                        showModalAndReload("Successfully Saved!!!");
                        break;
                    case "1":
                        showModalAlert("Email id already registered");
                        $("#rpemailid").focus();
                        break;
                    case "3":
                        showModalAlert("Email id cannot be empty");
                        $("#rpemailid").focus();
                        break;
                    case "4":
                        showModalAlert("Email id should be 1-50 characters long");
                        $("#rpemailid").focus();
                        break;
                    case "7":
                        showModalAlert("Institute Name cannot be empty");
                        $("#rpinstitutename").focus();
                        break;
                    case "8":
                        showModalAlert("Institute Name should be 1-100 characters long");
                        $("#rpinstitutename").focus();
                        break;
                    case "9":
                        showModalAlert("Mobile cannot be empty");
                        $("#rpmobileno").focus();
                        break;
                    case "10":
                        showModalAlert("Mobile should be 10 digits long");
                        $("#rpmobileno").focus();
                        break;
                    case "11":
                        showModalAlert("Name cannot be empty");
                        $("#rpname").focus();
                        break;
                    case "12":
                        showModalAlert("Name should be 1-50 characters long");
                        $("#rpname").focus();
                        break;
                    case "15":
                        showModalAlert("Specialization cannot be empty");
                        $("#rpspecialization").focus();
                        break;
                    case "16":
                        showModalAlert("Specialization should be 1-50 characters long");
                        $("#rpspecialization").focus();
                        break;
                    case "17":
                        showModalAlert("Office address cannot be empty");
                        $("#rpofficeaddress").focus();
                        break;
                    case "18":
                        showModalAlert("Office address should be 1-100 characters long");
                        $("#rpofficeaddress").focus();
                        break;
                    case "19":
                        showModalAlert("Residence address cannot be empty");
                        $("#rpresidentialaddress").focus();
                        break;
                    case "20":
                        showModalAlert("Residence address should be 1-100 characters long");
                        $("#rpresidentialaddress").focus();
                        break;
                    case "21":
                        showModalAlert('Qualification name not entered');
                        break;
                    case "22":
                        showModalAlert('Qualification name already exists');
                        break;
                    case "23":
                        showModalAlert('Designation name not entered');
                        break;
                    case "24":
                        showModalAlert('Designation name already exists');
                        break;
                    default:
                        showModalAlert("Save Failed!!!");
                        break;
                }
            },
            error: handleAjaxError
        });
    });

    $("#rpqualification").change(function () {
        $.ajax({
            type: "GET",
            async: false,
            url: API.getQualificationSubject,
            data: "qualificationcode=" + $('#rpqualification').val(),
            success: function (data) {
                $('#qualificationsubjectcode').empty().append($("<option>").val('0').text("Not Applicable"));
                data.forEach(function(item) {
                    $('#qualificationsubjectcode').append($("<option>").val(item[0]).text(item[1]));
                });
            },
            error: handleAjaxError
        });
    });

    $("#qclist, #qlist, #dlist").hide();

    $("#rpmobileno").focusout(function () {
        var m = $('#rpmobileno').val();
        if (m.length > 0 && m.length < 10) {
            $('#msg1').html("Mobile no. should be 10 digits");
            $("#rpmobileno").focus();
        } else {
            $('#msg1').html("");
        }
    });

    $("#rpmobileno").keypress(function () {
        if ($('#rpmobileno').val().length === 9) {
            $('#msg1').html("");
        }
    });

    $('input.alphabets').keyup(function () {
        if (this.value.match(/[^a-zA-Z. ]/g)) {
            this.value = this.value.replace(/[^a-zA-Z. ]/g, '');
        }
    });

    $(document).on("keypress", ".numbers", function (event) {
        if (event.which < 48 || event.which > 57) {
            event.preventDefault();
        }
    });

    $('input.numberspl').keyup(function () {
        if (this.value.match(/[^0-9\ -]/g)) {
            this.value = this.value.replace(/[^0-9\ -]/g, '');
        }
    });

    $("#rpemailid").focusout(function () {
        var re = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$/;
        var email = $("#rpemailid").val().trim();

        // Grab the hidden ID field. If it has a value, we are in Edit Mode.
        var slno = $("#rpslno").val();

        if (email.length !== 0) {
            if (re.test(email) === false) {
                showModalAlert("Please enter a valid Email ID");
                $("#rpemailid").val("").focus();

            } else if (!slno) {
                // ONLY DO THE AJAX CHECK IF slno IS EMPTY (CREATE MODE)
                $.ajax({
                    type: "POST",
                    url: API.checkIfEmailIsAvailable,
                    data: "rpemailid=" + email,
                    success: function (data) {
                        if (data === "1") {
                            showModalAlert("Email ID already registered");
                            $("#rpemailid").val("").focus();
                        }
                    },
                    error: handleAjaxError
                });
            }
        }
    });

    $("#designationcodess").change(function () {
        if ($(this).val() === "others") {
            $("#dlist").show();
            $("#dinput").prop("required", true);
        } else {
            $("#dlist").hide();
            $("#dinput").prop("required", false);
        }
    });

    $("#rpqualification").change(function () {
        if ($(this).val() === "others") {
            $("#qclist").show();
            $("#qcinput").prop("required", true);
        } else {
            $("#qclist").hide();
            $("#qcinput").prop("required", false);
        }
    });

    $('#backtotop').click(function () {
        $("html, body").animate({ scrollTop: 0 }, 600);
        return false;
    });

    $('#rpqualificationcategory').change(function () {
        if ($('#rpqualificationcategory').val() !== '-1') {
            callCustomAjaxasync(API.listQualification, "qualificationcategorycode=" + $('#rpqualificationcategory').val(), function (data) {
                $('#rpqualification').empty().append($('<option></option>').attr('value', '-1').text('Select'));
                data.forEach(function (item) {
                    $('#rpqualification').append($('<option></option>').attr('value', item.qualificationcode).text(item.qualificationname));
                });
                $('#rpqualification').append($('<option></option>').attr('value', 'others').text('Others')).val('-1').trigger('change');
            });
        } else {
            $('#rpqualification').empty().append($('<option></option>').attr('value', '-1').text('Select')).val('-1').trigger('change');
        }
    });

    $('#usertable tbody').on('click', 'td a.showmore', function (e) {
        e.preventDefault();
        var row = $(this).closest('tr');
        var morespan = row.find('span.more');
        var l = row.find('span.less');
        if (morespan.is(':hidden')) {
            $(this).text("show less...");
            l.hide();
            morespan.show();
        } else {
            $(this).text("show more...");
            l.show();
            morespan.hide();
        }
    });

    $('.sub-menu ul, .sub-sub-menu ul').hide();
    
    $(".sub-menu a").click(function () {
        $(this).parent(".sub-menu").children("ul").slideToggle("100");
        $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
    });

    $(".sub-sub-menu a").click(function () {
        $(this).parent(".sub-sub-menu").children("ul").slideToggle("100");
        $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
    });
});