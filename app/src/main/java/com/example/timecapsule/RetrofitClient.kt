package com.example.timecapsule

import ApiService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://port-0-guru2-backend-g0424l70py8py.gksl2.cloudtype.app/"  //기본 URL 지정

    //Retrofit 인스턴스의 싱글톤 객체
    val instance: ApiService by lazy {

        //Gson 인스턴스 생성 및 설정
        val gson = GsonBuilder().setLenient().create()

        //Retrofit 객체 생성
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)  //기본 URL 설정
            .addConverterFactory(GsonConverterFactory.create(gson)) // GsonConverterFactory를 사용하여 JSON 변환
            .build()

        //Retrofit 인스턴스 생성
        retrofit.create(ApiService::class.java)
    }
}


