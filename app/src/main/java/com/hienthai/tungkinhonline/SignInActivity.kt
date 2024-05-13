package com.hienthai.tungkinhonline

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hienthai.tungkinhonline.databinding.ActivitySignInBinding
import org.koin.android.ext.android.inject
import java.util.Locale

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val prefs: AppPrefs by inject()
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignIn.setSafeClickListener {
            val username = binding.edtUsername.text.toString()
            val password = binding.edtPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                binding.pbLoading.isInvisible = false
                databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (userSnapshot in dataSnapshot.children) {
                                val user = userSnapshot.getValue(User::class.java)
                                if (user != null && user.password == password) {
                                    binding.pbLoading.isInvisible = true
                                    prefs.remember = binding.cbRemember.isChecked
                                    prefs.id = user.id ?: ""
                                    startActivity(Intent(this@SignInActivity, MainActivity::class.java).apply {
                                        putExtra("USER_ID", user.id)
                                    })
                                    finish()
                                    return
                                }
                            }
                        }
                        binding.pbLoading.isInvisible = true
                        Toast.makeText(this@SignInActivity, getString(R.string.text_username_or_password_incorrect), Toast.LENGTH_SHORT).show()
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        binding.pbLoading.isInvisible = true
                        Toast.makeText(
                            this@SignInActivity,
                            "Database Error: ${p0.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })

            } else {
                Toast.makeText(this, getString(R.string.text_cannot_be_left_blank), Toast.LENGTH_SHORT).show()
            }
        }

        binding.root.setOnClickListener {
            hideKeyboard()
        }
    }

    override fun onStart() {
        super.onStart()

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.hasChild(prefs.id)) {
                    return
                } else {
                    if (prefs.remember) {
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java).apply {
                            putExtra("USER_ID", prefs.id)
                        })
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}