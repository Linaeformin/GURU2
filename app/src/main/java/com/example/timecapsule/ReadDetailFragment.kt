package com.example.timecapsule

import RetrofitClient
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.timecapsule.databinding.FragmentReadDetailBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReadDetailFragment : Fragment() {
    private lateinit var binding: FragmentReadDetailBinding
    private var viewableCapsuleData = ArrayList<ViewableCapsule>()
    private var gson: Gson = Gson()
    private var title: String=""
    private var content: String=""
    private var category: String=""
    private var fileName: String=""
    private var viewableAt: String=""
    private var latitude: Double=0.0
    private var longitude: Double=0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //바인딩 설정
        binding=FragmentReadDetailBinding.inflate(inflater,container,false)

        callReadDetailApi()     //클릭한 타임캡슐의 데이터를 받아옴

        //돌아가기 버튼을 클릭했을 때
        binding.readDetailLeftArrowBtn.setOnClickListener {
            //열람하기으로 화면 전환
            val readFragment = ReadFragment() // readFragment 인스턴스 생성
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frameLayout, readFragment)
            transaction.addToBackStack(null)
            transaction.commit()

            // 바텀 네비게이션에서 열람하기를 선택한 상태로 설정
            (requireActivity() as MainActivity).setSelectedNavItem(R.id.readFragment)
        }
        return binding.root
    }

    //클릭한 타임캡슐의 데이터를 받아옴
    private fun callReadDetailApi(){

        // 로그인에서 보낸 SharedPreference로 accessToken 가져오고 BearerToken 형식으로 저장
        val accessToken = getAccessToken()
        val bearerToken = "Bearer $accessToken"

        // 해당하는 항목(게시글)의 id 값을 gson으로 저장
        val viewableCapsuleJson=arguments?.getString("viewableCapsuleJson")
        val viewableCapsuleId:Int=gson.fromJson(viewableCapsuleJson, Int::class.java)

        //서버 연동
        RetrofitClient.Service.getViewableDetail(bearerToken, viewableCapsuleId).enqueue(object:
            Callback<ViewableCapsule>{
            override fun onResponse(
                call: Call<ViewableCapsule>,
                response: Response<ViewableCapsule>
            ) {
                if(response.isSuccessful){
                    val viewableTimeCapsule = response.body()

                        //viewableTimeCapsule 데이터를 초기화하고 각 항목을 데이터 클래스에 저장
                            viewableTimeCapsule?.let {
                            viewableCapsuleData.clear()
                            viewableCapsuleData.add(it)

                            title=viewableTimeCapsule.title
                            content=viewableTimeCapsule.content
                            category=viewableTimeCapsule.category
                            viewableAt=viewableTimeCapsule.viewableAt
                            latitude=viewableTimeCapsule.latitude
                            longitude=viewableTimeCapsule.longitude
                            fileName=viewableTimeCapsule.fileName

                            setView(it)     //화면을 구성하는 코드
                        }

                } else {
                    Log.d("Error", "Response failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ViewableCapsule>, t: Throwable) {
                Log.d("Error", "Response failed: ${t.message}")
            }
        })
    }

    // sharePreferences에 저장된 액세스 토큰 반환하는 메소드
    private fun getAccessToken(): String? {
        val sharedPreferences = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getString(LoginActivity.ACCESS_TOKEN, null)
    }

    //화면을 구성하는 코드
    private fun setView(detail: ViewableCapsule){
        binding.readDetailTitleTv.text=detail.title
        binding.writeContentTv.text=detail.content
        binding.readDetailDateTv.text=detail.viewableAt
        setImage(fileName)      //이미지뷰에 파일을 삽입하는 코드
    }

    //이미지뷰에 파일을 삽입하는 코드
    private fun setImage(fileName: String){
        Glide.with(this).load(fileName).into(binding.writeImageIv)
    }
}