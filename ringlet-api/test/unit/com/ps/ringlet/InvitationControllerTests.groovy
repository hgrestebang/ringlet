package com.ps.ringlet



import org.junit.*
import grails.test.mixin.*

@TestFor(InvitationController)
@Mock(Invitation)
class InvitationControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/invitation/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.invitationInstanceList.size() == 0
        assert model.invitationInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.invitationInstance != null
    }

    void testSave() {
        controller.save()

        assert model.invitationInstance != null
        assert view == '/invitation/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/invitation/show/1'
        assert controller.flash.message != null
        assert Invitation.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/invitation/list'

        populateValidParams(params)
        def invitation = new Invitation(params)

        assert invitation.save() != null

        params.id = invitation.id

        def model = controller.show()

        assert model.invitationInstance == invitation
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/invitation/list'

        populateValidParams(params)
        def invitation = new Invitation(params)

        assert invitation.save() != null

        params.id = invitation.id

        def model = controller.edit()

        assert model.invitationInstance == invitation
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/invitation/list'

        response.reset()

        populateValidParams(params)
        def invitation = new Invitation(params)

        assert invitation.save() != null

        // test invalid parameters in update
        params.id = invitation.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/invitation/edit"
        assert model.invitationInstance != null

        invitation.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/invitation/show/$invitation.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        invitation.clearErrors()

        populateValidParams(params)
        params.id = invitation.id
        params.version = -1
        controller.update()

        assert view == "/invitation/edit"
        assert model.invitationInstance != null
        assert model.invitationInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/invitation/list'

        response.reset()

        populateValidParams(params)
        def invitation = new Invitation(params)

        assert invitation.save() != null
        assert Invitation.count() == 1

        params.id = invitation.id

        controller.delete()

        assert Invitation.count() == 0
        assert Invitation.get(invitation.id) == null
        assert response.redirectedUrl == '/invitation/list'
    }
}
