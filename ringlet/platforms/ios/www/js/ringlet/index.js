'use strict';
var app = {

    initialize: function() {
        this.bindEvents();
    },

    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, true);
    },

    onDeviceReady: function() {
        try {
            FB.init({ appId: "715758418464029", nativeInterface: CDV.FB, useCachedDialogs: false });
        } catch (error) {
            console.log(error);
        }

        $( document ).on( "click", ".icon-arrow-left", function() {
            $.mobile.loading('hide');
        });
    }
};

function resizeLgo(){
    if(window.innerHeight < window.innerWidth){
        $('#landing-icon').css({'height': (window.innerHeight/2)+'px'});
    }
    else{
        $('#landing-icon').css({'height': (window.innerWidth/2)+'px'});
    }
}
function changeSizeImgList(img){
    document.getElementById(img.id).height = 76;
    document.getElementById(img.id).width = 76;
}

function imgError(image){
    document.getElementById(image.id).src = "img/house.png";
    document.getElementById(image.id).height = 76;
    document.getElementById(image.id).width = 76;
}
function changeSize(img){
    document.getElementById(img.id).height = (screen.height*0.4);
}

function carouselImageError(img){
    document.getElementById(img.id).height = (screen.height*0.4);
}