package com.ps.ringlet

class User {

    String username
    String passwordHash
    String facebookId
    String name
    String phone
    String bio
    int distanceFromPoint
    Boolean showOnMap
    Boolean sound
    Boolean connectionStatus
    List location
    UserToken token
    UserGender gender
    UserStatus status

    static hasMany = [ringlets:Long, friends:Long, usersBlocked:Long, photos:Long, proPurchase: Long]

    static transients = ["distanceFromPoint"]

    static mapping = {
        location geoIndex:true
    }

    static constraints = {
        username nullable: false, blank: false, unique: true
        passwordHash nullable: false, blank: false
        facebookId nullable: true
        name nullable: false, blank: false
        phone nullable: true
        bio nullable: true
        distanceFromPoint nullable: true
        showOnMap nullable: true
        sound nullable: true
        connectionStatus nullable: true
        location nullable: true
        token nullable: true
        gender nullable: true
        status nullable: true
        proPurchase nullable: true
        ringlets nullable: true
        friends nullable: true
        usersBlocked nullable: true
        photos nullable: true
    }

    def beforeInsert(){
        showOnMap = true
        sound = true
        connectionStatus = false
        status = UserStatus.ACTIVE
    }

    def toObject(){
        def photos = []
        this.photos.each {
            photos.add(Picture.findById(it).toObject())
        }
        Date actual = new Date()
        Purchase purchase = Purchase.findById(this.proPurchase?.last() as Long)
        return [id: this.id,
                username: this.username,
                facebookId: this.facebookId,
                name: this.name,
                phone: this.phone,
                bio: this.bio,
                distanceFromPoint: this.distanceFromPoint,
                showOnMap: this.showOnMap,
                sound: this.sound,
                connectionStatus: this.connectionStatus,
                location: this.location?[lat:this.location[0], lgn:this.location[1]]:null,
                token: this.token?.toObject(),
                gender:this.gender?.toString(),
                status:this.status?.toString(),
                isPro: purchase?actual.before(purchase?.expirationDate):false,
                ringlets: this.ringlets,
                friends: this.friends,
                usersBlocked: this.usersBlocked,
                photos: photos]
    }

    def showInformation(){
        def photos = []
        this.photos.each {
            photos.add(Picture.findById(it).toObject())
        }
        return [id: this.id,
                username: this.username,
                name: this.name,
                phone: this.phone,
                bio: this.bio,
                distanceFromPoint: this.distanceFromPoint,
                showOnMap: this.showOnMap,
                connectionStatus: this.connectionStatus,
                location: this.location?[lat:this.location[0], lgn:this.location[1]]:null,
                gender:this.gender?.toString(),
                status:this.status?.toString(),
                photos: photos]
    }

    def showInformation(def latitude, def longitude){
        double theDistance = (Math.sin(Math.toRadians(this.location.get(0))) *
                Math.sin(Math.toRadians(latitude)) +
                Math.cos(Math.toRadians(this.location.get(0))) *
                Math.cos(Math.toRadians(latitude)) *
                Math.cos(Math.toRadians(this.location.get(1) - longitude)))
        this.distanceFromPoint = new Double((Math.toDegrees(Math.acos(theDistance))) * 69.09).intValue();
        return this.showInformation();
    }
}