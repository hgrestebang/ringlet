package com.ps.ringlet

import org.apache.shiro.crypto.hash.Sha256Hash

import static org.junit.Assert.*
import org.junit.*

class UserControllerIntegrationTests {
    def contUser,token

    @Before
    void setUp() {
        //Setup logic here
        contUser = new UserController()
        token = User.findByUsername('admin@ringlet.me').token?.token
    }

    @After
    void tearDown() {
        // Tear down logic here
        User admin = User.findByUsername('admin123@ringlet.me')
        admin?.setUsername('admin@ringlet.me')
        admin?.setPasswordHash(new Sha256Hash('admin').toHex())
        admin?.save(flush: true)
    }

    @Test
    void testSearch(){
        contUser.params.token = token
        contUser.params.username = 'ringlet'
//        contUser.params.name = 'Administrator'
        contUser.search()
        assert contUser.response.text != '[{"response":"not_found"}]'
        assert contUser.response.json.id?.size() > 0
        assert contUser.response.json.id?.contains(1)
        //assert contUser.response.json.response == 'not_found'
    }

    @Test
    void testSearchFail(){
        contUser.params.token = token
        //contUser.params.name = 'Administrator'
        contUser.params.username = 'ringlett'
        //assert contUser.response.getJson().size() > 0
        assert contUser.response.text != '[{"response":"not_found"}]'
    }

    @Test
    void testGetAll(){
        contUser.params.token = token
        contUser.getAll()
        assert contUser.response.text != '{"response":"bad_request"}'
        assert contUser.response.getJson().size() > 0
        assert contUser.response.json.id.size() > 0
        assert contUser.response.json.id[0] == 2
        assertEquals(contUser.response.json.username[0],'user1@ringlet.me')
    }

    @Test
    void testNearBy(){
        contUser.params.token = token
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
    void testGetFriendsFail(){
        contUser.params.token = User.findByUsername('user5@ringlet.me')
        contUser.getFriends()
        assert contUser.response.json.id.size() == 0
    }

    @Test
    void testGetCurrent(){
        contUser.params.token = token
        contUser.getCurrent()
        assertNotNull(contUser.response)
        assert contUser.response.json.token.token == token
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
        def userT = [email: "test@ringlet.me", password:'admin', name:'Create Test',]
        contUser.params.user = userT
        contUser.create()
        assert contUser.response.text == '{"response":"user_created"}'
        assert User.findByUsername('test@ringlet.me')

    }

    @Test
    void testCreateFail(){
        def userT = [email: "test@ringlet.me", password:'admin']
        contUser.params.user = userT
        contUser.create()
        assert contUser.response.text == '{"response":"email_used"}'
    }

    @Test
    void testUpdateFail(){
        contUser.params.token = User.findByUsername('user4@ringlet.me').token?.token
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
        assert User.findByToken(UserToken.findByToken(token)).usersBlocked?.contains(8L)
    }

    @Test
    void testRemoveBlockUser(){
        contUser.params.token = token
        contUser.params.id = 8
        contUser.removeBlockUser()
        assert contUser.response.text == '{"response":"user_unblocked"}'
        assert User.findByToken(UserToken.findByToken(token)).usersBlocked?.contains(8L) == false
    }

    @Test
    void testDeleteAccount(){
        contUser.params.token = User.findByUsername('test@ringlet.me') // Token of new user
        contUser.deleteAccount()
        assert contUser.response.text == '{"response":"user_deleted"}'
        assert User.findByUsername('test@ringlet.me').status == UserStatus.REMOVED

        User createU = User.findByUsername('test@ringlet.me')
        createU?.delete()
        createU?.save(flush: true)
    }


}
