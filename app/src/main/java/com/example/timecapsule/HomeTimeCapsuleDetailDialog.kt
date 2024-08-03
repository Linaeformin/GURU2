package com.example.timecapsule

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.timecapsule.databinding.DialogHomeTimeCapsuleBinding

class HomeTimeCapsuleDetailDialog : DialogFragment() {
    private lateinit var binding: DialogHomeTimeCapsuleBinding
    private var id: Int? =null
    private var title: String=""
    private var viewableAt: String=""
    private var latitude: Double=0.0
    private var longitude: Double=0.0
    private var daysLeft: Int=-1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //바인딩 설정
        binding=DialogHomeTimeCapsuleBinding.inflate(inflater,container,false)

        // 팝업창 모서리 둥글게 만들기
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        getCapsuleData()
        setView()

        binding.dialogHomeCancelIv.setOnClickListener {
            dismiss()
        }

        binding.dialogHomeLockedBtn.setOnClickListener {
            Toast.makeText(requireContext(),"오픈할 수 없습니다.",Toast.LENGTH_SHORT).show()
        }

        binding.dialogHomeOpenBtn.setOnClickListener {
            Log.d("여기서는",id.toString())
            id?.let { it1 -> sendId(it1) }
        }

        return binding.root
    }

    private fun getCapsuleData(){
        id= arguments?.getInt("capsuleId")!!
        title=arguments?.getString("capsuleTitle")!!
        viewableAt=arguments?.getString("capsuleViewableAt")!!
        latitude=arguments?.getDouble("capsuleLatitude")!!
        longitude=arguments?.getDouble("capsuleLongitude")!!
        daysLeft=arguments?.getInt("capsuleDaysLeft")!!
        Log.d("갯",daysLeft.toString())
    }

    @SuppressLint("SetTextI18n")
    private fun setView(){
        binding.dialogHomeTitleTv.text=title
        binding.dialogHomeDateTv.text=viewableAt
        Log.d("캡슐 아이디",id.toString())
        Log.d("얼마나",daysLeft.toString())
        if (daysLeft>0){
            binding.dialogHomeLockedBtn.visibility=View.VISIBLE
            binding.dialogHomeOpenBtn.visibility=View.GONE
            binding.dialogHomeLockedBtn.text="캡슐 오픈까지 D-$daysLeft"
        }
        else {
            binding.dialogHomeLockedBtn.visibility=View.GONE
            binding.dialogHomeOpenBtn.visibility=View.VISIBLE
        }
    }

    private fun sendId(id: Int){
        val result=Bundle()
        result.putInt("postId",id)
        parentFragmentManager.setFragmentResult("detailId",result)
        dismiss()
    }
}