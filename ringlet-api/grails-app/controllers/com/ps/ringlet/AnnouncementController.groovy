package com.ps.ringlet

import grails.converters.JSON
import org.apache.commons.lang.RandomStringUtils


class AnnouncementController {

    static allowedMethods = [getByUser: "GET", create: "POST", delete: "PUT"]

    def getByUser(){
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        def announcements = []
        def codes = []
        Announcement.findAllByOwnerAndOwnerStatus(user, MessageStatus.SEEN).each {
            if(!codes.contains(it.groupCode)){
                announcements.add(it.showInformation())
                codes.add(it.groupCode)
            }
        }
        Announcement.findAllByRecipientAndRecipientStatusNotEqual(user, MessageStatus.DELETED).each {
            announcements.add(it.showInformation())
        }
        render announcements as JSON
    }

    def create() {
        def message = [response:""]
        def totalSend=0
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        String code = RandomStringUtils.random(12, true, true)
        User.findAllByLocationWithinCircle([[user.location[0] as Double, user.location[1] as Double], (params.announcement.radius.miles as Double) / 69]).each {
            if(user.id != it.id && !it.usersBlocked?.contains(user.id) && !user.usersBlocked?.contains(it.id) && it.status != UserStatus.REMOVED){
                new Announcement(message: params.announcement.body as String, groupCode: code, dateCreated: new Date(), owner: user, recipient: it, location: [user.location[0] as Double, user.location[1] as Double], radius: params.announcement.radius.miles as Double).save(flush: true)
                totalSend++;
            }
        }
        message.response = "announcement_created"
        message.totalSend=totalSend.toString()
        render message as JSON
    }

    def delete() {
        def message = [response:""]
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        Announcement announcement = Announcement.findById(params.id as Long)
        if(announcement.owner == user){
            Announcement.findAllByGroupCode(announcement.groupCode).each {
                it.setOwnerStatus(MessageStatus.DELETED)
                it.save(flush: true)
            }
        }
        else if(announcement.recipient == user){
            announcement.setRecipientStatus(MessageStatus.DELETED)
            announcement.save(flush: true)
        }
        message.response = "announcement_deleted"
        render message as JSON
    }
}
