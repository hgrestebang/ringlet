package com.ps.ringlet



import org.junit.*
import grails.test.mixin.*

@TestFor(PurchaseController)
@Mock([User,UserService,UserToken,Purchase])
class PurchaseControllerTests {
    def user,user2,token,invitation

    void setUp() {
        mockForConstraintsTests(User)
        mockForConstraintsTests(UserToken)
        mockForConstraintsTests(UserService)
        mockForConstraintsTests(Purchase)

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
    }

    void testMakePurchase(){
        params.token = 'token123'
        params.transaction = 'Purchase test'
        params.amount = '95'
        params.itemId = '12345'
        controller.makePurchase()
        assertEquals(response.json.response,'user_updated')
    }

    void testCalculateExpiration(){
        assert controller.calculateExpiration() != null
    }
}
