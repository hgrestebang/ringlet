package com.ps.ringlet

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
@Mock([User,UserToken,UserService,Purchase])
class UserControllerTests {
    def user,user2,user3
    def token
    def purchase

    void setUp() {
        mockForConstraintsTests(User)
        mockForConstraintsTests(UserToken)
        mockForConstraintsTests(UserService)
        mockForConstraintsTests(Purchase)
        //mockFor(UserToken)
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
        )

        user = new User(
                username: 'test@ringlet.me',
                passwordHash: '5bbf1a9e0de062225a1b',
                facebookId: '12345',
                name: 'User Test',
                phone: '123456789',
                bio: 'User for test',
                distanceFromPoint: 12,
                coins: 50,
                showOnMap: true,
                sound: true,
                connectionStatus: true,
                location: [37.33233141d, -122.031286d],
                token: token,
                gender: 'MALE',
                status: 'ACTIVE',
                proPurchase: purchase,
                ringlets:[],
                friends:['2','3'],
                usersBlocked:[],
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
                coins: 50,
                showOnMap: true,
                sound: true,
                connectionStatus: true,
                location: [37.33233141d, -122.031286d],
                token: token,
                gender: 'MALE',
                status: 'ACTIVE',
                proPurchase: purchase,
                ringlets:[2,3],
                friends:[4,3],
                usersBlocked:[5,7],
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
                coins: 50,
                showOnMap: true,
                sound: true,
                connectionStatus: true,
                location: [37.33233141d, -122.031286d],
                token: token,
                gender: 'MALE',
                status: 'ACTIVE',
                proPurchase: purchase,
                ringlets:[],
                friends:[],
                usersBlocked:[],
                photos:[]
        ).save()
    }

    void tearDown() {
        // Tear down logic here
    }

    void testGetCurrent(){
        params.token = 'token123'
        controller.getCurrent()
        println(response)
        assert response != null
    }

    void testGetByUsername(){
        params.username = 'test@ringlet.me'
        controller.getByUsername()
        assertNotNull(response)
    }

    void testGetById(){
        params.id = 1
        controller.getById()
        assert response != null
    }

    void testCreateFail(){
        params.user = user
        controller.create()
        assert response.text == '{"response":"email_used"}'
    }

    void testCreate(){
        //params.user = user2
        params.user.username = 'test1@ringlet.me'
        controller.create()
        assert response.text == '{"response":"user_created"}'
    }

    void testForgotPassword(){
         params.username = 'test1@ringlet.me'
         controller.forgotPassword()
         assert response.text == '{"response":"user_not_found"}'
     }

    void testForgotPasswordEmail(){
        params.username = 'test@ringlet.me'
        controller.forgotPassword()
        assert response.text == '{"response":"email_send"}'
    }

    void testGetAll(){
        params.token = 'token123'
        controller.getAll()
        assert response.text != '{"response":"bad_request"}'
    }

    void testGetAllFail(){
        params.token = 'token123'
        controller.getAll()
        assert response.text == '{"response":"bad_request"}'
    }

    void testNearBy(){
        /*params.token = 'token123'
        controller.nearBy()*/

    }

    void testGetFriends(){
        params.token = 'token123'
        controller.getFriends()
        //def friends = response.getJson()[0].toString()
        //def friend = JSON.parse(friends)
        assert response.getJson().size() > 0
    }

    void testGetFriendsNot(){
        params.token = 'token123'
        controller.getFriends()
        assert response.getJson().size() == 0
    }

    void testUpdate(){
        /*params.token = 'token123'
        params.user = user
        //params.user.userLocation = '{lat:37.33233141d, lgn:-122.031286d}'
        controller.update()
        def message = JSON.parse(response.getJson()[0].toString())
        assert message == 'user_updated'*/
    }

    void testAddBlockUser(){
        params.token = 'token123'
        params.id = 3
        controller.addBlockUser()
        assert response.text == '{"response":"user_blocked"}'

    }

    void testRemoveBlockUser(){
        params.token = 'token123'
        params.id = 3
        controller.removeBlockUser()
        assert response.text == '{"response":"user_unblocked"}'
    }

    void testDeleteAccount(){
        /*params.token = 'token123'
        controller.deleteAccount()
        assert response.text == '{"response":"user_deleted"}'*/
    }
}
