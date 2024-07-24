package com.example.timecapsule

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.timecapsule.databinding.FragmentWriteBinding
import java.io.ByteArrayOutputStream
import java.time.LocalDate

private var settingOption: Int=0    //설정 옵션 정의

class WriteFragment : Fragment() {
    private lateinit var binding: FragmentWriteBinding

    private var year: String="2024"
    private var month: String="7"
    private var day: String="25"

    private val galleryCode = 100
    private val captureImage=1
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
        val currentDate= LocalDate.now()

        //오늘의 년도, 월, 일을 저장
        year=currentDate.year.toString()
        month=currentDate.monthValue.toString()
        day=currentDate.dayOfMonth.toString()

        binding.writeDateTv.text= "$year.$month.$day"
    }

    @Suppress("DEPRECATION")
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(intent, galleryCode)
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == galleryCode && resultCode == Activity.RESULT_OK) {
            data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    addImageToLayout(imageUri)
                    if (i == 2) break // Allow up to 5 images
                }
            } ?: data?.data?.let { uri ->
                addImageToLayout(uri)
            }
        } else if (requestCode == captureImage && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                val uri = getImageUri(requireContext(), it)
                addImageToLayout(uri)
            }
        }
    }

    private fun addImageToLayout(uri: Uri) {
        // 최대 이미지 개수 제한 (예: 5개)
        if (selectedImages.size >= 5) return

        // ImageView의 크기 설정 (예: 200dp x 200dp)
        val imageView = binding.writeImageIv
        val layoutParams = imageView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.width = dpToPx() // 200dp를 픽셀로 변환
        layoutParams.height = dpToPx() // 200dp를 픽셀로 변환
        imageView.layoutParams = layoutParams

        // ImageView에 이미지 설정
        imageView.setImageURI(uri)

        // 선택된 이미지 목록에 추가
        selectedImages.add(uri)
    }

    // dp를 px로 변환하는 메서드
    private fun dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (200 * density).toInt()
    }

    @Suppress("DEPRECATION")
    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
}
