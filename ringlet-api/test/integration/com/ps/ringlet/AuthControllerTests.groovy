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
         //token=d8cf4380-395b-4cd7-8fd5-106a52f2ba89

        contAuth.params.passwordHash = 'admin'
        contAuth.params.username = 'admin@ringlet.me'
        contAuth.login()
        assert contAuth.response.json.username == 'admin@ringlet.me'
        assert contAuth.response.json.id == 1
    }

    @Test
    void testLogout(){
        contAuth.params.token = 'd8cf4380-395b-4cd7-8fd5-106a52f2ba89'
        contAuth.logout()
        assert contAuth.response.json.response == 'logout_successfully'
    }

    @Test
    void testLogoutFail(){
        contAuth.params.token = 'admin'
        contAuth.logout()
        assert contAuth.response.json.response == 'logout_error'
    }

    @Test
    void testAuthenticateUserFail(){
        contAuth.params.facebookId = 'admin'
        contAuth.authenticateUser()
        assert contAuth.response.json.response == 'user_not_found'
    }

}
