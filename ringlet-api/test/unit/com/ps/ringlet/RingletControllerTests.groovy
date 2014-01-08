package com.ps.ringlet



import org.junit.*
import grails.test.mixin.*
import groovy.mock.interceptor.*
import grails.converters.*

@TestFor(RingletController)
@Mock([User,UserToken,UserService,Ringlet])
class RingletControllerTests {
    def user
    def token
    def ringlet

    void setUp() {
        mockForConstraintsTests(User)
        mockForConstraintsTests(UserToken)
        mockForConstraintsTests(UserService)
        mockForConstraintsTests(Ringlet)


        token = new UserToken(
                token: 'token123',
                valid: true
        ).save()

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
                //proPurchase: purchase,
                ringlets:[3],
                friends:['2','3'],
                usersBlocked:[],
                photos:['http://test']
        ).save()

        ringlet = new Ringlet(
                name: 'Ringlet Test',
                owner: user
        ).save()
    }

    void testGetByUser(){
        params.token = 'token123'
        controller.getByUser()
        assert response.getJson().size() > 0
    }

    void testCreateFail(){
        params.token = 'token123'
        params.name = 'Ringlet Test'
        controller.create()
        assert response.text == '{"response":"ringlet_name_used"}'
    }

}
