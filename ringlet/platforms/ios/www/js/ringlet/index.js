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