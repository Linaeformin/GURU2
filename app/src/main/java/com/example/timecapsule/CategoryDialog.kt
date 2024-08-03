package com.example.timecapsule

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.timecapsule.databinding.DialogWriteCategoryBinding

private var categoryOption: Int = 0     //이전 옵션 정의

class CategoryDialog : DialogFragment() {
    //바인딩 설정
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

        //이전 값으로 업데이트
        uiUpdate()

        // radioGroup에 체인지 리스너 설정
        binding.writeCategoryRg.setOnCheckedChangeListener { _, checkedId ->
            categoryOption = checkedId
            when (checkedId) {
                R.id.write_category_school_rb -> {
                    sendCategoryOption("학교")    // 선택한 옵션을 부모 Fragment로 전달하는 함수
                }
                R.id.write_category_family_rb -> {
                    sendCategoryOption("가족")    // 선택한 옵션을 부모 Fragment로 전달하는 함수
                }
                R.id.write_category_travel_rb -> {
                    sendCategoryOption("여행")    // 선택한 옵션을 부모 Fragment로 전달하는 함수
                }
                R.id.write_category_friend_rb -> {
                    sendCategoryOption("친구")    // 선택한 옵션을 부모 Fragment로 전달하는 함수
                }
                R.id.write_category_food_rb -> {
                    sendCategoryOption("음식")    // 선택한 옵션을 부모 Fragment로 전달하는 함수
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
    private fun sendCategoryOption(option: String) {
        val result = Bundle()
        result.putString("selectedCategory", option)
        parentFragmentManager.setFragmentResult("categorySelection", result)
    }

    //이전에 선택한 카테고리 UI 업데이트
    private fun uiUpdate(){
        // Arguments에서 categoryOption 값을 가져오기
        val args = arguments
        if (args != null) {
            categoryOption = args.getInt("categoryOption", 0)  // 기본값을 0으로 설정
        }
        //radioGroup 미리 세팅
        binding.writeCategoryRg.check(categoryOption)  // 선택된 옵션 체크
    }
}
