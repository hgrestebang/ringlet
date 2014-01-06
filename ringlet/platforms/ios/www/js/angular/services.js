'use strict';
angular.module('services',['ngResource']).
    factory('DAO', function($resource){
        return $resource('http://:serverHost/:appName/:controller/:action',{
            format:'json', callback:'JSON_CALLBACK'},{
            'get':   {method:'GET', isArray:false, timeout:15000},
            'query': {method:'GET', isArray: true, timeout:15000},
            'save':  {method:'POST', params:{server:'@serverHost', version:'@appName', controller:'@controller', action:'@action'}, isArray:false, timeout:15000},
            'update':{method:'PUT', params:{server:'@serverHost', version:'@appName', controller:'@controller', action:'@action'}, isArray:false, timeout:15000},
            'delete':{method:'DELETE', isArray:false, timeout:15000}
        });
    });