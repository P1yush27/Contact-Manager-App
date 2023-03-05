package com.example.contactmanagerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.contactmanagerapp.databinding.ActivitySignInBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var binding : ActivitySignInBinding

    // Creating companion object to pass username to the next Screen i.e. ContactActivity
    companion object{
        const val KEY1 = "SignInActivity.username"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // What happens on clicking SignIn button
        binding.btnSignIn.setOnClickListener{

            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPass.text.toString().trim()

            // Checking if all fields are filled
            if(username.isNotEmpty() && password.isNotEmpty()){
                signIn(username, password)
            }else if(username.isEmpty()){
                Toast.makeText(this, "Please enter Username", Toast.LENGTH_SHORT).show()
            }else if(password.isEmpty()){
                Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show()
            }
        }

        // If user wants to create another account, clicking this text view redirects user to the Sign Up page
        binding.tvSignUp.setOnClickListener {
            val signUpIntent = Intent(this, SignUpActivity :: class.java)
            startActivity(signUpIntent)
        }
    }

    private fun signIn(username: String, password: String) {
        // Going till the 'Users' Path in the DB
        database = FirebaseDatabase.getInstance().getReference("Users")
        // Checking if the username exists in DB or not
        database.child(username).get().addOnSuccessListener {
            if(it.exists()){
                // If username exists validating password
                val actualPassword = it.child("password").value.toString()
                if(password == actualPassword){
                    // If correct password entered redirecting to ContactActivity with username data
                    val contactIntent = Intent(this, ContactActivity ::class.java)
                    contactIntent.putExtra(KEY1, username)
                    startActivity(contactIntent)
                }else{
                    // If incorrect password showing Toast and clearing password field
                    Toast.makeText(this, "Password is incorrect", Toast.LENGTH_SHORT).show()
                    binding.etPass.text?.clear()
                }
            }else{
                // If username isn't found then showing Toast
                Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Servers are down. Please try again later", Toast.LENGTH_SHORT).show()
        }
    }
}