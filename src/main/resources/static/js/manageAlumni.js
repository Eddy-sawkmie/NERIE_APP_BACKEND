function showModalAlert(message, title = 'Message') {
    $('#feedbackModalLabel').text(title);
    $('#feedbackModalBody').html(message);
    var myModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('feedbackModal'));
    myModal.show();
}

$(document).ready(function () {
    $("#talumniid").submit(function (e) {
        e.preventDefault();

        callCustomAjax(
            API.saveAlumni,
            $("#talumniid").serialize(),
            function (data) {
                if (data) {
                    const message = $("#alumniid").val()
                        ? "Alumni details were successfully updated!"
                        : "Alumni details were successfully added.";

                    showModalAlert(message, "Success");

                    $('#feedbackModal').one('hidden.bs.modal', function () {
                        window.location.reload();
                    });

                } else {
                    showModalAlert("An error occurred. Please check the details and try again.", "Operation Failed");
                    $("#alumniid").val("");
                }
            }
        );
    });

    $("#departmentcode").change(function () {
        if ($("#departmentcode").val() !== "-1") {
            callCustomAjaxasync(
                API.listCourseAcademicByDeptCode,
                "departmentcode=" + $("#departmentcode").val(),
                function (data) {
                    $("#coursecode").empty();
                    $("#coursecode").append(
                        $('<option data-duration="0" value="-1" disabled selected>--Select Course--</option>')
                    );
                    data.forEach(function (item) {
                        $("#coursecode").append(
                            $('<option data-duration="' + item[5] + '" value="' + item[0] + '">' + item[1] + "</option>")
                        );
                    });
                    $("#coursecode").val("-1").trigger("change");
                }
            );
        } else {
            $("#coursecode").empty();
            $("#coursecode").append($('<option data-duration="0" value="-1" disabled selected>--Select Course--</option>'));
            $("#coursecode").val("-1").trigger("change");
        }
    });

    $("#coursecode").change(function () {
        var element = document.getElementById("coursecode");
        if(element.selectedIndex === -1) return;

        var duration = element.options[element.selectedIndex].getAttribute("data-duration");
        $.confirm({
            content: function () {
                var self = this;
                return $.ajax({
                    type: "POST",
                    url: API.generateAcademicYearByDuration,
                    data: "duration=" + duration,
                })
                .done(function (data) {
                    var ay = data;
                    $("#batch").empty();
                    var options = '<option value="-1" selected disabled>Select Batch</option>';
                    for (var i = 0; i < ay.length; i++) {
                        options += '<option value="' + ay[i] + '">' + ay[i] + "</option>";
                    }
                    $("#batch").append(options);
                    self.close();
                })
                .fail(function () {
                    self.setContent("Something went wrong.");
                    self.setTitle("");
                });
            },
        });
    });

    $("input.name").keyup(function () {
        if (this.value.match(/[^a-zA-Z \s]/)) {
            this.value = this.value.replace(/[^a-zA-Z \s]*$/, "");
        }
    });

    $("input.number").keyup(function () {
        if (this.value.match(/[^0-9 ]/g)) {
            this.value = this.value.replace(/[^0-9 ]/g, "");
        }
    });

    $(".mobile").focusout(function () {
        var tmp = $(".mobile").val();
        if (tmp.length !== 10 && tmp.length > 0) {
            $("#msgMobile").html("Mobile No. should be 10 digits");
            return false;
        } else {
            $("#msgMobile").html("");
            return true;
        }
    });

    $(".email").focusout(function (e) {
        var re = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$/;
        if ($(".email").val().replace(/\s/g, "").length !== 0) {
            if (re.test($(".email").val()) === false) {
                $("#msgEmail").html("Please enter a valid Email ID");
                $(".email").focus();
                return false;
            } else {
                $("#msgEmail").html("");
                return true;
            }
        }
    });

    $("#rollno").on("change", function () {
        if ($("#rollno").val()) {
            callCustomAjax(
                API.checkAlumniExist,
                "rollno=" + $("#rollno").val(),
                function (data) {
                    if (data == "1") {
                        $("#msgRollNo").html("Student with this Roll No. already exists");
                        $("#rollno").focus();
                    } else {
                        $('#msgRollNo').html('');
                    }
                }
            );
        }
    });

    $('#usertable').on('click', '.editalum', function () {
        const alumniId = $(this).data("id");
        if (alumniId) {
            editalumni(alumniId);
        }
    });

    // Bootstrap 5 Formatted Datatables
    $("#usertable").DataTable({
        retrieve: true,
        bDestroy: true,
        ordering: false,
        pageLength: 10,
        lengthMenu: [[10, 20, 50, -1],[10, 20, 50, "All"]],
        dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
             '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
             'rtip',
        buttons:[
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                title: 'Alumni List',
                className: 'btn btn-success btn-sm mb-3',
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                }
            }
        ]
    });
});

function editalumni(alumniid) {
    $.ajax({
        type: "GET",
        async: true,
        url: `${API.getAlumniDetails}/${encodeURIComponent(alumniid)}`,
        success: function (data) {
            if (data.length > 0) {
                const alumniData = data[0];

                const departmentCode = alumniData[10];
                const courseCode = alumniData[11];
                const batchYear = alumniData[6];

                $("#alumniid").val(alumniData[0]);
                $("#rollno").val(alumniData[1]);
                $("#fname").val(alumniData[2]);
                $("#mname").val(alumniData[3]);
                $("#lname").val(alumniData[4]);
                $("#gender").val(alumniData[5]);
                $("#email").val(alumniData[8]);
                $("#mobileno").val(alumniData[7]);
                $("#currentoccupation").val(alumniData[9]);

                $("#departmentcode").val(departmentCode);

                callCustomAjaxasync(
                    API.listCourseAcademicByDeptCode,
                    "departmentcode=" + departmentCode,
                    function (courseData) {
                        $("#coursecode").empty().append($('<option data-duration="0" value="-1" disabled>--Select Course--</option>'));
                        courseData.forEach(function (item) {
                            $("#coursecode").append($('<option data-duration="' + item[5] + '" value="' + item[0] + '">' + item[1] + "</option>"));
                        });

                        $("#coursecode").val(courseCode);

                        const duration = $('#coursecode').find(':selected').data('duration');
                        if (duration && duration > 0) {
                             $.ajax({
                                type: "POST",
                                url: API.generateAcademicYearByDuration,
                                data: "duration=" + duration,
                                success: function (batchData) {
                                    if (batchYear && !batchData.includes(batchYear)) {
                                        batchData.unshift(batchYear);
                                    }

                                    $("#batch").empty().append($('<option value="-1" disabled>Select Batch</option>'));

                                    var options = '';
                                    batchData.forEach(function(year) {
                                        options += '<option value="' + year + '">' + year + "</option>";
                                    });
                                    $("#batch").append(options);

                                    $("#batch").val(batchYear);
                                }
                            });
                        }
                    }
                );
                $(window).scrollTop(0);
            } else {
                 showModalAlert("No alumni details found for this ID.", "Not Found");
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            showModalAlert("Error: " + textStatus + " - " + errorThrown, "Request Failed");
        },
    });
}