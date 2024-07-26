package com.example.timecapsule

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timecapsule.databinding.ActivityLoginBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //화면 설정
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //로그인 버튼을 클릭했을 때
        binding.loginLoginBtn.setOnClickListener {

            //아이디와 패스워드 변수에 저장
            val username = binding.loginIdEmailEt.text.toString()
            val password = binding.loginPasswordEt.text.toString()

            try {
                // 아이디나 비밀번호가 비어있으면 예외를 발생시킴
                if (username.isEmpty() || password.isEmpty()) {
                    throw IllegalArgumentException("아이디와 패스워드를 입력하세요.")
                }

                // 아이디와 비밀번호가 모두 입력된 경우 로그인 API 호출
                callLoginApi(username, password)

            } catch (e: IllegalArgumentException) {

                // 예외 발생 시 토스트 메시지로 사용자에게 알림
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }

        // 회원가입 버튼 클릭 시 회원가입 Activity로 화면 전환
        binding.loginSignUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    //로그인 API 연동
    private fun callLoginApi(username: String, password: String) {
        //로그인 요청 데이터 생성
        val loginRequest = ApiService.UserRequest(username, password)

        //Retrofit으로 로그인 API 호출
        RetrofitClient.instance.login(loginRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                //서버 응답에 성공한 경우
                if (response.isSuccessful) {

                    //메인 Activity로 화면 전환
                    val intent=Intent(this@LoginActivity,MainActivity::class.java)
                    startActivity(intent)

                    // 현재 Activity 종료
                    finish()

                } else {

                    //사용자에게 실패 메시지 출력
                    Toast.makeText(this@LoginActivity, "등록된 아이디가 아니거나 비밀번호가 아닙니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //네트워크 요청 실패 처리
                Toast.makeText(this@LoginActivity, "로그인 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}