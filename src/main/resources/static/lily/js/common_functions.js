function populatePieChart(chartname, chartdata, labels, sidelabelsids, colorarray, charttype, includetotal)
{
    //destroy the canvas 
    $('canvas#' + chartname).parent().html('<canvas id="' + chartname + '" style="height: 250px;"></canvas>');
    if (sidelabelsids.length !== 0) {
        for (i = 0; i < sidelabelsids.length; i++) {
            $('#' + sidelabelsids[i]).html(chartdata[i]);
            if (includetotal) {
                $('#' + sidelabelsids[i]).siblings().css('color', colorarray[i]);
            } else {
                if (i != 0) {
                    $('#' + sidelabelsids[i]).siblings().css('color', colorarray[i - 1]);
                }
            }
        }
    }

//    debugger;
    var pieChartCanvas = $('#' + chartname).get(0).getContext('2d');
    var pieData = {
        labels: labels,
        datasets: [
            {
                data: chartdata.slice((includetotal ? 0 : 1), ((includetotal ? 0 : 1) + labels.length)),
                backgroundColor: colorarray.slice(0, ((includetotal ? 0 : 1) + labels.length))
            }
        ]
    }

    var pieOptions = {
        legend: {
            display: true
        }
    }

    var pieChart = new Chart(pieChartCanvas, {
        type: charttype,
        data: pieData,
        options: pieOptions
    });
}   