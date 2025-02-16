package com.example.logincommunityapp

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.logincommunityapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySignUpBinding.inflate(layoutInflater) }
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var hidden: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 회원가입 완료 버튼
        binding.btnSignUpPageClearButton.setOnClickListener {
            val email = binding.etSignUpPageLoginIdText.text.toString()
            val password = binding.etSignUpPagePasswordText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.empty_text_message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signUp(email, password)
        }

        // 비밀번호 숨기기 기능
        binding.ivHiddenPassword.setOnClickListener {
            if (hidden) {
                binding.etSignUpPagePasswordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.ivHiddenPassword.setImageResource(R.drawable.hidden)
            } else {
                binding.etSignUpPagePasswordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.ivHiddenPassword.setImageResource(R.drawable.show)
            }

            binding.etSignUpPagePasswordText.setSelection(binding.etSignUpPagePasswordText.text.length)

            hidden = !hidden
        }
    }

    // 회원가입 완료 여부 메소드
    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, R.string.sign_up_success_message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, R.string.sign_up_fail_message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}