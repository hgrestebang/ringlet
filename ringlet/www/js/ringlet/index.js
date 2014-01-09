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

function loadPopUp(img){
    var myNode = document.getElementById("imagePopUpDiv");
    myNode.innerHTML = '';

    var elem = document.createElement("img");
    elem.setAttribute("src", img.src);
    elem.setAttribute("alt", "Image User");
    document.getElementById("imagePopUpDiv").appendChild(elem)
    var positionTo = $( ".selector" ).popup( "option", "positionTo" );
    $("#imagePopUpDiv").popup( "open", "option", "positionTo", "window" );

}

function resizeLogo(){
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
    $('#'+img.id).css({'width': ((screen.width-32)/3) + 'px'});
    $('#'+img.id).css({'height': ((screen.width-32)/3)+ 'px'});
}

function carouselImageError(img){
    document.getElementById(img.id).height = (screen.height*0.3);
}

$(document).on("pageshow","#home",function(){
    $("#listing-lImages" ).listview( "refresh" );
});