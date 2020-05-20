package com.example.a3461chatmessage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.a3461chatmessage.messages.ChatLogActivity
import com.example.a3461chatmessage.messages.NewMessageActivity
import com.example.a3461chatmessage.models.User
import com.google.firebase.auth.FirebaseAuth
import com.example.a3461chatmessage.registerlogin.RegisterActivity
import com.example.a3461chatmessage.registerlogin.MyProfileActivity
import com.example.a3461chatmessage.views.LatestMessageRow
import com.google.firebase.database.*
import com.letsbuildthatapp.kotlinmessenger.models.ChatMessage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*

class LatestMessagesActivity : AppCompatActivity() {

  companion object {
    var currentUser: User? = null
    val TAG = "LatestMessages"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_latest_messages)

    recycler_view_latest.adapter = adapter
    recycler_view_latest.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL)) //Set line between users in chatlog

    //set item click listner on your adapter
    adapter.setOnItemClickListener { item, view ->
      val intent = Intent(this, ChatLogActivity::class.java)

      val row = item as LatestMessageRow
      intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
      startActivity(intent)
    }
//    setupDummyRows()
    listenForLatestMessages()

    fetchCurrentUser()

    verifyUserIsLoggedIn()
  }


  val latestMessagesMap = HashMap<String, ChatMessage>()

  private fun refreshRecyclerViewMessages() {
    adapter.clear()
    latestMessagesMap.values.forEach {
      adapter.add(LatestMessageRow(it))

    }
  }

  private fun listenForLatestMessages() {
    val fromId = FirebaseAuth.getInstance().uid
    val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
    ref.addChildEventListener(object: ChildEventListener {
      override fun onChildAdded(p0: DataSnapshot, p1: String?) {
        val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
        latestMessagesMap[p0.key!!] = chatMessage
        refreshRecyclerViewMessages()
      }

      override fun onChildChanged(p0: DataSnapshot, p1: String?) {
        val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
        latestMessagesMap[p0.key!!] = chatMessage
        refreshRecyclerViewMessages()
      }

      override fun onChildMoved(p0: DataSnapshot, p1: String?) {

      }
      override fun onChildRemoved(p0: DataSnapshot) {

      }
      override fun onCancelled(p0: DatabaseError) {

      }
    })
  }

  val adapter = GroupAdapter<ViewHolder>()


  private fun fetchCurrentUser() {
    val uid = FirebaseAuth.getInstance().uid
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
    ref.addListenerForSingleValueEvent(object: ValueEventListener {

      override fun onDataChange(p0: DataSnapshot) {
        currentUser = p0.getValue(User::class.java)
        Log.d("LatestMessages", "Current user ${currentUser?.profileImageUrl}")
      }

      override fun onCancelled(p0: DatabaseError) {

      }
    })
  }

  //check users has successfully logged in
  private fun verifyUserIsLoggedIn() {
    val uid = FirebaseAuth.getInstance().uid
    if (uid == null) {
      val intent = Intent(this, RegisterActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
      startActivity(intent)
    }
  }

  // when click on menu it launches this function
  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    // handles each of the menu options separately
    // treat the "when" as a switch case
    when (item?.itemId) {
      R.id.menu_new_message -> {
        val intent = Intent(this, NewMessageActivity::class.java)
        //launch intent to NewMessageActivity page
        startActivity(intent)
      }

      R.id.menu_profile -> {
        val intent = Intent(this, MyProfileActivity::class.java)
        //launch intent to MyProfileActivity page
        startActivity(intent)
      }

      R.id.menu_sign_out -> {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        //launch intent to RegisterActivity page
        startActivity(intent)
      }
    }

    return super.onOptionsItemSelected(item)
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {//Shows new message + log out
    menuInflater.inflate(R.menu.nav_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

}
