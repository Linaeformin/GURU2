package com.example.timecapsule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.timecapsule.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //화면 설정
        binding= ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}