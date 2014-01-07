'use strict';
var ringlet = angular.module('ringlet',['services']);

function UserCtrl($scope, DAO){

//---------------------------- Variables Initialization ------------------------------------------
    $scope.user = {email:'', password:''};
    $scope.showErrors = false;
    $scope.showFunctionError = false;
    $scope.showServerError = false;
    $scope.showPasswordError = false;
    $scope.showMessage = false;
    $scope.passwordConfirm = '';
    $scope.emailForgot = '';
    $scope.images = [];
    var appConfig = {serverHost:'192.168.0.102', appName:'ringlet', token:''};
    var carousel = $("#signup-carousel");

    function initializeVariables(){
        $scope.user = {email:'', password:''};
        $scope.showErrors = false;
        $scope.showFunctionError = false;
        $scope.showServerError = false;
        $scope.showPasswordError = false;
        $scope.showMessage = false;
        $scope.passwordConfirm = '';
        $scope.emailForgot = '';
        $scope.images = [];
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
                    $.mobile.loading( 'hide', {textVisible: false});
                    window.location.href="#home";
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

//---------------------------- Carousel Functions ------------------------------------------------
    carousel.owlCarousel({ autoPlay: false, itemsMobile : [479,3], itemsTablet: [768,3]});

    function addItem(data, id){
        var content = "<div class='item'><img class='user-image' id='"+id+"' onload='resizePhoto(this.id)' src='data:image/jpeg;base64,"+data+"'></div>";
        carousel.data('owlCarousel').addItem(content);
    }

    $scope.deletePhoto = function(){
        $scope.images.pop();
        carousel.data('owlCarousel').removeItem();
        $("#camera").popup("close");
    }

//---------------------------- Camera Functions --------------------------------------------------
    function onPhotoDataSuccess(imageData){
        $scope.images.push({data:imageData, isPublic:false});
        addItem(imageData, $scope.images.length);
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
}