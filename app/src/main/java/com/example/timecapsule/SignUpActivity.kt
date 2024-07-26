package com.example.timecapsule

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timecapsule.databinding.ActivitySignupBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 화면 설정
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 회원가입 등록 버튼을 클릭했을 때
        binding.signUpBtn.setOnClickListener {
            val email = binding.signUpIdEmailEt.text.toString()
            val password = binding.signUpPasswordEt.text.toString()
            val passwordCheck = binding.signUpPasswordCheckEt.text.toString()

            //이메일이나 패스워드, 패스워드 확인이 입력되어 있지 않은 경우
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            } else {
                //패스워드가 패스워드 확인과 다른 경우
                if (password != passwordCheck) {
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    //회원가입 API 호출
                    callSignUpApi(email, password)
                }
            }
        }
    }

    //회원가입 API 연동
    private fun callSignUpApi(username: String, password: String) {
        //회원가입 요청 데이터 생성
        val signUpRequest = ApiService.SignUpRequest(username, password)

        //Retrofit으로 회원가입 API 호출
        RetrofitClient.instance.signUp(signUpRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                //서버 응답에 성공한 경우
                if (response.isSuccessful) {

                    //사용자에게 성공 메시지 출력
                    val responseBody = response.body()?.string()
                    Toast.makeText(this@SignUpActivity, "$responseBody", Toast.LENGTH_SHORT).show()

                    //로그인 화면으로 이동
                    val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                    startActivity(intent)

                    // 현재 액티비티 종료
                    finish()

                } else {

                    //사용자에게 실패 메시지 출력
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@SignUpActivity, "${errorBody ?: response.message()}입니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@SignUpActivity, "회원가입 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}


