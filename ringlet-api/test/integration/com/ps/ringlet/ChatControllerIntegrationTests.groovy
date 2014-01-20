package com.ps.ringlet

import static org.junit.Assert.*
import org.junit.*

class ChatControllerIntegrationTests {
    def contChat
    def token = '3d231daf-1d95-4991-a2df-b390e1deb75e'

    @Before
    void setUp() {
        // Setup logic here
        contChat = new ChatController()
    }

    @After
    void tearDown() {
        // Tear down logic here
    }


    @Test
    void testCreate(){
        contChat.params.token = token
        contChat.params.recipient = 2
        contChat.params.chat = "Test for chat"
        contChat.create()
        assert contChat.response.text == '{"response":"chat_created"}'
        assert Chat.findByOwnerAndRecipient(User.findByToken(UserToken.findByToken(token)),User.findById(2 as long)).message == contChat.params.chat
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
        assert Chat.fin
    }
}
