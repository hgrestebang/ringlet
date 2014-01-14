package com.ps.ringlet

import static org.junit.Assert.*
import org.junit.*

class UserControllerIntegrationTests {
    def contUser

    @Before
    void setUp() {
        // Setup logic here
        contUser = new UserController()
    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void testSearch(){
        contUser.params.token = 'admin'
        contUser.params.name = 'Administrator'
        contUser.params.username = 'admin@ringlet.me'
        contUser.params.phone = '123456789'
        assert contUser.response.getJson().size() > 0
        //assert contUser.response.json.response == 'not_found'
    }

    @Test
    void testSearchFail(){
        contUser.params.token = 'admin'
        contUser.params.name = 'Administrator'
        contUser.params.username = 'admin@ringlet.me'
        contUser.params.phone = '123456789'
        //assert contUser.response.getJson().size() > 0
        assert contUser.response.json.response == 'not_found'
    }

    @Test
    void testNearBy(){
        contUser.params.token = 'admin'

    }
}
