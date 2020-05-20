package com.example.a3461chatmessage.registerlogin

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
import com.example.a3461chatmessage.LoginActivity
import com.example.a3461chatmessage.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

  companion object {
    const val TAG = "RegisterActivity"
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

      //set intent to knows to select any image type
      intent.type = "image/*"

      // pass intent to show up as a photo selector in the phone
      startActivityForResult(intent, 0)
    }
  }

  // initialize Global variable
  var selectedPhotoUri: Uri? = null

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
      // proceed and check what the selected image was
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
    //takes id email_register" text and converts to a String and sets to value email
    val email = email_register.text.toString()
    //takes id password_register" text and converts to a String and sets to value password
    val password = password_register.text.toString()

    //error check if any text field are empty
    if (email.isEmpty() || password.isEmpty()) {
      Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
      return //no need to return
    }

    Log.d(TAG, "Attempting to create user with email: $email")

    // Firebase Authentication to create a user with string email and string password
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener {
          //the "it" is the Task<authResult> and it gives you the result
          if (!it.isSuccessful) return@addOnCompleteListener

          // else if successful
          Log.d(TAG, "Successfully created user with uid: ${it.result?.user?.uid}")

          //call function
          uploadImageToFirebaseStorage()
        }

      //error checking to ensure email is in proper format
        .addOnFailureListener{
          Log.d(TAG, "Failed to create user: ${it.message}")
          Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
        }
  }


  private fun uploadImageToFirebaseStorage() {
    // error check
    if (selectedPhotoUri == null) return

    // generates a random string
    val filename = UUID.randomUUID().toString()

    // images in phone to Firebase Storage images directory saved as filename (random string)
    val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

    // putFile takes in a Uri. "!!" is needed to convert from Uri? to Uri
    ref.putFile(selectedPhotoUri!!)
        .addOnSuccessListener {
          Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

          ref.downloadUrl.addOnSuccessListener {
            //access to the file location
            Log.d(TAG, "File Location: $it")

            // call function
            saveUserToFirebaseDatabase(it.toString())
          }
        }
        .addOnFailureListener {
          Log.d(TAG, "Failed to upload image to storage: ${it.message}")
        }
  }

  private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
    // get the string uid
    val uid = FirebaseAuth.getInstance().uid ?: ""

    // In Firebase Database under users node, get the uid
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

    val user = User(uid, username_register.text.toString(), profileImageUrl)

    ref.setValue(user)
        .addOnSuccessListener {
          Log.d(TAG, "Finally we saved the user to Firebase Database")

          // Launch into after login to see all the latest messages page
          val intent = Intent(this, LatestMessagesActivity::class.java)
          // so in latest msg page, when click back in phone then it would go to
          // phone home screen (outside of app). w/o the line below back in phone is register page
          intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
          startActivity(intent)

        }
        .addOnFailureListener {
          Log.d(TAG, "Failed to set value to database: ${it.message}")
        }
  }

  // User object
  class User(val uid: String, val username: String, val profileImageUrl: String){
    // Prevent a 'no-user argument' error
    constructor(): this("","","")
  }

}
