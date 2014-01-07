import com.ps.ringlet.User
import com.ps.ringlet.RackSpace
import com.ps.ringlet.UserGender
import org.apache.shiro.crypto.hash.Sha256Hash

class BootStrap {

    def rackSpaceService

    def init = { servletContext ->
        if(User.count()==0){
            new User(username: "admin@ringlet.me", passwordHash: new Sha256Hash("admin").toHex(), name: "Administrator", phone: "88888888", bio: "I am the Administrator", location: [37.33233141d, -122.031286d], gender: UserGender.MALE).save(flush: true)
            for (i in 1..15){
                new User(username: "user"+i+"@ringlet.me", passwordHash: new Sha256Hash("user"+i).toHex(), name: "User"+i, phone: "888888"+i, bio: "I am the user number "+i, location: [37.33233141d+(i/10), -122.031286d], gender: UserGender.MALE).save(flush: true)
            }
        }
        if (RackSpace.count() == 0){
            new RackSpace(authUser:"gallyboatn", authKey:"c5ca5b78ae594db7a943f5dd0cb5001f", host:"https://identity.api.rackspacecloud.com/v1.0", container:"ringlet").save(flush: true)
            rackSpaceService.authentication()
            rackSpaceService.setContainerURL()
        }
    }
}