package com.example.a3461chatmessage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.example.a3461chatmessage.R
import com.example.a3461chatmessage.LatestMessagesActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity: AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_login)
    Toast.makeText(this, "Firebase connection success", Toast.LENGTH_SHORT).show() //Test if its connected to the firebase

    button_login.setOnClickListener {
      performLogin()
    }

    back_to_registration.setOnClickListener{
      finish()
    }
  }

  private fun performLogin() {
    val email = email_login.text.toString()
    val password = password_login.text.toString()

    if (email.isEmpty() || password.isEmpty()) {
      Toast.makeText(this, "Please fill out email/pw.", Toast.LENGTH_SHORT).show()
      return
    }

    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener {
          if (!it.isSuccessful) return@addOnCompleteListener

         // Log.d("Login", "Successfully logged in: ${it.result.user.uid}")

          val intent = Intent(this, LatestMessagesActivity::class.java)
          intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
          startActivity(intent)
        }
        .addOnFailureListener {
          Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
        }
  }

}