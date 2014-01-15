package com.ps.ringlet

import static org.junit.Assert.*
import org.junit.*

class AuthControllerTests {
    def contAuth,token

    @Before
    void setUp() {
        // Setup logic here
        contAuth = new AuthController()
        token = '3d231daf-1d95-4991-a2df-b390e1deb75e'
    }

    /*@After
    void tearDown() {
        // Tear down logic here
    }*/

    @Test
    void testLogin(){
        contAuth.params.passwordHash = 'admin'
        contAuth.params.username = 'admin@ringlet.me'
        contAuth.login()
        assert contAuth.response.json.username == 'admin@ringlet.me'
        assert contAuth.response.json.id == 1
    }

    @Test
    void testLogout(){
        contAuth.params.token = '7526bf2d-5b70-4f73-92dd-33748f32bf77'
        contAuth.logout()
        assert contAuth.response.json.response == 'logout_successfully'
        assert User.findByToken(UserToken.findByToken('7526bf2d-5b70-4f73-92dd-33748f32bf77')).token.valid == false
    }

    @Test
    void testLogoutFail(){
        contAuth.params.token = 'admin'
        contAuth.logout()
        assert contAuth.response.json.response == 'logout_error'
    }

    @Test
    void testAuthenticateUserFail(){
        /*contAuth.params.facebookId = 'admin'
        contAuth.authenticateUser()
        assert contAuth.response.json.response == 'user_not_found'*/
    }

}
