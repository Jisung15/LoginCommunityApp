package com.example.logincommunityapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.logincommunityapp.databinding.ActivityHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class HomeActivity : AppCompatActivity() {

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private val itemList = mutableListOf<Item>()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: ItemListAdapter

    private val postData = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val post = result.data?.getParcelableExtra<Item>("postData")
            post?.let {
                itemList.add(it)
                adapter.submitList(itemList.toList())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        adapter = ItemListAdapter(itemList) { item ->
            deletePost(item)
        }

        binding.recyclerView.adapter = adapter

        binding.floatingButton.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            postData.launch(intent)
        }

        postUpload()
    }

    private fun postUpload() {
        db.collection("post")
            .get()
            .addOnSuccessListener { result ->
                itemList.clear()
                for (document in result) {
                    val post = document.toObject(Item::class.java)
                    itemList.add(post)
                }
                adapter.submitList(itemList.toList())
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "에러 발생! : $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deletePost(item: Item) {
        db.collection("posts")
            .document(item.idText)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                itemList.remove(item)
                adapter.submitList(itemList.toList())
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "에러 발생! : $exception", Toast.LENGTH_SHORT).show()
            }
    }
}