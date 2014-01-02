package com.ps.ringlet

import grails.converters.JSON

class InvitationController {

    static allowedMethods = [getByUser: "GET", create: "POST", acceptInvitation: "PUT", declineInvitation: "PUT", delete: "PUT"]

    def getByUser(){
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        def invitations = []
        Invitation.findAllByRecipientAndRecipientStatus(user, MessageStatus.UNSEEN).each {
            invitations.add(it.showInformation())
        }
        Invitation.findAllByOwnerAndOwnerStatus(user, MessageStatus.SEEN).each {
            invitations.add(it.showInformation())
        }
        render invitations as JSON
    }

    def create() {
        def message = [response:""]
        User owner = User.findByToken(UserToken.findByToken(params.token as String))
        User recipient = User.findById(params.invitation.recipientId as Long)
        if(recipient){
            new Invitation(message: params.invitation.message, dateCreated: new Date(), owner: owner, recipient: recipient).save(flush: true)
            message.response = "invitation_created"
        }
        else{
            message.response = "invitation_not_created"
        }
        render message as JSON
    }

    def acceptInvitation() {
        def message = [response:""]
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        Invitation invitation = Invitation.findById(params.id as Long)
        if(invitation.recipient == user){
            invitation.owner.addToFriends(user.id).save(flush: true)
            user.addToFriends(invitation.owner.id).save(flush: true)
            invitation.setRecipientStatus(MessageStatus.ACCEPTED)
            invitation.save(flush: true)
            message.response = "invitation_accepted"
        }
        render message as JSON
    }

    def declineInvitation() {
        def message = [response:""]
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        Invitation invitation = Invitation.findById(params.id as Long)
        if(invitation.recipient == user){
            invitation.setRecipientStatus(MessageStatus.DELETED)
            invitation.save(flush: true)
            message.response = "invitation_declined"
        }
        render message as JSON
    }

    def delete() {
        def message = [response:""]
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        Invitation invitation = Invitation.findById(params.id as Long)
        if(invitation.owner == user){
            invitation.setOwnerStatus(MessageStatus.DELETED)
            invitation.save(flush: true)
            message.response = "invitation_deleted"
        }
        render message as JSON
    }
}