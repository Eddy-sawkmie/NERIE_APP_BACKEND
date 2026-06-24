$(document).ready(function () {
  var st = $('#sysdate').val();
  var tzo = $('#timeZoneOffset').val();

  // Use current time if server time isn't provided
  var jsVar = st ? parseInt(st) : new Date().getTime();
  var timeZoneOffset = tzo ? parseInt(tzo) : 0;

  function myTimerInternal() {
    jsVar = jsVar + 1000;
    var d = new Date(jsVar);

    var day = String(d.getDate()).padStart(2, '0');
    var month = String(d.getMonth() + 1).padStart(2, '0'); // Month is 0-indexed
    var year = d.getFullYear();
    var sd = day + '/' + month + '/' + year;

    var hh = d.getHours();
    var mm = String(d.getMinutes()).padStart(2, '0');
    var ss = String(d.getSeconds()).padStart(2, '0');
    var ampm = (hh >= 12) ? "PM" : "AM";

    if (hh > 12) {
      hh -= 12;
    } else if (hh === 0) {
      hh = 12;
    }

    // Update DOM elements if they exist
    if ($('#servdate').length) $('#servdate').text(sd);
    if ($('#servtime').length) $('#servtime').text(hh + ":" + mm + ":" + ss + " " + ampm);
  }

  // Only start timer if server date input exists or fallback is desired
  if ($('#sysdate').length || $('#servtime').length) {
    myTimerInternal();
    setInterval(myTimerInternal, 1000);
  }
});