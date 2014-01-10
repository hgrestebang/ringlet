package com.ps.ringlet



import org.junit.*
import grails.test.mixin.*
import groovy.mock.interceptor.*
import grails.converters.*

@TestFor(InvitationController)
@Mock([User,UserToken,UserService,Invitation])
class InvitationControllerTests {
    def user,user2,token,invitation

    void setUp() {
        mockForConstraintsTests(User)
        mockForConstraintsTests(UserToken)
        mockForConstraintsTests(UserService)
        mockForConstraintsTests(Invitation)

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
                token: token
        ).save()

        user2 = new User(
                username: 'test2@ringlet.me',
                passwordHash: '5bbf1a9e0de062225a1b',
                facebookId: '12345',
                name: 'User Test2',
                phone: '123456789',
                token: new UserToken(
                        token: 'token1234',
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
        params.token = 'token123'
        controller.getByUser()
        assert response.getJson().size() > 0
    }

    void testCreate(){
        params.token = 'token1234'
        def InvitationT = [message: "Test Invitation 2",recipientId: 1]
        params.invitation = InvitationT
        controller.create()
        assert response.text == '{"response":"invitation_created"}'
    }

    void testCreateFail(){
        params.token = 'token123'
        def InvitationT = [recipientId : 2]
        params.invitation = InvitationT
        controller.create()
        assert response.text == '{"response":"invitation_not_created"}'
    }

    void testAcceptInvitation(){
        params.token = 'token1234'
        params.id = 1
        controller.acceptInvitation()
        assert response.text == '{"response":"invitation_accepted"}'
    }

    void testDeclineInvitation(){
        params.token = 'token1234'
        params.id = 1
        controller.declineInvitation()
        assert response.text == '{"response":"invitation_declined"}'
    }

    void testDelete(){
        params.token = 'token123'
        params.id = 1
        controller.delete()
        assert response.text == '{"response":"invitation_deleted"}'
    }
}
