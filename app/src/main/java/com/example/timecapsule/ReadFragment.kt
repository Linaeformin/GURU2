package com.example.timecapsule

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

        //선택한 카테고리에 따라 textView의 style 속성 변경
        binding.readCategoryAllTv.setOnClickListener {
            updateTextViewStyle(binding.readCategoryAllTv)
            sendCategoryOption("전체")
        }

        //선택한 카테고리에 따라 textView의 style 속성 변경
        binding.readCategoryFamilyTv.setOnClickListener {
            updateTextViewStyle(binding.readCategoryFamilyTv)
            sendCategoryOption("가족")
        }

        //선택한 카테고리에 따라 textView의 style 속성 변경
        binding.readCategoryFoodTv.setOnClickListener {
            updateTextViewStyle(binding.readCategoryFoodTv)
            sendCategoryOption("음식")
        }

        //선택한 카테고리에 따라 textView의 style 속성 변경
        binding.readCategorySchoolTv.setOnClickListener {
            updateTextViewStyle(binding.readCategorySchoolTv)
            sendCategoryOption("학교")
        }

        //선택한 카테고리에 따라 textView의 style 속성 변경
        binding.readCategoryTravelTv.setOnClickListener {
            updateTextViewStyle(binding.readCategoryTravelTv)
            sendCategoryOption("여행")
        }

        //선택한 카테고리에 따라 textView의 style 속성 변경
        binding.readCategoryFriendTv.setOnClickListener {
            updateTextViewStyle(binding.readCategoryFriendTv)
            sendCategoryOption("친구")
        }

        return binding.root
    }

    //선택한 카테고리에 따라 textView의 style 속성 변경
    private fun updateTextViewStyle(clickedTextView: TextView) {
        // 모든 TextView를 normal로 설정
        binding.readCategoryAllTv.setTypeface(null, Typeface.NORMAL)
        binding.readCategoryFamilyTv.setTypeface(null, Typeface.NORMAL)
        binding.readCategoryFoodTv.setTypeface(null, Typeface.NORMAL)
        binding.readCategorySchoolTv.setTypeface(null, Typeface.NORMAL)
        binding.readCategoryTravelTv.setTypeface(null, Typeface.NORMAL)
        binding.readCategoryFriendTv.setTypeface(null, Typeface.NORMAL)

        // 클릭된 TextView를 bold로 설정
        clickedTextView.setTypeface(null, Typeface.BOLD)
    }

    private fun sendCategoryOption(option: String) {
        val result = Bundle()
        result.putString("selectedCategory", option)
        parentFragmentManager.setFragmentResult("categorySelection", result)
    }
}