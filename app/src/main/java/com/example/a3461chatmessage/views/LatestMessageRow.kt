package com.example.a3461chatmessage.views

import com.example.a3461chatmessage.R
import com.example.a3461chatmessage.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.letsbuildthatapp.kotlinmessenger.models.ChatMessage
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>() { //Responsible for current user you are talking to
    var chatPartnerUser: User?= null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.message_textview_latest_message.text = chatMessage.text

        val chatPartnerId: String
        if( chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.username_textview_latest_message.text = chatPartnerUser?.username

                val targetImageView = viewHolder.itemView.imageview_latest_message
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }


    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}