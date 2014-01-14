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
                token: 'token123',
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
                sound: true,
                connectionStatus: true,
                location: [37.33233141d, -122.031286d],
                token: token,
                gender: 'MALE',
                status: 'ACTIVE',
                proPurchase: purchase,
                ringlets:[],
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
                sound: true,
                connectionStatus: true,
                location: [37.33233141d, -122.031286d],
                token: new UserToken(
                        token: 'token12345',
                        valid: true
                ).save(),
                gender: 'MALE',
                status: 'ACTIVE',
                //proPurchase: purchase,
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
                distanceFromPoint: 12,
                showOnMap: true,
                sound: true,
                connectionStatus: true,
                location: [37.33233141d, -122.031286d],
                token: new UserToken(
                        token: 'token1234',
                        valid: true
                ).save(),
                gender: 'MALE',
                status: 'ACTIVE',
                //proPurchase: purchase,
                ringlets:[],
                friends:[],
                usersBlocked:[],
                photos:[]
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

    void testGetCurrent(){
        params.token = 'token123'
        controller.getCurrent()
        assertNotNull(response)
        assert response.json.token.token == 'token123'
        assert response.json.token.id == 1
        assert response.json.id == 1
        assert response.json.username == 'test@ringlet.me'
    }

    void testGetByUsername(){
        params.username = 'test@ringlet.me'
        controller.getByUsername()
        assertNotNull(response)
        assert response.json.id == 1
        assert response.json.username == 'test@ringlet.me'
    }

    void testGetById(){
        params.id = 1
        controller.getById()
        assertEquals(response.json.id,1)
        assertEquals(response.json.username,'test@ringlet.me')
    }

    void testCreateFail(){
        def userT = [email: "test@ringlet.me"]
        params.user = userT
        controller.create()
        assert response.text == '{"response":"email_used"}'
    }

    void testCreate(){
        def userT = [email: "test5@ringlet.me",password: "test1234"]
        params.user = userT
        controller.create()
        assert response.text == '{"response":"user_created"}'
    }

    void testForgotPassword(){
        /*params.username = 'test@ringlet.me'
        controller.forgotPassword()
        assert response.text == '{"response":"email_send"}'*/
    }

    void testForgotPasswordFailUser(){
         params.username = 'test1@ringlet.me'
         controller.forgotPassword()
         assert response.text == '{"response":"user_not_found"}'
     }

    void testForgotPasswordEmail(){
        params.username = 'test@ringlet.me'
        controller.forgotPassword()
        assert response.text == '{"response":"email_not_send"}'
    }

    void testGetAll(){
        params.token = 'token123'
        controller.getAll()
        assert response.text != '{"response":"bad_request"}'
        assert response.getJson().size() > 0
        assert response.json.id.size() > 0
        assert response.json.id[0] == 2
    }

    /*
    void testGetAllFail(){
        params.token = 'token1234'
        controller.getAll()
        assert response.text == '{"response":"bad_request"}'
    } */

    void testNearBy(){
        /*params.token = 'token123'
        controller.nearBy()*/

    }

    void testGetFriends(){
        params.token = 'token123'
        controller.getFriends()
        assert response.getJson().size() > 0
        assert response.json.id.size() > 0
        assert response.json.id[0] == 2
    }

    void testGetFriendsFail(){
        params.token = 'token1234'
        controller.getFriends()
        assert response.getJson().size() == 0
    }

    void testUpdate(){
        params.token = 'token123'
        def userT = [username: "test@ringlet.me"]
        params.user = userT
        controller.update()
        assert response.text == '{"response":"user_updated"}'
    }

    void testUpdateFail(){
        params.token = 'token1234'
        def userT = [username: "test@ringlet.me"]
        params.user = userT
        controller.update()
        assert response.text == '{"response":"email_used"}'
    }

    void testAddBlockUser(){
        params.token = 'token1234'
        params.id = 2
        controller.addBlockUser()
        assert response.text == '{"response":"user_blocked"}'
        //assert user3.usersBlocked?.contains(1)

    }

    void testRemoveBlockUser(){
        params.token = 'token123'
        params.id = 3
        controller.removeBlockUser()
        assert response.text == '{"response":"user_unblocked"}'
    }

    void testDeleteAccount(){
        params.token = 'token123'
        controller.deleteAccount()
        assert response.text == '{"response":"user_deleted"}'
    }

    void testChangePassword(){
        params.token = 'token123'
        params.currentPassword =  'admin'
        params.newPassword = 'admin123'
        controller.changePassword()
        assertEquals(response.text,'{"response":"user_updated"}')
        assert user.passwordHash == new Sha256Hash('admin123').toHex()
    }

    void testChangePasswordFail(){
        params.token = 'token123'
        params.currentPassword =  'admin'
        params.newPassword = 'admin123'
        controller.changePassword()
        assertEquals(response.text,'{"response":"password_incorrect"}')
    }
}
