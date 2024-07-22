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
        //바인딩 설정
        binding= FragmentWriteBinding.inflate(inflater, container, false)

        binding.writeCalendarBtn.setOnClickListener {
            popupCalendar()
        }

        binding.writeCategoryBtn.setOnClickListener {
            popupCategory()
        }
        return binding.root
    }

    private fun popupCalendar(){
        val dialog=CalendarDialog()
        dialog.show(parentFragmentManager,"")
    }

    private fun popupCategory(){
        val dialog=CategoryDialog()
        dialog.show(parentFragmentManager,"")
    }
}