package com.example.timecapsule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.timecapsule.databinding.FragmentReadBinding
import com.google.android.material.tabs.TabLayoutMediator

class ReadFragment : Fragment() {
    private lateinit var binding: FragmentReadBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //바인딩 설정
        binding= FragmentReadBinding.inflate(inflater, container, false)

        //탭 레이아웃 텍스트 배열로 설정
        val tabTitleArray= arrayOf("열람 가능","열람 불가능")

        //탭 레이아웃 실행
        binding.readVp.adapter=ReadVPAdapter(requireActivity())
        TabLayoutMediator(binding.readTl,binding.readVp){
            tab, position->tab.text=tabTitleArray[position]
        }.attach()

        return binding.root
    }
}