package com.ps.ringlet

import grails.converters.JSON

class RingletController {

    static allowedMethods = [getByUser: "GET", create: "POST", update: "PUT", addUser: "PUT", removeUser: "PUT", delete: "DELETE"]

    def getByUser(){
        def ringlets = []
        User.findByToken(UserToken.findByToken(params.token as String)).ringlets.each {
            ringlets.add(Ringlet.findById(it).toObject())
        }
        render ringlets as JSON
    }

    def create() {
        def message = [response:""]
        User owner = User.findByToken(UserToken.findByToken(params.token as String))
        if(Ringlet.findByOwnerAndName(owner, params.name as String)){
            message.response = "ringlet_name_used"
        }
        else{
            if(params.name){
                Ringlet ringlet = new Ringlet(name: params.name as String)
                ringlet.setOwner(owner)
                ringlet.save(flush: true)
                owner.addToRinglets(ringlet.id)
                owner.save(flush: true)
                message.response = "ringlet_created"
            }
        }
        render message as JSON
    }

    def update() {
        def message = [response:""]
        if(params.ringlet){
            User owner = User.findByToken(UserToken.findByToken(params.token as String))
            Ringlet ringlet = Ringlet.findByOwnerAndId(owner, params.ringlet.id as Long)
            Ringlet validateRinglet = Ringlet.findByOwnerAndName(owner, params.ringlet.name as String)
            if(validateRinglet && validateRinglet != ringlet){
                message.response = "ringlet_name_used"
            }
            else{
                ringlet.setName(params.ringlet.name as String)
                ringlet.save(flush: true)
                message.response = "ringlet_updated"
            }
        }
        render message as JSON
    }

    def addUser(){
        def message = [response:""]
        User owner = User.findByToken(UserToken.findByToken(params.token as String))
        Ringlet ringlet = Ringlet.findByOwnerAndId(owner, params.ringletId as Long)
        if(!ringlet.users.contains(params.userId as Long)){
            ringlet.addToUsers(params.userId as Long)
            message.response = "user_added"
        }
        render message as JSON
    }

    def removeUser(){
        def message = [response:""]
        User owner = User.findByToken(UserToken.findByToken(params.token as String))
        Ringlet ringlet = Ringlet.findByOwnerAndId(owner, params.ringletId as Long)
        if(ringlet.users.contains(params.userId as Long)){
            ringlet.users.remove(params.userId as Long)
            message.response = "user_removed"
        }
        render message as JSON
    }

    def delete() {
        def message = [response:""]
        User owner = User.findByToken(UserToken.findByToken(params.token as String))
        Ringlet ringlet = Ringlet.findByOwnerAndId(owner, params.id as Long)
        if(ringlet){
            ringlet.delete(flush: true)
            message.response = "ringlet_deleted"
        }
        else{
            message.response = "ringlet_not_deleted"
        }
        render message as JSON
    }
}