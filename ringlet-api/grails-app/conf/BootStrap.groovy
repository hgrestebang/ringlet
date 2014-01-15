import com.ps.ringlet.Picture
import com.ps.ringlet.Purchase
import com.ps.ringlet.User
import com.ps.ringlet.RackSpace
import com.ps.ringlet.UserGender
import org.apache.shiro.crypto.hash.Sha256Hash

class BootStrap {

    def rackSpaceService, userService

    def init = { servletContext ->
        if(User.count()==0){
            for (i in 1..21){
                new Picture(path: "http://4e93bdb8654bd6162582-bb35557e61ab782de4fbdbabd809c93f.r87.cf1.rackcdn.com/"+i+".jpeg").save(flush: true)
            }
            new User(username: "admin@ringlet.me", passwordHash: new Sha256Hash("admin").toHex(), name: "Administrator", phone: "88888888", bio: "I am the Administrator", location: [37.33233141d, -122.031286d], friends: [2,3,4], usersBlocked: [5,6], gender: UserGender.MALE,photos: [1l,2l,11l,12l,21l]).save(flush: true)
            for (i in 1..8){
               new User(username: "user"+i+"@ringlet.me", passwordHash: new Sha256Hash("user"+i).toHex(), name: "User"+i, phone: "888888"+i, bio: "I am the user number "+i, location: [37.33233141d+(i/10), -122.031286d], gender: UserGender.MALE,photos: [i+2l,i+12l]).save(flush: true)
            }
        }
        if(Purchase.count()==0){
            def actual = new Date()
            def expiration = new Date()
            expiration.setYear(actual.getYear()+10)
            User user = User.findByUsername("admin@ringlet.me")
            Purchase purchase= new Purchase(owner: user,purchaseDate:new Date(), transaction: "AdminTransaction", amount: "0", itemName: "com.ps.mconn.ringlet.prouser",expirationDate: expiration )
            purchase.save(flush: true)
            user.addToProPurchase(purchase.id)
            user.save()
        }
        if (RackSpace.count() == 0){
            new RackSpace(authUser:"gallyboatn", authKey:"c5ca5b78ae594db7a943f5dd0cb5001f", host:"https://identity.api.rackspacecloud.com/v1.0", container:"ringlet").save(flush: true)
            rackSpaceService.authentication()
            rackSpaceService.setContainerURL()
        }
    }
}