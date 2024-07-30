package com.example.timecapsule

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.timecapsule.databinding.ActivityLoginBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val ACCESS_TOKEN="token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //화면 설정
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

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

    // 로그인 API 연동
    private fun callLoginApi(username: String, password: String) {
        // 로그인 요청 데이터 생성
        val loginRequest = ApiService.UserRequest(username, password)

        // Retrofit으로 로그인 API 호출
        RetrofitClient.Service.login(loginRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // 서버 응답 본문을 문자열로 읽어오기
                    val responseBody = response.body()?.string()

                    if (responseBody != null) {
                        // SharedPreferences에 토큰 저장
                        sharedPreferences.edit {
                            putString(ACCESS_TOKEN, responseBody.toString())
                        }

                        // 로그인 성공 후 다음 작업 (예: 메인 액티비티로 전환)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish() // 현재 액티비티 종료
                    } else {
                        // 응답 본문이 null인 경우
                        Toast.makeText(this@LoginActivity, "응답 본문이 비어있습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // 응답 실패 시
                    Toast.makeText(this@LoginActivity, "로그인 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // API 호출 실패 시
                Toast.makeText(this@LoginActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}