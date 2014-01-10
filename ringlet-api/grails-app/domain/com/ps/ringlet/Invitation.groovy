package com.ps.ringlet

class Invitation {

    String message
    Date dateCreated
    MessageStatus ownerStatus
    MessageStatus recipientStatus
    User owner
    User recipient

    static constraints = {
        message nullable: true
        dateCreated nullable: true
        ownerStatus nullable: true
        recipientStatus nullable: true
        owner nullable: true
        recipient nullable: true
    }

    def beforeInsert(){
        ownerStatus = MessageStatus.SEEN
        recipientStatus = MessageStatus.UNSEEN
    }

    def toObject(){
        return [id: this.id,
                message: this.message,
                dateCreated: this.dateCreated,
                ownerStatus: this.ownerStatus?.toString(),
                recipientStatus: this.recipientStatus?.toString(),
                owner: this.owner?.showInformation(),
                recipient: this.recipient?.showInformation()]
    }

    def showInformation(){
        return [id: this.id,
                message: this.message,
                dateCreated: this.dateCreated,
                ownerStatus: this.ownerStatus?.toString(),
                recipientStatus: this.recipientStatus?.toString(),
                owner: this.owner?.id,
                recipient: this.recipient?.id,
                ownerName: this.owner?.name]
    }
}