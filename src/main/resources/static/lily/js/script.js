/**
 * WEBSITE: https://themefisher.com
 * REFACTORED FOR: UX4G / Bootstrap 5 Compatibility
 */

(function ($) {
    'use strict';

    // This function runs once the entire page is loaded
    $(document).ready(function () {

       // 1. Sticky Menu
       // This logic remains valid for UX4G as it manipulates custom classes (.top-header, .navigation)
       $(window).scroll(function () {
          var height = $('.top-header').innerHeight();
          // Check if header exists to avoid console errors
          if ($('header').length) {
              if ($('header').offset().top > 10) {
                 $('.top-header').addClass('hide');
                 $('.navigation').addClass('nav-bg');
                 $('.navigation').css('margin-top', '-' + height + 'px');
              } else {
                 $('.top-header').removeClass('hide');
                 $('.navigation').removeClass('nav-bg');
                 $('.navigation').css('margin-top', '-' + 0 + 'px');
              }
          }
       });

       // 2. Navbar Dropdown
       // UX4G (Bootstrap 5) handles dropdowns automatically via data-bs-toggle.
       // The original code manually animated the dropdown.
       // We only apply this if NOT using standard BS5 data attributes,
       // but for smoother UX4G integration, it's often better to rely on CSS transitions.
       /*
       if ($(window).width() < 992) {
          $('.navigation .dropdown-toggle').on('click', function () {
             $(this).siblings('.dropdown-menu').animate({
                height: 'toggle'
             }, 300);
          });
       }
       */

       // 3. Background-images
       // Helper to set background images defined in HTML data attributes
       $('[data-background]').each(function () {
          $(this).css({
             'background-image': 'url(' + $(this).data('background') + ')'
          });
       });

       // 4. Hero Slider (Slick)
       // Ensure Slick is loaded before running
       if ($.fn.slick) {
           $('.hero-slider').slick({
              autoplay: true,
              autoplaySpeed: 7500,
              pauseOnFocus: false,
              pauseOnHover: false,
              infinite: true,
              arrows: true,
              fade: true,
              // Note: Ensure FontAwesome or Themify icons are loaded for these classes
              prevArrow: '<button type=\'button\' class=\'prevArrow\'><i class=\'ti-angle-left\'></i></button>',
              nextArrow: '<button type=\'button\' class=\'nextArrow\'><i class=\'ti-angle-right\'></i></button>',
              dots: true
           });

           // Enable animations inside slick
           if($.fn.slickAnimation) {
               $('.hero-slider').slickAnimation();
           }
       }

       // 5. Venobox popup
       if ($.fn.venobox) {
           $('.venobox').venobox();
       }

       // 6. Filterizr
       var containerEl = document.querySelector('.filtr-container');
       if (containerEl && $.fn.filterizr) {
          var filterizd = $('.filtr-container').filterizr({});
       }

       // Active changer for filters
       $('.filter-controls li').on('click', function () {
          $('.filter-controls li').removeClass('active');
          $(this).addClass('active');
       });
    });

    // 7. Count Up Animation
    function counter() {
       var oTop;
       if ($('.count').length !== 0) {
          oTop = $('.count').offset().top - window.innerHeight;
       }
       if ($(window).scrollTop() > oTop) {
          $('.count').each(function () {
             var $this = $(this),
                countTo = $this.attr('data-count');
             $({
                countNum: $this.text()
             }).animate({
                countNum: countTo
             }, {
                duration: 1000,
                easing: 'swing',
                step: function () {
                   $this.text(Math.floor(this.countNum));
                },
                complete: function () {
                   $this.text(this.countNum);
                }
             });
          });
       }
    }

    $(window).on('scroll', function () {
       counter();
    });

})(jQuery);