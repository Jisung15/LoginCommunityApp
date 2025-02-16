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

        // 로그인 버튼 누르면 로그인
        binding.btnLoginPageLoginButton.setOnClickListener {
            val email = binding.etLoginPageLoginIdText.text.toString()
            val password = binding.etLoginPagePasswordText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.empty_text_message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            login(email, password)
        }

        // 회원가입 버튼 누르면 회원가입 페이지로 이동
        binding.btnLoginPageSignUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // 비밀번호 숨기기 기능
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

        // 회원탈퇴 기능
        binding.btnDeleteProfileButton.setOnClickListener {
            deleteDialog()
        }
    }

    // 로그인 성공 여부 확인 메소드
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

    // 회원 탈퇴 기능 메소드
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

    // 회원 탈퇴 성공 여부 메소드
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