package com.ps.ringlet

import org.apache.shiro.crypto.hash.Sha256Hash

import static org.junit.Assert.*
import org.junit.*

class UserControllerIntegrationTests {
    def contUser, token

    @Before
    void setUp() {
        // Setup logic here
        contUser = new UserController()
        token = '10dfc7e8-8cb9-45c2-9b11-ce0fed8846c4'
    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void testSearch(){
        contUser.params.token = 'd8cf4380-395b-4cd7-8fd5-106a52f2ba89'
        contUser.params.username = 'ringlet'
//        contUser.params.name = 'Administrator'
//        contUser.params.phone = '123456789'
        contUser.search()
        assert contUser.response.getJson().size() > 0
        //assert contUser.response.json.response == 'not_found'
    }

    @Test
    void testSearchFail(){
        contUser.params.token = 'd8cf4380-395b-4cd7-8fd5-106a52f2ba89'
        //contUser.params.name = 'Administrator'
        contUser.params.username = 'ringlett'
        //assert contUser.response.getJson().size() > 0
        assert contUser.response.json.response == 'not_found'
    }

    @Test
    void testGetAll(){
        contUser.params.token = 'd8cf4380-395b-4cd7-8fd5-106a52f2ba89'
        contUser.getAll()
        assert contUser.response.text != '{"response":"bad_request"}'
        assert contUser.response.getJson().size() > 0
        assert contUser.response.json.id.size() > 0
        assert contUser.response.json.id[0] == 2
        assertEquals(contUser.response.json.username[0],'user1@ringlet.me')
    }

    @Test
    void testNearBy(){
        contUser.params.token = 'd8cf4380-395b-4cd7-8fd5-106a52f2ba89'
        contUser.nearBy()
        assertNotNull(contUser.response)
        assert contUser.response.json.size() > 0
        assertEquals(contUser.response.json[0].username,'user1@ringlet.me')
    }

    @Test
    void testGetFriends(){
        contUser.params.token = token
        contUser.getFriends()
        assert contUser.response.json.id.size() > 0
        assert contUser.response.json[0].id == 2
    }

    @Test
    void testGetCurrent(){
        contUser.params.token = 'd8cf4380-395b-4cd7-8fd5-106a52f2ba89'
        contUser.getCurrent()
        assertNotNull(contUser.response)
        assert contUser.response.json.token.token == 'd8cf4380-395b-4cd7-8fd5-106a52f2ba89'
        assert contUser.response.json.id == 1
        assertEquals(contUser.response.json.username,'admin@ringlet.me')
    }

    @Test
    void testGetByUsername(){
        contUser.params.username = 'admin@ringlet.me'
        contUser.getByUsername()
        assertNotNull(contUser.response)
        assert contUser.response.json.id == 1
        assertEquals(contUser.response.json.username, 'admin@ringlet.me')
    }

    @Test
    void testGetById(){
        contUser.params.id = 1
        contUser.getById()
        assertEquals(contUser.response.json.id,1)
        assertEquals(contUser.response.json.username,'admin@ringlet.me')
    }

    @Test
    void testCreate(){
        def userT = [email: "test@ringlet.me", password:'admin']
        contUser.params.user = userT
        contUser.create()
        assert contUser.response.text == '{"response":"user_created"}'
        //assert User.findByUsername('test@ringlet.me')

    }

    @Test
    void testCreateFail(){
        def userT = [email: "admin@ringlet.me", password:'admin']
        contUser.params.user = userT
        contUser.create()
        assert contUser.response.text == '{"response":"email_used"}'
    }

    @Test
    void testUpdateFail(){
        contUser.params.token = '49258e40-9ecb-4443-b87e-8621c805aacf'//user1
        def userT = [username: "admin@ringlet.me"]
        contUser.params.user = userT
        contUser.update()
        assert contUser.response.text == '{"response":"email_used"}'
    }
    @Test
    void testUpdate(){
        contUser.params.token = token
        def userT = [username: "admin123@ringlet.me"]
        contUser.params.user = userT
        contUser.update()
        assert contUser.response.text == '{"response":"user_updated"}'
        assert User.findByUsername('admin123@ringlet.me')
    }

    @Test
    void testChangePassword(){
        contUser.params.token = token
        contUser.params.currentPassword =  'admin'
        contUser.params.newPassword = 'admin123'
        contUser.changePassword()
        assertEquals(contUser.response.text,'{"response":"user_updated"}')
        assert User.findByUsername('admin@ringlet.me').passwordHash == new Sha256Hash('admin123').toHex()
    }

    @Test
    void testChangePasswordFail(){
        contUser.params.token = token
        contUser.params.currentPassword =  'admin12345'
        contUser.params.newPassword = 'admin123'
        contUser.changePassword()
        assertEquals(contUser.response.text,'{"response":"password_incorrect"}')
    }

    @Test
    void testForgotPassword(){
        contUser.params.username = 'admin@ringlet.me'
        contUser.forgotPassword()
        assert contUser.response.text == '{"response":"email_send"}'
    }

    @Test
    void testForgotPasswordFailUser(){
        contUser.params.username = 'test1@ringlet.me'
        contUser.forgotPassword()
        assert contUser.response.text == '{"response":"user_not_found"}'
    }

    @Test
    void testAddBlockUser(){
        contUser.params.token = token
        contUser.params.id = 8
        contUser.addBlockUser()
        assert contUser.response.text == '{"response":"user_blocked"}'
        assert User.findByToken(token).usersBlocked.contains(8)
        //assert user3.usersBlocked?.contains(1)
    }


}
