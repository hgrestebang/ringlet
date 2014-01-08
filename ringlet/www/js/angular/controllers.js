'use strict';
var ringlet = angular.module('ringlet',['services']);

function UserCtrl($scope, DAO){

//---------------------------- Variables Initialization ------------------------------------------
    $scope.user = {email:'', password:'', gender:'MALE'};
    $scope.userLocation = {};
    $scope.showErrors = false;
    $scope.showFunctionError = false;
    $scope.showServerError = false;
    $scope.showPasswordError = false;
    $scope.showMessage = false;
    $scope.passwordConfirm = '';
    $scope.emailForgot = '';
    $scope.ringlet=[];
    $scope.ringlets=[];
    var appConfig = {serverHost:'192.168.0.102', appName:'ringlet', token:''};
    var owl = $("#listing-item-gallery");
    var carousel = $("#signup-carousel");
    var profileCarousel = $("#profile-carousel");
    var photoCount = 0;
    var carouselLength = 0;
    $scope.images = [];
    $scope.deleteImages = [];

    function initializeVariables(){
        $scope.user = {email:'', password:'', gender:'MALE'};
        $scope.userLocation = {};
        $scope.showErrors = false;
        $scope.showFunctionError = false;
        $scope.showServerError = false;
        $scope.showPasswordError = false;
        $scope.showMessage = false;
        $scope.passwordConfirm = '';
        $scope.emailForgot = '';
        $scope.images = [];
        $scope.deleteImages = [];
        carouselLength = 0;
        appConfig = {serverHost:'192.168.0.102', appName:'ringlet', token:''};
    }

    $scope.errorValidation = function(){
        $scope.showErrors = false;
        $scope.showFunctionError = false;
        $scope.showServerError = false;
        $scope.showPasswordError = false;
        $scope.showMessage = false;
        $scope.passwordConfirm = '';
        $scope.emailForgot = '';
    }

//---------------------------- Authentication functions ------------------------------------------
    $scope.validateLogin = function(notValid){
        if(notValid){
            $scope.showErrors = true;
        }
        else{
            $scope.errorValidation();
            $scope.login();
        }
    }

    $scope.login = function(){
        $.mobile.loading( 'show', {textVisible: false});
        DAO.save({serverHost:appConfig.serverHost, appName:appConfig.appName, controller:'auth', action:'login', username:$scope.user.email, passwordHash:$scope.user.password, facebookId:$scope.user.id},
            function(result){
                if(result.response == "bad_login"){
                    $scope.showErrors = true;
                    $scope.showFunctionError = true;
                    $.mobile.loading( 'hide', {textVisible: false});
                }
                else{
                    $scope.user = result;
                    appConfig.token = result.token.token;
                    $scope.getNearByRinglets();
                }
            },
            function(error){
                $scope.showErrors = true;
                $scope.showServerError = true;
                $.mobile.loading( 'hide', {textVisible: false});
            });
    };

    $scope.logout = function(){
        DAO.get({serverHost:appConfig.serverHost, appName:appConfig.appName, token:appConfig.token, controller:'auth', action:'logout'},
            function(result){
                if(result.response == "logout_successfully"){
                    initializeVariables();
                    window.location.href="#landing-screen";
                }
            });
    };
    //-----------------------------Listings functions-------------------------------------------------
    $scope.getNearByRinglets = function(){
        DAO.query({serverHost:appConfig.serverHost, appName:appConfig.appName, controller:'user', action:'nearBy',token: appConfig.token},
            function(result){
                if(result.response == "bad_request"){
                    console.log("Error loading information");
                }
                else{
                    $scope.ringlets=result;
                    $.mobile.loading( 'hide', {textVisible: false});
                    window.location.href="#home";
                }
            }
        );
    }

    $scope.getRinglets = function(ringlet){
        $scope.ringlet = ringlet;
        deleteItems();
        for(var i=0; i< $scope.ringlet.photos.length; i++){
            addItem($scope.ringlet.photos[i].id, $scope.ringlet.photos[i].path);
        }
    }

//---------------------------- Facebook Authentication -------------------------------------------
    $scope.facebookLogin = function(){
        $.mobile.loading( 'show', {textVisible: false});
        FB.login(
            function(response) {
                if (response.status == "connected"){
                    facebookGetUser();
                }
            },{scope: "email"}
        );
    }

    function facebookGetUser(){
        FB.api('/me', {fields: 'id, name, gender, email'}, function(response){
            if (!response.error){
                $scope.user = response;
                authenticateUser();
            }
        });
    }

    function authenticateUser(){
        DAO.get({serverHost:appConfig.serverHost, appName:appConfig.appName, controller:'auth', action:'authenticateUser', facebookId:$scope.user.id},
            function(result){
                if(result.response == "user_not_found"){
                    window.location.href="#signup";
                }
                else{
                    $scope.login();
                }
            });
    }

//---------------------------- User Functions ----------------------------------------------------
    $scope.currentUser = function(){
        $.mobile.loading( 'show', {textVisible: false});
        $scope.deletePhotoProfile('true');
        $scope.images = [];
        $scope.deleteImages = [];
        DAO.get({serverHost:appConfig.serverHost, appName:appConfig.appName, token:appConfig.token, controller:'user', action:'getCurrent'},
            function(result){
                $scope.user = result;
                for(var i=0; i<$scope.user.photos.length; i++){
                    $scope.images.push({id:$scope.user.photos[i].id, path:$scope.user.photos[i].path, delete:false, data:''});
                    addPhotoProfile($scope.user.photos[i].path, $scope.images.length, true);
                }
                $.mobile.loading( 'hide', {textVisible: false});
                window.location.href="#profile";
            },
            function(error){
                console.log(error);
                $.mobile.loading( 'hide', {textVisible: false});
            });
    }

    $scope.validateForgot = function(notValid){
        if(notValid){
            $scope.showErrors = true;
        }
        else{
            $scope.showErrors = false;
            $scope.showFunctionError = false;
            $scope.showServerError = false;
            $scope.showPasswordError = false;
            $scope.showMessage = false;
            $scope.forgotPassword();
        }
    }

    $scope.forgotPassword = function(){
        $.mobile.loading( 'show', {textVisible: false});
        DAO.update({serverHost:appConfig.serverHost, appName:appConfig.appName, token:appConfig.token, controller:'user', action:'forgotPassword', username:$scope.emailForgot},
            function(result){
                if(result.response == "email_send"){
                    $scope.showErrors = true;
                    $scope.showMessage = true;
                    $.mobile.loading( 'hide', {textVisible: false});
                }
                else if(result.response == "user_not_found"){
                    $scope.showErrors = true;
                    $scope.showFunctionError = true;
                    $.mobile.loading( 'hide', {textVisible: false});
                }
                else if(result.response == "email_not_send"){
                    $scope.showErrors = true;
                    $scope.showPasswordError = true;
                    $.mobile.loading( 'hide', {textVisible: false});
                }
            },
            function(error){
                $scope.showErrors = true;
                $scope.showServerError = true;
                $.mobile.loading( 'hide', {textVisible: false});
            });
    }

    $scope.validateSignup = function(notValid){
        if(notValid){
            $scope.showErrors = true;
        }
        else{
            $scope.showErrors = false;
            $scope.showFunctionError = false;
            $scope.showServerError = false;
            $scope.signup();
        }
    }

    $scope.signup = function(){
        $.mobile.loading( 'show', {textVisible: false});
        if($scope.user.id){
            $scope.user.facebookId = $scope.user.id;
            $scope.user.id = null;
        }
        if($scope.userLocation.lat){
            $scope.user.userLocation = $scope.userLocation;
        }
        DAO.save({serverHost:appConfig.serverHost, appName:appConfig.appName, controller:'user', action:'create', user:$scope.user, images:$scope.images},
            function(result){
                if(result.response == "user_created"){
                    $.mobile.loading( 'hide', {textVisible: false});
                    $scope.login();
                }
                else if(result.response == "email_used"){
                    $scope.showErrors = true;
                    $scope.showFunctionError = true;
                    $.mobile.loading( 'hide', {textVisible: false});
                }
            },
            function(error){
                $scope.showErrors = true;
                $scope.showServerError = true;
                $.mobile.loading( 'hide', {textVisible: false});
            });
    }

    $scope.validateProfile = function(notValid){
        if(notValid){
            $scope.showErrors = true;
        }
        else{
            $scope.errorValidation();
            $scope.updateProfile();
        }
    }

    $scope.updateProfile = function(){
        $.mobile.loading( 'show', {textVisible: false});
        console.log($scope.deleteImages);
        for(var i=0; i<$scope.deleteImages.length; i++){
            $scope.images.push($scope.deleteImages[i]);
        }
        DAO.update({serverHost:appConfig.serverHost, appName:appConfig.appName, token:appConfig.token, controller:'user', action:'update', user:$scope.user, images:$scope.images},
            function(result){
                if(result.response == "user_updated"){
                    $scope.showErrors = true;
                    $scope.showMessage = true;
                    $.mobile.loading( 'hide', {textVisible: false});
                }
                else if(result.response == "email_used"){
                    $scope.showErrors = true;
                    $scope.showFunctionError = true;
                    $.mobile.loading( 'hide', {textVisible: false});
                }
            },
            function(error){
                $scope.showErrors = true;
                $scope.showServerError = true;
                $.mobile.loading( 'hide', {textVisible: false});
            });
    }

//---------------------------- Carousel Functions ------------------------------------------------
    carousel.owlCarousel({ autoPlay: false, itemsMobile : [479,3], itemsTablet: [768,3]});
    owl.owlCarousel({autoplay:false, itemsMobile : [479,3], itemsTablet: [768,3]});
    profileCarousel.owlCarousel({ autoPlay: false, itemsMobile : [479,3], itemsTablet: [768,3], afterAction : afterAction});

    function afterAction(){
        carouselLength = this.owl.owlItems.length;
        console.log(carouselLength);
    }

    function addPhoto(data, id){
        var content = "<div class='item'><img class='user-image' id='"+id+"' onload='resizePhoto(this.id)' src='data:image/jpeg;base64,"+data+"'></div>";
        carousel.data('owlCarousel').addItem(content);
    }

    function addPhotoProfile(data, id, isPath){
        var content = "";
        if(isPath){
            content = "<div class='item'><img class='user-image' id='"+id+"' onload='resizePhotoProfile(this.id)' src='"+data+"'></div>";
        }
        else{
            content = "<div class='item'><img class='user-image' id='"+id+"' onload='resizePhotoProfile(this.id)' src='data:image/jpeg;base64,"+data+"'></div>";
        }
        profileCarousel.data('owlCarousel').addItem(content);
    }

    function addItem(id, src){
        photoCount++;
        var content = "<div class='item'><img class='user-image' id='"+id+"' onclick='loadPopUp(this,this.id)' onload='changeSize(this)' src='"+src+"'></div>";
        owl.data('owlCarousel').addItem(content);
    }

    $scope.deletePhoto = function(){
        $scope.images.pop();
        carousel.data('owlCarousel').removeItem();
        $("#camera").popup("close");
    }

    $scope.deletePhotoProfile = function(removeAll){
        if(removeAll == 'true'){
            while(carouselLength > 1){
                profileCarousel.data('owlCarousel').removeItem();
            }
        }
        else{
            if($scope.images[$scope.images.length-1].id != ""){
                $scope.images[$scope.images.length-1].delete = true;
                $scope.deleteImages.push($scope.images.pop());
                console.log($scope.deleteImages)
            }
            else{
                $scope.images.pop()
            }
            profileCarousel.data('owlCarousel').removeItem();
            $("#profile-camera").popup("close");
        }
    }

    function deleteItems(){
        for(photoCount; photoCount>0; photoCount--){
            owl.data('owlCarousel').removeItem();
        }
    }

//---------------------------- Camera Functions --------------------------------------------------
    function onPhotoDataSuccess(imageData){
        $scope.images.push({data:imageData});
        addPhoto(imageData, $scope.images.length);
        $scope.$apply();
    }

    function onPhotoDataSuccessProfile(imageData){
        $scope.images.push({id:"", data:imageData, delete:false});
        addPhotoProfile(imageData, $scope.images.length, false);
        $scope.$apply();
    }

    function onFail(error) {
        console.log(error);
        $scope.$apply();
    }

    $scope.capturePhoto = function() {
        navigator.camera.getPicture(onPhotoDataSuccess, onFail, {quality:100, destinationType:0, sourceType:1, allowEdit:false, encodingType:0, targetWidth:250, targetHeight:250});
        $("#camera").popup("close");
    }

    $scope.getPhoto = function() {
        navigator.camera.getPicture(onPhotoDataSuccess, onFail, {quality:100, destinationType:0, sourceType:0, allowEdit:false, encodingType:0, targetWidth:250, targetHeight:250});
        $("#camera").popup("close");
    }

    $scope.capturePhotoProfile = function() {
        navigator.camera.getPicture(onPhotoDataSuccessProfile, onFail, {quality:100, destinationType:0, sourceType:1, allowEdit:false, encodingType:0, targetWidth:250, targetHeight:250});
        $("#profile-camera").popup("close");
    }

    $scope.getPhotoProfile = function() {
        navigator.camera.getPicture(onPhotoDataSuccessProfile, onFail, {quality:100, destinationType:0, sourceType:0, allowEdit:false, encodingType:0, targetWidth:250, targetHeight:250});
        $("#profile-camera").popup("close");
    }

//---------------------------- Location Functions ------------------------------------------------
    $scope.getLocation = function(){
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                function(position){
                    $scope.userLocation = {lat:position.coords.latitude, lgn:position.coords.longitude};
                });
        }
    }
}