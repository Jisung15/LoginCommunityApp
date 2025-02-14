package com.example.logincommunityapp

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.logincommunityapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var hidden: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.main)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnLoginPageLoginButton.setOnClickListener {
            val email = binding.etLoginPageLoginIdText.text.toString()
            val password = binding.etLoginPagePasswordText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.empty_text_message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            login(email, password)
        }

        binding.btnLoginPageSignUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.ivHiddenPassword.setOnClickListener {
            if (hidden) {
                binding.etLoginPagePasswordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.ivHiddenPassword.setImageResource(R.drawable.hidden)
            } else {
                binding.etLoginPagePasswordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.ivHiddenPassword.setImageResource(R.drawable.show)
            }

            binding.etLoginPagePasswordText.setSelection(binding.etLoginPagePasswordText.text.length)

            hidden = !hidden
        }

        binding.btnDeleteProfileButton.setOnClickListener {
            deleteDialog()
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, R.string.login_success_message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    Toast.makeText(this, R.string.login_fail_message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun deleteDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.deleteProfile_dialog_title)
            .setMessage(R.string.deleteProfile_dialog_message)
            .setPositiveButton(R.string.dialog_yes_message) { _, _ ->
                deleteProfile()
            }
            .setNegativeButton(R.string.dialog_no_message) { dialog, _ ->
                dialog.dismiss()
            }

        builder.create()
        builder.show()
    }

    private fun deleteProfile() {
        val user = auth.currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, R.string.deleteProfile_success_message, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, R.string.deleteProfile_fail_message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}