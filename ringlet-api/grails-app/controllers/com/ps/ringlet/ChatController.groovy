package com.ps.ringlet

import grails.converters.JSON

class ChatController {

    static allowedMethods = [getAll: "GET", getByUser: "GET", create: "POST", update: "PUT", delete: "PUT"]

    def apnService

    def getAll(){
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        def chats = []
        Chat.findAllByRecipientAndRecipientStatusNotEqual(user, MessageStatus.DELETED).each {
            chats.add(it.showInformation())
        }

        Chat.findAllByOwnerAndOwnerStatusNotEqual(user, MessageStatus.DELETED).each {
            chats.add(it.showInformation())
        }
        chats.sort{it.dateCreated}
        render chats as JSON
    }

    def getByUser(){
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        User recipient = User.findById(params.recipientId as Long)
        def chats = []
        Chat.findAllByOwnerAndRecipientAndOwnerStatusNotEqual(user, recipient, MessageStatus.DELETED).each {
            chats.add(it.showInformation())
        }
        Chat.findAllByOwnerAndRecipientAndRecipientStatusNotEqual(recipient, user, MessageStatus.DELETED).each {
            chats.add(it.showInformation())
        }
        render chats as JSON
    }

    def create() {
        def message = [response:""]
        User owner = User.findByToken(UserToken.findByToken(params.token as String))
        User recipient = User.findById(params.recipient as Long)
        if(recipient){
            new Chat(message: params.chat, dateCreated: new Date(), owner: owner, recipient: recipient).save(flush: true)
            def messages = "You have received a new chat from: "+owner.name
            apnService.pushNotifications(recipient.id.toString() as String, messages.toString() as String)
            message.response = "chat_created"
        }
        else{
            message.response = "chat_not_created"
        }
        render message as JSON
    }

    def update() {
        def message = [response:""]
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        Chat chat = Chat.findById(params.id as Long)
        if(chat.recipient == user){
            chat.setRecipientStatus(MessageStatus.SEEN)
        }
        chat.save(flush: true)
        message.response = "chat_updated"
        render message as JSON
    }

    def delete() {
        def message = [response:""]
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        Chat chat = Chat.findById(params.id as Long)
        if(chat.owner == user){
            chat.setOwnerStatus(MessageStatus.DELETED)
        }
        else if(chat.recipient == user){
            chat.setRecipientStatus(MessageStatus.DELETED)
        }
        chat.save(flush: true)
        message.response = "chat_deleted"
        render message as JSON
    }
}