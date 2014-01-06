package com.ps.ringlet

import grails.converters.JSON
import org.apache.shiro.authc.SimpleAccount
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.crypto.hash.Sha256Hash

class AuthController {

    static allowedMethods = [login: "POST", logout: "GET", authenticateUser: "GET"]

    def userService
    def credentialMatcher

    def login(){
        def message = [response:"bad_login"]
        try {
            if(params.facebookId){
                User user = User.findByFacebookId(new Sha256Hash(params.facebookId as String).toHex() as String)
                if(user){
                    userService.validateToken(user)
                    render user.toObject() as JSON
                }
                else{
                    render message as JSON
                }
            }
            else if(params.passwordHash){
                User user = User.findByUsername(params.username as String)
                if(user){
                    UsernamePasswordToken authToken = new UsernamePasswordToken(user.username as String, params.passwordHash as String)
                    SimpleAccount account = new SimpleAccount(user.username as String, user.passwordHash as String, "API")
                    if (credentialMatcher.doCredentialsMatch(authToken, account)){
                        userService.validateToken(user)
                        render user.toObject() as JSON
                    }
                    else{
                        render message as JSON
                    }
                }
                else{
                    render message as JSON
                }
            }
            else{
                render message as JSON
            }
        }
        catch (e){
            render message as JSON
        }
    }

    def logout() {
        def message = [response:""]
        try {
            User user = User.findByToken(UserToken.findByToken(params.token as String))
            user.token.setValid(false)
            user.token.save(flush: true)
            message.response = "logout_successfully"
            render message as JSON
        }
        catch (e){
            message.response = "logout_error"
            render message as JSON
        }
    }

    def authenticateUser(){
        def message = [response:""]
        User user = User.findByFacebookId(new Sha256Hash(params.facebookId as String).toHex() as String)
        if(!user){
            message.response = "user_not_found"
            render message as JSON
        }
    }
}