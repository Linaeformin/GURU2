package com.example.timecapsule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.timecapsule.databinding.ItemTimecapsuleUnviewableBinding

class ReadUnviewableRVAdapter(private val capsuleList: ArrayList<UnviewableCapsule>): RecyclerView.Adapter<ReadUnviewableRVAdapter.ViewHolder>() {

    //항목 클릭 처리를 위한 인터페이스
    interface MyItemClickListener {
        fun onItemUnviewableClick(unviewableCapsule: UnviewableCapsule)
    }

    //항목 클릭 리스너를 저장
    private lateinit var mItemClickListener: MyItemClickListener

    //외부에서 항목 클릭 리스너를 설정
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ReadUnviewableRVAdapter.ViewHolder {
        //바인딩 설정
        val binding: ItemTimecapsuleUnviewableBinding = ItemTimecapsuleUnviewableBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )

        return ViewHolder(binding)
    }

    //들어온 데이터 값의 크기를 받아오는 코드
    override fun getItemCount(): Int = capsuleList.size

    //클릭한 항목에 데이터 바인딩, 클릭 리스너 설정
    override fun onBindViewHolder(holder: ReadUnviewableRVAdapter.ViewHolder, position: Int) {
        if (position >= 0 && position < capsuleList.size) {
            holder.bind(capsuleList[position])
            holder.itemView.setOnClickListener {
                mItemClickListener.onItemUnviewableClick(capsuleList[position])
            }
        }
    }

    inner class ViewHolder(val binding: ItemTimecapsuleUnviewableBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(capsule: UnviewableCapsule) {
            //제목과 날짜를 각 아이템에 반영
            binding.itemReadCapsuleTitleTv.text = capsule.title
            binding.itemReadCapsuleDayTv.text=capsule.viewableAt
        }
    }
}

