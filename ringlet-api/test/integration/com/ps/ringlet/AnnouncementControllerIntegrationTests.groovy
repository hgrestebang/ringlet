package com.ps.ringlet

import static org.junit.Assert.*
import org.junit.*

class AnnouncementControllerIntegrationTests {
    def contAnnouncement,token

    @Before
    void setUp() {
        // Setup logic here
        contAnnouncement = new AnnouncementController()
        token = User.findByUsername('admin@ringlet.me').token?.token

        if(!Announcement.findAll()){
             new Announcement(
                     message: 'Announcement Test',
                     groupCode: '1',
                     dateCreated: new Date(),
                     ownerStatus: MessageStatus.SEEN,
                     recipientStatus: MessageStatus.UNSEEN,
                     owner: User.findByUsername('admin@ringlet.me'),
                     recipient: User.findByUsername('user1@ringlet.me'),
                     location: [37.33233141d, -122.031286d],
                     radius: 30
             ).save()
        }
    }

    @After
    void tearDown() {
        // Tear down logic here
        Announcement announcementR = Announcement.findById(1L)
        announcementR?.setOwnerStatus(MessageStatus.SEEN)
        announcementR?.setRecipientStatus(MessageStatus.UNSEEN)
        announcementR?.save(flush: true)
    }

    @Test
    void testCreate(){
        contAnnouncement.params.announcement = [body:"Create Test Announcement", radius: [miles: '10']]
        contAnnouncement.params.token = token
        contAnnouncement.create()
        assert contAnnouncement.response.json.response == 'announcement_created'
        assert Announcement.findByOwnerAndRadius(User.findByToken(UserToken.findByToken(token)),10).message ==  "Create Test Announcement"
    }

    @Test
    void testDelete(){
        contAnnouncement.params.token = token
        contAnnouncement.params.id = 1
        contAnnouncement.delete()
        assert contAnnouncement.response.text == '{"response":"announcement_deleted"}'
        assert  Announcement.findById(contAnnouncement.params.id as long).ownerStatus == MessageStatus.DELETED
    }

    @Test
    void testGetByUser(){
        contAnnouncement.params.token = token
        contAnnouncement.getByUser()
        assert contAnnouncement.response.getJson().size() > 0
        assert Announcement.findByOwner(User.findByToken(UserToken.findByToken(contAnnouncement.params.token))).message == 'Announcement Test'
        assert Announcement.findByOwner(User.findByToken(UserToken.findByToken(contAnnouncement.params.token))).groupCode == '1'
    }
}
