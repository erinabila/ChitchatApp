package com.example.a3461chatmessage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.a3461chatmessage.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log_activitiy.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.profile_activity.*
import kotlinx.android.synthetic.main.profile_activity.view.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class ProfileActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Profile Activity", "reached here!")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)


    }

//    private fun fetchUsers() {
//        val ref = FirebaseDatabase.getInstance().getReference("/users")
//        val current_user = FirebaseAuth.getInstance().currentUser
//
//
//        ref.addListenerForSingleValueEvent(object: ValueEventListener {
//
//            override fun onDataChange(p0: DataSnapshot) {
//                val adapter = GroupAdapter<ViewHolder>()
//
//                p0.children.forEach {
//                    val user = it.getValue(User::class.java)
//
//                }
//
//                adapter.setOnItemClickListener { item, view ->
//
//                    val userItem = item as UserItem
//
//                    finish()
//
//                }
//
//                recylerview_newmessage.adapter = adapter
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//    }
//
//    class UserItem(val user: User) : Item<ViewHolder>() {
//        override fun bind(viewHolder: ViewHolder, position: Int) {
//            viewHolder.itemView.username_textview_new_message.text = user.username
//
//            Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.profile_photo)
//        }
//
//        override fun getLayout(): Int {
//            return R.layout.user_row_new_message
//        }
//    }
}