package com.hienthai.tungkinhonline

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hienthai.tungkinhonline.databinding.ActivityRankBinding

class RankActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRankBinding
    private val adapter by lazy { RankAdapter() }
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private val listUser = arrayListOf<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        binding.run {
            rcvRank.layoutManager = LinearLayoutManager(this@RankActivity)
            rcvRank.addItemDecoration(
                DividerItemDecoration(
                    this@RankActivity,
                    LinearLayoutManager.VERTICAL
                )
            )

            rcvRank.adapter = adapter
        }

        loadListUsers()
    }

    private fun loadListUsers() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        listUser.add(user ?: User())
                    }
                    adapter.submitList(listUser.sortedByDescending { it.count })
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(
                    this@RankActivity,
                    "Database Error: ${p0.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }
}