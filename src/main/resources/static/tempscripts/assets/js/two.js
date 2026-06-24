(function() {

// Localize jQuery variable
var jQuery;

/******** Load jQuery if not present *********/
/*  Removed Version check  || window.jQuery.fn.jquery !== '1.4.2' */
if (window.jQuery === undefined) {
    var script_tag = document.createElement('script');
    script_tag.setAttribute("type","text/javascript");
    script_tag.setAttribute("src",
        "https://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js");
    if (script_tag.readyState) {
      script_tag.onreadystatechange = function () { // For old versions of IE
          if (this.readyState == 'complete' || this.readyState == 'loaded') {
              scriptLoadHandler();
          }
      };
    } else {
      script_tag.onload = scriptLoadHandler;
    }
    // Try to find the head, otherwise default to the documentElement
    (document.getElementsByTagName("head")[0] || document.documentElement).appendChild(script_tag);
} else {
    // The jQuery version on the window is the one we want to use
    jQuery = window.jQuery;
    main();
}

/******** Called once jQuery has loaded ******/
function scriptLoadHandler() {
    // Restore $ and window.jQuery to their previous values and store the
    // new jQuery in our local jQuery variable
    jQuery = window.jQuery.noConflict(true);
    // Call our main function
    main();
}

/******** Our main function ********/
function main() {
    jQuery(document).ready(function($) {
        /******* Load CSS *******/
        var css_link = $("<link>", {
            rel: "stylesheet",
            type: "text/css",
            href: "https://twoyears.mygov.in/popup/style.css"
        });
        css_link.appendTo('head');
        var htmldata = '<div class="overlay-bg"></div><div class="overlay-content popup3"><a href="#" class="close-btn"> X </a><iframe height="600px" width="100%" src="https://twoyears.mygov.in/?base=yes"  name="mygov" scrolling="no" frameborder="0" marginheight="0px" marginwidth="0px" id="mygov"></iframe></div>';
        /******* Load HTML *******/
        $('body').append(htmldata);

        // function to show our popups
        function showPopup(whichpopup){
            var docHeight = $(document).height(); //grab the height of the page
            var scrollTop = $(window).scrollTop(); //grab the px value from the top of the page to where you're scrolling
            $('.overlay-bg').show().css({'height' : docHeight}); //display your popup background and set height to the page height
            $('.popup'+whichpopup).show().css({'top': scrollTop+20+'px'}); //show the appropriate popup and set the content 20px from the window top
        }

        // function to close our popups
        function closePopup(){
            $('.overlay-bg, .overlay-content').hide(); //hide the overlay
        }

        // timer if we want to show a popup after a few seconds.
        //get rid of this if you don't want a popup to show up automatically
        setTimeout(function() {
            // Show popup3 after 2 seconds
            showPopup(3);
        }, 2000);


        // show popup when you click on the link
        $('.show-popup').click(function(event){
            event.preventDefault(); // disable normal link function so that it doesn't refresh the page
            var selectedPopup = $(this).data('showpopup'); //get the corresponding popup to show

            showPopup(selectedPopup); //we'll pass in the popup number to our showPopup() function to show which popup we want
        });

        // hide popup when user clicks on close button or if user clicks anywhere outside the container
        $('.close-btn, .overlay-bg').click(function(){
            closePopup();
        });

        // hide the popup when user presses the esc key
        $(document).keyup(function(e) {
            if (e.keyCode == 27) { // if user presses esc key
                closePopup();
            }
        });
    });
}

})(); // We call our anonymous function immediately