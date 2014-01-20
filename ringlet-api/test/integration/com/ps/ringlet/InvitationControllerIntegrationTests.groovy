package com.ps.ringlet

import static org.junit.Assert.*
import org.junit.*

class InvitationControllerIntegrationTests {
    def contInvitation,token,tokenU2,tokenU3

    @Before
    void setUp() {
        // Setup logic here
        contInvitation = new InvitationController()
        token = User.findByUsername('admin@ringlet.me').token?.token
        tokenU2 = User.findByUsername('user1@ringlet.me').token?.token
        tokenU3 = User.findByUsername('user2@ringlet.me').token?.token

        if(!Invitation.findAll()){
           new Invitation(
                    message: 'Invitation Test 1',
                    dateCreated: new Date(),
                    ownerStatus: MessageStatus.SEEN,
                    recipientStatus: MessageStatus.UNSEEN,
                    owner: User.findByUsername('admin@ringlet.me'),
                    recipient: User.findByUsername('user1@ringlet.me')
            ).save()
        }

    }

    @After
    void tearDown() {
        // Tear down logic here
        Invitation invitationR = Invitation.findById(1L)
        invitationR?.setOwnerStatus(MessageStatus.SEEN)
        invitationR?.setRecipientStatus(MessageStatus.UNSEEN)
        invitationR?.save(flush: true)
    }

    @Test
    void testCreate() {
        Invitation RemoveInv = Invitation.findByOwnerAndRecipient(User.findByToken(UserToken.findByToken(token)),User.findById(4 as long))
        if(RemoveInv != null){
            RemoveInv.delete()
            RemoveInv.save(flush: true)
        }
        contInvitation.params.token = token
        def invitationT = [message: "Invitation Create Test",recipientId: 4]
        contInvitation.params.invitation = invitationT
        contInvitation.create()
        assert contInvitation.response.text == '{"response":"invitation_created"}'
        assert Invitation.findByOwnerAndRecipient(User.findByToken(UserToken.findByToken(token)),User.findById(invitationT.recipientId as long)).message == invitationT.message
    }

    @Test
    void testCreateFail() {
        contInvitation.params.token = token
        def invitationT = [message: "Invitation Create Test",recipientId: 2]
        contInvitation.params.invitation = invitationT
        contInvitation.create()
        assert contInvitation.response.text == '{"response":"invitation_not_created"}'
//        assert Invitation.findByOwnerAndRecipient(User.findByToken(UserToken.findByToken(token)),User.findById(invitationT.recipientId as long)).message == invitationT.message
        assert Invitation.findByOwnerAndRecipient(User.findByToken(UserToken.findByToken(token)),User.findById(invitationT.recipientId as long))
    }

    @Test
    void testGetByUser(){
        contInvitation.params.token = tokenU2
        contInvitation.getByUser()
        assert contInvitation.response.json.id.size() >= 0
        if(contInvitation.response.json.id.size() > 0){
            assert contInvitation.response.json[0]?.recipientStatus == "UNSEEN"
        }
    }

    @Test
    void testAcceptInvitation(){
        contInvitation.params.token = tokenU2 // recipient
        contInvitation.params.id = 1 //Invitation Id
        contInvitation.acceptInvitation()
        assert contInvitation.response.text == '{"response":"invitation_accepted"}'
        assert Invitation.findById(contInvitation.params.id as long).recipientStatus == MessageStatus.ACCEPTED
        assert User.findByToken(UserToken.findByToken(tokenU2)).friends?.contains(Invitation.findById(contInvitation.params.id as long).ownerId)
    }

    @Test
    void testDeclineInvitation(){
        contInvitation.params.token = tokenU2 // recipient
        contInvitation.params.id = 1 //Invitation Id
        contInvitation.declineInvitation()
        assert contInvitation.response.text == '{"response":"invitation_declined"}'
        Invitation invt = Invitation.findById(contInvitation.params.id as long)
        assert invt.recipientStatus == MessageStatus.DELETED
//        assertNull(User.findByToken(UserToken.findByToken(tokenU2)).friends?.contains(invt.ownerId))
    }

    @Test
    void testDelete(){
        contInvitation.params.token = token
        contInvitation.params.id = 1
        contInvitation.delete()
        assert contInvitation.response.text == '{"response":"invitation_deleted"}'
        assert Invitation.findById(contInvitation.params.id as long).ownerStatus == MessageStatus.DELETED
    }
}
