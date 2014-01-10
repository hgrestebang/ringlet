package com.ps.ringlet

class Announcement {

    String message
    String groupCode
    Date dateCreated
    MessageStatus ownerStatus
    MessageStatus recipientStatus
    User owner
    User recipient
    List location
    int radius

    static constraints = {
        message nullable: true
        groupCode nullable: true
        dateCreated nullable: true
        ownerStatus nullable: true
        recipientStatus nullable: true
        owner nullable: true
        recipient nullable: true
        location nullable: true
        radius nullable: true
    }

    def beforeInsert(){
        ownerStatus = MessageStatus.SEEN
        recipientStatus = MessageStatus.UNSEEN
    }

    def toObject(){
        return [id: this.id,
                message: this.message,
                groupCode: this.groupCode,
                dateCreated: this.dateCreated,
                ownerStatus: this.ownerStatus?.toString(),
                recipientStatus: this.recipientStatus?.toString(),
                owner: this.owner?.showInformation(),
                recipient: this.recipient?.showInformation(),
                location: this.location,
                radius: this.radius]
    }

    def showInformation(){
        return [id: this.id,
                message: this.message,
                groupCode: this.groupCode,
                dateCreated: this.dateCreated,
                ownerStatus: this.ownerStatus?.toString(),
                recipientStatus: this.recipientStatus?.toString(),
                owner: this.owner?.showInformation(),
                recipient: this.recipient?.id,
                location: this.location,
                radius: this.radius]
    }
}