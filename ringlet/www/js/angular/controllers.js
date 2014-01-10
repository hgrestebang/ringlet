'use strict';
var ringlet = angular.module('ringlet',['services']);

function UserCtrl($scope, $compile, DAO, $timeout){

//---------------------------- Variables Initialization ------------------------------------------
    $scope.user = {email:'', password:'', gender:'MALE'};
    $scope.userSearch = {name:'', username:'', phone:''};
    $scope.invitation = {message:'I want to add you to my friends', recipientId:''};
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
    $scope.announcements=[];
    $scope.announcementItem=[];
    $scope.images = [];
    $scope.deleteImages = [];
    $scope.announcement = {location:[]};
    $scope.searchRadius = [
        {miles:'5', radius:'5 miles'},
        {miles:'10', radius:'10 miles'},
        {miles:'20', radius:'20 miles'},
        {miles:'30', radius:'30 miles'},
        {miles:'40', radius:'40 miles'},
        {miles:'50', radius:'50 miles'}];
    $scope.announcement.radius = $scope.searchRadius[0];

    var appConfig = {serverHost:'186.15.176.147', appName:'ringlet', token:''};
    var owl = $("#listing-item-gallery");
    var carousel = $("#signup-carousel");
    var profileCarousel = $("#profile-carousel");
    var photoCount = 0;
    var carouselLength = 0;
    var chatFunction = null;
    var announcementFunction = null;
    var invitationFunction = null;
    var map = L.map('map-area');
    var mapAnnouncemnt = L.map('map-Announcement',{
        dragging: false,
        touchZoom: false,
        zoomControl: true,
        scrollWheelZoom: false,
        doubleClickZoom: false,
        boxZoom: true,
        tap: false,
        trackResize: true
    });
    var mapItemAnnouncement = L.map('map-Item-Announcement',{
        dragging: false,
        touchZoom: false,
        zoomControl: true,
        scrollWheelZoom: false,
        doubleClickZoom: false,
        boxZoom: true,
        tap: false,
        trackResize: true
    });

    function initializeVariables(){
        $scope.user = {email:'', password:'', gender:'MALE'};
        $scope.userSearch = {name:'', username:'', phone:''};
        $scope.invitation = {message:'I want to add you to my friends', recipientId:''};
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
        appConfig = {serverHost:'186.15.176.147', appName:'ringlet', token:''};
    }

    $scope.errorValidation = function(){
        $scope.userSearch = {name:'', username:'', phone:''};
        $scope.invitation = {message:'I want to add you to my friends', recipientId:''};
        $scope.showErrors = false;
        $scope.showFunctionError = false;
        $scope.showServerError = false;
        $scope.showPasswordError = false;
        $scope.showMessage = false;
        $scope.currentPassword = '';
        $scope.newPassword = '';
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
                else if(result.id == undefined){
                    $scope.showErrors = true;
                    $scope.showServerError = true;
                    $.mobile.loading( 'hide', {textVisible: false});
                }
                else{
                    $scope.user = result;
                    if($scope.user.friends == null) $scope.user.friends = [];
                    $scope.loadPurchase();
                    appConfig.token = result.token.token;
                    chatFunction = $timeout(serverChat, 2000);
                    announcementFunction = $timeout(serverAnnouncement, 4000);
                    invitationFunction = $timeout(serverInvitation, 6000);
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
    $scope.isFriend = function(){
        if($scope.user.friends != null) return ($scope.user.friends.indexOf($scope.ringster.id) > -1);
        else return false;
    }

    $scope.currentUser = function(){
        $.mobile.loading( 'show', {textVisible: false});
        $scope.errorValidation();
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

    $scope.getFriends = function(){
        $.mobile.loading( 'show', {textVisible: false});
        $scope.errorValidation();
        DAO.query({serverHost:appConfig.serverHost, appName:appConfig.appName, token:appConfig.token, controller:'user', action:'getFriends'},
            function(result){
                $scope.ringsters = result;
                $.mobile.loading( 'hide', {textVisible: false});
                window.location.href="#home";
            },
            function(error){
                console.log(error);
                $.mobile.loading( 'hide', {textVisible: false});
            });
    }

    $scope.validateSearch = function(){
        if($scope.userSearch.name == "" && $scope.userSearch.username == "" && $scope.userSearch.phone == ""){
            $scope.showErrors = true;
            $scope.showMessage = true;
        }
        else{
            $scope.showErrors = false;
            $scope.showFunctionError = false;
            $scope.showServerError = false;
            $scope.showMessage = false;
            $scope.makeSearch();
        }
    }

    $scope.makeSearch = function(){
        $.mobile.loading( 'show', {textVisible: false});
        DAO.query({serverHost:appConfig.serverHost, appName:appConfig.appName, token:appConfig.token, controller:'user', action:'search', name:$scope.userSearch.name, username:$scope.userSearch.username, phone:$scope.userSearch.phone},
            function(result){
                if(result[0].response == "not_found"){
                    $scope.showErrors = true;
                    $scope.showFunctionError = true;
                    $.mobile.loading( 'hide', {textVisible: false});
                }
                else{
                    $scope.ringsters = result;
                    $.mobile.loading( 'hide', {textVisible: false});
                    $.mobile.changePage("#home");
                }
            },
            function(error){
                $scope.showErrors = true;
                $scope.showServerError = true;
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
        DAO.update({serverHost:appConfig.serverHost, appName:appConfig.appName, controller:'user', action:'forgotPassword', username:$scope.emailForgot},
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

    $scope.validateChangePassword = function(notValid){
        if(notValid){
            $scope.showErrors = true;
        }
        else{
            $scope.showErrors = false;
            $scope.showFunctionError = false;
            $scope.showServerError = false;
            $scope.showPasswordError = false;
            $scope.changePassword();
        }
    }

    $scope.changePassword = function(){
        if($scope.newPassword == $scope.passwordConfirm){
            $.mobile.loading( 'show', {textVisible: false});
            DAO.update({serverHost:appConfig.serverHost, appName:appConfig.appName, token:appConfig.token, controller:'user', action:'changePassword', currentPassword:$scope.currentPassword, newPassword:$scope.newPassword},
                function(result){
                    if(result.response == "user_updated"){
                        $.mobile.loading( 'hide', {textVisible: false});
                        $scope.currentUser();
                    }
                    else if(result.response == "password_incorrect"){
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
        else{
            $scope.showErrors = true;
            $scope.showPasswordError = true;
            $.mobile.loading( 'hide', {textVisible: false});
        }
    }

    $scope.getInvitations = function(){
        $.mobile.loading( 'show', {textVisible: false});
        $scope.errorValidation();
        DAO.query({serverHost:appConfig.serverHost, appName:appConfig.appName, token:appConfig.token, controller:'invitation', action:'getByUser'},
            function(result){
                $scope.invitations = result;
                $.mobile.loading( 'hide', {textVisible: false});
                window.location.href="#invitation-list";
            },
            function(error){
                console.log(error);
                $.mobile.loading( 'hide', {textVisible: false});
            });
    };

//---------------------------- Server Functions --------------------------------------------------
    var serverInvitation = function(){
        DAO.query({serverHost:appConfig.serverHost, appName:appConfig.appName, token:appConfig.token, controller:'invitation', action:'getByUser'},
            function(result){
                $scope.invitations = result;
                invitationFunction = $timeout(serverInvitation, 22000);
            });
    };

    var serverAnnouncement = function(){
        DAO.query({serverHost:appConfig.serverHost, appName:appConfig.appName, token:appConfig.token, controller:'announcement', action:'getByUser'},
            function(result){
                if(result.response == "not_found"){
                    $scope.announcements = [];
                }
                else{
                    $scope.announcements=result;
                }
                announcementFunction = $timeout(serverAnnouncement, 15000);
            });
    };

    var serverChat = function(){
        DAO.query({serverHost:appConfig.serverHost, appName:appConfig.appName, token:appConfig.token, controller:'chat', action:'getAll'},
            function(result){
                $scope.chats = result;
                chatFunction = $timeout(serverChat, 4000);
            });
    };

    $scope.stopInvitationFunction = function(){
        $timeout.cancel(invitationFunction);
    };

    $scope.startInvitationFunction = function(){
        invitationFunction = $timeout(serverInvitation, 22000);
    };

    $scope.stopAnnouncementFunction = function(){
        $timeout.cancel(announcementFunction);
    };

    $scope.startAnnouncementFunction = function(){
        announcementFunction = $timeout(serverAnnouncement, 15000);
    };

    $scope.stopChatFunction = function(){
        $timeout.cancel(chatFunction);
    };

    $scope.startChatFunction = function(){
        chatFunction = $timeout(serverChat, 4000);
    };

    $(document).on("pagebeforeshow","#invitation-list",function(){
        $scope.stopInvitationFunction();
    });

    $(document).on("pageshow","#invitation-list",function(){
        $("#invitation-list-view" ).listview( "refresh" );
    });

    $(document).on("pagehide","#invitation-list",function(){
        $scope.startInvitationFunction();
    });

    $(document).on("pagebeforeshow","#announcement-List",function(){
        $scope.stopInvitationFunction();
    });

    $(document).on("pageshow","#announcement-List",function(){
        $("#listing-Announcement" ).listview( "refresh" );
    });

    $(document).on("pagehide","#announcement-List",function(){
        $scope.startInvitationFunction();
    });

//---------------------------- Invitation Functions ----------------------------------------------
    $scope.getInvitations = function(){
        $.mobile.loading( 'show', {textVisible: false});
        $scope.errorValidation();
        DAO.query({serverHost:appConfig.serverHost, appName:appConfig.appName, token:appConfig.token, controller:'invitation', action:'getByUser'},
            function(result){
                $scope.invitations = result;
                $.mobile.loading( 'hide', {textVisible: false});
                window.location.href="#invitation-list";
            },
            function(error){
                console.log(error);
                $.mobile.loading( 'hide', {textVisible: false});
            });
    };

    $scope.getInvitation = function(invitation){
        $scope.invitation = invitation;
    };

    $scope.validateInvitation = function(notValid){
        if(notValid){
            $scope.showErrors = true;
        }
        else{
            $scope.showErrors = false;
            $scope.showFunctionError = false;
            $scope.showServerError = false;
            $scope.sendInvitation();
        }
    }

    $scope.sendInvitation = function(){
        $.mobile.loading( 'show', {textVisible: false});
        $scope.invitation.recipientId = $scope.ringster.id;
        DAO.save({serverHost:appConfig.serverHost, appName:appConfig.appName, token:appConfig.token, controller:'invitation', action:'create', invitation:$scope.invitation},
            function(result){
                if(result.response == "invitation_created"){
                    $.mobile.loading( 'hide', {textVisible: false});
                    window.location.href="#listing-item";
                }
                else if(result.response == "invitation_not_created"){
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

    $scope.acceptInvitation = function(){
        $.mobile.loading( 'show', {textVisible: false});
        DAO.update({serverHost:appConfig.serverHost, appName:appConfig.appName, token:appConfig.token, controller:'invitation', action:'acceptInvitation', id:$scope.invitation.id},
            function(result){
                if(result.response == "invitation_accepted"){
                    $.mobile.loading( 'hide', {textVisible: false});
                    $scope.user.friends.push($scope.invitation.owner);
                    $scope.getInvitations();

                }
                else{
                    $scope.showErrors = true;
                    $scope.showServerError = true;
                    $.mobile.loading( 'hide', {textVisible: false});
                }
            },
            function(error){
                $scope.showErrors = true;
                $scope.showServerError = true;
                $.mobile.loading( 'hide', {textVisible: false});
            });
    };

    $scope.declineInvitation = function(){
        $.mobile.loading( 'show', {textVisible: false});
        DAO.update({serverHost:appConfig.serverHost, appName:appConfig.appName, token:appConfig.token, controller:'invitation', action:'declineInvitation', id:$scope.invitation.id},
            function(result){
                if(result.response == "invitation_declined"){
                    $.mobile.loading( 'hide', {textVisible: false});
                    $scope.getInvitations();

                }
                else{
                    $scope.showErrors = true;
                    $scope.showServerError = true;
                    $.mobile.loading( 'hide', {textVisible: false});
                }
            },
            function(error){
                $scope.showErrors = true;
                $scope.showServerError = true;
                $.mobile.loading( 'hide', {textVisible: false});
            });
    };

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
    //----------------------------------Announcement Functions--------------------------------------------------------------
    $scope.saveAnnouncement = function(){
        if($scope.announcement.body!=""){
            $.mobile.loading( 'show', {textVisible: false});
            DAO.save({serverHost:appConfig.serverHost, appName:appConfig.appName, controller:'announcement', action:'create',token:appConfig.token, announcement: $scope.announcement},
                function(result){
                    if(result.response == "announcement_created"){
                        $scope.announcement.total=result.totalSend
                        $scope.announcement.body=""
                        $scope.announcement.radius = $scope.searchRadius[0];
                        var positionTo = $( ".selector" ).popup( "option", "positionTo" );
                        $("#popup-Announcement").popup( "open", "option", "positionTo", "window" );
                    }
                    else{

                    }
                    $.mobile.loading( 'hide', {textVisible: false});
                },
                function(error){
                    $scope.showErrors = true;
                    $scope.showServerError = true;
                    $.mobile.loading( 'hide', {textVisible: false});
                });
        }
        else
        {
            $scope.showErrors = true;
            $scope.showFunctionError = true;
        }
    }

    $scope.getAnnouncements = function(){
        DAO.query({serverHost:appConfig.serverHost, appName:appConfig.appName, controller:'announcement', action:'getByUser',token: appConfig.token},
            function(result){
                if(result.response == "not_found"){
                    $scope.announcements=[];
                    console.log("Error loading information");
                }
                else{
                    $scope.announcements=[];
                    $scope.announcements=result;
                }
            }
        );
    }

    $scope.getTimeAgo = function (dateIn){
        var date = Date.parse(dateIn);
        var time;
        var minutes = Math.round((((new Date() - date) % 86400000) % 3600000) / 60000);
        if(minutes == 0) time = "a few seconds ago";
        else if(minutes == 1) time = "1 minute ago";
        else if(minutes < 60) time = minutes+" minutes ago";
        else{
            var hours = Math.round(((new Date() - date) % 86400000) / 3600000);
            if(hours == 1) time = "1 hour ago";
            else if(hours < 24) time = hours+" hours ago";
            else{
                var days = Math.round((new Date() - date) / 86400000);
                if(days == 1) time = "1 day ago";
                else time = days+" days ago";
            }
        }
        return time;
    }

    $scope.getAnnouncement=function(announcement){
        $scope.announcementItem = announcement;
        $scope.announcementItemForm();
    }

    $scope.deleteAnnouncement = function(){
        $.mobile.loading( 'show', {textVisible: false});
        var ref=""
        if($scope.announcements[1]!=undefined)
        {
            ref="#announcement-List";
        }else{
            ref="#home";
        }
        DAO.update({serverHost:appConfig.serverHost, appName:appConfig.appName, controller:'announcement', action:'delete',token: appConfig.token,id:$scope.announcementItem.id},
            function(result){
                if(result.response == "announcement_deleted"){
                    console.log("element deleted");
                    $scope.getAnnouncements();
                    $scope.$apply();
                    console.log(ref)
                        window.location.href=ref;
                    $.mobile.loading( 'hide', {textVisible: false});
                }
                else{
                    $scope.showErrors = true;
                    $scope.showServerError = true;
                }
            },
            function(error){
                $scope.showErrors = true;
                $scope.showServerError = true;
                $.mobile.loading( 'hide', {textVisible: false});
            });
    }

    //----------------------------------Map Functions--------------------------------------------------------------
    $scope.initMap = function(){
//        clearMap();
        var sHeight = screen.height;
        var sWidth = screen.width;

        L.tileLayer('http://{s}.tile.cloudmade.com/2bb3a432a04845c3bda71c1fb668f4e5/997/256/{z}/{x}/{y}.png', {
            attribution: 'Powered by PureSrc', maxZoom: 18, setView: true}).addTo(map);
        map.setView([$scope.userLocation.lat, $scope.userLocation.lgn], 14);

        var markers = L.markerClusterGroup();

        for (var i=0; i< $scope.ringsters.length; i++){

            var msg =  '<div class="clickDiv" id="'+$scope.ringsters[i].id+'"><a data-transition="slidefade">';
            msg +=      '<img id="imgMap-'+$scope.ringsters[i].id+'" class="img-popup" src="'+$scope.ringsters[i].photos[0].path+'" onload="changeSizeImgList(this)" onerror="imgError(this)>';
            msg +=      '<br/><strong style="color: black; text-decoration: none">'+""+$scope.ringsters[i].name+""+'</strong><br/><br/>';
            msg +=    '</a></div>';
            var marker = L.marker(new L.LatLng($scope.ringsters[i].location.lat, $scope.ringsters[i].location.lgn));
            marker.bindPopup(msg);
            markers.addLayer(marker);
        }

        map.addLayer(markers);
    }

    $('#map').bind('pageshow', function() {
        map.invalidateSize();
    });
    $('#announcement').bind('pageshow', function() {
        mapAnnouncemnt.invalidateSize();
    });
    $('#announcement-item').bind('pageshow', function() {
        mapItemAnnouncement.invalidateSize();
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

    $( document ).on( "click", ".clickDiv", function() {
        for(var i=0; i<$scope.ringsters.length; i++){
            if(this.id.toString()==$scope.ringsters[i].id.toString()){
                $scope.getRingsters($scope.ringsters[i])
            }
        }
        $scope.$apply();
        window.location.href="#listing-item";

    });

    $scope.announcementForm = function(){
        $scope.announcement.location[0]=$scope.userLocation.lat;
        $scope.announcement.location[1]=$scope.userLocation.lgn;
        mapAnnouncemnt.setView([$scope.announcement.location[0],$scope.announcement.location[1]], 9);
        L.tileLayer('http://{s}.tile.cloudmade.com/2bb3a432a04845c3bda71c1fb668f4e5/997/256/{z}/{x}/{y}.png', {
            attribution: 'Powered by PureSrc',
            setView: true
        }).addTo(mapAnnouncemnt);
        $scope.printRadius();
        var userIcon = L.AwesomeMarkers.icon({
            icon: 'bullhorn',
            color: 'darkgreen'
        })
       var marker = L.marker([$scope.announcement.location[0],$scope.announcement.location[1]], {icon:userIcon}, {draggable:false}).addTo(mapAnnouncemnt);
    }
    $scope.announcementItemForm = function(){
        mapItemAnnouncement.setView([$scope.announcementItem.location[0],$scope.announcementItem.location[1]], 9);
        L.tileLayer('http://{s}.tile.cloudmade.com/2bb3a432a04845c3bda71c1fb668f4e5/997/256/{z}/{x}/{y}.png', {
            attribution: 'Powered by PureSrc',
            setView: true
        }).addTo(mapItemAnnouncement);
        markRadius(mapAnnouncemnt, $scope.announcementItem.location[0], $scope.announcementItem.location[1], $scope.announcementItem.radius );
        var userIcon = L.AwesomeMarkers.icon({
            icon: 'bullhorn',
            color: 'darkgreen'
        })
        var marker = L.marker([$scope.announcementItem.location[0],$scope.announcementItem.location[1]], {icon:userIcon}, {draggable:false}).addTo(mapItemAnnouncement);
    }

    $scope.printRadius = function(){
        markRadius(mapAnnouncemnt, $scope.announcement.location[0], $scope.announcement.location[1], $scope.announcement.radius.miles );
    }

    function markRadius(map, lat, lng, radius){
        if(!$scope.circle){
            $scope.circle = L.circle([lat, lng], radius*1600, {
                color: 'red',
                fillColor: '#f03',
                fillOpacity: 0.5
            }).addTo(map);
        }
        else{
            map.removeLayer($scope.circle);
            $scope.circle =  L.circle([lat, lng], radius*1600, {
                color: 'red',
                fillColor: '#f03',
                fillOpacity: 0.5
            }).addTo(map);
        }
    }
}