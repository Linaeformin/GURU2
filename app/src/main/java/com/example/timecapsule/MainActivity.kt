package com.example.timecapsule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.timecapsule.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //화면 설정
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //네비게이션 실행
        initBottomNavigation()
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
}