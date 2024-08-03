package com.example.timecapsule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.timecapsule.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    //바인딩 설정
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //화면 설정
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //네비게이션 실행
        initBottomNavigation()

        // 소프트 키보드 동작 방식을 SOFT_INPUT_ADJUST_PAN으로 설정
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun initBottomNavigation(){

        //homeFragment를 기본으로 설정
        binding.mainBnv.selectedItemId = R.id.homeFragment
        supportFragmentManager.beginTransaction().replace(R.id.main_frameLayout, HomeFragment()).commitAllowingStateLoss()

        //선택된 아이템에 따라 fragment 전환
        binding.mainBnv.setOnItemSelectedListener { item->
            when (item.itemId){
                R.id.homeFragment->{
                    supportFragmentManager.beginTransaction().replace(R.id.main_frameLayout, HomeFragment()).commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.writeFragment->{
                    supportFragmentManager.beginTransaction().replace(R.id.main_frameLayout, WriteFragment()).commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.readFragment->{
                    supportFragmentManager.beginTransaction().replace(R.id.main_frameLayout, ReadFragment()).commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    //외부에서 네비게이션 아이템을 설정할 수 있는 함수
    fun setSelectedNavItem(itemId: Int) {
        binding.mainBnv.selectedItemId = itemId
    }
}