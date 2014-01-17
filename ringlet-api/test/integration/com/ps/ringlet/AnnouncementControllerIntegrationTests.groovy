package com.ps.ringlet

import static org.junit.Assert.*
import org.junit.*

class AnnouncementControllerIntegrationTests {
    def contAnnouncement
    def token = '3d231daf-1d95-4991-a2df-b390e1deb75e'

    @Before
    void setUp() {
        // Setup logic here
        contAnnouncement = new AnnouncementController()
    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void testCreate(){
        contAnnouncement.params.announcement = [body:"Create Test Announcement", radius: [miles: '10']]
        contAnnouncement.params.token = token
        contAnnouncement.create()
        assert contAnnouncement.response.json.response == 'announcement_created'
        //assert Announcement.findLastIndexOf()
        //assertEquals(Announcement.findById(1L).id,1)
    }

    @Test
    void testDelete(){
        contAnnouncement.params.token = token
        contAnnouncement.params.id = 10
        contAnnouncement.delete()
        assert contAnnouncement.response.text == '{"response":"announcement_deleted"}'
        //assertNull(Ringlet.findById(contAnnouncement.params.id  as long))
        assert  Announcement.findById(10).ownerStatus == MessageStatus.DELETED
    }

    @Test
    void testGetByUser(){
        contAnnouncement.params.token = token
        contAnnouncement.getByUser()
        assert contAnnouncement.response.getJson().size() > 0
    }
}
