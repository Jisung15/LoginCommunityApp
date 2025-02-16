package com.example.logincommunityapp

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.logincommunityapp.databinding.ActivityCreatePostBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CreatePostActivity : AppCompatActivity() {

    private val binding by lazy { ActivityCreatePostBinding.inflate(layoutInflater) }
    private val db = FirebaseFirestore.getInstance()
    private val data = "postData"
    private val post = "POST"

    // 이미지 관련 registerForActivityResult
    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.ivProfileImage.setImageURI(it)
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

        // 이미지 뷰 클릭 시 갤러리에서 사진 가져오기
        binding.ivProfileImage.setOnClickListener {
            getImage.launch("image/*")
        }

        // 그렇게 해서 가져온 사진과 게시글 작성 내용을 올리기
        binding.btnAddPost.setOnClickListener {
            val idText = binding.etPostTitle.text.toString()
            val content = binding.etPostContent.text.toString()
            val heartCount = "0"
            val answerCount = "0"
            val profileImageUri = binding.ivProfileImage.drawable.toBitmap().let {
                saveImageToStorage(it)
            }

            val item = Item(profileImageUri, idText, content, heartCount, answerCount)
            postUpload(item)
        }
    }

    // 게시글 업로드하는 메소드
    private fun postUpload(item: Item) {
        val postData = db.collection(post).document(item.idText)
        postData.set(item)
            .addOnSuccessListener {
                Toast.makeText(this, R.string.post_upload_success_message, Toast.LENGTH_SHORT).show()
                val intent = Intent()
                intent.putExtra(data, item)
                setResult(RESULT_OK, intent)
                finish()
            }
            .addOnFailureListener { _ ->
                Toast.makeText(this, R.string.post_upload_fail_message, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
    }

    // 이것도 잘 모르겠다
    private fun saveImageToStorage(bitmap: Bitmap): String {
        val imageUri = saveToInternalStorage(bitmap)
        return imageUri.toString()
    }

    // 이거는 ChatGpt 물어 보고 넣은 건데 잘 모르겠다
    private fun saveToInternalStorage(bitmap: Bitmap): Uri {
        val contextWrapper = ContextWrapper(applicationContext)

        val directory = contextWrapper.filesDir
        val file = File(directory, "profile_image_${System.currentTimeMillis()}.png")

        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Uri.fromFile(file)
    }

    // 뒤로 가기 하면 메인 화면으로 되돌아가기
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}