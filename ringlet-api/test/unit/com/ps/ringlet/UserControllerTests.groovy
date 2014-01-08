package com.ps.ringlet

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestMixin(GrailsUnitTestMixin)
@TestFor(UserController)
@Mock(User)
class UserControllerTests {

    /*void tokenSetUP(){
        mockForConstraintsTests(UserToken)
        def token = new UserToken(
                token: 'random123',
                valid: true
                //lastUpdated: 'Tue Jan 07 12:41:31 CST 2014'
        ).save()
    }*/

    void setUp() {
        mockForConstraintsTests(User)
        //mockForConstraintsTests(UserToken)
        /*def token = new UserToken(
                token: 'random123',
                valid: true
                //lastUpdated: 'Tue Jan 07 12:41:31 CST 2014'
        ).save()*/

        def user = new User(
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
                token: 'token123',
                gender: 'MALE',
                status: 'ACTIVE',
                //proPurchase: purchase,
                ringlets:[2,3],
                friends:[4,3],
                usersBlocked:[5,7],
                photos:['http://test']
        ).save()
    }

    void tearDown() {
        // Tear down logic here
    }

    void testGetCurrent(){
        setUp()
        params.token = 'token123'
        UserToken.findByToken('123').toObject()
        //controller.getCurrent()
        //assert response != null
    }

    void testGetByUsername(){
        setUp()
        params.username = 'test@ringlet.me'
        controller.getByUsername()
        assertNotNull(response)
    }

    void testGetById(){
        setUp()
        params.id = 1
        controller.getById()
        assert response != null
    }

    void testCreateFail(){
        setUp()
        //params.user = user
        controller.create()
        assert response == 'email_used'
    }

    void testCreate(){
        setUp()
        params.user = new User(
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
                token: 'random123',
                gender: 'MALE',
                status: 'ACTIVE',
                //proPurchase: purchase,
                ringlets:[2,3],
                friends:[4,3],
                usersBlocked:[5,7],
                photos:['http://test']
        ).save()
        println(params)
        //params.username = 'test1@ringlet.me'
        controller.create()
        assert response == 'user_created'
    }

    void testForgotPassword(){
         setUp()
         params.username = 'test1@ringlet.me'
         controller.forgotPassword()
         assert response.text == '{"response":"user_not_found"}'
     }

    void testForgotPasswordEmail(){
        setUp()
        params.username = 'test@ringlet.me'
        controller.forgotPassword()
        assert response.text == '{"response":"email_send"}'
    }
}
