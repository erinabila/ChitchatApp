package com.example.a3461chatmessage.messages

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.example.a3461chatmessage.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.a3461chatmessage.models.User
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*



class NewMessageActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_new_message)

    // Sets header title in this page
    supportActionBar?.title = "Select User"

    fetchUsers()
  }

  companion object {
    const val USER_KEY = "USER_KEY"
  }

  private fun fetchUsers() {
    val ref = FirebaseDatabase.getInstance().getReference("/users")
    val currentuser = FirebaseAuth.getInstance().currentUser

    ref.addListenerForSingleValueEvent(object: ValueEventListener {

      override fun onDataChange(p0: DataSnapshot) {

        val adapter = GroupAdapter<ViewHolder>()

        p0.children.forEach {
          Log.d("NewMessage", it.toString())
          //get value of user object in User class from RegisterActivity
          val user = it.getValue(User::class.java)
          if (user != null && user.uid != currentuser!!.uid) { //It filters the current user
            //add objects to adapter
            adapter.add(UserItem(user))
          }
        }

        adapter.setOnItemClickListener { item, view ->
          val userItem = item as UserItem
          val intent = Intent(view.context, ChatLogActivity::class.java)
          //pass User object when opening ChatLogActivity to msg the selected user
          intent.putExtra(USER_KEY, userItem.user)
          startActivity(intent)
          finish()
        }
        recylerview_newmessage.adapter = adapter
      }
      override fun onCancelled(p0: DatabaseError) {
      }
    })
  }
}

class UserItem(val user: User): Item<ViewHolder>() {
  override fun bind(viewHolder: ViewHolder, position: Int) {
    //will be called in our list for each user object later on
    viewHolder.itemView.username_textview_new_message.text = user.username

    Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageview_new_message)
  }

  override fun getLayout(): Int {
    return R.layout.user_row_new_message
  }
}
