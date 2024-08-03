package com.example.timecapsule

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.timecapsule.databinding.DialogHomeTimeCapsuleBinding

class HomeTimeCapsuleDetailDialog : DialogFragment() {
    //바인딩 설정
    private lateinit var binding: DialogHomeTimeCapsuleBinding

    //각 데이터 정의
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

        //HomeFragment에서 데이터를 받아옴
        getCapsuleData()

        //초기 화면 설정
        setView()

        //돌아가기 버튼을 눌렀을 경우
        binding.dialogHomeCancelIv.setOnClickListener {
            dismiss()
        }

        //열람 불가능한 캡슐의 버튼을 눌렀을 경우
        binding.dialogHomeLockedBtn.setOnClickListener {
            Toast.makeText(requireContext(),"오픈할 수 없습니다.",Toast.LENGTH_SHORT).show()
        }

        //열람 가능한 캡슐의 버튼을 눌렀을 경우
        binding.dialogHomeOpenBtn.setOnClickListener {
            id?.let { it1 -> sendId(it1) }
        }

        return binding.root
    }

    //HomeFragment에서 데이터를 받아옴
    private fun getCapsuleData(){
        id= arguments?.getInt("capsuleId")!!
        title=arguments?.getString("capsuleTitle")!!
        viewableAt=arguments?.getString("capsuleViewableAt")!!
        latitude=arguments?.getDouble("capsuleLatitude")!!
        longitude=arguments?.getDouble("capsuleLongitude")!!
        daysLeft=arguments?.getInt("capsuleDaysLeft")!!
    }

    //초기 화면 설정
    @SuppressLint("SetTextI18n")
    private fun setView(){
        binding.dialogHomeTitleTv.text=title
        binding.dialogHomeDateTv.text=viewableAt

        //열람 불가능한 캡슐일 경우
        if (daysLeft>0){
            binding.dialogHomeLockedBtn.visibility=View.VISIBLE
            binding.dialogHomeOpenBtn.visibility=View.GONE
            binding.dialogHomeLockedBtn.text="캡슐 오픈까지 D-$daysLeft"
        }
        //열람 가능한 캡슐일 경우
        else {
            binding.dialogHomeLockedBtn.visibility=View.GONE
            binding.dialogHomeOpenBtn.visibility=View.VISIBLE
        }
    }

    //열람 가능한 타임캡슐을 오픈할 경우 id값을 parentFragment에 전달
    private fun sendId(id: Int){
        val result=Bundle()
        result.putInt("postId",id)
        parentFragmentManager.setFragmentResult("detailId",result)
        dismiss()
    }
}