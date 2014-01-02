package com.ps.ringlet

class RackSpace {

    String authUser         //Username RackSpace account
    String authKey          //API Key to make http requests
    String host             //RackSpace host and version
    String container        //Container name to store images
    String storageUrl       //URL to save images in the container
    String cdnManagementUrl //CDN container administration
    String authToken        //Token to make http requests
    String cdnUri           //URI to make the image public path
    String cdnSslUri        //SSLURI to make the image public path
    String cdnStreamingUri  //StreamingURI to make the image public path

    static constraints = {
        authUser nullable: true
        authKey nullable: true
        host nullable: true
        storageUrl nullable: true
        cdnManagementUrl nullable: true
        authToken nullable: true
        cdnUri nullable: true
        cdnSslUri nullable: true
        cdnStreamingUri nullable: true
        container nullable: true
    }
}