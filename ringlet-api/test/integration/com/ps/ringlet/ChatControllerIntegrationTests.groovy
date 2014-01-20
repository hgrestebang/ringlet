package com.ps.ringlet

import static org.junit.Assert.*
import org.junit.*

class ChatControllerIntegrationTests {
    def contChat,token

    @Before
    void setUp() {
        // Setup logic here
        contChat = new ChatController()
        token = User.findByUsername('admin@ringlet.me').token?.token

        if(!Chat.findAll()){
            new Chat(
                    message: 'Chat message test',
                    dateCreated: new Date(),
                    owner: User.findByUsername('admin@ringlet.me'),
                    recipient: User.findByUsername('user1@ringlet.me'),
                    ownerStatus: MessageStatus.SEEN,
                    recipientStatus: MessageStatus.UNSEEN
            ).save()
        }
    }

    @After
    void tearDown() {
        // Tear down logic here
        Chat chatR = Chat.findById(1L)
        chatR?.setOwnerStatus(MessageStatus.SEEN)
        chatR?.setRecipientStatus(MessageStatus.UNSEEN)
        chatR?.save(flush: true)
    }


    @Test
    void testCreate(){
        contChat.params.token = token
        contChat.params.recipient = 3
        contChat.params.chat = "Test for chat"
        contChat.create()
        assert contChat.response.text == '{"response":"chat_created"}'
        assert Chat.findByOwnerAndRecipient(User.findByToken(UserToken.findByToken(token)),User.findById(contChat.params.recipient as long)).message == contChat.params.chat
    }

    @Test
    void testUpdate(){
        def recipientToken = User.findById(2 as long).token.token
        contChat.params.token = recipientToken
        Chat chatT = Chat.findByRecipientAndRecipientStatus(User.findById(2 as long),MessageStatus.UNSEEN)
        contChat.params.id = chatT.id
        contChat.update()
        assert contChat.response.text == '{"response":"chat_updated"}'
        assert Chat.findById(chatT.id as long).recipientStatus == MessageStatus.SEEN

    }

    @Test
    void testGetAll(){
        contChat.params.token = token
        contChat.getAll()
        assert contChat.response.getJson().size() >= 0
        assert contChat.response.json.id.size() > 0
    }

    @Test
    void testGetByUser(){
        contChat.params.token = token
        contChat.params.recipientId = 2
        contChat.getByUser()
        assert contChat.response.json.size() >= 0
        assert contChat.response.json?.message.contains('Chat message test')
    }

    @Test
    void testDelete(){
        contChat.params.token = token
        contChat.params.id = 1 //chat id
        contChat.delete()
        assert contChat.response.json.response == 'chat_deleted'
        if ((Chat.findById(1L).ownerStatus == MessageStatus.DELETED)||(Chat.findById(1L).recipientStatus == MessageStatus.DELETED)){
            assert true
        }else{
            assert  false
        }
    }
}
