//jq = jQuery.noConflict();
//(function ($) {
//    alert("in")
//    var token = $("meta[name='_csrf']").attr("content");
//    var header = $("meta[name='_csrf_header']").attr("content");
//    alert("token=" + token)
//    jQuery(document).ajaxSend(function (event, xhr, options) {
//        alert("header=" + header)
//        alert("token=" + token)
//        console.log("header=" + header)
//        console.log("token=" + token)
//        xhr.setRequestHeader(header, token);
//    });
//})(jQuery);

$(document).ready(function () {
//    console.log("ajaxheader");
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    jQuery(document).ajaxSend(function (event, xhr, options) {
//        console.log("header=" + header)
//        console.log("token=" + token)
        xhr.setRequestHeader(header, token);
    });
});


