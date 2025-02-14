package com.example.logincommunityapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.logincommunityapp.databinding.ActivityCreatePostBinding
import com.google.firebase.firestore.FirebaseFirestore

class CreatePostActivity : AppCompatActivity() {

    private val binding by lazy { ActivityCreatePostBinding.inflate(layoutInflater) }
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnAddPost.setOnClickListener {
            val profileImage = R.drawable.ic_launcher_background
            val idText = binding.etPostTitle.text.toString()
            val content = binding.etPostContent.text.toString()
            val heartCount = "0"
            val answerCount = "0"

            val item = Item(profileImage, idText, content, heartCount, answerCount)
            postUpload(item)
        }
    }

    private fun postUpload(item: Item) {
        val postData = db.collection("posts").document(item.idText)
        postData.set(item)
            .addOnSuccessListener {
                Toast.makeText(this, "게시글 업로드 완료!", Toast.LENGTH_SHORT).show()
                val intent = Intent()
                intent.putExtra("postData", item)
                setResult(RESULT_OK, intent)
                finish()
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "에러 발생! : $error", Toast.LENGTH_SHORT).show()
            }
    }
}