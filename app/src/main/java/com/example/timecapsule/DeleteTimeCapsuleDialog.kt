package com.example.timecapsule

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.timecapsule.databinding.DialogDeleteBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteTimeCapsuleDialog : DialogFragment() {
    //바인딩 설정
    private lateinit var binding: DialogDeleteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 바인딩 설정
        binding = DialogDeleteBinding.inflate(inflater, container, false)

        // 팝업창 모서리 둥글게 만들기
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //삭제하기 버튼 클릭
        binding.dialogDeleteYesBtn.setOnClickListener{
            callDeleteApi()

            //화면 전환을 위한 변수 선언
            val readFragment = ReadFragment() // homeFragment 인스턴스 생성
            val transaction = parentFragmentManager.beginTransaction()

            //열람하기로 화면 전환
            transaction.replace(R.id.main_frameLayout, readFragment)
            transaction.addToBackStack(null)
            transaction.commit()

            // 바텀 네비게이션에서 열람하기를 선택한 상태로 설정
            (requireActivity() as MainActivity).setSelectedNavItem(R.id.readFragment)

            //FragmentTransaction이 완료되도록 강제 실행
            parentFragmentManager.executePendingTransactions()

            //dialog를 2초 뒤에 사라지게 하는 코드
            dismissDialog()
        }

        //취소를 클릭했을 때
        binding.dialogDeleteNoBtn.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    //타임캡슐 삭제하기 Api 연동
    private fun callDeleteApi(){
        // 로그인에서 보낸 SharedPreference로 accessToken 가져오고 BearerToken 형식으로 저장
        val accessToken = getAccessToken()
        val bearerToken = "Bearer $accessToken"

        // 해당하는 항목(게시글)의 id 값을 gson으로 저장
        val viewableCapsuleId= arguments?.getString("id")?.toInt()

        //서버 연동
        RetrofitClient.Service.deleteTimeCapsule(bearerToken, viewableCapsuleId!!).enqueue(object:
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if(response.isSuccessful){
                    Toast.makeText(requireContext(),"삭제되었습니다", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("에러", "Response failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("에러", "Response failed: ${t.message}")
            }
        })
    }

    // sharePreferences에 저장된 액세스 토큰 반환하는 메소드
    private fun getAccessToken(): String? {
        val sharedPreferences = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getString(LoginActivity.ACCESS_TOKEN, null)
    }

    //dialog를 2초 뒤에 사라지게 하는 코드
    private fun dismissDialog() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                dismiss()
            }
        }, 200)
    }
}