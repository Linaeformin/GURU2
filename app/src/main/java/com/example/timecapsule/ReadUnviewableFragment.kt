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
    private var category:String="전체"    //초기 카테고리는 전체로 설정

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

        callReadUnviewableApi()  //열람 불가능한 캡슐 데이터를 받아옴

        getToastMessage()   //토스트 메시지로 디데이를 보여주는 코드

        //부모 fragment에서 보낸 selectedCategory 내의 카테고리를 textView에 반영
        parentFragmentManager.setFragmentResultListener("categorySelection", this) { _, bundle ->
            category = bundle.getString("selectedCategory").toString()
            callReadUnviewableApi()
        }

        return binding.root
    }

    //토스트 메시지로 열람 가능한 디데이를 보여주는 코드
    private fun getToastMessage(){
        //해당 항목을 클릭했다면
        unviewableRVAdapter.setMyItemClickListener(object : ReadUnviewableRVAdapter.MyItemClickListener{
            override fun onItemUnviewableClick(unviewableCapsule: UnviewableCapsule) {
                val gson= Gson()

                //남은 일자를 가져와서 토스트 메시지로 출력
                val unviewableOpenDate=gson.toJson(unviewableCapsule.viewableAt)
                Toast.makeText(requireContext(),"열람 가능일이 ${unviewableOpenDate}일 남았어요!",Toast.LENGTH_SHORT).show()
            }
        })
    }

    //열람 불가능한 타임캡슐 데이터를 받아옴
    private fun callReadUnviewableApi(){

        // 로그인에서 보낸 SharedPreference로 accessToken 가져오고 BearerToken 형식으로 저장
        val accessToken = getAccessToken()
        val bearerToken = "Bearer $accessToken"

        //서버 연동
        RetrofitClient.Service.getUnviewableTimeCapsules(bearerToken, category).enqueue(object :
            Callback<List<UnviewableCapsule>>{
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<List<UnviewableCapsule>>,
                response: Response<List<UnviewableCapsule>>
            ) {
                if(response.isSuccessful){
                    val unviewableCapsule=response.body()

                    //UnviewableTimeCapsule 데이터를 초기화하고 각 항목을 데이터 클래스에 저장
                    unviewableCapsule?.let{capsules->
                        unviewableCapsuleData.clear()
                        unviewableCapsuleData.addAll(capsules.map{item->
                            UnviewableCapsule(
                                id = item.id,
                                title = item.title,
                                viewableAt = item.viewableAt
                            )
                        })

                        for(i in 0..<unviewableCapsuleData.size){
                            Log.d("데이터 클래스 확인",unviewableCapsuleData[i].id.toString())
                        }
                        //데이터가 바뀌었음을 명시
                        unviewableRVAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.d("Error","Response failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<UnviewableCapsule>>, t: Throwable) {
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