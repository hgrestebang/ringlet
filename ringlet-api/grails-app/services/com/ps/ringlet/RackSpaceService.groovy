package com.ps.ringlet

import groovyx.net.http.HTTPBuilder
import org.apache.commons.codec.binary.Base64
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

class RackSpaceService {

    static transactional = false

    def authentication(){
        RackSpace data = RackSpace.findById(1)
        def http = new HTTPBuilder(data.host)
        http.request(GET){
            headers.'X-Auth-User' = data.authUser
            headers.'X-Auth-Key' = data.authKey
            response.success = { resp->
                data.storageUrl = resp.headers.'X-Storage-Url'
                data.cdnManagementUrl = resp.headers.'X-CDN-Management-Url'
                data.authToken = resp.headers.'X-Auth-Token'
                data.save(flush: true)
            }
            response.failure = {
                log.error "Rackspace authentication error"
            }
        }
    }

    def setContainerURL(){
        RackSpace data = RackSpace.findById(1)
        def http = new HTTPBuilder(data.cdnManagementUrl+"/"+data.container)
        http.request(PUT){
            headers.'X-Auth-Token' = data.authToken
            headers.'X-TTL' = 900
            headers.'X-CDN-Enabled' = "True"
            response.success = { resp->
                data.cdnUri = resp.headers.'X-Cdn-Uri'
                data.cdnSslUri = resp.headers.'X-Cdn-Ssl-Uri'
                data.cdnStreamingUri = resp.headers.'X-Cdn-Streaming-Uri'
                data.save(flush: true)
            }
            response.failure = {
                log.error "Rackspace setContainerURL error"
            }
        }
    }

    def storeImage(String imageData, Long pictureName){
        RackSpace data = RackSpace.findById(1)
        def http = new HTTPBuilder(data.storageUrl+"/"+data.container+"/"+pictureName+".jpeg")
        http.request(PUT, BINARY){
            headers.'X-Auth-Token' = data.authToken
            headers.'Content-Type' = "image/jpeg"
            body = Base64.decodeBase64(imageData.getBytes())
            response.success = { resp->
                return "success"
            }
            response.failure = {
                log.error "Rackspace storeImage error"
            }
        }
    }

    def deleteImage(Long pictureName){
        RackSpace data = RackSpace.findById(1)
        def http = new HTTPBuilder(data.storageUrl+"/"+data.container+"/"+pictureName+".jpeg")
        http.request(DELETE){
            headers.'X-Auth-Token' = data.authToken
            response.success = { resp->
                return "success"
            }
            response.failure = {
                log.error "Rackspace deleteImage error"
            }
        }
    }
}
