function myFunction(x) {
    x.classList.toggle("change");
}

$("#menu-toggle").click(function (e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
});

$(document).ready(function () {
    // Back to top animation
    $('#backtotop').click(function () {
        $("html, body").animate({scrollTop: 0}, 600);
        return false;
    });

    // Sub-menu toggle logic
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

    // Hide/Show Buttons based on Report Type
    $("#view_aggregateid").hide();
    $("#view_detailsid").hide();
    $("#view_rpid").hide(); // Default hidden until selection

    $("#reporttype").change(function (e) {
        var rtype = $("#reporttype").val();
        if (rtype === "1") {
            $("#view_aggregateid").show();
            $("#view_detailsid").show();
            $("#view_rpid").hide();
        } else if (rtype === "2") {
            $("#view_aggregateid").hide();
            $("#view_detailsid").hide();
            $("#view_rpid").show();
        } else {
            $("#view_aggregateid, #view_detailsid, #view_rpid").hide();
        }
    });

    // Cascade: Financial Year -> Programs
    $("#finyear").change(function () {
        var fy = $("#finyear").val();
        $('#programs,#phaseid').empty();
        $('#programs,#phaseid').append($('<option></option>').attr("value", "").text("Select"));
        if (fy) {
            var fystart = fy.split("##")[0];
            var fyend = fy.split("##")[1];

            callCustomAjax(API.getFYCourseList, "fystart=" + fystart + "&fyend=" + fyend, function (data) {
                if (data) {
                    data.forEach(function (x) {
                        $('#programs').append($('<option></option>').attr("value", x[0]).text(x[1]).attr("title", x[1]));
                    });
                    $('#programs > option').text(function (i, text) {
                        if (text.length > 100) {
                            return text.substr(0, 100) + '...';
                        }
                        return text;
                    });
                }
            });
        }
    });

    // Cascade: Programs -> Phases
    $('#programs').change(function () {
        $('#phaseid').empty();
        $('#phaseid').append($('<option></option>').attr("value", "").text("Select"));
        if ($('#programs').val() && $('#programs').val() !== "-1") {
            callCustomAjax(API.getPhasesBasedOnProgram, "programcode=" + $('#programs').val(), function (data) {
                if (data) {
                    data.forEach(function (x) {
                        $('#phaseid').append($('<option></option>').attr("value", x[0]).text(x[1]));
                    });
                }
            });
        }
    });
});

// Submit Button Listeners
document.addEventListener('DOMContentLoaded', function () {
    const viewAggregateButton = document.getElementById('view_aggregateid');
    const viewDetailsButton = document.getElementById('view_detailsid');
    const viewRpButton = document.getElementById('view_rpid');

    if(viewAggregateButton) {
        viewAggregateButton.addEventListener('click', function () {
            getlistreportfunc('1');
        });
    }

    if(viewDetailsButton) {
        viewDetailsButton.addEventListener('click', function () {
            getlistreportfunc('2');
        });
    }

    if(viewRpButton) {
        viewRpButton.addEventListener('click', function () {
            getlistreportfunc('3');
        });
    }
});

function getlistreportfunc(status) {
    if ($("#reporttype").val() === "-1") {
        alert("Please select report type");
        $("#reporttype").focus();
        return false;
    }
    if ($("#programs").val() === "-1" || $("#programs").val() === "") {
        alert("Please select the Program");
        $("#programs").focus();
        return false;
    }
    if ($("#phaseid").val() === "" || $("#phaseid").val() === "Select") {
        alert("Please select the Phase");
        $("#phaseid").focus();
        return false;
    }

    var url = API.printReportAttendance + "?status=" + status + "&phaseid=" + $("#phaseid").val();
    window.open(url, '_blank');
}