package com.example.timecapsule

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.timecapsule.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(){
    //바인딩 설정
    lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //바인딩 및 화면 설정
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //첫 번째 splash 화면을 보여주는 코드
        showFirstSplash()

        // 일정 시간 지연 다음 splash 화면으로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            showSecondSplash()
        }, 3000)
    }

    //첫 번째 splash 화면을 보여주는 코드
    private fun showFirstSplash(){
        binding.splash1Iv.visibility= View.VISIBLE
    }

    //두 번쨰 splash 화면을 보여주는 코드
    private fun showSecondSplash(){
        binding.splash1Iv.visibility = View.GONE
        binding.splash2Iv.visibility = View.VISIBLE

        // 일정 시간 지연 이후 실행하기 위한 코드
        Handler(Looper.getMainLooper()).postDelayed({

            // 일정 시간이 지나면 LoginActivity로 이동
            val intent= Intent( this, LoginActivity::class.java)
            startActivity(intent)

            // 이전 키를 눌렀을 때 스플래스 스크린 화면으로 이동을 방지하기 위해
            // 이동한 다음 사용안함으로 finish 처리
            finish()

        }, 2000)
    }
}