package com.example.timecapsule

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.timecapsule.databinding.FragmentWriteBinding
import java.time.LocalDate

class WriteFragment : Fragment() {
    private lateinit var binding: FragmentWriteBinding

    private var year: String="2024"
    private var month: String="7"
    private var day: String="25"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 바인딩 설정
        binding = FragmentWriteBinding.inflate(inflater, container, false)

        //현재 날짜를 받아 textView에 반영
        getCurrentDate()

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

    //현재 날짜를 받아 textView에 반영
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate() {
        val currentDate= LocalDate.now()

        //오늘의 년도, 월, 일을 저장
        year=currentDate.year.toString()
        month=currentDate.monthValue.toString()
        day=currentDate.dayOfMonth.toString()

        binding.writeDateTv.text= "$year.$month.$day"
    }
}
