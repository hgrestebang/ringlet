package com.ps.ringlet



import org.junit.*
import grails.test.mixin.*

@TestFor(ChatController)
@Mock(Chat)
class ChatControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/chat/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.chatInstanceList.size() == 0
        assert model.chatInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.chatInstance != null
    }

    void testSave() {
        controller.save()

        assert model.chatInstance != null
        assert view == '/chat/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/chat/show/1'
        assert controller.flash.message != null
        assert Chat.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/chat/list'

        populateValidParams(params)
        def chat = new Chat(params)

        assert chat.save() != null

        params.id = chat.id

        def model = controller.show()

        assert model.chatInstance == chat
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/chat/list'

        populateValidParams(params)
        def chat = new Chat(params)

        assert chat.save() != null

        params.id = chat.id

        def model = controller.edit()

        assert model.chatInstance == chat
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/chat/list'

        response.reset()

        populateValidParams(params)
        def chat = new Chat(params)

        assert chat.save() != null

        // test invalid parameters in update
        params.id = chat.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/chat/edit"
        assert model.chatInstance != null

        chat.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/chat/show/$chat.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        chat.clearErrors()

        populateValidParams(params)
        params.id = chat.id
        params.version = -1
        controller.update()

        assert view == "/chat/edit"
        assert model.chatInstance != null
        assert model.chatInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/chat/list'

        response.reset()

        populateValidParams(params)
        def chat = new Chat(params)

        assert chat.save() != null
        assert Chat.count() == 1

        params.id = chat.id

        controller.delete()

        assert Chat.count() == 0
        assert Chat.get(chat.id) == null
        assert response.redirectedUrl == '/chat/list'
    }
}
