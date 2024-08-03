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
    private lateinit var binding: FragmentHomeBinding
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var homeTimeCapsuleData = ArrayList<HomeTimeCapsule>()
    private var homeTimeCapsuleDetailData = ArrayList<HomeTimeCapsuleDetail>()
    private var bearerToken: String = ""
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

        Log.d("강종 위치","SUCCESS1")
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
        Log.d("강종 위치","SUCCESS2")

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

        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment.getMapAsync(this)

        try {
            // CategoryDialog에서 보낸 selectedCategory 내의 카테고리를 textView에 반영
            parentFragmentManager.setFragmentResultListener("detailId", this) { _, bundle ->
                val result = bundle.getInt("postId")
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frameLayout, ReadDetailFragment().apply {
                        // 아이템 아이디를 상세보기 페이지로 전달
                        arguments = Bundle().apply {
                            val gson = Gson()
                            val viewableCapsuleJson = gson.toJson(result)
                            putString("viewableCapsuleJson", viewableCapsuleJson)
                        }
                    }).commitAllowingStateLoss()
            }
        } catch (e: Exception) {
            Log.d("글쓰기에서 넘어왔을 때", "SUCCESS")
        }

        return binding.root
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude, longitude))
        naverMap.moveCamera(cameraUpdate)
        naverMap.addOnCameraChangeListener { _, _ ->
            updateMarkerSizes()
        }
        Log.d("맵","준비")

        homeTimeCapsuleDetailData.clear()
        homeTimeCapsuleData.clear()

        // 지도가 준비되면 마커를 추가합니다.
        if (homeTimeCapsuleDetailData.size == homeTimeCapsuleData.size) {
            Log.d("위치","OnMap")
            //getMarker(homeTimeCapsuleDetailData.size)
            callReadViewableApi()

        }
    }

    private fun updateMarkerSizes() {
        val zoomLevel = naverMap.cameraPosition.zoom
        iconSize = calculateMarkerSize(zoomLevel)
    }

    private fun calculateMarkerSize(zoomLevel: Double): Int {
        return when {
            zoomLevel < 10 -> 300 // Small size for zoomed out
            zoomLevel < 15 -> 250  // Medium size for medium zoom
            else -> 200  // Large size for zoomed in
        }
    }

    private fun getMarker(count: Int) {
        Log.d("강종", "SUCCESS1")
        if (!::naverMap.isInitialized) {
            Log.d("강종", "NaverMap is not initialized")
            return
        }
        val markers = mutableListOf<Marker>()
        Log.d("강종-1", "SUCCESS1")
        Log.d("강종-1-1", "$count")
        for (i in 0 until count) { // 루프 범위를 0 until count로 변경
            try {
                val detailData = homeTimeCapsuleDetailData.getOrNull(i)

                if (detailData != null) {
                    val marker = Marker().apply {
                        val lat = detailData.latitude
                        val lnt = detailData.longitude
                        position = LatLng(lat, lnt)
                        map = naverMap
                        icon = OverlayImage.fromResource(R.drawable.icon_marker)
                        width = iconSize / 2
                        height = iconSize
                        Log.d("강종-2", "SUCCESS1")
                    }

                    Log.d("강종-3", "SUCCESS1")

                    markers.add(marker)

                    Log.d("강종-4", "SUCCESS1")

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

                    Log.d("강종-5", "SUCCESS1")
                } else {
                    Toast.makeText(requireContext(),"데이터를 불러오는 데 실패했습니다. 다시 시도해주세요.",Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.d("강종의 원인", "${e.message}")
            }
        }
    }

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
                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val location = task.result
                        latitude = location?.latitude!!
                        longitude = location.longitude
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("강종 에러", "${e.message}")
        }
    }

    // 열람 가능한 타임캡슐 데이터를 받아옴
    private fun callReadViewableApi() {
        // 로그인에서 보낸 SharedPreference로 accessToken 가져오고 BearerToken 형식으로 저장
        val accessToken = getAccessToken()
        bearerToken = "Bearer $accessToken"

        Log.d("토큰", bearerToken)
        // 서버 연동
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
                    Log.d("볼 수 있는","SUCCESS")
                    callReadUnviewableApi()
                } else {
                    Log.d("볼 수 있는", "Response failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ViewableCapsule>>, t: Throwable) {
                Log.d("볼 수 있는", "API call failed: ${t.message}")
            }
        })
    }

    // 열람 불가능한 타임캡슐 데이터를 받아옴
    private fun callReadUnviewableApi() {
        Log.d("볼 수 없는","SUCCESS")
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
                    Log.d("볼 수 없는","SUCCESS1")
                    for (i in 0 until homeTimeCapsuleData.size) {
                        val id = homeTimeCapsuleData[i].id
                        callHomeApi(id)
                    }
                    Log.d("볼 수 없는","SUCCESS2")
                } else {
                    Log.d("볼 수 없는", "Response failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<UnviewableCapsule>>, t: Throwable) {
                Log.d("볼 수 없는", "API call failed: ${t.message}")
            }
        })
    }

    private fun callHomeApi(id: Int) {
        Log.d("홈 호출","SUCCESS")
        RetrofitClient.Service.getHomeTimeCapsule(bearerToken, id).enqueue(object : Callback<HomeTimeCapsuleDetail> {
            override fun onResponse(
                call: Call<HomeTimeCapsuleDetail>,
                response: Response<HomeTimeCapsuleDetail>
            ) {
                if (response.isSuccessful) {
                    val homeTimeCapsuleDetail = response.body()

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
                    val size=homeTimeCapsuleData.size
                    val size2=homeTimeCapsuleDetailData.size
                    Log.d("홈 data","$size")
                    Log.d("홈 detail","$size2")
                    if (homeTimeCapsuleDetailData.size == homeTimeCapsuleData.size) {
                        for (i in homeTimeCapsuleDetailData.indices) {
                            Log.d("위치","OnCallHome")
                            getMarker(i + 1) // i + 1로 변경
                        }
                    }
                } else {
                    Log.d("홈", "fail")
                }
            }

            override fun onFailure(call: Call<HomeTimeCapsuleDetail>, t: Throwable) {
                Log.d("홈", "fail1: ${t.message}")
            }
        })
    }

    // 로그인에서 보낸 SharedPreference로 accessToken 가져오기
    private fun getAccessToken(): String? {
        val sharedPreferences = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getString(LoginActivity.ACCESS_TOKEN, null)
    }
}

