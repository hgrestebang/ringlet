package com.ps.ringlet

import static org.junit.Assert.*
import org.junit.*

class RingletControllerIntegrationTests {
    def contRinglet
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
        contRinglet.params.name = 'Ringlet Test'
        contRinglet.create()
        assert contRinglet.response.text == '{"response":"ringlet_created"}'
        assertEquals(Ringlet.findByOwnerAndName(User.findById(1), contRinglet.params.name).name,contRinglet.params.name)
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
        contRinglet.params.ringlet = [name:'Ringlet Test', id:2]
        contRinglet.params.token = token
        contRinglet.update()
        assert contRinglet.response.text == '{"response":"ringlet_updated"}'
    }

}
