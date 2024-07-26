package com.example.timecapsule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.timecapsule.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //화면 설정
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginLoginBtn.setOnClickListener {

        }

        //회원가입 버튼 클릭 시 회원가입 Activity로 화면 전환
        binding.loginSignUpBtn.setOnClickListener {
            val intent= Intent(this, SignUpActivity()::class.java)
            startActivity(intent)
        }
    }

}