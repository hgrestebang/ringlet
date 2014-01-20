package com.ps.ringlet

import static org.junit.Assert.*
import org.junit.*

class PurchaseControllerIntegrationTests {
    def tokenU2, contPurchase

    @Before
    void setUp() {
        // Setup logic here
        contPurchase = new PurchaseController()
        tokenU2 = User.findByUsername('user1@ringlet.me').token?.token
    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void testMakePurchase() {
        contPurchase.params.token = tokenU2
        contPurchase.params.itemId = 2L
        contPurchase.params.transaction = 'Purchase test'
        contPurchase.params.amount = '5'
        contPurchase.makePurchase()
        assertEquals(contPurchase.response.json.response,'user_updated')
        assert Purchase.findByOwner(User.findByUsername('user1@ringlet.me')).transaction == 'Purchase test'
    }
}
