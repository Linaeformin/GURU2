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

        callReadDetail()

        binding.readDetailLeftArrowBtn.setOnClickListener {  }
        return binding.root
    }
    private fun callReadDetail(){
        val accessToken = getAccessToken()
        val bearerToken = "Bearer $accessToken"

        val viewableCapsuleJson=arguments?.getString("viewableCapsuleJson")
        val viewableCapsuleId:Int=gson.fromJson(viewableCapsuleJson, Int::class.java)

        Log.d("아이디",viewableCapsuleId.toString())

        RetrofitClient.Service.getViewableDetail(bearerToken, viewableCapsuleId).enqueue(object:
            Callback<ViewableCapsule>{
            override fun onResponse(
                call: Call<ViewableCapsule>,
                response: Response<ViewableCapsule>
            ) {
                if(response.isSuccessful){
                    val viewableTimeCapsule = response.body()
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

                        setView(it)

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

    private fun setView(detail: ViewableCapsule){
        binding.readDetailTitleTv.text=detail.title
        binding.writeContentTv.text=detail.content
        binding.readDetailDateTv.text=detail.viewableAt
        setImage(fileName)
    }

    private fun setImage(fileName: String){
        Glide.with(this).load(fileName).into(binding.writeImageIv)
    }
}