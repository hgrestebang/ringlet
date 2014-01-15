package com.ps.ringlet

import javapns.Push
import javapns.communication.exceptions.KeystoreException
import javapns.notification.PushedNotification
import javapns.notification.ResponsePacket
import javax.naming.CommunicationException

class ApnService {

    static transactional = false

//        def certification = "grails-app/conf/ck.p12"
    def certification= "ck.p12"

    def pushNotifications(String userID, String message){


        User user = User.findById(userID as Long)
        if(user.deviceToken){
            if(message!=""){
                sendMessageToDevices(user.deviceToken,message)
            }
        }
    }

    def sendMessageToDevices(String deviceToken, String messageKey) {
        try {
            List<PushedNotification> notifications =  Push.alert(messageKey, certification, "Puresrc2013", false, deviceToken)
            for (PushedNotification notification : notifications) {
                if (notification.isSuccessful()) {
                    System.out.println("Push notification sent successfully to: " + notification.getDevice().getToken())
                } else {
                    String invalidToken = notification.getDevice().getToken()
                    Exception theProblem = notification.getException()
                    theProblem.printStackTrace()
                    ResponsePacket theErrorResponse = notification.getResponse()
                    if (theErrorResponse != null) {
                        System.out.println(theErrorResponse.getMessage())
                    }
                }
            }
        } catch (KeystoreException e) {
            e.printStackTrace()
        } catch (CommunicationException e) {
            e.printStackTrace()
        }
    }
}
