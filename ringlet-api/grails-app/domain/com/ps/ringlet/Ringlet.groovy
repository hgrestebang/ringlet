package com.ps.ringlet

class Ringlet {

    String name
    User owner

    static hasMany = [users:Long]

    static belongsTo = User

    static constraints = {
        name nullable: false
        owner nullable: false
        users nullable: true
    }

    def toObject(){
        return [id: this.id,
                name: this.name,
                users: this.users]
    }

    def showInformation(){
        return [id: this.id,
                name: this.name,
                owner: this.owner.showInformation()]
    }
}