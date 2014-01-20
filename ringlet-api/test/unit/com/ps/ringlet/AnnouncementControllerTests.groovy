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
                name: 'User Test',
                token: new UserToken(
                        token: 'token',
                        valid: true
                ).save(),
                location: [37.33233141d, -122.031286d]
        ).save()

        user2 = new User(
                username: 'test1@ringlet.me',
                passwordHash: '5bbf1a9e0de062225a1b',
                name: 'User Test 2',
                token: new UserToken(
                        token: 'token1',
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
        params.token = 'token'
        controller.getByUser()
        assert response.getJson().size() >= 0
        assert response.json[0]?.message ==  'Announcement Test'
    }

    void testGetByUserNoAnnouncement(){
        params.token = 'token123'
        controller.getByUser()
        assert response.text == '[{"response":"not_found"}]'
    }

    void testCreate(){
        /*params.announcement = [body:"Create Test Announcement 000", radius: [miles: '10']]
        params.token = 'token1'
        controller.create()
        assert response.text == '{"response":"announcement_created"}'
        assert Announcement.findByMessage('Create Test Announcement 000') */
    }

    void testCreateFail(){
        /*params.token = 'token123'
        controller.create()
        assert response.text == '' */
    }

    void testDelete(){
        params.token = 'token'
        params.id = 1
        controller.delete()
        assert response.text == '{"response":"announcement_deleted"}'
        assert announcement.ownerStatus ==  MessageStatus.DELETED
    }
}
