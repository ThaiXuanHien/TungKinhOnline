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
import com.hienthai.tungkinhonline.databinding.ActivitySignupBinding
import org.koin.android.ext.android.inject
import java.util.Date

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val prefs: AppPrefs by inject()
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        binding.btnSignUp.setSafeClickListener {
            val username = binding.edtUsername.text.toString()
            val password = binding.edtPassword.text.toString()
            val rePassword = binding.edtRePassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty() && rePassword.isNotEmpty()) {
                if (password == rePassword) {
                    binding.pbLoading.isInvisible = false
                    databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                binding.pbLoading.isInvisible = true
                                val id = databaseReference.push().key
                                val user = User(
                                    id = id,
                                    username = username,
                                    password = password,
                                    count = 0,
                                    registerTime = DateUtil.convertDateToTime(
                                        Date(),
                                        TimeFormat.YYYYMMDD
                                    )
                                )

                                databaseReference.child(id ?: "").setValue(user)
                                prefs.id = id ?: ""
                                val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                binding.pbLoading.isInvisible = true
                                Toast.makeText(this@SignUpActivity, getString(R.string.text_user_exists), Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                            binding.pbLoading.isInvisible = true
                            Toast.makeText(
                                this@SignUpActivity,
                                "Database Error: ${p0.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        
                    })

                } else {
                    Toast.makeText(this, getString(R.string.text_password_do_not_match), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.text_cannot_be_left_blank), Toast.LENGTH_SHORT).show()
            }
        }

        binding.root.setOnClickListener {
            hideKeyboard()
        }

    }
}