package com.ps.ringlet



import org.junit.*
import grails.test.mixin.*
import groovy.mock.interceptor.*
import grails.converters.*

@TestFor(RingletController)
@Mock([User,UserToken,UserService,Ringlet])
class RingletControllerTests {
    def user,user2,token,ringlet

    void setUp() {
        mockForConstraintsTests(User)
        mockForConstraintsTests(UserToken)
        mockForConstraintsTests(UserService)
        mockForConstraintsTests(Ringlet)

        token = new UserToken(
                token: 'token',
                valid: true
        ).save()

        user = new User(
                username: 'test@ringlet.me',
                passwordHash: '5bbf1a9e0de062225a1b',
                name: 'User Test',
                showOnMap: true,
                connectionStatus: true,
                location: [37.33233141d, -122.031286d],
                token: token,
                gender: 'MALE',
                status: 'ACTIVE',
                ringlets:[1],
                friends:[2,3],
                usersBlocked:[],
        ).save()

        user2 = new User(
                username: 'test2@ringlet.me',
                passwordHash: '5bbf1a9e0de062225a1b',
                name: 'User Test 2',
                location: [37.33233141d, -122.031286d],
                token: new UserToken(
                        token: 'token1',
                        valid: true
                ).save(),
                gender: 'MALE',
                status: 'ACTIVE'
        ).save()

        ringlet = new Ringlet(
                name: 'Ringlet Test',
                owner: user,
                users: [1,3]
        ).save()
    }

    void testGetByUser(){
        params.token = 'token'
        controller.getByUser()
        assert response.json.id?.contains(1)
        assertEquals(response.json?.name[0],'Ringlet Test')
        assert response.json.users?.size() > 0
        assert response.json?.users[0].contains(1)
    }

    void testCreateFail(){
        params.token = 'token'
        params.name = 'Ringlet Test'
        controller.create()
        assert response.text == '{"response":"ringlet_name_used"}'
    }

    void testCreate(){
        params.token = 'token1'
        params.name = 'Ringlet Create Test'
        controller.create()
        assert response.text == '{"response":"ringlet_created"}'
        assert user2.ringlets?.contains(2L)
        assert Ringlet.findById(2L)?.name == 'Ringlet Create Test'
        assert Ringlet.findById(2L)?.owner == user2

    }

    void testUpdateFail(){
        params.ringlet = new Ringlet(
                name: 'Ringlet Test',
                owner: user,
                users: [1,2,3]
        ).save()
        params.token = 'token'
        controller.update()
        assert response.text == '{"response":"ringlet_name_used"}'
        assertNotNull(Ringlet.findByName('Ringlet Test'))
    }

    void testUpdate(){
        params.ringlet = new Ringlet(
                name: 'Ringlet Test 1',
                owner: user
        ).save()
        params.token = 'token'
        controller.update()
        assert response.text == '{"response":"ringlet_updated"}'
        assertNotNull(Ringlet.findByOwnerAndName(user,"Ringlet Test 1"))
    }

    void testAddUser(){
        params.token = 'token'
        params.ringletId = 1
        params.userId = 2
        controller.addUser()
        assert response.text == '{"response":"user_added"}'
        assert ringlet.users?.contains(2L)
    }

    void testRemoveUser(){
        params.token = 'token'
        params.ringletId = 1
        params.userId = 3
        controller.removeUser()
        assert response.text == '{"response":"user_removed"}'
        assertEquals(ringlet.users?.contains(3L),false)
    }

    void testDeleteFail(){
        params.token =  'token'
        params.id = 2
        controller.delete()
        assertEquals(response.text,'{"response":"ringlet_not_deleted"}')
    }

    void testDelete(){
        params.token = 'token'
        params.id = 1
        controller.delete()
        assertEquals(response.text,'{"response":"ringlet_deleted"}')
        assertNull(Ringlet.findById(1L))
    }

}
