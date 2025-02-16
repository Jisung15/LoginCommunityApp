package com.example.logincommunityapp

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.logincommunityapp.databinding.ActivityHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private val itemList = mutableListOf<Item>()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: ItemListAdapter
    private val post = "POST"
    private val data = "postData"

    private val postData =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val post = result.data?.getParcelableExtra<Item>(data)
                post?.let {
                    itemList.add(0, it)
                    adapter.submitList(itemList.toList())
                    SharedPreferencesUtil.saveItemList(this, itemList)
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

        val savedItemList = SharedPreferencesUtil.getItemList(this)
        itemList.clear()
        itemList.addAll(savedItemList)

        adapter = ItemListAdapter(itemList) { item -> deletePost(item) }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        binding.recyclerView.adapter = adapter

        adapter.submitList(itemList.toList())

        binding.floatingButton.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            postData.launch(intent)
        }

        binding.logoutButton.setOnClickListener {
            Toast.makeText(this, R.string.logout_message, Toast.LENGTH_SHORT).show()
            SharedPreferencesUtil.saveItemList(this, itemList)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun deletePost(item: Item) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.post_delete_dialog_title)
            .setMessage(R.string.post_delete_dialog_message)
            .setPositiveButton(R.string.dialog_yes_message) { _, _ ->
                db.collection(post)
                    .document(item.idText)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, R.string.post_delete__success_message, Toast.LENGTH_SHORT).show()
                        itemList.remove(item)
                        val updatedList = ArrayList(itemList)
                        adapter.submitList(updatedList)
                        SharedPreferencesUtil.saveItemList(this, itemList)
                    }
                    .addOnFailureListener { _ ->
                        Toast.makeText(this, R.string.post_delete_fail_message, Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton(R.string.dialog_no_message) {dialog, _ ->
                dialog.dismiss()
            }
        builder.show()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_finish_dialog_title)
            .setMessage(R.string.app_finish_dialog_message)

        val listener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> finishAffinity()
                DialogInterface.BUTTON_NEGATIVE -> dialog?.dismiss()
            }
        }

        builder.setNegativeButton(R.string.dialog_no_message, listener)
        builder.setPositiveButton(R.string.dialog_yes_message, listener)
        builder.setOnCancelListener {  }

        builder.show()
    }
}