package com.example.timecapsule

import RetrofitClient
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.timecapsule.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(), OnMapReadyCallback {
    //바인딩 설정
    private lateinit var binding: FragmentHomeBinding

    //네이버 맵 객체
    private lateinit var naverMap: NaverMap
    //FusedLocationProviderClient 설정
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //타임캡슐 데이터 리스트 정의
    private var homeTimeCapsuleData = ArrayList<HomeTimeCapsule>()
    private var homeTimeCapsuleDetailData = ArrayList<HomeTimeCapsuleDetail>()

    //인증 토큰 정의
    private var bearerToken: String = ""

    //마커 아이콘 크기 정의
    private var iconSize: Int = 0

    // 위도와 경도를 받을 변수
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 바인딩 설정
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // 위치 권한 요청 런처
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                // 권한이 허용된 경우 위치 가져오기
                getLastLocation()
            } else {
                // 위치 권한이 없을 경우 로그인 화면으로 전환
                Toast.makeText(requireContext(), "위치 권한이 없습니다. 설정에서 권한을 설정해주세요.", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
            }
        }

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

        //MapFragment 초기화
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }
        mapFragment.getMapAsync(this)

        try {
            // HomeTimeCapsuleDetailDialog에서 보낸 캡슐의 id를 받아옴
            parentFragmentManager.setFragmentResultListener("detailId", this) { _, bundle ->
                val result = bundle.getInt("postId")

                //ReadDetailFragment로 이동
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frameLayout, ReadDetailFragment().apply {

                        // 게시글 아이디를 상세보기 페이지로 전달
                        arguments = Bundle().apply {
                            val gson = Gson()
                            val viewableCapsuleJson = gson.toJson(result)
                            putString("viewableCapsuleJson", viewableCapsuleJson)
                        }
                    }).commitAllowingStateLoss()
            }
        } catch (e: Exception) {
            Log.d("글쓰기에서 넘어왔을 때", "데이터 없음")
        }

        return binding.root
    }

    //맵이 준비되었다면
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        //카메라를 현재 위치로 이동
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude, longitude))
        naverMap.moveCamera(cameraUpdate)

        //카메라 이동 시 마커 크기 업데이트
        naverMap.addOnCameraChangeListener { _, _ ->
            updateMarkerSizes()
        }

        //타임캡슐 데이터 초기화
        homeTimeCapsuleDetailData.clear()
        homeTimeCapsuleData.clear()

        //타임캡슐 데이터와 상세 데이터가 같으면 열람 가능한 Api 호출
        if (homeTimeCapsuleDetailData.size == homeTimeCapsuleData.size) {
            callReadViewableApi()
        }
    }

    //줌 레벨에 따라 마커 크기 계산
    private fun updateMarkerSizes() {
        val zoomLevel = naverMap.cameraPosition.zoom
        iconSize = calculateMarkerSize(zoomLevel)
    }

    //줌 레벨에 따라 크기 지정
    private fun calculateMarkerSize(zoomLevel: Double): Int {
        return when {
            zoomLevel < 10 -> 300
            zoomLevel < 15 -> 250
            else -> 200
        }
    }

    //마커를 호출하는 함수
    private fun getMarker(count: Int) {
        //마커 리스트 생성
        val markers = mutableListOf<Marker>()

        for (i in 0 until count) {
            try {
                val detailData = homeTimeCapsuleDetailData.getOrNull(i) //변수 설정

                if (detailData != null) {
                    val marker = Marker().apply {
                        //위도와 경도 변수에 저장
                        val lat = detailData.latitude
                        val lnt = detailData.longitude

                        position = LatLng(lat, lnt) //마커 위치 지정
                        map = naverMap

                        //마커 이미지와 크기 지정
                        icon = OverlayImage.fromResource(R.drawable.icon_marker)
                        width = iconSize / 2
                        height = iconSize
                    }
                    markers.add(marker) //마커 리스트에 마커 추가

                    //마커를 클릭했을 때 각 데이터로 dialog 팝업
                    marker.setOnClickListener {
                        val id = detailData.id
                        val title = detailData.title
                        val viewableAt = detailData.viewableAt
                        val latitude = detailData.latitude
                        val longitude = detailData.longitude
                        val daysLeft = detailData.daysLeft
                        popupCapsule(id, title, viewableAt, latitude, longitude, daysLeft)
                        false
                    }
                } else {
                    Toast.makeText(requireContext(),"데이터를 불러오는 데 실패했습니다. 다시 시도해주세요.",Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.d("에러", "${e.message}")
            }
        }
    }

    //캡슐 dialog 팝업 및 각 데이터 전달
    private fun popupCapsule(id: Int, title: String, viewableAt: String, latitude: Double, longitude: Double, daysLeft: Int) {
        val dialog = HomeTimeCapsuleDetailDialog().apply {
            arguments = Bundle().apply {
                putInt("capsuleId", id)
                putString("capsuleTitle", title)
                putString("capsuleViewableAt", viewableAt)
                putDouble("capsuleLatitude", latitude)
                putDouble("capsuleLongitude", longitude)
                putInt("capsuleDaysLeft", daysLeft)
            }
        }
        dialog.show(parentFragmentManager, "CapsuleDialog")
    }

    private fun getLastLocation() {
        try {
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
                //위치 가져오기
                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val location = task.result

                        //위도와 경도에 대입
                        latitude = location?.latitude!!
                        longitude = location.longitude
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("에러", "${e.message}")
        }
    }

    //열람 가능한 타임캡슐 데이터를 받아옴
    private fun callReadViewableApi() {
        //로그인에서 보낸 SharedPreference로 accessToken 가져오고 BearerToken 형식으로 저장
        val accessToken = getAccessToken()
        bearerToken = "Bearer $accessToken"

        //서버 연동
        RetrofitClient.Service.getViewableTimeCapsules(bearerToken, "전체").enqueue(object :
            Callback<List<ViewableCapsule>> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<List<ViewableCapsule>>,
                response: Response<List<ViewableCapsule>>
            ) {
                if (response.isSuccessful) {
                    val viewableTimeCapsule = response.body()

                    // ViewableTimeCapsule 데이터를 초기화하고 각 항목을 데이터 클래스에 저장
                    homeTimeCapsuleDetailData.clear()
                    viewableTimeCapsule?.let { capsules ->
                        homeTimeCapsuleData.addAll(capsules.map { item ->
                            HomeTimeCapsule(
                                id = item.id
                            )
                        })
                    }
                    callReadUnviewableApi()
                } else {
                    Log.d("에러", "Response failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ViewableCapsule>>, t: Throwable) {
                Log.d("에러", "API call failed: ${t.message}")
            }
        })
    }

    // 열람 불가능한 타임캡슐 데이터를 받아옴
    private fun callReadUnviewableApi() {
        // 서버 연동
        RetrofitClient.Service.getUnviewableTimeCapsules(bearerToken, "전체").enqueue(object :
            Callback<List<UnviewableCapsule>> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<List<UnviewableCapsule>>,
                response: Response<List<UnviewableCapsule>>
            ) {
                if (response.isSuccessful) {
                    val unviewableCapsule = response.body()

                    // UnviewableTimeCapsule 데이터를 초기화하고 각 항목을 데이터 클래스에 저장
                    unviewableCapsule?.let { capsules ->
                        homeTimeCapsuleData.addAll(capsules.map { item ->
                            HomeTimeCapsule(
                                id = item.id,
                            )
                        })
                    }

                    //타임캡슐 데이터의 크기만큼 반복하여 id값으로 Api 호출
                    for (i in 0 until homeTimeCapsuleData.size) {
                        val id = homeTimeCapsuleData[i].id
                        callHomeApi(id)
                    }
                } else {
                    Log.d("에러", "Response failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<UnviewableCapsule>>, t: Throwable) {
                Log.d("에러", "API call failed: ${t.message}")
            }
        })
    }

    //타임캡슐 id별로 데이터를 요청하는 Api 연동
    private fun callHomeApi(id: Int) {
        RetrofitClient.Service.getHomeTimeCapsule(bearerToken, id).enqueue(object : Callback<HomeTimeCapsuleDetail> {
            override fun onResponse(
                call: Call<HomeTimeCapsuleDetail>,
                response: Response<HomeTimeCapsuleDetail>
            ) {
                if (response.isSuccessful) {
                    val homeTimeCapsuleDetail = response.body()

                    //homeTimeCapsuleDetail 데이터 클래스에 각 항목을 저장
                    homeTimeCapsuleDetail?.let { item ->
                        homeTimeCapsuleDetailData.add(
                            HomeTimeCapsuleDetail(
                                id = item.id,
                                title = item.title,
                                viewableAt = item.viewableAt,
                                latitude = item.latitude,
                                longitude = item.longitude,
                                daysLeft = item.daysLeft
                            )
                        )
                    }

                    //더 이상 homeTimeCapsuleDetail 데이터 클래스에 추가할 항목이 없을 경우 마커를 띄움
                    if (homeTimeCapsuleDetailData.size == homeTimeCapsuleData.size) {
                        for (i in homeTimeCapsuleDetailData.indices) {
                            getMarker(i + 1) // i + 1로 변경
                        }
                    }
                }
            }

            override fun onFailure(call: Call<HomeTimeCapsuleDetail>, t: Throwable) {
                Log.d("에러", "${t.message}")
            }
        })
    }

    // 로그인에서 보낸 SharedPreference로 accessToken 가져오기
    private fun getAccessToken(): String? {
        val sharedPreferences = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getString(LoginActivity.ACCESS_TOKEN, null)
    }
}

