package com.ps.ringlet

class UserToken {

    String token
    boolean valid
    Date lastUpdated

    static constraints = {
        token nullable: false, blank: false
        valid nullable: true
        lastUpdated nullable: true
    }

    def beforeInsert(){
        lastUpdated = new Date()
    }

    def beforeUpdate(){
        lastUpdated = new Date()
    }

    def toObject(){
        return [id: this.id,
                token: this.token,
                lastUpdated: this.lastUpdated,
                valid: this.valid]
    }
}