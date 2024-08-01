package com.example.timecapsule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.timecapsule.databinding.FragmentReadDetailBinding

class ReadDetailFragment : Fragment() {
    private lateinit var binding: FragmentReadDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //바인딩 설정
        binding=FragmentReadDetailBinding.inflate(inflater,container,false)

        return binding.root
    }
}