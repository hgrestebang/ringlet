package com.ps.ringlet

import grails.converters.JSON

class PurchaseController {

    static allowedMethods = [makePurchase: "POST"]

    def makePurchase(){
        def message = [response:""]
        User user = User.findByToken(UserToken.findByToken(params.token as String))
        Purchase purchase
        Date date = new Date()
        if(params.purchase.itemId == "com.ps.mconn.ringlet.prouser"){
            purchase = Purchase.findByOwnerAndItemName(user, params.purchase.itemId as String)
            if(purchase){
                purchase.setAmount(params.purchase.amount as String)
            }
            else {
                purchase = new Purchase(transaction: params.purchase.transaction as String, itemName: params.purchase.itemId as String, amount: params.purchase.amount as String, owner: user)
            }
            purchase.setPurchaseDate(new Date().parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",date.format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")))
            date.setMonth( date.getMonth()+1)
            purchase.setExpirationDate(new Date().parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",date.format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")))
            message.response = "user_pro"
        }
        else {
            purchase = new Purchase(transaction: params.purchase.transaction as String, itemName: params.purchase.itemId as String, amount: params.purchase.amount as String, owner: user)
            purchase.setPurchaseDate(new Date().parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",date.format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")))
            if(params.purchase.itemId == "com.ps.mconn.ringlet.coins5"){
                user.setCoins(user.coins + 50)
            }
            else if(params.purchase.itemId == "com.ps.mconn.ringlet.coins20"){
                user.setCoins(user.coins + 20)
            }
            message.response = "coins_added"
        }
        purchase.save(flush: true)
        user.save(flush: true)
        render message as JSON
    }
}