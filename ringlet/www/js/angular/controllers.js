'use strict';
var ringlet = angular.module('ringlet',['services']);

function UserCtrl($scope, DAO, $compile, $timeout){

    var appConfig = {serverHost:'192.168.0.101', appName:'ringlet', token:''};


    //-----------------------------Listings functions-------------------------------------------------
    $scope.getRinglets = function(ringlets){
        $scope.ringlets = ringlets;


    };
    $scope.refreshListingImages = function(){
        $('#listing-lImages').listview('refresh');
        return true;
    }
}