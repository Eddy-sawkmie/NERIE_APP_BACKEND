let attendancePieChart = null;

function createOrUpdatePieChart(canvasId, data, labels, colors) {
    const ctx = document.getElementById(canvasId).getContext('2d');

    if (attendancePieChart) {
        attendancePieChart.destroy();
    }

    attendancePieChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: colors,
                borderColor: '#ffffff',
                borderWidth: 4,
                hoverOffset: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutoutPercentage: 70, // Slightly thicker doughnut for modern look
            legend: {
                display: false // We handle the legend visually via the summary cards
            },
            tooltips: {
                callbacks: {
                    label: function(tooltipItem, data) {
                        let label = data.labels[tooltipItem.index] || '';
                        if (label) {
                            label += ': ';
                        }
                        label += data.datasets[tooltipItem.datasetIndex].data[tooltipItem.index] + ' Days';
                        return label;
                    }
                },
                backgroundColor: 'rgba(24, 28, 50, 0.9)', // Modern dark tooltip
                titleFontColor: '#fff',
                bodyFontColor: '#fff',
                cornerRadius: 6,
                xPadding: 12,
                yPadding: 12,
                displayColors: false // Hide the color square in the tooltip
            }
        }
    });
}

$(document).ready(function () {
    var attenTable = $('#attendancetable').DataTable({
        // Modern DOM structure matching your study materials layout
        dom: '<"d-flex justify-content-between align-items-center mb-3"Bf>rt<"d-flex justify-content-between align-items-center mt-3"ip>',
        pageLength: 10,
        lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "All"]],
        language: {
            emptyTable: "<div class='text-center p-4'><i class='fas fa-clipboard-list fs-1 text-muted mb-3'></i><br>No attendance records available.</div>",
            search: "_INPUT_",
            searchPlaceholder: "Search records..."
        },
        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fas fa-file-excel me-1"></i> Export Excel',
                title: 'Attendance Report',
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                },
                className: 'btn btn-sm btn-success btn-modern'
            }
        ]
    });

    // Style the DataTables search input
    $('.dataTables_filter input').addClass('form-control form-control-sm modern-select d-inline-block w-auto');

    $('#viewattendance').on('click', function () {
        var subjectcode = $("#subjectcode").val();
        if (subjectcode === '-1') {
            Notiflix.Notify.Info('Please select a subject.');
            $("#subjectcode").focus();
            return;
        }

        var month = $("#month").val();
        var attendanceUrl = API.studentAttendance;
        
        // Show loading indicator
        var $btn = $(this);
        var originalBtnText = $btn.html();
        $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-2"></i> Loading...');

        $.ajax({
            type: "GET",
            url: attendanceUrl,
            data: {
                subjectcode: subjectcode,
                month: month
            },
            success: function (data) {
                attenTable.clear().draw();
                $('#divChart').hide();
                $btn.prop('disabled', false).html(originalBtnText);

                if (data && data.length > 0) {
                    let cpresent = 0;
                    let cabsent = 0;
                    let marksRows = [];

                    data.forEach(function (item, index) {
                        // Using the modern status pills instead of Bootstrap 4 badges
                        let statusHtml = item[0].toUpperCase() === 'P' 
                            ? '<span class="status-pill pill-success"><i class="fas fa-check-circle me-1"></i> Present</span>' 
                            : '<span class="status-pill pill-danger"><i class="fas fa-times-circle me-1"></i> Absent</span>';

                        marksRows.push([
                            `<div class="text-center fw-semibold text-muted">${index + 1}</div>`,
                            `<div class="text-center">${statusHtml}</div>`,
                            `<span class="fw-medium text-dark">${item[1]}</span>`, // Date
                            `<span class="text-muted"><i class="far fa-clock me-1"></i> ${item[2]} - ${item[3]}</span>` // Timing
                        ]);

                        if (item[0].toUpperCase() === 'P') {
                            cpresent++;
                        } else {
                            cabsent++;
                        }
                    });

                    attenTable.rows.add(marksRows).draw(false);

                    // Update Summary Cards with animation
                    $({ Counter: 0 }).animate({ Counter: cpresent }, {
                        duration: 1000,
                        easing: 'swing',
                        step: function (now) { $('#countPresent').text(Math.ceil(now)); }
                    });
                    
                    $({ Counter: 0 }).animate({ Counter: cabsent }, {
                        duration: 1000,
                        easing: 'swing',
                        step: function (now) { $('#countAbsent').text(Math.ceil(now)); }
                    });
                    
                    $({ Counter: 0 }).animate({ Counter: cpresent + cabsent }, {
                        duration: 1000,
                        easing: 'swing',
                        step: function (now) { $('#countTotal').text(Math.ceil(now)); }
                    });

                    // Update Pie Chart with exact brand colors
                    createOrUpdatePieChart(
                        'attendanceChart',           
                        [cpresent, cabsent],         
                        ['Present', 'Absent'],       
                        ['#198754', '#dc3545']       
                    );

                    $('#divChart').fadeIn(400); 
                } else {
                    Notiflix.Notify.Info('No attendance record found for the selected criteria.');
                    // Inject empty state directly so the modal still shows "no data" gracefully
                    attenTable.settings()[0].oLanguage.sEmptyTable = "<div class='text-center p-4'><i class='fas fa-calendar-times fs-1 text-muted mb-3'></i><br>No attendance records found for this period.</div>";
                    attenTable.draw();
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                $btn.prop('disabled', false).html(originalBtnText);
                console.error("AJAX Error:", textStatus, errorThrown);
                Notiflix.Notify.Failure("An error occurred while fetching data.");
            }
        });
    });
});