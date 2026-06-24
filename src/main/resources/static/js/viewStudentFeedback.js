$(document).ready(function () {
    if (typeof Notiflix !== 'undefined') {
        Notiflix.Notify.Init({
            position: 'right-top',
            timeout: 3000
        });
    }

    let feedTable = $('#feedbacktable').DataTable({
        retrieve: true,
        bDestroy: true,
        ordering: false,
        pageLength: 10,
        lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
        language: {
            emptyTable: "Select a subject to view student feedback records."
        },
        dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
             'rtip',
        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                title: 'Student Feedbacks',
                className: 'btn btn-success btn-sm mb-3',
                exportOptions: { columns: ':visible' }
            },
            {
                extend: 'pdfHtml5',
                text: '<i class="fa fa-file-pdf-o"></i> PDF',
                title: 'Student Feedbacks',
                className: 'btn btn-danger btn-sm mb-3',
                exportOptions: { columns: ':visible' }
            }
        ]
    });

    $('#viewstudentfeedback').on('click', function () {
        const subjectcode = $("#subjectcode").val();

        if (!subjectcode || subjectcode === '-1') {
            Notiflix.Notify.Info('Please select a subject.');
            $("#subjectcode").focus();
            return;
        }

        Notiflix.Loading.Standard('Fetching feedback...');

        feedTable.settings()[0].oLanguage.sEmptyTable = "No feedback found for the selected subject.";

        $.ajax({
            type: "GET",
            url: API.getFeedbackListBasedOnSubjectCode,
            data: { subjectcode: subjectcode },
            dataType: 'json',
            success: function (data) {
                Notiflix.Loading.Remove();
                feedTable.clear();

                if (data && Array.isArray(data) && data.length > 0) {
                    const rows = data.map(item => [
                        item[2], // Student ID
                        item[3], // Student Name
                        item[0], // Feedback
                        item[1]  // Date
                    ]);
                    feedTable.rows.add(rows).draw();
                    Notiflix.Notify.Success(`${data.length} feedback record(s) loaded successfully.`);
                } else {
                    feedTable.draw();
                    Notiflix.Notify.Info("No feedback found for the selected subject.");
                }

                feedTable.columns.adjust().draw();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Notiflix.Loading.Remove();
                Notiflix.Notify.Failure(`An error occurred: ${textStatus}.`);
                console.error("AJAX Error:", textStatus, errorThrown);
                feedTable.clear().draw();
            }
        });
    });

    $("#menu-toggle").on("click", function (e) {
        e.preventDefault();
        $("#wrapper").toggleClass("toggled");
    });

    const backToTop = $('#backtotop');
    if (backToTop.length) {
        backToTop.on("click", function () {
            $("html, body").animate({scrollTop: 0}, 600);
            return false;
        });
    }

    const loader = $("#loader");
    if (loader.length) {
         loader.fadeOut("slow");
    }
});