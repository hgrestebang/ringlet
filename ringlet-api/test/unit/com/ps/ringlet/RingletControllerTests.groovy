package com.ps.ringlet



import org.junit.*
import grails.test.mixin.*

@TestFor(RingletController)
@Mock(Ringlet)
class RingletControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/ringlet/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.ringletInstanceList.size() == 0
        assert model.ringletInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.ringletInstance != null
    }

    void testSave() {
        controller.save()

        assert model.ringletInstance != null
        assert view == '/ringlet/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/ringlet/show/1'
        assert controller.flash.message != null
        assert Ringlet.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/ringlet/list'

        populateValidParams(params)
        def ringlet = new Ringlet(params)

        assert ringlet.save() != null

        params.id = ringlet.id

        def model = controller.show()

        assert model.ringletInstance == ringlet
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/ringlet/list'

        populateValidParams(params)
        def ringlet = new Ringlet(params)

        assert ringlet.save() != null

        params.id = ringlet.id

        def model = controller.edit()

        assert model.ringletInstance == ringlet
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/ringlet/list'

        response.reset()

        populateValidParams(params)
        def ringlet = new Ringlet(params)

        assert ringlet.save() != null

        // test invalid parameters in update
        params.id = ringlet.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/ringlet/edit"
        assert model.ringletInstance != null

        ringlet.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/ringlet/show/$ringlet.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        ringlet.clearErrors()

        populateValidParams(params)
        params.id = ringlet.id
        params.version = -1
        controller.update()

        assert view == "/ringlet/edit"
        assert model.ringletInstance != null
        assert model.ringletInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/ringlet/list'

        response.reset()

        populateValidParams(params)
        def ringlet = new Ringlet(params)

        assert ringlet.save() != null
        assert Ringlet.count() == 1

        params.id = ringlet.id

        controller.delete()

        assert Ringlet.count() == 0
        assert Ringlet.get(ringlet.id) == null
        assert response.redirectedUrl == '/ringlet/list'
    }
}
