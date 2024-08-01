package com.example.timecapsule

import RetrofitClient
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.timecapsule.databinding.FragmentReadViewableBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReadViewableFragment: Fragment() {
    private lateinit var binding: FragmentReadViewableBinding
    private var viewableCapsuleData = ArrayList<ViewableCapsule>()
    private lateinit var viewableRVAdapter: ReadViewableRVAdapter
    private var category:String="전체"    //초기 카테고리는 전체로 설정

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //바인딩 설정
        binding = FragmentReadViewableBinding.inflate(inflater, container, false)

        // 어댑터 초기화 및 RecyclerView 설정
        viewableRVAdapter = ReadViewableRVAdapter(viewableCapsuleData)
        binding.readViewableRv.adapter = viewableRVAdapter
        binding.readViewableRv.layoutManager = GridLayoutManager(context, 2)

        // 데이터 로드 및 RecyclerView 업데이트
        callReadViewableApi()

        // 각 아이템을 클릭했을 때 게시글 상세보기 페이지로 전환
        goToReadDetail()

        //부모 fragment에서 보낸 selectedCategory 내의 카테고리를 textView에 반영
        parentFragmentManager.setFragmentResultListener("categorySelection", this) { _, bundle ->
            category = bundle.getString("selectedCategory").toString()
            callReadViewableApi()
        }

        return binding.root
    }

    // 각 아이템을 클릭했을 때 게시글 상세보기 페이지로 전환
    private fun goToReadDetail() {
        viewableRVAdapter.setMyItemClickListener(object : ReadViewableRVAdapter.MyItemClickListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemViewableClick(viewableCapsule: ViewableCapsule) {
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frameLayout, ReadDetailFragment().apply {
                        // 아이템 아이디를 상세보기 페이지로 전달
                        arguments = Bundle().apply {
                            val gson = Gson()
                            val viewableCapsuleJson = gson.toJson(viewableCapsule.id)
                            putString("viewableCapsuleJson", viewableCapsuleJson)
                        }
                    }).commitAllowingStateLoss()
            }
        })
    }


    //열람 가능한 타임캡슐 데이터를 받아옴
    private fun callReadViewableApi() {

        // 로그인에서 보낸 SharedPreference로 accessToken 가져오고 BearerToken 형식으로 저장
        val accessToken = getAccessToken()
        val bearerToken = "Bearer $accessToken"

        //서버 연동
        RetrofitClient.Service.getViewableTimeCapsules(bearerToken, category).enqueue(object :
            Callback<List<ViewableCapsule>> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<List<ViewableCapsule>>,
                response: Response<List<ViewableCapsule>>
            ) {
                if (response.isSuccessful) {
                    val viewableTimeCapsule = response.body()

                    //ViewableTimeCapsule 데이터를 초기화하고 각 항목을 데이터 클래스에 저장
                    viewableTimeCapsule?.let { capsules ->
                        viewableCapsuleData.clear()
                        viewableCapsuleData.addAll(capsules.map { item ->
                            ViewableCapsule(
                                id = item.id,
                                title = item.title,
                                content = item.content,
                                category = item.category,
                                fileName = item.fileName,
                                viewableAt = item.viewableAt,
                                latitude = item.latitude,
                                longitude = item.longitude
                            )
                        })
                        //데이터가 바뀌었음을 명시
                        viewableRVAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.d("Error", "Response failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ViewableCapsule>>, t: Throwable) {
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
