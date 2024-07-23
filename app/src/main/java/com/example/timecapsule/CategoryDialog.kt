package com.example.timecapsule

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.timecapsule.databinding.DialogWriteCategoryBinding

private var previousOption: Int = 0     //이전 옵션 정의

class CategoryDialog : DialogFragment() {

    private lateinit var binding: DialogWriteCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 바인딩 설정
        binding = DialogWriteCategoryBinding.inflate(inflater, container, false)

        // 팝업창 모서리 둥글게 만들기
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //이전에 선택한 카테고리 UI 업데이트
        updateUI(previousOption)

        // radioGroup에 체인지 리스너 설정
        binding.writeCategoryRg.setOnCheckedChangeListener { group, checkedId ->
            previousOption = checkedId
            when (checkedId) {
                R.id.write_category_school_rb -> {
                    sendSortOption("학교")    // 선택한 옵션을 부모 Fragment로 전달하는 함수
                }
                R.id.write_category_family_rb -> {
                    sendSortOption("가족")    // 선택한 옵션을 부모 Fragment로 전달하는 함수
                }
                R.id.write_category_travel_rb -> {
                    sendSortOption("여행")    // 선택한 옵션을 부모 Fragment로 전달하는 함수
                }
                R.id.write_category_friend_rb -> {
                    sendSortOption("친구")    // 선택한 옵션을 부모 Fragment로 전달하는 함수
                }
                R.id.write_category_food_rb -> {
                    sendSortOption("음식")    // 선택한 옵션을 부모 Fragment로 전달하는 함수
                }
            }
        }

        //취소 버튼을 클릭하면 팝업창 삭제
        binding.dialogHomeCancelIv.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    //WriteFragment에 선택된 카테고리 전송
    private fun sendSortOption(option: String) {
        val result = Bundle()
        result.putString("selectedCategory", option)
        parentFragmentManager.setFragmentResult("categorySelection", result)
    }

    //선택된 카테고리에 따라 UI를 업데이트함
    private fun updateUI(selectedOption: Int){
        if(selectedOption!=0){
            binding.writeCategoryRg.check(selectedOption)   //이전 카테고리 check
        }
    }
}
