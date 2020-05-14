package com.example.a3461chatmessage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.example.a3461chatmessage.LatestMessagesActivity
import com.example.a3461chatmessage.R
import com.example.a3461chatmessage.User
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

  companion object {
    val TAG = "RegisterActivity"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    button_register.setOnClickListener {
      performRegister()
    }

    already_have_account.setOnClickListener {
      Log.d(TAG, "Try to show login activity")

      // launch the login activity somehow
      val intent = Intent(this, LoginActivity::class.java)
      startActivity(intent)
    }

    selectphoto.setOnClickListener {
      Log.d(TAG, "Try to show photo selector")

      val intent = Intent(Intent.ACTION_PICK)
      intent.type = "image/*"
      startActivityForResult(intent, 0)
    }
  }

  var selectedPhotoUri: Uri? = null

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
      // proceed and check what the selected image was....
      Log.d(TAG, "Photo was selected")

      selectedPhotoUri = data.data

      val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

      round_photo.setImageBitmap(bitmap)

      selectphoto.alpha = 0f

//      val bitmapDrawable = BitmapDrawable(bitmap)
//      selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
    }
  }

  private fun performRegister() {
    val email = email_register.text.toString()
    val password = password_register.text.toString()

    if (email.isEmpty() || password.isEmpty()) {
      Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
      return
    }

    Log.d(TAG, "Attempting to create user with email: $email")

    // Firebase Authentication to create a user with email and password
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener {
          if (!it.isSuccessful) return@addOnCompleteListener

          // else if successful
          //Log.d(TAG, "Successfully created user with uid: ${it.result.user.uid}")

          uploadImageToFirebaseStorage()
        }
        .addOnFailureListener{
          Log.d(TAG, "Failed to create user: ${it.message}")
          Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
        }
  }

  private fun uploadImageToFirebaseStorage() {
    if (selectedPhotoUri == null) return

    val filename = UUID.randomUUID().toString()
    val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

    ref.putFile(selectedPhotoUri!!)
        .addOnSuccessListener {
          Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

          ref.downloadUrl.addOnSuccessListener {
            Log.d(TAG, "File Location: $it")

            saveUserToFirebaseDatabase(it.toString())
          }
        }
        .addOnFailureListener {
          Log.d(TAG, "Failed to upload image to storage: ${it.message}")
        }
  }

  private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
    val uid = FirebaseAuth.getInstance().uid ?: ""
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

    val user = User(uid, username_register.text.toString(), profileImageUrl)

    ref.setValue(user)
        .addOnSuccessListener {
          Log.d(TAG, "Finally we saved the user to Firebase Database")

          val intent = Intent(this, LatestMessagesActivity::class.java)
          intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
          startActivity(intent)

        }
        .addOnFailureListener {
          Log.d(TAG, "Failed to set value to database: ${it.message}")
        }
  }

}