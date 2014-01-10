package com.ps.ringlet



import org.junit.*
import grails.test.mixin.*
import groovy.mock.interceptor.*
import grails.converters.*

@TestFor(ChatController)
@Mock([Chat,User,UserService,UserToken])
class ChatControllerTests {
    def user,user2,token,chat

    void setUp() {
        mockForConstraintsTests(User)
        mockForConstraintsTests(UserToken)
        mockForConstraintsTests(UserService)
        mockForConstraintsTests(Chat)

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

        chat = new Chat(
                message: 'Chat message test',
                dateCreated: new Date(),
                owner: user,
                recipient: user2,
                ownerStatus: MessageStatus.SEEN,
                recipientStatus: MessageStatus.DELETED
        ).save()
    }

    void testGetByUser(){
        params.token = 'token123'
        params.recipientId = 2
        controller.getByUser()
        assert response.getJson().size() > 0
    }

    void testGetByUserNOTUSER(){
        params.token = 'token123'
        params.recipientId = 2
        controller.getByUser()
        assert response.getJson().size() == 0
    }

    void testCreate(){
        /*params.token = 'token1234'
        def InvitationT = [message: "Test Invitation 2",recipientId: 1]
        params.invitation = InvitationT
        controller.create()
        assert response.text == '{"response":"chat_created"}'*/
    }

    void testCreateFail(){
        /*params.token = 'token1234'
        def InvitationT = [message: "Test Invitation 2"]
        params.invitation = InvitationT
        controller.create()
        assert response.text == '{"response":"chat_not_created"}'*/
    }
}
