package com.ps.ringlet

import static org.junit.Assert.*
import org.junit.*

class AuthControllerTests {
    def contAuth

    @Before
    void setUp() {
        // Setup logic here
        contAuth = new AuthController()
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
        contAuth.params.token = User.findByUsername('user8@ringlet.me').token?.token
        contAuth.logout()
        assert contAuth.response.json.response == 'logout_successfully'
        assert User.findByToken(UserToken.findByToken(User.findByUsername('user8@ringlet.me').token?.token)).token.valid == false
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
