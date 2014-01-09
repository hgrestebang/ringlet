package com.ps.ringlet

class Purchase {

    String itemName
    String transaction
    String amount
    Date purchaseDate
    Date expirationDate
    User owner

    static constraints = {
        itemName nullable: false
        transaction nullable: false
        purchaseDate nullable: false
        amount nullable:true
        expirationDate nullable: true
        owner nullable: false
    }

    def beforeInsert(){
    }

    def beforeUpdate(){
    }

    def toObject(){
        return [id: this.id,
                itemName: this.itemName,
                transaction: this.transaction,
                purchaseDate: this.purchaseDate,
                amount: this.amount,
                owner: this.owner.showInformation(),
                expirationDate: this.expirationDate ]
    }

    def showInformation(){
        return [id: this.id,
                itemName: this.itemName,
                transaction: this.transaction,
                purchaseDate: this.purchaseDate,
                amount: this.amount,
                expirationDate: this.expirationDate ]
    }
}