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

        binding.btnSignUpPageLoginButton.setOnClickListener {
            val email = binding.etSignUpPageLoginIdText.text.toString()
            val password = binding.etSignUpPagePasswordText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 모두 입력해야 합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signUp(email, password)
        }

        binding.btnHiddenPassword.setOnClickListener {
            if (hidden) {
                binding.etSignUpPagePasswordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.btnHiddenPassword.text = "비밀번호 보기"
            } else {
                binding.etSignUpPagePasswordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.btnHiddenPassword.text = "비밀번호 숨기기"
            }

            binding.etSignUpPagePasswordText.setSelection(binding.etSignUpPagePasswordText.text.length)

            hidden = !hidden
        }
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "회원가입에 실패하였습니다. 입력을 다시 한 번 확인해 주세요.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}