import com.ps.ringlet.User
import com.ps.ringlet.UserToken
import grails.converters.JSON

class SecurityFilters {

    def filters = {
        all(uri: "/**") {
            before = {
                if(controllerName.equals('auth') || ((controllerName.equals('user') && (actionName.equals('create') || actionName.equals('forgotPassword'))))){
                    return true
                }
                else{
                    User user = User.findByToken(UserToken.findByToken(params.token as String))
                    if(user.token.valid){
                        return true
                    }else{
                        def message = [response:"bad_token"]
                        render message as JSON
                        return false
                    }
                }
            }
            after = { Map model ->}
        }
    }
}