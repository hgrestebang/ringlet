package com.ps.ringlet



import org.junit.*
import grails.test.mixin.*
import groovy.mock.interceptor.*
import grails.converters.*

@TestFor(InvitationController)
@Mock([User,UserToken,ApnService,UserService,Invitation])
class InvitationControllerTests {
    def user,user2,user3,token,invitation

    void setUp() {
        mockForConstraintsTests(User)
        mockForConstraintsTests(UserToken)
        mockForConstraintsTests(UserService)
        mockForConstraintsTests(Invitation)

        token = new UserToken(
                token: 'token',
                valid: true
        ).save()

        user = new User(
                username: 'test@ringlet.me',
                passwordHash: '5bbf1a9e0de062225a1b',
                name: 'User Test',
                token: token
        ).save()

        user2 = new User(
                username: 'test2@ringlet.me',
                passwordHash: '5bbf1a9e0de062225a1b',
                name: 'User Test2',
                token: new UserToken(
                        token: 'token1',
                        valid: true
                ).save()
        ).save()

        user3 = new User(
                username: 'test3@ringlet.me',
                passwordHash: '5bbf1a9e0de062225a1b',
                name: 'User Test3',
                token: new UserToken(
                        token: 'token2',
                        valid: true
                ).save()
        ).save()

        invitation = new Invitation(
                message: 'Invitation Test 1',
                dateCreated: new Date(),
                ownerStatus: MessageStatus.SEEN,
                recipientStatus: MessageStatus.UNSEEN,
                owner: user,
                recipient: user2
        ).save()
    }

    void testGetByUser(){
        params.token = 'token1'
        controller.getByUser()
        assert response.getJson().size() > 0
        assertEquals(response.json.id[0],1)
        assertEquals(response.json.message[0],'Invitation Test 1')
    }

    void testCreate(){
        params.token = 'token1'
        def InvitationT = [message: "Test Invitation 2",recipientId: 3]
        params.invitation = InvitationT
        controller.create()
        assert response.text == '{"response":"invitation_created"}'
        assert Invitation.findByOwnerAndRecipient(user2,user3)
    }

    void testCreateFail(){
        params.token = 'token'
        def InvitationT = [recipientId : 2]
        params.invitation = InvitationT
        controller.create()
        assert response.text == '{"response":"invitation_not_created"}'
    }

    void testAcceptInvitation(){
        params.token = 'token1'
        params.id = 1
        controller.acceptInvitation()
        assert response.text == '{"response":"invitation_accepted"}'
        invitation.recipientStatus = MessageStatus.ACCEPTED
        assert user2.friends?.contains(1L)
        assert user.friends?.contains(2L)
    }

    void testDeclineInvitation(){
        params.token = 'token1'
        params.id = 1
        controller.declineInvitation()
        assert response.text == '{"response":"invitation_declined"}'
        invitation.recipientStatus = MessageStatus.DELETED
    }

    void testDelete(){
        params.token = 'token'
        params.id = 1
        controller.delete()
        assert response.text == '{"response":"invitation_deleted"}'
        invitation.ownerStatus == MessageStatus.DELETED
    }
}
