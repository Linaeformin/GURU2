package com.example.timecapsule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.timecapsule.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainBnv.selectedItemId = R.id.homeFragment
        initBottomNavigation()
    }

    private fun initBottomNavigation(){
        supportFragmentManager.beginTransaction().replace(R.id.main_frameLayout, HomeFragment()).commitAllowingStateLoss()

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