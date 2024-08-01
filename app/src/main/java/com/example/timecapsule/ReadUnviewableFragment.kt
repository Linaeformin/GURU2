package com.example.timecapsule

import RetrofitClient
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.timecapsule.databinding.FragmentReadUnviewableBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReadUnviewableFragment: Fragment() {
    private lateinit var binding: FragmentReadUnviewableBinding
    private var unviewableCapsuleData=ArrayList<UnviewableCapsule>()
    private lateinit var unviewableRVAdapter: ReadUnviewableRVAdapter
    private var category:String="전체"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //바인딩 설정
        binding=FragmentReadUnviewableBinding.inflate(inflater,container,false)

        // 어댑터 초기화 및 RecyclerView 설정
        unviewableRVAdapter = ReadUnviewableRVAdapter(unviewableCapsuleData)
        binding.readUnviewableRv.adapter = unviewableRVAdapter
        binding.readUnviewableRv.layoutManager = GridLayoutManager(context, 2)

        callReadUnviewableApi()  //열람 불가능한 캡슐을 RVA로 보여주는 코드

        getToastMessage()

        //CategoryDialog에서 보낸 selectedCategory 내의 카테고리를 textView에 반영
        parentFragmentManager.setFragmentResultListener("categorySelection", this) { _, bundle ->
            category = bundle.getString("selectedCategory").toString()
            callReadUnviewableApi()
        }

        return binding.root
    }

    //열람 불가능한 캡슐을 RVA로 보여주는 코드
    private fun getToastMessage(){
        unviewableRVAdapter.setMyItemClickListener(object : ReadUnviewableRVAdapter.MyItemClickListener{
            override fun onItemUnviewableClick(unviewableCapsule: UnviewableCapsule) {
                val gson= Gson()
                val unviewableOpenDate=gson.toJson(unviewableCapsule.viewableAt)
                Toast.makeText(requireContext(),"열람 가능일이 ${unviewableOpenDate}일 남았어요!",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun callReadUnviewableApi(){
        val accessToken = getAccessToken()
        val bearerToken = "Bearer $accessToken"

        RetrofitClient.Service.getUnviewableTimeCapsules(bearerToken, category).enqueue(object :
            Callback<List<UnviewableCapsule>>{
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<List<UnviewableCapsule>>,
                response: Response<List<UnviewableCapsule>>
            ) {
                Log.d("확인","SUCCESS")
                if(response.isSuccessful){
                    Log.d("확인","SUCCESS1")
                    val unviewableCapsule=response.body()
                    Log.d("확인","SUCCESS2")
                    unviewableCapsule?.let{capsules->
                        unviewableCapsuleData.clear()
                        Log.d("확인","SUCCESS3")
                        unviewableCapsuleData.addAll(capsules.map{item->
                            UnviewableCapsule(
                                id = item.id,
                                title = item.title,
                                viewableAt = item.viewableAt
                            )
                        })
                        Log.d("확인","SUCCESS4")
                        unviewableRVAdapter.notifyDataSetChanged()
                        Log.d("확인","SUCCESS5")
                    }
                } else {
                    Log.d("Error","Response failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<UnviewableCapsule>>, t: Throwable) {
                Log.d("카테고리","SUCCESS5")
                Log.d("Error", "API call failed: ${t.message}")
            }

        })
    }

    // 로그인에서 보낸 SharedPreference로 accessToken 가져오기
    private fun getAccessToken(): String? {
        val sharedPreferences = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getString(LoginActivity.ACCESS_TOKEN, null)
    }
}