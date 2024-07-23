package com.example.timecapsule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.timecapsule.databinding.FragmentWriteBinding

class WriteFragment : Fragment() {
    private lateinit var binding: FragmentWriteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 바인딩 설정
        binding = FragmentWriteBinding.inflate(inflater, container, false)

        // 캘린더 버튼 클릭시 팝업창 출력
        binding.writeCalendarBtn.setOnClickListener {
            popupCalendar()
        }

        // 카테고리 버튼 클릭시 팝업창 출력
        binding.writeCategoryBtn.setOnClickListener {
            popupCategory()
        }

        //CategoryDialog에서 보낸 selectedCategory 내의 카테고리를 textView에 반영
        parentFragmentManager.setFragmentResultListener("categorySelection", this) { _, bundle ->
            val result = bundle.getString("selectedCategory")
            binding.writeCategoryTv.text = result
        }

        return binding.root
    }

    // 캘린더 팝업창 출력
    private fun popupCalendar() {
        val dialog = CalendarDialog()
        dialog.show(parentFragmentManager, "")
    }

    // 카테고리 팝업창 출력
    private fun popupCategory() {
        val dialog = CategoryDialog()
        dialog.show(parentFragmentManager, "")
    }
}
