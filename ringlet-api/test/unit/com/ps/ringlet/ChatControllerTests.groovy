package com.ps.ringlet



import org.junit.*
import grails.test.mixin.*
import groovy.mock.interceptor.*
import grails.converters.*

@TestFor(ChatController)
@Mock([Chat,User,ApnService,UserService,UserToken])
class ChatControllerTests {
    def user,user2,user3 ,token,chat

    void setUp() {
        mockForConstraintsTests(User)
        mockForConstraintsTests(UserToken)
        mockForConstraintsTests(UserService)
        mockForConstraintsTests(Chat)

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

        chat = new Chat(
                message: 'Chat message test',
                dateCreated: new Date(),
                owner: user,
                recipient: user2,
                ownerStatus: MessageStatus.SEEN,
                recipientStatus: MessageStatus.UNSEEN
        ).save()
    }

    void testGetByUser(){
        params.token = 'token'
        params.recipientId = 2
        controller.getByUser()
        assert response.getJson().size() > 0
        assert response.json?.message.contains('Chat message test')
        assert response.json?.owner.id.contains(1)
    }

    void testGetByUserNOTUSER(){
        params.token = 'token123'
        params.recipientId = 2
        controller.getByUser()
        assert response.getJson().size() == 0
    }

    void testCreate(){
        params.token = 'token'
        params.recipient = 3
        params.chat = "Create Chat Message Test"
        controller.create()
        assert response.text == '{"response":"chat_created"}'
        assert Chat.findByOwnerAndRecipient(user,user3).message == params.chat
    }

    void testCreateFail(){
        params.token = 'token'
        //params.recipient = 3
        params.chat = "Create Chat Message Test"
        controller.create()
        assert response.text == '{"response":"chat_not_created"}'
    }

    void testUpdate(){
        params.token = 'token1'
        params.id = 1
        controller.update()
        assert response.json?.response == "chat_updated"
        assert chat.recipientStatus == MessageStatus.SEEN
    }

    void testDelete(){
        params.token = 'token1'
        params.id = 1
        controller.delete()
        assert response.json?.response == "chat_deleted"
        assert chat.recipientStatus == MessageStatus.DELETED

    }
}
