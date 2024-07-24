package com.example.timecapsule

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.timecapsule.databinding.FragmentWriteBinding
import java.time.LocalDate

private var settingOption: Int=0    //설정 옵션 정의

class WriteFragment : Fragment() {
    private lateinit var binding: FragmentWriteBinding

    private var year: String="2024"
    private var month: String="7"
    private var day: String="25"

    private val galleryCode = 100
    private val selectedImages=mutableListOf<Uri>()

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 바인딩 설정
        binding = FragmentWriteBinding.inflate(inflater, container, false)

        binding.writeSettingRg.check(R.id.write_setting_rb)
        //현재 날짜를 받아 textView에 반영
        getCurrentDate()

        //radioGroup에 체인지 리스너 설정
        binding.writeSettingRg.setOnCheckedChangeListener{ _, checkedId ->
            settingOption=checkedId

            //체크한 옵션에 따라 textView 설정
            when(checkedId) {
                R.id.write_setting_rb -> {
                    binding.writeCalendarBtn.visibility=View.VISIBLE
                    binding.writeDateTv.text= "$year.$month.$day"
                }
                R.id.write_no_setting_rb -> {
                    binding.writeDateTv.text="즉시 열람 가능"
                    binding.writeCalendarBtn.visibility=View.GONE
                }
            }
        }

        // 업로드 버튼 클릭시 사진 선택 창 출력
        binding.writeUploadBtn.setOnClickListener {
            openGallery()
        }
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
        //오늘의 년도, 월, 일을 변수에 저장
        val currentDate= LocalDate.now()

        //오늘의 년도, 월, 일을 저장
        year=currentDate.year.toString()
        month=currentDate.monthValue.toString()
        day=currentDate.dayOfMonth.toString()

        binding.writeDateTv.text= "$year.$month.$day"
    }

    @Suppress("DEPRECATION")
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply { //데이터 요청
            type = "image/*"    //데이터 유형은 이미지
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)    //이미지를 하나만 선택할 수 있도록 지정
        }
        startActivityForResult(intent, galleryCode)
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == galleryCode && resultCode == Activity.RESULT_OK) {
            // 사용자가 단일 이미지를 선택한 경우 처리
            data?.data?.let { uri ->
                addImageToLayout(uri)
            }
        }
    }


    private fun addImageToLayout(uri: Uri) {
        // 최대 이미지 개수 1개로 제한
        if (selectedImages.size >= 1) return

        val imageView = binding.writeImageIv
        val layoutParams = imageView.layoutParams as ConstraintLayout.LayoutParams  //ConstraintLayout.LayoutParams로 형 변환
        layoutParams.width = dpToPx() // 200dp를 픽셀로 변환
        layoutParams.height = dpToPx() // 200dp를 픽셀로 변환
        imageView.layoutParams = layoutParams   //이미지뷰에 변경된 레이아웃 파라미터 적용

        // ImageView에 이미지 설정
        imageView.setImageURI(uri)

        // 선택된 이미지 목록에 추가
        selectedImages.add(uri)
    }

    // dp를 px로 변환하는 메서드
    private fun dpToPx(): Int {
        val density = resources.displayMetrics.density  //디스플레이의 밀도 저장
        return (200 * density).toInt()  //200dp로 설정
    }
}
