package com.ps.ringlet

import org.apache.shiro.crypto.hash.Sha256Hash

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*
import groovy.mock.interceptor.*
import grails.converters.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestMixin(GrailsUnitTestMixin)
@TestFor(UserController)
@Mock([User,UserToken,UserService,Purchase,Ringlet])
class UserControllerTests {
    def user,user2,user3
    def token,purchase,ringlet

    void setUp() {
        mockForConstraintsTests(User)
        mockForConstraintsTests(UserToken)
        mockForConstraintsTests(UserService)
        mockForConstraintsTests(Purchase)
        mockForConstraintsTests(Ringlet)

        token = new UserToken(
                token: 'token',
                valid: true
        ).save()

        purchase = new Purchase(
                itemName: 'Purchase test',
                transaction: 'transaction123',
                amount: '12',
                purchaseDate: new Date(),
                expirationDate: new Date()+10,
                recurring: true,
                owner: user
        ).save()

        user = new User(
                username: 'test@ringlet.me',
                passwordHash: new Sha256Hash('admin').toHex(),
                facebookId: '12345',
                name: 'User Test',
                phone: '123456789',
                bio: 'User for test',
                distanceFromPoint: 12,
                showOnMap: true,
                connectionStatus: true,
                location: [37.33233141d, -122.031286d],
                token: token,
                gender: 'MALE',
                status: 'ACTIVE',
                proPurchase: purchase,
                ringlets:[1],
                friends:[2],
                usersBlocked:[3],
                photos:['http://test']
        ).save()

        user2 = new User(
                username: 'test2@ringlet.me',
                passwordHash: '5bbf1a9e0de062225a1b',
                facebookId: '12345',
                name: 'User Test 2',
                phone: '123456789',
                bio: 'User2 for test',
                distanceFromPoint: 12,
                showOnMap: true,
                connectionStatus: true,
                location: [37.33233141d, -122.031286d],
                token: new UserToken(
                        token: 'token1',
                        valid: true
                ).save(),
                gender: 'MALE',
                status: 'ACTIVE',
                ringlets:[1],
                friends:[1],
                usersBlocked:[],
                photos:['http://test']
        ).save()

        user3 = new User(
                username: 'test3@ringlet.me',
                passwordHash: '5bbf1a9e0de062225a1b',
                facebookId: '12345',
                name: 'User Test 3',
                phone: '123456789',
                bio: 'User3 for test',
                showOnMap: true,
                connectionStatus: true,
                location: [37.33233141d, -122.031286d],
                token: new UserToken(
                        token: 'token2',
                        valid: true
                ).save(),
                gender: 'MALE',
                status: 'ACTIVE',
                friends:[]
        ).save()

        ringlet = new Ringlet(
                name: 'Ringlet Test',
                owner: user2,
                users: [2]

        ).save()
    }

    void tearDown() {
        // Tear down logic here
    }

    void testSearch(){
    /*
        Problems with the criteria
    */
    }

    void testGetCurrent(){
        params.token = 'token'
        controller.getCurrent()
        assertNotNull(response)
        assert response.json.token.token == 'token'
        assertEquals(response.json.token.id,1)
        assertEquals(response.json.id,1)
        assert response.json.username == 'test@ringlet.me'
    }

    void testGetByUsername(){
        params.username = 'test@ringlet.me'
        controller.getByUsername()
        assertNotNull(response)
        assertEquals(response.json.id,1)
        assertEquals(response.json.username,params.username)
    }

    void testGetById(){
        params.id = 1
        controller.getById()
        assertEquals(response.json.id,params.id)
        assertEquals(response.json.username,'test@ringlet.me')
    }

    void testCreateFail(){
        def userT = [email: "test@ringlet.me"]
        params.user = userT
        controller.create()
        assert response.text == '{"response":"email_used"}'
        assertNotNull(User.findByUsername(userT.email))
    }

    void testCreate(){
        def userT = [email: "test4@ringlet.me", password:'admin', name:'Create User Test',]
        params.user = userT
        controller.create()
        assert response.text == '{"response":"user_created"}'
        assertNotNull(User.findByUsername(userT.email))
        assertEquals(User.findByUsername(userT.email)?.name,userT.name)
    }

    void testForgotPassword(){
        /*
        //--- Problems with email plugin --
        params.username = 'test@ringlet.me'
        controller.forgotPassword()
        assert response.text == '{"response":"email_send"}'*/
    }

    void testForgotPasswordFailUser(){
         params.username = 'test1@ringlet.me'
         controller.forgotPassword()
         assert response.text == '{"response":"user_not_found"}'
        assertNull(User.findByUsername(params.username))
     }

    void testForgotPasswordEmail(){
        params.username = 'test@ringlet.me'
        controller.forgotPassword()
        assert response.text == '{"response":"email_not_send"}'
    }

    void testGetAll(){
        params.token = token.token
        controller.getAll()
        assert response.text != '{"response":"bad_request"}'
        assert response.json.id.size() >= 0
        assertEquals(response.json[0]?.id,2)
        assertEquals(response.json[0]?.username,'test2@ringlet.me')
    }

    void testNearBy(){
/*      //** Problems with plugin **
        params.token = 'token'
        controller.nearBy()
        assert response.json?.id.size() >= 0*/
    }

    void testGetFriends(){
        params.token = token.token
        controller.getFriends()
        assert response.json.id.size() > 0
        assertEquals(response.json.id[0],2)
        assertEquals(response.json[0]?.username,'test2@ringlet.me')
    }

    void testUpdate(){
        params.token = token.token
        params.user = [username: "admin@ringlet.me"]
        controller.update()
        assert response.text == '{"response":"user_updated"}'
        assertEquals(user.username,"admin@ringlet.me")
    }

    void testUpdateFail(){
        params.token = 'token1'
        params.user = [username: "test@ringlet.me"]
        controller.update()
        assert response.text == '{"response":"email_used"}'
        assertNotNull(User.findByUsername(params.user.username))
    }

    void testUpdateDeviceToken(){
        params.token = token.token
        params.devicetoken = "deviceToken"
        controller.updateDeviceToken()
        assert response.text == 'ok'
        assertEquals(user.deviceToken,params.devicetoken)
    }

    void testAddBlockUser(){
        params.token = 'token2'
        params.id = 2
        controller.addBlockUser()
        assert response.text == '{"response":"user_blocked"}'
        assert user3.usersBlocked?.contains(params.id as long)
    }

    void testRemoveBlockUser(){
        params.token = token.token
        params.id = 3
        controller.removeBlockUser()
        assert response.text == '{"response":"user_unblocked"}'
        assertEquals(user.usersBlocked?.contains(params.id as long),false)
    }

    void testRemoveFriend(){
        params.token = token.token
        params.friendId = 2
        controller.removeFriend()
        assertEquals(response.json.response,"friend_removed")
        assertEquals(user.friends?.contains(params.friendId as long),false)
        assertEquals(user2.friends?.contains(1L),false)
    }

    void testDeleteAccount(){
        params.token = token.token
        controller.deleteAccount()
        assert response.text == '{"response":"user_deleted"}'
        assertEquals(user.status,UserStatus.REMOVED)
        assertEquals(user2.friends?.contains(1L),false)
    }

    void testChangePassword(){
        params.token = token.token
        params.currentPassword =  'admin'
        params.newPassword = 'admin123'
        controller.changePassword()
        assertEquals(response.text,'{"response":"user_updated"}')
        assert user.passwordHash == new Sha256Hash('admin123').toHex()
    }

    void testChangePasswordFail(){
        params.token = token.token
        params.currentPassword =  'admin1'
        params.newPassword = 'admin123'
        controller.changePassword()
        assertEquals(response.text,'{"response":"password_incorrect"}')
    }
}
