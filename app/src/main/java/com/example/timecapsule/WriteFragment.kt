package com.example.timecapsule

import RetrofitClient
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.timecapsule.databinding.FragmentWriteBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.time.LocalDate
import android.Manifest
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private var settingOption: Int = 0    //설정 옵션 정의

class WriteFragment : Fragment() {
    //바인딩 설정
    private lateinit var binding: FragmentWriteBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var categoryDialog: CategoryDialog? = null

    //열람 가능한 년도, 월, 일을 받을 변수
    private var year: String = ""
    private var month: String = ""
    private var day: String = ""

    //열람 가능일 변수
    private var openDate: String = ""

    private val galleryCode = 100
    private val selectedImages = mutableListOf<Uri>()
    private var imageUri: Uri? = null

    //위도와 경도를 받을 변수
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    // 위치 권한 요청 런처
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            // 권한이 허용된 경우 위치 가져오기
            getLastLocation()
        }
        else {
            //위치 권한이 없을 경우 로그인 화면으로 전환
            Toast.makeText(requireContext(),"위치 권한이 없습니다. 설정에서 권한을 설정해주세요.",Toast.LENGTH_SHORT).show()
            val intent=Intent(requireActivity(),LoginActivity::class.java)
            startActivity(intent)
        }
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 바인딩 설정
        binding = FragmentWriteBinding.inflate(inflater, container, false)

        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // 위치 권한 확인 및 요청
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한 요청
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            // 권한이 이미 허용된 경우 위치 가져오기
            getLastLocation()
        }

        // 열람 가능일 설정으로 기본 세팅
        binding.writeSettingRg.check(R.id.write_setting_rb)

        // 현재 날짜를 받아 textView에 반영
        getCurrentDate()

        // radioGroup에 체인지 리스너 설정
        binding.writeSettingRg.setOnCheckedChangeListener { _, checkedId ->
            settingOption = checkedId

            // 체크한 옵션에 따라 textView 설정
            when (checkedId) {
                R.id.write_setting_rb -> {
                    binding.writeCalendarBtn.visibility = View.VISIBLE
                    binding.writeDateTv.text = "$year.$month.$day"
                }
                R.id.write_no_setting_rb -> {
                    binding.writeDateTv.text = "즉시 열람 가능"
                    binding.writeCalendarBtn.visibility = View.GONE
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
            if (binding.writeTitleEt.text.toString().isEmpty() || binding.writeContentEt.text.toString().isEmpty() || binding.writeCategoryTv.text.toString() == "설정 안 함") {
                if (binding.writeTitleEt.text.toString().isEmpty()) {
                    Toast.makeText(requireContext(), "제목을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                if (binding.writeContentEt.text.toString().isEmpty()) {
                    Toast.makeText(requireContext(), "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                if (binding.writeCategoryTv.text.toString() == "설정 안 함") {
                    Toast.makeText(requireContext(), "카테고리를 설정하세요.", Toast.LENGTH_SHORT).show()
                }
            } else {
                getOpenDate()   //열람 가능일을 정하는 코드
                callWriteApi()    //서버 api 연동

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

    private fun getLastLocation() {
        val locationCode = 1
        // 위치 권한 확인 및 요청
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                locationCode
            )
        } else {
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val location = task.result
                    latitude = location?.latitude!!
                    longitude = location.longitude
                }
            }
        }
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
                result.let {
                    year = it[0].toString()
                    month = it[1].toString()
                    day = it[2].toString()
                }

                //textView에 반영
                binding.writeDateTv.text = "$year.$month.$day"

            } else {
                //설정한 날짜가 없다면 현재 날짜 유지
                getCurrentDate()
            }
        }
    }

    // 캘린더 팝업창 출력
    private fun popupCalendar() {
        //캘린더에 데이터 전달
        val dialog = CalendarDialog().apply {
            arguments = Bundle().apply {
                putString("calendarDate", "$year-$month-$day")
            }
        }
        dialog.show(parentFragmentManager, "")  //CalendarDialog 실행
    }

    // 카테고리 팝업창 출력
    private fun popupCategory() {
        categoryDialog = CategoryDialog()

        // "설정 안 함"일 때 categoryOption을 0으로 설정
        if (binding.writeCategoryTv.text.toString() == "설정 안 함") {
            val args = Bundle()
            args.putInt("categoryOption", 0)
            categoryDialog!!.arguments = args
        }
        categoryDialog!!.show(parentFragmentManager, "CategoryDialog")
    }

    //현재 날짜를 받아 textView에 반영
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate() {
        //오늘의 년도, 월, 일을 변수에 저장
        val currentDate = LocalDate.now()

        //오늘의 년도, 월, 일을 저장
        year = currentDate.year.toString()
        month = currentDate.monthValue.toString()
        day = currentDate.dayOfMonth.toString()

        //열람 가능일로 textView 저장
        binding.writeDateTv.text = "$year.$month.$day"
    }

    //타임캡슐이 열리는 날짜
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getOpenDate(): String {
        //설정하지 않았을 경우, 열람 가능 날짜를 오늘로 설정
        if(binding.writeDateTv.text == "즉시 열람 가능"){
            getCurrentDate()
        }
        openDate = "$year-${month.padStart(2, '0')}-${day.padStart(2, '0')}T00:00:00"
        return openDate
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

        imageUri = uri
    }

    // dp를 px로 변환하는 메서드
    private fun dpToPx(): Int {
        val density = resources.displayMetrics.density //디스플레이의 밀도 저장
        return (200 * density).toInt() //200dp로 설정
    }


    private fun callWriteApi() {
        //토큰을 저장하는 변수
        val accessToken = getAccessToken()
        val bearerToken = "Bearer $accessToken"

        Log.d("토큰",bearerToken)

        //jsonObject 객체 생성 및 데이터 삽입
        val jsonObject=JSONObject()

        jsonObject.put("title",binding.writeTitleEt.text.toString())
        jsonObject.put("content",binding.writeContentEt.text.toString())
        jsonObject.put("category",binding.writeCategoryTv.text.toString())
        jsonObject.put("viewableAt",openDate)
        jsonObject.put("latitude",latitude)
        jsonObject.put("longitude",longitude)

        // JSON 문자열로 변환
        val jsonString = jsonObject.toString()

        // JSON 문자열을 RequestBody로 변환
        val mediaType = "application/json".toMediaTypeOrNull()
        val requestBody = jsonString.toRequestBody(mediaType)

        //이미지 파일 MultipartBody.Part로 저장하는 변수
        val imageFile = imageUri?.let { File(absolutelyPath(it, requireContext())) }
        val requestFile = imageFile?.asRequestBody("image/*".toMediaTypeOrNull())
        var body = requestFile?.let { MultipartBody.Part.createFormData("imageFile", imageFile.name, it) }

        //이미지를 삽입하지 않았을 때 이미지의 값을 null로 설정
        if(imageFile==null){
            body=null
        }

        //글쓰기 Api 연동
        RetrofitClient.Service.writeTimeCapsule(bearerToken, body, requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "등록되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "등록에 실패했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //로그인에서 보낸 SharedPreference로 accessToken 가져오기
    private fun getAccessToken(): String? {
        val sharedPreferences = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getString(LoginActivity.ACCESS_TOKEN, null)
    }

    //절대 경로를 구하는 함수
    @SuppressLint("Recycle")
    private fun absolutelyPath(path: Uri?, context: Context): String {
        val proj=arrayOf(MediaStore.Images.Media.DATA)
        val c=context.contentResolver.query(path!!, proj, null, null, null)
        val index=c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        val result=c?.getString(index!!)

        return result!!
    }
}
