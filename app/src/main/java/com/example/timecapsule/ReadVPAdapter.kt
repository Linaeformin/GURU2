package com.example.timecapsule

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ReadVPAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    //탭 레이아웃 아이템 개수를 2로 설정
    override fun getItemCount(): Int=2

    //첫 번째 탭은 열람 가능, 두 번째 탭은 열람 불가능한 프래그먼트로 전환
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> ReadViewableFragment()
            else-> ReadUnviewableFragment()
        }
    }
}