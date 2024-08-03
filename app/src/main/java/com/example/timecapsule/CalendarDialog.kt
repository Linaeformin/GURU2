package com.example.timecapsule

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.timecapsule.databinding.DialogCalendarBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

class CalendarDialog : DialogFragment() {
    //바인딩 설정
    private lateinit var binding: DialogCalendarBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //바인딩 설정
        binding = DialogCalendarBinding.inflate(inflater, container, false)

        //팝업창 모서리 둥글게 만들기
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //지난 날짜 비가시화
        val currentDate = LocalDate.now()
        val currentDay = CalendarDay.from(currentDate.year, currentDate.monthValue-1, currentDate.dayOfMonth)
        binding.calendarView.state().edit().setMinimumDate(currentDay).commit()

        //WriteFragment에서 받아온 데이터를 반영
        getCalendarDate()

        //날짜를 설정했을 경우
        binding.calendarView.setOnDateChangedListener { _, date, _ -> // 선택된 날짜 가져오기
            val dateArray = intArrayOf(date.year, date.month + 1, date.day)
            sendCalendarOption(dateArray)
        }

        return binding.root
    }

    //캘린더 초기 화면 설정
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    private fun getCalendarDate() {

        //calendarDate를 받아옴
        val dateString=arguments?.getString("calendarDate")
        val dateFormat=SimpleDateFormat("yyyy-MM-dd")   //데이터 포맷 지정
        val date: Date

        try {
            // 문자열을 Date 객체로 변환
            date=dateString?.let { dateFormat.parse(it) } ?: throw IllegalArgumentException("Invalid date string")
            val calendarDay=CalendarDay.from(date)  //CalendarDay 객체로 변환
            binding.calendarView.selectedDate = calendarDay     //선택해둔 날짜를 표시

        } catch (e: Exception) {
            Toast.makeText(requireContext(),"잘못된 날짜입니다.",Toast.LENGTH_SHORT).show()
        }
    }

    //WriteFragment에 선택된 날짜 전송
    private fun sendCalendarOption(option: IntArray) {
        val result = Bundle()
        result.putIntArray("selectedCalendar", option)
        parentFragmentManager.setFragmentResult("selectedCalendar", result)
    }
}
