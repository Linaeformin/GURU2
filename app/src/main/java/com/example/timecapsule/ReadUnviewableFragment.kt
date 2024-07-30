package com.example.timecapsule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.timecapsule.databinding.FragmentReadUnviewableBinding

class ReadUnviewableFragment: Fragment() {
    private lateinit var binding: FragmentReadUnviewableBinding
    private var unviewableCapsuleData=ArrayList<UnviewableCapsule>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //바인딩 설정
        binding=FragmentReadUnviewableBinding.inflate(inflater,container,false)

        getUnviewableCapsule()  //열람 불가능한 캡슐을 RVA로 보여주는 코드

        return binding.root
    }

    //열람 불가능한 캡슐을 RVA로 보여주는 코드
    private fun getUnviewableCapsule(){
        //임시 데이터 삽입
        unviewableCapsuleData.add(UnviewableCapsule("제목1"))
        unviewableCapsuleData.add(UnviewableCapsule("제목2"))
        unviewableCapsuleData.add(UnviewableCapsule("제목3"))
        unviewableCapsuleData.add(UnviewableCapsule("제목4"))
        unviewableCapsuleData.add(UnviewableCapsule("제목1"))
        unviewableCapsuleData.add(UnviewableCapsule("제목2"))
        unviewableCapsuleData.add(UnviewableCapsule("제목3"))
        unviewableCapsuleData.add(UnviewableCapsule("제목4"))
        unviewableCapsuleData.add(UnviewableCapsule("제목1"))
        unviewableCapsuleData.add(UnviewableCapsule("제목2"))
        unviewableCapsuleData.add(UnviewableCapsule("제목3"))
        unviewableCapsuleData.add(UnviewableCapsule("제목4"))

        //RVA 실행
        val unviewableRVAdapter=ReadUnviewableRVAdapter(unviewableCapsuleData)
        binding.readUnviewableRv.adapter=unviewableRVAdapter
        binding.readUnviewableRv.layoutManager= GridLayoutManager(context,2)
    }
}