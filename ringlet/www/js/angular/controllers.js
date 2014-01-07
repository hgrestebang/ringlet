'use strict';
var ringlet = angular.module('ringlet',['services']);

function UserCtrl($scope, DAO){

    $scope.user = {email:'', password:''};
    $scope.showErrors = false;
    $scope.showFunctionError = false;
    $scope.showServerError = false;
    $scope.showPasswordError = false;
    $scope.showMessage = false;
    $scope.passwordConfirm = '';
    $scope.emailForgot = '';
    $scope.ringlet=[];
    $scope.ringlets=[];
    var appConfig = {serverHost:'192.168.0.101', appName:'ringlet', token:''};
    var owl = $("#listing-item-gallery");
    var photoCount = 0;

    function initializeVariables(){
        $scope.user = {email:'', password:''};
        appConfig = {serverHost:'192.168.0.101', appName:'ringlet', token:''};
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

//-----------------------------Authentication functions-------------------------------------------
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
                    console.log($scope.ringlets);
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
            addItem($scope.ringlet.photos[i].id, $scope.ringlet.photos[i].photo_protocol+$scope.ringlet.photos[i].photo_host+"/"+$scope.ringlet.photos[i].photo_path);
        }
    }

//------------------Facebook Authentication-------------------------------------------------------
    $scope.facebookLogin = function(){
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

//-----------------------------Carousel functions-------------------------------------------------
    owl.owlCarousel({navigation:true, slideSpeed:300, paginationSpeed:400, lazyLoad:true, singleItem:true});

    function addItem(id, src){
        photoCount++;
        var content = "<div class='item'><img class='lazyOwl' id='"+id+"' data-src='"+src+"' onload='changeSize(this)' onerror='carouselImageError(this)'></div>";
        owl.data('owlCarousel').addItem(content);
    }

    function deleteItems(){
        for(photoCount; photoCount>0; photoCount--){
            owl.data('owlCarousel').removeItem();
        }
    }

}