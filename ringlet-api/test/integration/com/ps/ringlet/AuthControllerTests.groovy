package com.ps.ringlet

import static org.junit.Assert.*
import org.junit.*

class AuthControllerIntegrationTests {
    def contAuth = new AuthController()

    @Before
    void setUp() {
        // Setup logic here
    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void testLogin(){
        contAuth.params.passwordHash = 'admin'
        contAuth.params.username = 'admin@ringlet.me'
        contAuth.login()
        assert contAuth.response.json.response == 'bad_login'
        println('TEST........................')
    }

    @Test
    void testLogout(){
        contAuth.params.token = 'admin'
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
