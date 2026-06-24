function getDaysInMonth(month, year) {
    month--;
    var date = new Date(year, month, 1);
    var days = [];
    while (date.getMonth() === month) {
        days.push(new Date(date));
        date.setDate(date.getDate() + 1);
    }
    return days;
}

$(document).ready(function () {

    function getDataTableConfig(emptyTableMessage) {
        return {
            retrieve: true,
            bDestroy: true,
            ordering: false,
            pageLength: 10,
            lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
            language: {
                emptyTable: emptyTableMessage || "No attendance records found."
            },
            dom: '<"row"<"col-12 d-flex justify-content-end"B>>' +
                 '<"row mb-3"<"col-md-6"l><"col-md-6"f>>' +
                 'rtip',
            buttons: [
                {
                    extend: 'excelHtml5',
                    text: '<i class="fa fa-file-excel-o"></i> Excel',
                    title: 'Student Attendance List',
                    exportOptions: { columns: ':visible:not(.noExport)' },
                    className: 'btn btn-success btn-sm mb-3'
                }
            ]
        };
    }

    let attenTable = $('#attendancetable').DataTable(
        getDataTableConfig("Select the filters above to view attendance records.")
    );

    document.getElementById("coursecode").addEventListener("change", function () {
        const courseCode = this.value;
        const subjectDropdown = document.getElementById("subjectcode");

        subjectDropdown.innerHTML = '<option value="-1">Loading...</option>';

        if (courseCode === "-1") {
            subjectDropdown.innerHTML = '<option value="-1">Select Subject</option>';
            return;
        }

        fetch(API.getSubjectsByCourseAndFaculty + `?coursecode=${encodeURIComponent(courseCode)}`)
            .then(response => response.json())
            .then(data => {
                subjectDropdown.innerHTML = '<option value="-1">Select Subject</option>';
                data.forEach(subject => {
                    const option = document.createElement("option");
                    option.value = subject.subjectcode;
                    option.text = subject.subjectname;
                    option.dataset.isshortterm = subject.isshortterm;
                    subjectDropdown.appendChild(option);
                });
            })
            .catch(error => {
                console.error("Error fetching subjects:", error);
                subjectDropdown.innerHTML = '<option value="-1">Error loading subjects</option>';
            });
    });

    document.getElementById("subjectcode").addEventListener("change", function () {
        const selectedOption = this.options[this.selectedIndex];
        const isShortTerm = selectedOption.dataset.isshortterm;
        const semDropdown = document.getElementById("sem");
        const semLabel = document.getElementById("semphaselabel");

        semDropdown.innerHTML = "";

        if (!isShortTerm) {
            semDropdown.innerHTML = '<option value="-1">Select Semester/Phase</option>';
            return;
        }

        if (isShortTerm === "1") {
            semLabel.textContent = "Phase";
            semDropdown.innerHTML = `
                <option value="-1">Select Phase</option>
                <option value="P1">Phase I</option>
                <option value="P2">Phase II</option>
                <option value="P3">Phase III</option>
            `;
        } else {
            semLabel.textContent = "Semester";
            semDropdown.innerHTML = `
                <option value="-1">Select Semester</option>
                <option value="S1">1st Semester</option>
                <option value="S2">2nd Semester</option>
                <option value="S3">3rd Semester</option>
                <option value="S4">4th Semester</option>
            `;
        }
    });

    $('#viewstudentattendance').on('click', function () {
        const coursecode = $("#coursecode").val();
        const subjectcode = $("#subjectcode").val();
        const month = $("#month").val();
        const year = $("#year").val();
        const sem = $("#sem").val();

        if (coursecode === '-1') { Notiflix.Notify.Info('Please select a Course'); return; }
        if (subjectcode === '-1') { Notiflix.Notify.Info('Please select a Subject'); return; }
        if (!month) { Notiflix.Notify.Info('Please select a Month'); return; }
        if (!year) { Notiflix.Notify.Info('Please select a Year'); return; }
        if (sem === '-1') { Notiflix.Notify.Info('Please select a Semester/Phase'); return; }

        Notiflix.Loading.Standard('Fetching Attendance...');

        $.ajax({
            type: "GET",
            url: API.getStudentAttendance,
            data: {
                subjectcode: subjectcode,
                month: month,
                year: year,
                semflg: sem
            },
            success: function (data) {
                Notiflix.Loading.Remove();
                const mthText = $("#month option:selected").text();
                const subText = $("#subjectcode option:selected").text();

                if ($.fn.DataTable.isDataTable('#attendancetable')) {
                    attenTable.destroy();
                }
                $('#attendancetable thead').empty();
                $('#attendancetable tbody').empty();

                if (data && data.length > 0) {
                    const weekday = ["S", "M", "T", "W", "T", "F", "S"];
                    const days = getDaysInMonth(parseInt(month), parseInt(year));

                    $("#tabcap").html(`<strong>Month: ${mthText} - ${year}, Subject: ${subText}</strong>`);

                    let thead = '<tr>' +
                                '<th rowspan="2" class="align-middle">Sl. No.</th>' +
                                '<th rowspan="2" class="align-middle">Student ID</th>' +
                                '<th rowspan="2" class="align-middle">Student Name</th>';
                    days.forEach(d => thead += `<th>${weekday[d.getDay()]}</th>`);
                    thead += '</tr><tr>';
                    days.forEach(d => thead += `<th>${d.getDate()}</th>`);
                    thead += '</tr>';
                    $('#attendancetable thead').html(thead);

                    let rowdataHtml = "";
                    data.forEach((sdata, index) => {
                        rowdataHtml += `<tr><td>${index + 1}</td>` +
                                       `<td>${sdata[0]}</td>` +
                                       `<td>${sdata[1]}</td>`;

                        var attendance = {};
                        if (sdata[2] && sdata[2].length > 0) {
                            var records = sdata[2].split(',');
                            records.forEach(function (rec) {
                                var b = rec.split('$$');
                                var status = b[0];
                                var day = parseInt(b[1]);
                                var timeRange = b[2];
                                if (!attendance[day]) attendance[day] = [];
                                attendance[day].push({ status: status, time: timeRange });
                            });
                        }

                        days.forEach(d => {
                            const dayid = d.getDate();
                            let ap = "";
                            if (attendance[dayid]) {
                                attendance[dayid].forEach(function (y) {
                                    if (y.status === "P") {
                                        ap += "<span class='text-success fw-bold'>P</span> <small>(" + y.time + ")</small><br/>";
                                    } else {
                                        ap += "<span class='text-danger fw-bold'>A</span> <small>(" + y.time + ")</small><br/>";
                                    }
                                });
                            }
                            rowdataHtml += `<td>${ap}</td>`;
                        });
                        rowdataHtml += "</tr>";
                    });

                    $('#attendancetable tbody').html(rowdataHtml);
                    attenTable = $('#attendancetable').DataTable(getDataTableConfig());
                } else {
                    Notiflix.Notify.Info('No Attendance Record Found.');
                    $('#attendancetable thead').html("<tr><th>Student Attendance</th></tr>");
                    attenTable = $('#attendancetable').DataTable(getDataTableConfig('No records found for this selection.'));
                }
            },
            error: function (jqXHR) {
                Notiflix.Loading.Remove();
                Notiflix.Notify.Failure("Error fetching attendance data.");
                if ($.fn.DataTable.isDataTable('#attendancetable')) attenTable.destroy();
                $('#attendancetable thead').html("<tr><th>Error</th></tr>");
                attenTable = $('#attendancetable').DataTable(getDataTableConfig("System error."));
            }
        });
    });
});