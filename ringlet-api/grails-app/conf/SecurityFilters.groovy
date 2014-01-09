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
                    UserToken token = UserToken.findByToken(params.token as String)
                    if(token){
                        if(token.valid){
                            return true
                        }
                    }
                    def message = [response:"bad_token"]
                    render message as JSON
                    return false
                }
            }
            after = { Map model ->}
        }
    }
}