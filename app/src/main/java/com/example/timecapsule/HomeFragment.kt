package com.example.timecapsule

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.timecapsule.databinding.FragmentHomeBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons

class HomeFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var naverMap: NaverMap
    private val marker=Marker()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //바인딩 설정
        binding=FragmentHomeBinding.inflate(inflater, container, false)

        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(naverMap: NaverMap){
        this.naverMap=naverMap
        val cameraUpdate=CameraUpdate.scrollTo(LatLng(37.6492802,127.1168727))
        naverMap.moveCamera(cameraUpdate)
        marker.position= LatLng(37.6492802,127.1168727)
        marker.map=naverMap
        marker.icon=MarkerIcons.BLACK
        marker.iconTintColor= Color.RED
    }
}