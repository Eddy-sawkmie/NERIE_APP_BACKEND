//function myFunction(x) {
//    x.classList.toggle("change");
//}
//
//$("#menu-toggle").click(function (e) {
//    e.preventDefault();
//    $("#wrapper").toggleClass("toggled");
//});
//
//$(document).ready(function () {
//    $('#backtotop').click(function () {
//        $("html, body").animate({scrollTop: 0}, 600);
//        return false;
//    });
//
//    $('.sub-menu ul').hide();
//    $('.sub-sub-menu ul').hide();
//    $(".sub-menu a").click(function () {
//        $(this).parent(".sub-menu").children("ul").slideToggle("100");
//        $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
//    });
//    $(".sub-sub-menu a").click(function () {
//        $(this).parent(".sub-sub-menu").children("ul").slideToggle("100");
//        $(this).find(".right").toggleClass("fa-caret-up fa-caret-down");
//    });
//
//    var date = new Date();
//    var today = new Date(date.getFullYear(), date.getMonth(), date.getDate());
//
//    if ($.fn.datepicker) {
//        $('#dateselect').datepicker({
//            dateFormat: 'dd-mm-yy',
//            maxDate: new Date(),
//            autoclose: true,
//            todayHighlight: true
//        });
//        $('#dateselect').datepicker('setDate', today);
//    }
//
//    if ($.fn.datetimepicker) {
//        $("#starttime, #endtime").datetimepicker({
//            format: 'LT',
//            icons: {
//                time: 'fa fa-clock-o',
//                date: 'fa fa-calendar',
//                up: 'fa fa-chevron-up',
//                down: 'fa fa-chevron-down',
//                previous: 'fa fa-chevron-left',
//                next: 'fa fa-chevron-right',
//                today: 'fa fa-check',
//                clear: 'fa fa-trash',
//                close: 'fa fa-times'
//            }
//        });
//    }
//
//    var stdtable = $('#studentlist').DataTable({
//        retrieve: true,
//        bDestroy: true,
//        ordering: false,
//        pageLength: 10,
//        lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
//        language: { emptyTable: "No students available." },
//        autoWidth: false,
//        dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
//             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
//             'rtip',
//        buttons: [
//            {
//                extend: 'excelHtml5',
//                text: '<i class="fa fa-file-excel-o"></i> Excel',
//                title: 'Attendance List',
//                exportOptions: { columns: ':visible:not(.noExport)' },
//                className: 'btn btn-success btn-sm mb-3'
//            }
//        ]
//    });
//
//    $("#selectAllBtn").click(function (e) {
//        e.preventDefault();
//        var btn = $(this);
//
//        var allInputs = stdtable.$('input[type="checkbox"]');
//
//        if (btn.text().trim() === "De-Select All") {
//            allInputs.prop("checked", false);
//            btn.html('<i class="fas fa-check-square me-1"></i> Select All');
//        } else {
//            allInputs.prop("checked", true);
//            btn.html('<i class="far fa-square me-1"></i> De-Select All');
//        }
//    });
//
//    $("#sem").change(function () {
//        let semphase = $(this).val();
//        if (semphase === "-1") {
//            $("#subject").html('<option value="">-- Select Subject --</option>');
//            return;
//        }
//        $.ajax({
//            url: "/attendance/getSubjectsBySemPhase",
//            type: "GET",
//            data: {
//                semphase: semphase
//            },
//            success: function (data) {
//                if (!data || data.length === 0) {
//                    $("#subject")
//                        .html('<option value="">-- No subjects available --</option>');
//
//                    Notiflix.Notify.Failure(
//                        "No subjects assigned for selected Semester/Phase."
//                    );
//                    return;
//                }
//                Notiflix.Notify.Success(
//                        "Subjects loaded successfully."
//                    );
//                let options =
//                    '<option value="">-- Select Subject --</option>';
//                $.each(data, function (i, item) {
//                    options +=
//                        '<option value="' +item[0] +'">' +item[1] +'</option>';
//                });
//                $("#subject").html(options);
//            },
//            error: function () {
//                Notiflix.Notify.Failure(
//                    "Error loading subjects."
//                );
//            }
//        });
//    });
//
//    $("#subject").change(function () {
//            var subjectcode = $("#subject").val();
//
//            $("#selectAllBtn").html('<i class="fas fa-check-square me-1"></i> De-Select All');
//
//            stdtable.clear().draw();
//
//            $('#dlist').addClass('d-none');
//
//            if (subjectcode) {
//                Notiflix.Loading.Standard('Loading student list...');
//
//            $.ajax({
//                type: "GET",
//                url: API.getStudentsListBasedOnSubjectCode,
//                data: { subjectcode: subjectcode },
//                success: function (data) {
//                    Notiflix.Loading.Remove();
//
//                    if (!data || data.length === 0) {
//                        Notiflix.Notify.Warning("No students found for this subject.");
//                        $('#dlist').removeClass('d-none');
//                        stdtable.columns.adjust().draw();
//                        return;
//                    }
//
//                    $('#dlist').removeClass('d-none');
//
//                    var rowsToAdd = [];
//                    data.forEach(function (x) {
//                        var id = x[0] || '';
//                        var fname = x[1] || '';
//                        var mname = x[2] || '';
//                        var lname = x[3] || '';
//                        var sname = [fname, mname, lname].filter(Boolean).join(" ");
//
//                        // Checkbox HTML
//                        var checkboxHtml = '<div class="text-center"><div class="form-check d-inline-block"><input class="form-check-input" type="checkbox" checked="checked" /></div></div>';
//
//                        rowsToAdd.push([
//                            id,
//                            sname,
//                            checkboxHtml
//                        ]);
//                    });
//
//                    stdtable.rows.add(rowsToAdd).draw();
//
//                    stdtable.columns.adjust().draw();
//                },
//                error: function (jqXHR, textStatus, errorThrown) {
//                    Notiflix.Loading.Remove();
//                    Notiflix.Notify.Failure("Error: " + textStatus + " - " + errorThrown);
//                    // Show container on error so user sees something happened
//                    $('#dlist').removeClass('d-none');
//                    stdtable.columns.adjust().draw();
//                }
//            });
//        }
//    });
//
//    // ----------------------------------------------------
//    // FORM SUBMISSION
//    // ----------------------------------------------------
//    $("#attendanceform").submit(function (e) {
//        e.preventDefault();
//
//        // Capture Time Values
//        const originalStartTime = $("#starttime").val();
//        const originalEndTime = $("#endtime").val();
//
//        function convertTo24Hour(timeVal) {
//            if (!timeVal || !timeVal.includes(" ")) return timeVal;
//            let parts = timeVal.trim().split(" ");
//            let time = parts[0];
//            let ampm = parts[1];
//
//            let timeParts = time.split(":");
//            let hh = parseInt(timeParts[0], 10);
//            let mm = timeParts[1];
//
//            if (ampm.toUpperCase() === "PM") {
//                if (hh !== 12) hh += 12;
//            } else { // AM
//                if (hh === 12) hh = 0;
//            }
//
//            let hhStr = (hh < 10) ? "0" + hh : "" + hh;
//            return hhStr + ":" + mm;
//        }
//
//        const startTime24 = convertTo24Hour(originalStartTime);
//        const endTime24 = convertTo24Hour(originalEndTime);
//
//        $("#starttime").val(startTime24);
//        $("#endtime").val(endTime24);
//
//        var attendanceArray = [];
//
//        stdtable.rows().every(function () {
//            var rowNode = this.node();
//            var $row = $(rowNode);
//
//            var studentId = this.data()[0];
//
//            var isChecked = $row.find('input[type="checkbox"]').prop('checked');
//            var status = isChecked ? "P" : "A";
//
//            if(studentId) {
//                attendanceArray.push({
//                    studentid: studentId,
//                    pora: status
//                });
//            }
//        });
//
//        if (attendanceArray.length === 0) {
//            Notiflix.Notify.Failure("No students to submit.");
//            return;
//        }
//
//        var formData = new FormData(this);
//        formData.append('attendancejsonstring', JSON.stringify(attendanceArray));
//
//        $("#starttime").val(originalStartTime);
//        $("#endtime").val(originalEndTime);
//
//        Notiflix.Loading.Standard('Saving attendance...');
//        $.ajax({
//            type: "POST",
//            url: API.uploadStudentAttendance,
//            data: formData,
//            processData: false,
//            contentType: false,
//            success: function (data) {
//                Notiflix.Loading.Remove();
//                if (data === "0") {
//                    Notiflix.Notify.Failure("Unable to save. Please try again.");
//                } else if (data === "-1") {
//                    Notiflix.Notify.Failure("Error processing request. Check data/formats.");
//                } else {
//                    Notiflix.Notify.Success("Successfully saved.");
//                    setTimeout(function() {
//                        window.location.reload();
//                    }, 1000);
//                }
//            },
//            error: function (jqXHR, textStatus, errorThrown) {
//                Notiflix.Loading.Remove();
//                Notiflix.Notify.Failure("Error: " + textStatus + " - " + errorThrown);
//            }
//        });
//    });
//});


function myFunction(x) {
    x.classList.toggle("change");
}

$("#menu-toggle").click(function (e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
});

$(document).ready(function () {
    $('#backtotop').click(function () {
        $("html, body").animate({scrollTop: 0}, 600);
        return false;
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

    var date = new Date();
    var today = new Date(date.getFullYear(), date.getMonth(), date.getDate());

    if ($.fn.datepicker) {
        $('#dateselect').datepicker({
            dateFormat: 'dd-mm-yy',
            maxDate: new Date(),
            autoclose: true,
            todayHighlight: true
        });
        $('#dateselect').datepicker('setDate', today);
    }

    if ($.fn.datetimepicker) {
        $("#starttime, #endtime").datetimepicker({
            format: 'LT',
            icons: {
                time: 'fa fa-clock-o',
                date: 'fa fa-calendar',
                up: 'fa fa-chevron-up',
                down: 'fa fa-chevron-down',
                previous: 'fa fa-chevron-left',
                next: 'fa fa-chevron-right',
                today: 'fa fa-check',
                clear: 'fa fa-trash',
                close: 'fa fa-times'
            }
        });
    }

    var stdtable = $('#studentlist').DataTable({
        retrieve: true,
        bDestroy: true,
        ordering: false,
        pageLength: 10,
        lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
        language: { emptyTable: "No students available." },
        autoWidth: false,
        dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
             'rtip',
        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                title: 'Attendance List',
                exportOptions: { columns: ':visible:not(.noExport)' },
                className: 'btn btn-success btn-sm mb-3'
            }
        ]
    });

    $("#selectAllBtn").click(function (e) {
        e.preventDefault();
        var btn = $(this);

        var allInputs = stdtable.$('input[type="checkbox"]');

        if (btn.text().trim() === "De-Select All") {
            allInputs.prop("checked", false);
            btn.html('<i class="fas fa-check-square me-1"></i> Select All');
        } else {
            allInputs.prop("checked", true);
            btn.html('<i class="far fa-square me-1"></i> De-Select All');
        }
    });

    $("#sem").change(function () {
        let semphase = $(this).val();
        if (semphase === "-1") {
            $("#subject").html('<option value="">-- Select Subject --</option>');
            return;
        }
        $.ajax({
            url: "/attendance/getSubjectsBySemPhase",
            type: "GET",
            data: {
                semphase: semphase
            },
            success: function (data) {
                if (!data || data.length === 0) {
                    $("#subject")
                        .html('<option value="">-- No subjects available --</option>');

                    Notiflix.Notify.Failure(
                        "No subjects assigned for selected Semester/Phase."
                    );
                    return;
                }
                Notiflix.Notify.Success(
                        "Subjects loaded successfully."
                    );
                let options =
                    '<option value="">-- Select Subject --</option>';
                $.each(data, function (i, item) {
                    options +=
                        '<option value="' +item[0] +'">' +item[1] +'</option>';
                });
                $("#subject").html(options);
            },
            error: function () {
                Notiflix.Notify.Failure(
                    "Error loading subjects."
                );
            }
        });
    });

    $("#subject").change(function () {
            var subjectcode = $("#subject").val();

            $("#selectAllBtn").html('<i class="fas fa-check-square me-1"></i> De-Select All');

            stdtable.clear().draw();

            $('#dlist').addClass('d-none');

            if (subjectcode) {
                Notiflix.Loading.Standard('Loading student list...');

            $.ajax({
                type: "GET",
                url: API.getStudentsListBasedOnSubjectCode,
                data: { subjectcode: subjectcode },
                success: function (data) {
                    Notiflix.Loading.Remove();

                    if (!data || data.length === 0) {
                        Notiflix.Notify.Warning("No students found for this subject.");
                        $('#dlist').removeClass('d-none');
                        stdtable.columns.adjust().draw();
                        return;
                    }

                    $('#dlist').removeClass('d-none');

                    var rowsToAdd = [];
                    data.forEach(function (x) {
                        var id = x[0] || '';
                        var fname = x[1] || '';
                        var mname = x[2] || '';
                        var lname = x[3] || '';
                        var sname = [fname, mname, lname].filter(Boolean).join(" ");

                        // Checkbox HTML
                        var checkboxHtml = '<div class="text-center"><div class="form-check d-inline-block"><input class="form-check-input" type="checkbox" checked="checked" /></div></div>';

                        rowsToAdd.push([
                            id,
                            sname,
                            checkboxHtml
                        ]);
                    });

                    stdtable.rows.add(rowsToAdd).draw();

                    stdtable.columns.adjust().draw();
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Notiflix.Loading.Remove();
                    Notiflix.Notify.Failure("Error: " + textStatus + " - " + errorThrown);
                    // Show container on error so user sees something happened
                    $('#dlist').removeClass('d-none');
                    stdtable.columns.adjust().draw();
                }
            });
        }
    });

    // ----------------------------------------------------
    // FORM SUBMISSION
    // ----------------------------------------------------
    $("#attendanceform").submit(function (e) {
        e.preventDefault();

        // Capture Time Values
        const originalStartTime = $("#starttime").val();
        const originalEndTime = $("#endtime").val();

        function convertTo24Hour(timeVal) {
            if (!timeVal || !timeVal.includes(" ")) return timeVal;
            let parts = timeVal.trim().split(" ");
            let time = parts[0];
            let ampm = parts[1];

            let timeParts = time.split(":");
            let hh = parseInt(timeParts[0], 10);
            let mm = timeParts[1];

            if (ampm.toUpperCase() === "PM") {
                if (hh !== 12) hh += 12;
            } else { // AM
                if (hh === 12) hh = 0;
            }

            let hhStr = (hh < 10) ? "0" + hh : "" + hh;
            return hhStr + ":" + mm;
        }

        const startTime24 = convertTo24Hour(originalStartTime);
        const endTime24 = convertTo24Hour(originalEndTime);

        $("#starttime").val(startTime24);
        $("#endtime").val(endTime24);

        var attendanceArray = [];

        // Loop purely through DataTables internal memory, regardless of pagination
        stdtable.rows().every(function () {
            var rowData = this.data();
            var studentId = rowData[0];

            var rowNode = this.node();

            var isChecked = true;

            if (rowNode) {
                isChecked = $(rowNode).find('input[type="checkbox"]').prop('checked');
            } else {
                isChecked = true;
            }

            if (studentId) {
                attendanceArray.push({
                    studentid: studentId,
                    pora: isChecked ? "P" : "A"
                });
            }
        });

        if (attendanceArray.length === 0) {
            Notiflix.Notify.Failure("No students to submit.");
            return;
        }

        var formData = new FormData(this);
        formData.append('attendancejsonstring', JSON.stringify(attendanceArray));

        $("#starttime").val(originalStartTime);
        $("#endtime").val(originalEndTime);

        Notiflix.Loading.Standard('Saving attendance...');
        $.ajax({
            type: "POST",
            url: API.uploadStudentAttendance,
            data: formData,
            processData: false,
            contentType: false,
            success: function (data) {
                Notiflix.Loading.Remove();
                if (data === "0") {
                    Notiflix.Notify.Failure("Unable to save. Please try again.");
                } else if (data === "-1") {
                    Notiflix.Notify.Failure("Error processing request. Check data/formats.");
                } else {
                    Notiflix.Notify.Success("Successfully saved.");
                    setTimeout(function() {
                        window.location.reload();
                    }, 1000);
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Notiflix.Loading.Remove();
                Notiflix.Notify.Failure("Error: " + textStatus + " - " + errorThrown);
            }
        });
    });
});