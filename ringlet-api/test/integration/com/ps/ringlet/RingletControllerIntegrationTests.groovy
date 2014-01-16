package com.ps.ringlet

import static org.junit.Assert.*
import org.junit.*

class RingletControllerIntegrationTests {
    def contRinglet,idCreaRinglet
    def token = '3d231daf-1d95-4991-a2df-b390e1deb75e'

    @Before
    void setUp() {
        // Setup logic here
        contRinglet = new RingletController()
    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void testCreate(){
        contRinglet.params.token = token
        contRinglet.params.name = 'Ringlet Test 3'
        contRinglet.create()
        assert contRinglet.response.text == '{"response":"ringlet_created"}'
        assertEquals(Ringlet.findByOwnerAndName(User.findById(1), contRinglet.params.name).name,contRinglet.params.name)
        idCreaRinglet = Ringlet.findByOwnerAndName(User.findById(1), contRinglet.params.name).id

    }

    @Test
    void testGetByUser(){
        contRinglet.params.token = token
        contRinglet.getByUser()
        assert contRinglet.response.json.id.contains(1)
        assertEquals(contRinglet.response.json.name[0],'Ringlet Test')
        assert contRinglet.response.json.users.size() > 0
    }

    @Test
    void testCreateFail(){
        contRinglet.params.token = token
        contRinglet.params.name = 'Ringlet Test'
        contRinglet.create()
        assert contRinglet.response.text == '{"response":"ringlet_name_used"}'
    }

    @Test
    void testUpdate(){
        contRinglet.params.ringlet = [
                name: 'Ringlet Test Update',
                id:2]
        contRinglet.params.token = token
        contRinglet.update()
        assert contRinglet.response.text == '{"response":"ringlet_updated"}'
        assertEquals(Ringlet.findById(2).name, contRinglet.params.ringlet.name)
    }

    @Test
    void testUpdateError(){
        contRinglet.params.ringlet = [
                name: 'Ringlet Test',
                id:3]
        contRinglet.params.token = token
        contRinglet.update()
        assert contRinglet.response.text == '{"response":"ringlet_name_used"}'
        assertEquals(Ringlet.findById(1).name, contRinglet.params.ringlet.name)
    }

    @Test
    void testAddUser(){
        contRinglet.params.token = token
        contRinglet.params.ringletId = 1
        contRinglet.params.userId = 3
        contRinglet.addUser()
        assert contRinglet.response.text == '{"response":"user_added"}'
        assert Ringlet.findById(1).users?.contains(contRinglet.params.userId as Long)
    }

    @Test
    void testRemoveUser(){
        contRinglet.params.ringletId = 1
        contRinglet.params.userId = 3
        contRinglet.params.token = token
        contRinglet.removeUser()
        assert Ringlet.findById(1).users?.contains(contRinglet.params.userId as Long) == false
        assert contRinglet.response.text == '{"response":"user_removed"}'
    }

    @Test
    void testDelete(){
        contRinglet.params.token =  token
        contRinglet.params.id = 7 //idCreaRinglet
        contRinglet.delete()
        assert contRinglet.response.text == '{"response":"ringlet_deleted"}'
        assertNull(Ringlet.findById(idCreaRinglet as long))
    }

}
