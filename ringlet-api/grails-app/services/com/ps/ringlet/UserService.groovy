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
        Date actual = new Date()
        if(user.proPurchase){
                Purchase purchase = Purchase.findById(user.proPurchase.last() as Long)
            if(actual.before(purchase.expirationDate))
            {
                return true
            }
        }
        return false
    }
}