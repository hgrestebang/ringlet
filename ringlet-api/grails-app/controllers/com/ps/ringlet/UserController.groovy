package com.ps.ringlet

import org.apache.commons.lang.RandomStringUtils
import org.apache.shiro.crypto.hash.Sha256Hash
import grails.converters.JSON

class UserController {

    static allowedMethods = [search: "GET", getAll: "GET", nearBy: "GET", getFriends: "GET", getCurrent: "GET", getByUsername: "GET", getById: "GET",create: "POST", updateDeviceToken:"PUT",update: "PUT", changePassword: "PUT", forgotPassword: "PUT", addBlockUser: "PUT", removeBlockUser: "PUT", deleteAccount: "PUT"]

    def rackSpaceService
    def userService

    def search(){
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        def criteria = User.createCriteria()
        def users = []
        if(userService.validatePro(user)){
            criteria.list(){
                and{
                    if(params.name != "")ilike('name', "%${params.name}%")
                    if(params.username != "")ilike('username', "%${params.username}%")
                    if(params.phone != "")ilike('phone', "%${params.phone}%")
                }
            }.each {
                if(it.id != user.id) users.add(it.showInformation(user.location[0],user.location[1]))
            }
        }
        else{
            criteria.list(max: 10){
                and{
                    if(params.name != "")ilike('name', "%${params.name}%")
                    if(params.username != "")ilike('username', "%${params.username}%")
                    if(params.phone != "")ilike('phone', "%${params.phone}%")
                }
            }.each {
                if(it.id != user.id) users.add(it.showInformation(user.location[0],user.location[1]))
            }
        }
        if(users.size() > 0){
            render users as JSON
        }
        else{
            users.add([response:"not_found"])
            render users as JSON
        }
    }

    def getAll(){
        def users = []
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        if(userService.validatePro(user)){
            User.findAllByIdNotEqualAndShowOnMapAndStatusNotEqual(user.id, true, UserStatus.REMOVED).each {
                if(!it.usersBlocked?.contains(user.id)){
                    users.add(it.showInformation(user.location[0],user.location[1]))
                }
            }
        }
        else{
            def count = 0
            User.findAllByIdNotEqualAndShowOnMapAndStatusNotEqual(user.id, true, UserStatus.REMOVED).each {
                if(count < 10){
                    if(!it.usersBlocked?.contains(user.id)){
                        users.add(it.showInformation(user.location[0],user.location[1]))
                        count++
                    }
                }
            }
        }
        if(users.size() > 0){
            users.sort{it.id
                it.distanceFromPoint}
            render users as JSON
        }
        else{
            def message = [response:"bad_request"]
            render message as JSON
        }
    }

    def nearBy(){
        def users = []
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        if(userService.validatePro(user)){
            User.findAllByLocationWithinCircle([user.location, (30 as double) / 69]).each {
                if(user.id != it.id && it.showOnMap && !it.usersBlocked?.contains(user.id) && it.status != UserStatus.REMOVED){
                    users.add(it.showInformation(user.location[0],user.location[1]))
                }
            }
        }
        else{
            def count = 0
            User.findAllByLocationWithinCircle([user.location, (30 as double) / 69]).each {
                if(count < 10){
                    if(user.id != it.id && it.showOnMap && !it.usersBlocked?.contains(user.id) && it.status != UserStatus.REMOVED){
                        users.add(it.showInformation(user.location[0],user.location[1]))
                    }
                }
            }
        }
        if(users.size() > 0){
            users.sort{it.id
                it.distanceFromPoint}
            render users as JSON
        }
        else{
           getAll()
        }
    }

    def getFriends(){
        def users = []
        User.findByToken(UserToken.findByToken(params.token as String)).friends.each {
            users.add(User.findById(it).showInformation())
        }
        render users as JSON
    }

    def getCurrent(){
        render User.findByToken(UserToken.findByToken(params.token as String)).toObject() as JSON
    }

    def getByUsername(){
        render User.findByUsername(params.username as String).showInformation() as JSON
    }

    def getById(){
        render User.findById(params.id as Long).showInformation() as JSON
    }

    def create(){
        def message = [response:""]
        if(User.findByUsername(params.user.email)){
            message.response = "email_used"
        }
        else{
            if(params.user){
                User user = new User(params.user)
                user.setUsername(params.user.email as String)
                user.setPasswordHash(new Sha256Hash(params.user.password as String).toHex())
                if(params.user.facebookId){
                    user.setFacebookId(new Sha256Hash(params.user.facebookId as String).toHex() as String)
                }
                user.setLocation([37.33234141 as Double, -122.031286 as Double])
                if(params.user.userLocation){
                    user.setLocation([params.user.userLocation.lat as Double, params.user.userLocation.lgn as Double])
                }
                else{
                    user.setLocation([37.33234141 as Double, -122.031286 as Double])
                }
                user.save(flush: true)
                if(params.images){
                    params.images.each{
                        if(it.data != ""){
                            Picture image = new Picture().save(flush: true)
                            rackSpaceService.storeImage(it.data.toString(), image.id)
                            image.path = RackSpace.findById(1).cdnUri+"/"+image.id+".jpeg"
                            image.save(flush: true)
                            user.addToPhotos(image.id)
                        }
                    }
                }
                user.save(flush: true)
                message.response = "user_created"
            }
        }
        render message as JSON
    }

    def update(){
        def message = [response:""]
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        if(user){
            if(user.username != params.user.username && User.findByUsername(params.user.username as String)){
                message.response = "email_used"
            }
            else{
                user.properties = params.user
                if(params.user.facebookId){
                    user.setFacebookId(new Sha256Hash(params.user.facebookId as String).toHex() as String)
                }
                if(params.user.userLocation){
                    user.setLocation([params.user.userLocation.lat as Double, params.user.userLocation.lgn as Double])
                }
                if(params.images){
                    params.images.each{
                        if(it.id != ""){
                            Picture image = Picture.findById(it.id as Long)
                            if(image){
                                if(it.delete == true){
                                    rackSpaceService.deleteImage(image.id)
                                    if(user.photos.contains(image.id)){
                                        user.photos.remove(image.id)
                                    }
                                    image.delete(flush: true)
                                }
                                else if(it.data != ""){
                                    rackSpaceService.storeImage(it.data.toString(), image.id)
                                    if(!user.photos.contains(image.id)){
                                        user.addToPhotos(image.id)
                                    }
                                }
                            }
                        }
                        else if(it.data != ""){
                            Picture image = new Picture().save(flush: true)
                            rackSpaceService.storeImage(it.data.toString(), image.id)
                            image.path = RackSpace.findById(1).cdnUri+"/"+image.id+".jpeg"
                            image.save(flush: true)
                            user.addToPhotos(image.id)
                        }
                    }
                }
                user.save(flush: true)
                message.response = "user_updated"
            }
        }
        render message as JSON
    }

    def updateDeviceToken(){
        def message = [response:""]
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        if(user){
            user.deviceToken=params.devicetoken;
            user.save(flush: true)
            message.response = "user_updated"
        }
        render "ok"
    }

    def changePassword(){
        def message = [response:""]
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        if(user.passwordHash == new Sha256Hash(params.currentPassword).toHex()){
            user.setPasswordHash(new Sha256Hash(params.newPassword).toHex())
            user.save(flush: true)
            message.response = "user_updated"
        }
        else{
            message.response = "password_incorrect"
        }
        render message as JSON
    }

    def forgotPassword(){
        def message = [response:""]
        User user = User.findByUsername(params.username as String)
        if(user){
            String newPassword = RandomStringUtils.random(8, true, true)
            try {
                sendMail {
                    to user.username
                    subject "New password Ringlet"
                    body "Hello "+user.name+", this is your temporary password for Ringlet, please change the password as you enter the application by one of your choice. Username: "+user.username+", Password: "+newPassword
                }
                user.setPasswordHash(new Sha256Hash(newPassword).toHex())
                user.save(flush: true)
                message.response = "email_send"
            }catch(e) {
                message.response = "email_not_send"
            }
        }
        else{
            message.response = "user_not_found"
        }
        render message as JSON
    }

    def addBlockUser(){
        def message = [response:""]
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        if(user){
            if(user.friends.contains(params.id as Long)){
                user.friends.remove(params.id as Long)
            }
            if(!user.usersBlocked?.contains(params.id as Long)){
                user.addToUsersBlocked(params.id as Long)
                user.save(flush: true)
                message.response = "user_blocked"
            }
        }
        render message as JSON
    }

    def removeBlockUser(){
        def message = [response:""]
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        if(user){
            if(user.usersBlocked?.contains(params.id as Long)){
                user.usersBlocked.remove(params.id as Long)
                user.save(flush: true)
                message.response = "user_unblocked"
            }
        }
        render message as JSON
    }

    def deleteAccount(){
        def message = [response:""]
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        user.friends.each {
            User friend = User.findById(it)
            friend.friends.remove(user.id)
            friend.ringlets.each {
                Ringlet friendRinglet = Ringlet.findById(it)
                if(friendRinglet.users.contains(user.id)){
                    friendRinglet.removeFromUsers(user.id)
                    friendRinglet.save(flush: true)
                }
            }
            friend.save(flush: true)
        }
        User.list().each {
            if(it.usersBlocked?.contains(user.id)){
                it.usersBlocked.remove(user.id)
                it.save(flush: true)
            }
        }
        user.ringlets.each {
            Ringlet ringlet = Ringlet.findById(it)
            ringlet.delete(flush: true)
        }
        user.photos.each {
            rackSpaceService.deleteImage(it)
            Picture picture = Picture.findById(it)
            picture.delete(flush: true)
        }
        user.friends.clear()
        user.usersBlocked.clear()
        user.ringlets.clear()
        user.photos.clear()
        user.setFacebookId("")
        user.setStatus(UserStatus.REMOVED)
        user.save(flush: true)
        message.response = "user_deleted"
        render message as JSON
    }
}