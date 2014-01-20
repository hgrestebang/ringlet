package com.ps.ringlet

import static org.junit.Assert.*
import org.junit.*

class RingletControllerIntegrationTests {
    def contRinglet,idCreaRinglet
    def token

    @Before
    void setUp() {
        // Setup logic here
        contRinglet = new RingletController()
        token = User.findByUsername('admin@ringlet.me').token?.token

        if(!Ringlet.findAll()){
            new Ringlet(
                    name: 'Ringlet Test',
                    owner: User.findByUsername('admin@ringlet.me'),
                    users: [1,2]

            ).save()

            User owner = User.findByUsername('admin@ringlet.me')
            owner.addToRinglets(1L)
            owner.save(flush: true)
        }
    }

    @After
    void tearDown() {
        // Tear down logic here
        Ringlet ringletR = Ringlet.findById(1L)
        ringletR?.setName('Ringlet Test')
        ringletR?.save(flush: true)
    }

    @Test
    void testCreate(){
        contRinglet.params.token = token
        contRinglet.params.name = 'Ringlet Create Test'
        contRinglet.create()
        assert contRinglet.response.text == '{"response":"ringlet_created"}'
        assertEquals(Ringlet.findByOwnerAndName(User.findById(1), contRinglet.params.name).name,contRinglet.params.name)
        idCreaRinglet = Ringlet.findByOwnerAndName(User.findById(1), contRinglet.params.name).id
    }

    @Test
    void testGetByUser(){
        contRinglet.params.token = token
        contRinglet.getByUser()
        assert contRinglet.response.json.id.size() > 0
        assert contRinglet.response.json.id.contains(1)
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
                id:1]
        contRinglet.params.token = token
        contRinglet.update()
        assert contRinglet.response.text == '{"response":"ringlet_updated"}'
        assertEquals(Ringlet.findById(1).name, contRinglet.params.ringlet.name)
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
        assert Ringlet.findById(1).users?.contains(contRinglet.params.userId as long)
    }

    @Test
    void testRemoveUser(){
        contRinglet.params.ringletId = 1
        contRinglet.params.userId = 3
        contRinglet.params.token = token
        contRinglet.removeUser()
        assert Ringlet.findById(1).users?.contains(contRinglet.params.userId as long) == false
        assert contRinglet.response.text == '{"response":"user_removed"}'
    }

    @Test
    void testDelete(){
        contRinglet.params.token =  token
        contRinglet.params.id = Ringlet.findByOwnerAndName(User.findById(1L),'Ringlet Create Test').id
        contRinglet.delete()
        assert contRinglet.response.text == '{"response":"ringlet_deleted"}'
        assertNull(Ringlet.findById(contRinglet.params.id as long))
    }

}
