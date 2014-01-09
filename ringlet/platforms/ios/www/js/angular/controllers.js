'use strict';
var ringlet = angular.module('ringlet',['services']);

function UserCtrl($scope, $compile, DAO){

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
    $scope.ringster=[];
    $scope.ringsters=[];
    $scope.images = [];
    $scope.deleteImages = [];
    var appConfig = {serverHost:'192.168.0.109', appName:'ringlet', token:''};
    var owl = $("#listing-item-gallery");
    var carousel = $("#signup-carousel");
    var profileCarousel = $("#profile-carousel");
    var photoCount = 0;
    var carouselLength = 0;
    var map = L.map('map-area');

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
        appConfig = {serverHost:'192.168.0.109', appName:'ringlet', token:''};
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
                    $scope.loadPurchase();
                    appConfig.token = result.token.token;
                    $scope.getNearByRingsters();
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
    $scope.getNearByRingsters = function(){
        DAO.query({serverHost:appConfig.serverHost, appName:appConfig.appName, controller:'user', action:'nearBy',token: appConfig.token},
            function(result){
                if(result.response == "bad_request"){
                    console.log("Error loading information");
                }
                else{
                    $scope.ringsters=result;
                    $.mobile.loading( 'hide', {textVisible: false});
                    window.location.href="#home";
                }
            }
        );
    }

    $scope.getRingsters = function(ringster){
        $scope.ringster = ringster;
        deleteItems();
        for(var i=0; i< $scope.ringster.photos.length; i++){
            addItem('info'+$scope.ringster.photos[i].id, $scope.ringster.photos[i].path);
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
                    addPhotoProfile($scope.user.photos[i].path, 'profile'+$scope.images.length, true);
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
    }

    function addPhoto(data, id){
        var content = "<div class='item'><img class='user-image' id='"+id+"' onload='changeSize(this)' src='data:image/jpeg;base64,"+data+"'></div>";
        carousel.data('owlCarousel').addItem(content);
    }

    function addPhotoProfile(data, id, isPath){
        var content = "";
        if(isPath){
            content = "<div class='item'><img class='user-image' id='"+id+"' onload='changeSize(this)' src='"+data+"'></div>";
        }
        else{
            content = "<div class='item'><img class='user-image' id='"+id+"' onload='changeSize(this)' src='data:image/jpeg;base64,"+data+"'></div>";
        }
        profileCarousel.data('owlCarousel').addItem(content);
    }

    function addItem(id, src){
        photoCount++;
        var content = "<div class='item'><img class='user-image' id='"+id+"' onclick='loadPopUp(this)' onload='changeSize(this)' src='"+src+"'></div>";
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
        addPhoto(imageData, 'signup'+$scope.images.length);
        $scope.$apply();
    }

    function onPhotoDataSuccessProfile(imageData){
        $scope.images.push({id:"", data:imageData, delete:false});
        addPhotoProfile(imageData, 'profile'+$scope.images.length, false);
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

//-----------------------------APP Purchase-------------------------------------------------------
    $scope.loadPurchase=function(){
        $scope.IAP.initialize();
    }

    $scope.IAP = {
        list: [ "com.ps.mconn.ringlet.prouser"],
        products: {}
    };
    var localStorage = window.localStorage || {};
    $scope.reload = "yes";
    $scope.IAP.initialize = function () {
        // Check availability of the storekit plugin

        if (!window.InAppPurchase) {
            console.log('In-App Purchases not available');
            return;
        }


        InAppPurchase.prototype.init({
            ready:    $scope.IAP.onReady,
            purchase: $scope.IAP.onPurchase,
            restore:  $scope.IAP.onRestore,
            error:    $scope.IAP.onError
        });
    };

    $scope.IAP.onReady = function () {
        // Once setup is done, load all product data.
        InAppPurchase.prototype.load($scope.IAP.list, function (products, invalidIds) {
            console.log('IAPs loading done:');
            for (var j = 0; j < products.length; ++j) {
                var p = products[j];
                console.log('Loaded IAP(' + j + '). title:' + p.title +
                    ' description:' + p.description +
                    ' price:' + p.price +
                    ' id:' + p.id);
                $scope.IAP.products[p.id] = p;
            }
            $scope.IAP.loaded = true;

            for (var i = 0; i < invalidIds.length; ++i) {
                console.log('Error: could not load ' + invalidIds[i]);
            }
            renderIAPs();
        });
    };

    $scope.IAP.onPurchase = function (transactionId, productId, receipt) {
//        save in db  transactionId, productId
        var n = (localStorage['storekit.' + productId]|0) + 1;
        localStorage['storekit.' + productId] = n;
        if ($scope.IAP.purchaseCallback) {
            $scope.IAP.purchaseCallback(productId);
            delete $scope.IAP.purchaseCallbackl;
        }
        $scope.makePurchase(transactionId, productId, $scope.IAP.products[productId].price);
    };

    $scope.makePurchase =function(transaction,item,amount){
        DAO.save({serverHost:appConfig.serverHost, appName:appConfig.appName, token:appConfig.token, controller:'purchase', action:'makePurchase', itemId:item,transaction:transaction, amount:amount},
            function(result){
                if(result.response=="user_updated"){
                    $scope.user.isPro=true;
                }

            });
    }

    $scope.IAP.buy = function (productId, callback) {
        $scope.IAP.purchaseCallback = callback;
        InAppPurchase.prototype.purchase(productId);
    };

    $scope.IAP.onError = function (errorCode, errorMessage) {
        alert('Error: ' + errorMessage);
    };

    $scope.IAP.onRestore = function (transactionId, productId/*, transactionReceipt*/) {
        var n = (localStorage['storekit.' + productId]|0) + 1;
        localStorage['storekit.' + productId] = n;
    };

    $scope.IAP.restore = function () {
        InAppPurchase.prototype.restore();
    };

    $scope.buyItem= function(id){
        $scope.IAP.buy(id);
    }
    var renderIAPs = function () {
        if ($scope.IAP.loaded) {
            var html = "";
            for (var id in $scope.IAP.products) {
                var prod = $scope.IAP.products[id];

                html+="<a data-role='button' data-ng-click='buyItem(\"" + id + "\")'>"+
                    '<span class="left">'+ prod.title +
                    '</span><span>'  + prod.price  +'</span></a>'

            }
            var compile = $compile(html)($scope);
            $('#pro-ulPackages').append(compile);
        }
        else {
            console.log("In-App Purchases not available.");
        }
    };

    //----------------------------------Map Functions--------------------------------------------------------------
    $scope.initMap = function(){
//        clearMap();
        var sHeight = screen.height;
        var sWidth = screen.width;

        L.tileLayer('http://{s}.tile.cloudmade.com/2bb3a432a04845c3bda71c1fb668f4e5/997/256/{z}/{x}/{y}.png', {
            attribution: 'Powered by PureSrc', maxZoom: 18, setView: true}).addTo(map);
        map.setView([$scope.userLocation.lat, $scope.userLocation.lgn], 14);

//        var markers = L.markerClusterGroup();
//
//        for (var i=0; i< $scope.ringsters.length; i++){
//
//            var msg =  '<div class="clickDiv" id="'+$scope.ringsters[i].id+'" style="width:'+(sWidth-45)+'px"><a href="#listing-item" data-transition="slidefade">';
//            //msg +=      '<img id="imgMap-'+$scope.ringsters[i].id+'" class="img-popup" src="'+$scope.ringsters[i].photos[0].photo_protocol+$scope.ringsters[i].photos[0].photo_host+"/"+$scope.ringsters[i].photos[0].photo_path+'" onload="changeSizeImgList(this)" onerror="imgError(this)>';
//            msg +=      '<strong style="color: black;">'+""+$scope.ringsters[i].name+""+'</strong><br/><br/>';
//            msg +=    '</a></div>';
//            var marker = L.marker(new L.LatLng($scope.ringsters[i].location.lat, $scope.ringsters[i].location.lgn));
//            marker.bindPopup(msg);
//            markers.addLayer(marker);
//        }
//
//        map.addLayer(markers);
    }

    $('#map').bind('pageshow', function() {
        map.invalidateSize();
    });

    function clearMap() {
        for(var i in map._layers) {
            try {
                map.removeLayer(map._layers[i]);
            }
            catch(e) {
                console.log("problem with " + e + map._layers[i]);
            }

        }
    }

}