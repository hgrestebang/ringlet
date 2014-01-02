package com.ps.ringlet

class UserService {

    static transactional = false

    def validateToken(User user){
        Date valid = new Date()
        valid + 1
        if(!user.token){
            user.setToken(new UserToken( token: UUID.randomUUID().toString(), valid: true).save(flush: true))
            user.save(flush: true)
        }else if(user.token.lastUpdated.time > valid.time || !user.token.valid){
            user.token.token = UUID.randomUUID()
            user.token.valid = true
            user.save(flush: true)
        }
    }

    def validatePro(User user){
        if(user.proPurchase){
            Date date = new Date()
            if(user.proPurchase.expirationDate >= new Date().parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",date.format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))){
                return true
            }
        }
        return false
    }
}