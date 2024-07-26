package com.example.timecapsule

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
    }

}