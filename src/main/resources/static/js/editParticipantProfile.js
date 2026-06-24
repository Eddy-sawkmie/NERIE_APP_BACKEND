// --- Modal Helper Functions ---
function showModalAlert(message, title = 'Message') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);
    
    $('#feedbackModal .modal-footer').html(
        '<button type="button" id="modalOkButton" class="btn btn-primary btn-modern px-4" data-bs-dismiss="modal">OK</button>'
    );
    
    var modalElement = document.getElementById('feedbackModal');
    var myModal = bootstrap.Modal.getOrCreateInstance(modalElement);
    myModal.show();
}

function checkuserexistfunc() {
    var useridVal = $("#emailid").val().trim();
    if (useridVal.length > 0) {
        $.ajax({
            type: "POST", url: API.checkUserEmail, data: { userid: useridVal },
            success: function (data) {
                if (data == "1") {
                    showModalAlert("Email Id already exist!", "Duplicate Email");
                    $("#emailid").focus();
                }
            }, error: function () {
                showModalAlert("Error checking email.", "Error");
            }
        });
    }
}

function checkstate() {
    if ($("#statecode").val() === "" || $("#statecode").val() === null) {
        showModalAlert("Select Residential State first.", "Validation");
        if ($("#statecode").data('selectpicker')) {
            $("#statecode").selectpicker('toggle');
        } else {
            $("#statecode").focus();
        }
    }
}

$(document).ready(function () {
    var cancelbtn = document.getElementById('cancelbtn');
    cancelbtn.addEventListener('click', function() {
        document.getElementById('tparticipantfid').reset();  
        // Re-trigger visual updates
        $('#disabilityType').val(null).trigger('change');
        $('#isdifferentlyabled').trigger('change');
        $('#participantofficetypecode').trigger('change');
        $('#isminority').trigger('change');
    });
    
    if ($.fn.selectpicker) {
        $('.selectpicker').selectpicker();
    }

    //-------------------------------------------FOR DIFFERENTLY ABLED DETAILS--------------------------------------------------------
    $('#disabilityType').select2({
        placeholder: "--- Select Disability ---",
        width: '100%'
    });

    let dbValue = $('#differentlyableddetailsVal').val();
    if (dbValue) {
        let parts = dbValue.split(',').map(v => v.trim());
        let selectValues = [];
        let otherText = null;

        parts.forEach(function (p) {
            if (p.startsWith('Other (')) {
                let match = p.match(/\((.*?)\)/);
                if (match && match[1]) {
                    otherText = match[1];
                }
                selectValues.push('Other');
            } else {
                selectValues.push(p);
            }
        });

        $('#disabilityType').val(selectValues).trigger('change');

        if (otherText) {
            $('#otherDisabilityDiv').slideDown(250);
            $('#otherDisability').val(otherText).prop('required', true);
        }
    }

    const otherInput = $('#otherDisability');

    function toggleOtherField() {
        const selectedValues = $('#disabilityType').val() || [];
        const otherDisabilityDiv = $('#otherDisabilityDiv');
        if (selectedValues.includes('Other')) {
            otherDisabilityDiv.slideDown(250);
            otherInput.prop('required', true);
        } else {
            otherDisabilityDiv.slideUp(250);
            otherInput.prop('required', false).val('');
        }
    }

    $('#disabilityType').on('select2:select select2:unselect', function () {
        toggleOtherField();
    });

    toggleOtherField();

    $('#isdifferentlyabled').on('change', function () {
        if ($(this).val() === 'Y') {
            $('#differentlyableddetails').slideDown(250);
            $('#disabilityType').prop('required', true);
        } else {
            $('#differentlyableddetails').slideUp(250);
            $('#disabilityType').val(null).trigger('change');
            $('#disabilityType').prop('required', false);
            otherInput.prop('required', false).val('');
        }
    });

    //-------------------------------------------OFFICE TYPE--------------------------------------------------------
    function toggleOtherOfficeType() {
        if ($("#participantofficetypecode").val() == "5") {
            $(".otherparticipantofficetypediv").removeClass("d-none").hide().slideDown(250);
            $("#otherparticipantofficetype").prop("required", true);
        } else {
            $(".otherparticipantofficetypediv").slideUp(250, function() {
                $(this).addClass("d-none");
            });
            $("#otherparticipantofficetype").val("").prop("required", false);
        }
    }
    toggleOtherOfficeType();
    $("#participantofficetypecode").change(toggleOtherOfficeType);

    //-------------------------------------------MINORITY--------------------------------------------------------
    function toggleMinorityFields() {
        if ($("#isminority").val() == "Y") {
            $("#ifminoritydiv").removeClass("d-none").hide().slideDown(250);
            $("#minoritycode").prop("required", true).prop("disabled", false);
            if ($("#minoritycode").val() == "6") {
                 $("#ifotherminority").removeClass("d-none").hide().slideDown(250);
                 $("#others").prop("required", true);
            } else {
                 $("#ifotherminority").slideUp(250, function() { $(this).addClass("d-none"); });
                 $("#others").val("").prop("required", false);
            }
        } else {
            $("#ifminoritydiv").slideUp(250, function() { $(this).addClass("d-none"); });
            $("#minoritycode").val('').prop("required", false).prop("disabled", true);
            $("#ifotherminority").slideUp(250, function() { $(this).addClass("d-none"); });
            $("#others").val("").prop("required", false);
        }
        if ($('#minoritycode').data('selectpicker')) {
            $('#minoritycode').selectpicker('refresh');
        }
    }
    toggleMinorityFields();
    $("#isminority, #minoritycode").change(toggleMinorityFields);

    //-------------------------------------------VALIDATION RULES--------------------------------------------------------
    $(document).on("keypress", ".numbers", function (event) {
        if (event.which < 48 || event.which > 57) event.preventDefault();
    });
    $(document).on("keyup", ".alphabets", function () {
        this.value = this.value.replace(/[^a-zA-Z. ]/g, '');
    });
    $(document).on("focusout", ".defaultval", function () {
        if ($(this).val().trim().length === 0) $(this).val('0');
    });
    $(document).on("keyup", ".numberspl", function () {
        this.value = this.value.replace(/[^0-9\ -]/g, '');
    });

    $("#usermobile").focusout(function () {
        var m = $('#usermobile').val();
        if (m.length > 0 && m.length < 10) {
            $('#msg1').html('<i class="fas fa-exclamation-circle me-1"></i> Mobile no. should be exactly 10 digits');
        } else {
            $('#msg1').html("");
        }
    });
    $("#usermobile").keypress(function () {
        if ($('#usermobile').val().length === 9) $('#msg1').html("");
    });

    $("#emailid").focusout(function () {
        var emailVal = $(this).val().trim();
        var re = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$/;
        if (emailVal.length > 0 && !re.test(emailVal)) {
            showModalAlert("Please enter valid Email ID", "Validation");
            $(this).focus();
        } else if (emailVal.length > 0) {
            checkuserexistfunc();
        }
    });

    //-------------------------------------------AJAX SUBMIT--------------------------------------------------------
    $("#tparticipantfid").submit(function (e) {
        e.preventDefault();
        if ($("#participantofficestatecode").val() === "") {
            showModalAlert("Please Select the Office State", "Validation"); $("#participantofficestatecode").focus(); return false;
        }
        if ($("#statecode").val() === "") {
            showModalAlert("Please Select the Residential State", "Validation"); $("#statecode").focus(); return false;
        }
        if ($("#districtcode").val() === "" || $("#districtcode").val() === null) {
            showModalAlert("Please Select the Residential District", "Validation"); $("#districtcode").focus(); return false;
        }
        if ($("#designationcode").val() === "others" && $("#dinput").val().trim() === "") {
            showModalAlert("Please enter designation name when 'Others' is selected.", "Validation"); $("#dinput").focus(); return false;
        }

        var $btn = $("#submitBtn");
        var originalBtnText = $btn.html();
        $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-2"></i> Saving...');

        $.ajax({
            type: "POST", url: API.updateProfile, data: $("#tparticipantfid").serialize(),
            success: function (data) {
                $btn.prop('disabled', false).html(originalBtnText);

                if (data == "1") { showModalAlert("Email Id cannot be empty", "Warning"); $("#emailid").focus(); }
                else if (data == "3") { showModalAlert("Email id should be 1-50 characters long", "Warning"); $("#emailid").focus(); }
                else if (data == "4") { showModalAlert("Email id Already exist", "Warning"); $("#emailid").focus(); }
                else if (data == "2") {
                    showModalAlert("Profile successfully updated!", "Success");
                    
                    var modalElement = document.getElementById('feedbackModal');
                    modalElement.addEventListener('hidden.bs.modal', function () {
                        if (API.redirectAfterUpdate) {
                            window.location.href = API.redirectAfterUpdate;
                        }
                    }, { once: true });

                } else { showModalAlert("Save Failed! " + (data || "Please try again."), "Error"); }
            }, error: function (jqXHR, textStatus, errorThrown) {
                $btn.prop('disabled', false).html(originalBtnText);
                showModalAlert("Error: " + textStatus + " - " + errorThrown, "Error");
            }
        });
    });

    //-------------------------------------------DROPDOWN TRIGGERS--------------------------------------------------------
    $("#statecode").change(function () {
        var stateCodeVal = $(this).val();
        var $districtSelect = $('#districtcode');
        $districtSelect.empty().append($("<option>").val("").text("-- Select --"));

        if ($districtSelect.data('selectpicker')) {
            $districtSelect.selectpicker('refresh');
        }

        if(stateCodeVal) {
            $.ajax({
                type: "POST", url: API.getDistricts, data: { statecode: stateCodeVal },
                success: function (data) {
                    if(data && data.length > 0) {
                        $.each(data, function(i, d) { $districtSelect.append($("<option>").val(d.districtcode).text(d.districtname)); });
                    } else {
                        showModalAlert('No districts found for the selected state.', 'Information');
                    }
                    if ($districtSelect.data('selectpicker')) {
                        $districtSelect.selectpicker('refresh');
                    }
                }, error: function () {
                    showModalAlert("Error fetching districts.", "Error");
                }
            });
        }
    });

    $("#qualificationcode").change(function () {
        var qualCodeVal = $(this).val();
        var $subjectSelect = $('#qualificationsubjectcode');
        $subjectSelect.empty().append($("<option>").val('0').text("Not Applicable"));

        if ($subjectSelect.data('selectpicker')) {
            $subjectSelect.selectpicker('refresh');
        }

        if(qualCodeVal){
            $.ajax({
                type: "GET",
                url: API.getQualificationSubjects,
                data: { qualificationcode: qualCodeVal },
                success: function (data) {
                    if(data && data.length > 0) {
                        $.each(data, function(i, s) { $subjectSelect.append($("<option>").val(s[0]).text(s[1])); });
                    }
                    if ($subjectSelect.data('selectpicker')) {
                        $subjectSelect.selectpicker('refresh');
                    }
                },
                error: function () {
                    showModalAlert("Error fetching subjects.", "Error");
                }
            });
        }
    });

    function toggleDlist() {
        if ($("#designationcode").val() === "others") {
            $("#dlist").removeClass("d-none").hide().slideDown(250);
            $("#dinput").prop("required", true);
        } else {
            $("#dlist").slideUp(250, function() { $(this).addClass("d-none"); });
            $("#dinput").prop("required", false).val('');
        }
    }
    toggleDlist();
    $("#designationcode").change(toggleDlist);

    $('#districtcode').on('focus', function() {
        checkstate();
    });
});