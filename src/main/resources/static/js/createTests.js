function validateTestForm() {
  if ($("#subjectcode").val() === "") {
      showFeedbackModal("Please select a Subject.");
      return false;
  }

  const testNo = $("#testno").val().trim();
  if (testNo === "") {
      showFeedbackModal("Please enter a Test No.");
      return false;
  }
  if (!/^\d+$/.test(testNo)) {
      showFeedbackModal("Test No. must be a number.");
      return false;
  }

  if ($("#testname").val().trim() === "") {
      showFeedbackModal("Please enter a Test Name.");
      return false;
  }

  if ($("#testdate").val().trim() === "") {
      showFeedbackModal("Please select a Test Date.");
      return false;
  }

  const passMark = $("#passmark").val().trim();
  const fullMark = $("#fullmark").val().trim();

  if (passMark === "") {
      showFeedbackModal("Please enter a Pass Mark.");
      return false;
  }
  if (fullMark === "") {
      showFeedbackModal("Please enter a Full Mark.");
      return false;
  }

  if (parseInt(fullMark, 10) > 100) {
      showFeedbackModal("Full Mark cannot exceed 100.");
      return false;
  }
  if (parseInt(passMark, 10) > parseInt(fullMark, 10)) {
      showFeedbackModal("Pass Mark cannot be greater than Full Mark.");
      return false;
  }

  return true;
}

$(document).ready(function () {
  // ----------------------------------------------------
  // DATATABLE INITIALIZATION (Requested Style)
  // ----------------------------------------------------
  $('#usertable').DataTable({
      retrieve: true,
      bDestroy: true,
      ordering: false,
      pageLength: 10,
      lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
      language: { emptyTable: "No tests found." },
      dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
           '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
           'rtip',
      buttons: [
          {
              extend: 'excelHtml5',
              text: '<i class="fa fa-file-excel-o"></i> Excel',
              title: 'Tests List',
              exportOptions: { columns: ':visible:not(.noExport)' },
              className: 'btn btn-success btn-sm mb-3'
          }
      ]
  });

  // Handle only numeric input
  $('input.numeric').on('keyup', function () {
      this.value = this.value.replace(/[^0-9]/g, '');
  });

  // Mark validation logic
  $('#fullmark').on('blur', function() {
      if ($(this).val().trim() === '') return;
      let fullMarkValue = parseInt($(this).val(), 10);
      if (fullMarkValue > 100) {
          Notiflix.Notify.Warning("Full Mark cannot exceed 100.");
          $(this).val('100');
      }
      $('#passmark').trigger('blur');
  });

  $('#passmark').on('blur', function() {
      if ($(this).val().trim() === '') return;
      let passMarkValue = parseInt($(this).val(), 10);
      let fullMarkValue = parseInt($('#fullmark').val(), 10);

      if (passMarkValue > 100) {
          Notiflix.Notify.Warning("Pass Mark cannot exceed 100.");
          $(this).val('100');
          passMarkValue = 100;
      }

      if (!isNaN(fullMarkValue) && passMarkValue > fullMarkValue) {
          Notiflix.Notify.Warning("Pass Mark cannot be greater than Full Mark.");
          $(this).val($('#fullmark').val());
      }
  });

  // Ajax Form Submit
  $("#testsdetailsform").submit(function (e) {
      e.preventDefault();
      if (!validateTestForm()) return;

      var form = $(this);
      Notiflix.Loading.Standard('Saving Test Details...');

      $.ajax({
          type: "POST",
          url: form.attr("action"),
          data: form.serialize(),
          success: function (data) {
              Notiflix.Loading.Remove();
              var responseData = String(data).trim();
              if (responseData === '-1') {
                  showFeedbackModal("There was an error saving the Test.");
              } else if (responseData === '1') {
                  showFeedbackModal("Successfully Saved the Test.", true);
              } else {
                  showFeedbackModal("Test submission status: " + responseData);
              }
          },
          error: function (jqXHR, textStatus) {
              Notiflix.Loading.Remove();
              showFeedbackModal("An error occurred during submission: " + textStatus);
          }
      });
  });

  // Datepicker Initialization
  if ($.fn.datepicker) {
      $('.datepicker').datepicker({
          dateFormat: 'dd-mm-yy',
          orientation: 'bottom',
          autoclose: true
      });
      if ($('#testdate').val() === '') {
          $('#testdate').datepicker('setDate', new Date());
      }
  }

  $('#customresetbutton').on('click', customReset);

  // Edit button click handler
  $('#usertable tbody').on('click', '.editbtn', function(event) {
      event.preventDefault();
      var button = $(this);
      edittest(
          button.data('param1'), button.data('param2'),
          button.data('param3'), button.data('param4'),
          button.data('param5'), button.data('param6'),
          button.data('param7')
      );
  });
});

function customReset() {
  $('#testsdetailsform')[0].reset();
  $("#testid").val('');
  $("#subjectcode").prop('selectedIndex', 0);
  $('#testdate').datepicker('setDate', new Date());
  $("#subjectcode").focus();
}

function edittest(testid, testname, testno, subjectcode, testdateStr, passmark, fullmark) {
  $("#testid").val(testid);
  $("#testname").val(testname);
  $("#testno").val(testno);
  $("#subjectcode").val(String(subjectcode));
  $("#passmark").val(passmark);
  $("#fullmark").val(fullmark);

  if (testdateStr && testdateStr.trim() !== '') {
      $("#testdate").datepicker('setDate', testdateStr);
  } else {
      $("#testdate").val('');
  }

  $('html, body').animate({ scrollTop: 0 }, 'fast');
  $("#subjectcode").focus();
}

function showFeedbackModal(message, reloadOnClose = false) {
  $("#feedbackModalBody").text(message);
  $('#feedbackModal').off('hidden.bs.modal');

  if (reloadOnClose) {
      $('#feedbackModal').on('hidden.bs.modal', () => window.location.reload());
  }

  $("#feedbackModal").modal("show");
}