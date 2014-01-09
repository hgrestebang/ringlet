package com.ps.ringlet

import grails.converters.JSON

class PurchaseController {

    static allowedMethods = [makePurchase: "POST"]

    def makePurchase(){
        def message = [response:""]
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        Purchase purchase = new Purchase(owner: user,purchaseDate:new Date(), transaction: params.transaction, amount: params.amount, itemName: params.itemId,expirationDate: calculateExpiration() )
        purchase.save(flush: true)
        user.addToProPurchase(purchase.id)
        user.save()
        message.response = "user_updated"
        render message as JSON
    }
    def calculateExpiration(){
        def actual = new Date()
        def expiration = new Date()
            expiration.setMonth(actual.getMonth()+1)
        return expiration
    }
}