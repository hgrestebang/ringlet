'use strict';
angular.module('services',['ngResource']).
    factory('DAO', function($resource){
        return $resource('http://:serverHost/:appName/:controller/:action',{
            format:'json', callback:'JSON_CALLBACK'},{
            'get':   {method:'GET', isArray:false, timeout:20000},
            'query': {method:'GET', isArray: true, timeout:20000},
            'save':  {method:'POST', params:{serverHost:'@serverHost', appName:'@appName', controller:'@controller', action:'@action'}, isArray:false, timeout:20000},
            'update':{method:'PUT', params:{serverHost:'@serverHost', appName:'@appName', controller:'@controller', action:'@action'}, isArray:false, timeout:20000},
            'delete':{method:'DELETE', isArray:false, timeout:20000}
        });
    });