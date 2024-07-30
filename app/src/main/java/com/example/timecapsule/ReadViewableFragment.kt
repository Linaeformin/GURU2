package com.example.timecapsule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.timecapsule.databinding.FragmentReadViewableBinding

class ReadViewableFragment: Fragment() {
    private lateinit var binding: FragmentReadViewableBinding
    private var viewableCapsuleData=ArrayList<ViewableCapsule>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //바인딩 설정
        binding=FragmentReadViewableBinding.inflate(inflater,container,false)

        getViewableCapsule()    //열람 가능한 캡슐을 RVA로 보여주는 코드

        return binding.root
    }

    //열람 가능한 캡슐을 RVA로 보여주는 코드
    private fun getViewableCapsule(){
        //임시 데이터 삽입
        viewableCapsuleData.add(ViewableCapsule(R.drawable.read_timecapsule_1,"제목1"))
        viewableCapsuleData.add(ViewableCapsule(R.drawable.read_timecapsule_2,"제목2"))
        viewableCapsuleData.add(ViewableCapsule(R.drawable.read_timecapsule_3,"제목3"))
        viewableCapsuleData.add(ViewableCapsule(R.drawable.read_timecapsule_4,"제목4"))
        viewableCapsuleData.add(ViewableCapsule(R.drawable.read_timecapsule_1,"제목1"))
        viewableCapsuleData.add(ViewableCapsule(R.drawable.read_timecapsule_2,"제목2"))
        viewableCapsuleData.add(ViewableCapsule(R.drawable.read_timecapsule_3,"제목3"))
        viewableCapsuleData.add(ViewableCapsule(R.drawable.read_timecapsule_4,"제목4"))
        viewableCapsuleData.add(ViewableCapsule(R.drawable.read_timecapsule_1,"제목1"))
        viewableCapsuleData.add(ViewableCapsule(R.drawable.read_timecapsule_2,"제목2"))
        viewableCapsuleData.add(ViewableCapsule(R.drawable.read_timecapsule_3,"제목3"))
        viewableCapsuleData.add(ViewableCapsule(R.drawable.read_timecapsule_4,"제목4"))

        //RVA 실행
        val viewableRVAdapter=ReadViewableRVAdapter(viewableCapsuleData)
        binding.readViewableRv.adapter=viewableRVAdapter
        binding.readViewableRv.layoutManager=GridLayoutManager(context,2)
    }
}