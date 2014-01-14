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
                ringlets:[1],
                friends:[2,3],
                usersBlocked:[],
                photos:['http://test']
        ).save()

        ringlet = new Ringlet(
                name: 'Ringlet Test',
                owner: user,
                users: [1]

        ).save()
    }

    void testGetByUser(){
        params.token = 'token123'
        controller.getByUser()
        assert response.getJson().size() > 0
        assert response.json.id.contains(1)
        assertEquals(response.json.name[0],'Ringlet Test')
        assert response.json.users.size() > 0
    }

    void testCreateFail(){
        params.token = 'token123'
        params.name = 'Ringlet Test'
        controller.create()
        assert response.text == '{"response":"ringlet_name_used"}'
    }

    void testCreate(){
        params.token = 'token123'
        params.name = 'Ringlet Create Test'
        controller.create()
        assert response.text == '{"response":"ringlet_created"}'
    }

    void testUpdateFail(){
        params.ringlet = new Ringlet(
                name: 'Ringlet Test',
                owner: user,
                users: [1,2,3]
        ).save()
        params.token = 'token123'
        controller.update()
        assert response.text == '{"response":"ringlet_name_used"}'
    }

    void testUpdate(){
        params.ringlet = new Ringlet(
                name: 'Ringlet Test 1',
                owner: user
        ).save()
        params.token = 'token123'
        controller.update()
        assert response.text == '{"response":"ringlet_updated"}'
    }

    void testAddUser(){
        params.token = 'token123'
        params.ringletId = 1
        params.userId = 2
        controller.addUser()
        assert response.text == '{"response":"user_added"}'
    }

    void testRemoveUser(){
        params.token = 'token123'
        params.ringletId = 1
        params.userId = 1
        controller.removeUser()
        assert response.text == '{"response":"user_removed"}'
    }

    void testDeleteFail(){
        params.token =  'token123'
        params.id = 1
        controller.delete()
        assertEquals(response.text,'{"response":"ringlet_deleted"}')
    }

    void testDelete(){
        params.token =  'token123'
        params.id = 2
        controller.delete()
        assertEquals(response.text,'{"response":"ringlet_not_deleted"}')
    }

}
