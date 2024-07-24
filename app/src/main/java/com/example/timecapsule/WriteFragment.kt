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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.timecapsule.databinding.FragmentWriteBinding
import java.time.LocalDate

private var settingOption: Int=0    //설정 옵션 정의

class WriteFragment : Fragment() {
    //바인딩 설정
    private lateinit var binding: FragmentWriteBinding

    //열람 가능일을 받을 변수
    private var year: String=""
    private var month: String=""
    private var day: String=""

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

        //열람 가능일 설정으로 기본 세팅
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

        //등록 버튼을 눌렀을 때
        binding.writePostBtn.setOnClickListener {
            //제목과 내용을 입력하지 않은 채로 등록을 시도하는 경우, 토스트 메시지 출력
            if(binding.writeTitleEt.text.toString().isEmpty()||binding.writeContentEt.text.toString().isEmpty()){
                if(binding.writeTitleEt.text.toString().isEmpty()){
                    Toast.makeText(requireContext(), "제목을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                if(binding.writeContentEt.text.toString().isEmpty()){
                    Toast.makeText(requireContext(), "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            }
            //제목과 내용을 입력하고 등록을 시도한 경우, 토스트 메시지 출력 및 홈으로 화면 전환
            else {
                Toast.makeText(requireContext(),"등록되었습니다.", Toast.LENGTH_SHORT).show()

                //홈으로 화면 전환
                val homeFragment = HomeFragment() // homeFragment 인스턴스 생성
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.main_frameLayout, homeFragment)
                transaction.addToBackStack(null)
                transaction.commit()

                // 바텀 네비게이션에서 홈을 선택한 상태로 설정
                (requireActivity() as MainActivity).setSelectedNavItem(R.id.homeFragment)
            }
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // FragmentResultListener 설정
        parentFragmentManager.setFragmentResultListener("selectedCalendar", this) { _, bundle ->
            val result = bundle.getIntArray("selectedCalendar")

            //각 요소에 해당하는 값에 대입
            if (result != null) {
                result.let{
                    year= it[0].toString()
                    month=it[1].toString()
                    day=it[2].toString()
                }

                //textView에 반영
                binding.writeDateTv.text= "$year.$month.$day"

            } else {
                //설정한 날짜가 없다면 현재 날짜 유지
                getCurrentDate()
            }
        }
    }

    // 캘린더 팝업창 출력
    private fun popupCalendar() {

        //캘린더에 데이터 전달
        val dialog=CalendarDialog().apply {
            arguments=Bundle().apply {
                putString("calendarDate","$year-$month-$day")
            }
        }
        dialog.show(parentFragmentManager, "")  //CalendarDialog 실행
    }

    // 카테고리 팝업창 출력
    private fun popupCategory() {
        val dialog=CategoryDialog().apply {
            arguments=Bundle().apply {
                Bundle().putInt("categoryOption",0)
            }
        }
        dialog.show(parentFragmentManager, "")  //CategoryDialog 실행
    }

    //현재 날짜를 받아 textView에 반영
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate() {
        //오늘의 년도, 월, 일을 변수에 저장
        val currentDate=LocalDate.now()

        //오늘의 년도, 월, 일을 저장
        year=currentDate.year.toString()
        month=currentDate.monthValue.toString()
        day=currentDate.dayOfMonth.toString()

        //열람 가능일로 textView 저장
        binding.writeDateTv.text= "$year.$month.$day"
    }

    //갤러리 오픈
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

    //이미지 화면에 보이게 하는 코드
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
        val density = resources.displayMetrics.density.toInt()  //디스플레이의 밀도 저장
        return (200 * density)  //200dp로 설정
    }
}
