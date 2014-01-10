package com.ps.ringlet



import org.junit.*
import grails.test.mixin.*

@TestFor(AnnouncementController)
@Mock([User,UserToken,UserService,Announcement])
class AnnouncementControllerTests {
    def user,user2,announcement

    void setUp() {
        mockForConstraintsTests(Announcement)

        user = new User(
                username: 'test@ringlet.me',
                passwordHash: '5bbf1a9e0de062225a1b',
                facebookId: '12345',
                name: 'User Test',
                phone: '123456789',
                token: new UserToken(
                        token: 'token123',
                        valid: true
                ).save(),
                location: [37.33233141d, -122.031286d]
        ).save()

        user2 = new User(
                username: 'test@ringlet.me',
                passwordHash: '5bbf1a9e0de062225a1b',
                facebookId: '1234',
                name: 'User Test',
                phone: '123456789',
                token: new UserToken(
                        token: 'token1234',
                        valid: true
                ).save(),
                location: [37.33233141d, -122.031286d]
        ).save()

        announcement = new Announcement(
                message: 'Announcement Test',
                groupCode: '1',
                dateCreated: new Date(),
                ownerStatus: MessageStatus.SEEN,
                recipientStatus: MessageStatus.UNSEEN,
                owner: user,
                recipient: user2,
                location: [37.33233141d, -122.031286d],
                radius: 30
        ).save()
    }

    void testGetByUser(){
        params.token = 'token123'
        controller.getByUser()
        assert response.getJson().size() > 0
    }

    void testGetByUserNoAnnouncement(){
        params.token = 'token123'
        controller.getByUser()
        assert response.getJson().size() == 0
    }

    void testCreate(){
        /*params.token = 'token1234'
        def announcementT = [message: "Test Invitation 2",recipientId: 1]
        params.announcement = announcementT
        controller.create()
        assert response.text == '{"response":"announcement_created"}'*/
    }

    void testCreateFail(){
        /*params.token = 'token123'
        controller.create()
        assert response.text == '' */
    }

    void testDelete(){
        params.token = 'token123'
        params.id = 1
        controller.delete()
        assert response.text == '{"response":"announcement_deleted"}'
    }
}
