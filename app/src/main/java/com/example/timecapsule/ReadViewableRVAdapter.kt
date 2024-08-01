package com.example.timecapsule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.timecapsule.databinding.ItemTimecapsuleViewableBinding

class ReadViewableRVAdapter (private val capsuleList: ArrayList<ViewableCapsule>):RecyclerView.Adapter<ReadViewableRVAdapter.ViewHolder>() {

    //항목 클릭 처리를 위한 인터페이스
    interface MyItemClickListener{
        fun onItemViewableClick(viewableCapsule: ViewableCapsule)
    }

    //항목 클릭 리스너를 저장
    private lateinit var mItemClickListener: MyItemClickListener

    //외부에서 항목 클릭 리스너를 설정
    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener=itemClickListener
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ReadViewableRVAdapter.ViewHolder {
        //바인딩 설정
        val binding: ItemTimecapsuleViewableBinding=ItemTimecapsuleViewableBinding.inflate(
            LayoutInflater.from(viewGroup.context),viewGroup,false)

        return ViewHolder(binding)
    }

    //들어온 데이터 값의 크기를 받아오는 코드
    override fun getItemCount(): Int=capsuleList.size

    //클릭한 항목에 데이터 바인딩, 클릭 리스너 설정
    override fun onBindViewHolder(holder: ReadViewableRVAdapter.ViewHolder, position: Int) {
        if (position>=0&&position<capsuleList.size){
            holder.bind(capsuleList[position])
            holder.itemView.setOnClickListener{
                mItemClickListener.onItemViewableClick(capsuleList[position])
            }
        }
    }

    inner class ViewHolder(val binding: ItemTimecapsuleViewableBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(capsule: ViewableCapsule){
            //제목과 이미지를 각 아이템에 반영
            binding.itemReadCapsuleTitleTv.text=capsule.title

            //랜덤으로 타임캡슐 디자인 선정
            val range=(1..4)
            when (range.random()) {
                1 -> binding.itemReadCapsuleImageIv.setImageResource(R.drawable.read_timecapsule_1)
                2 -> binding.itemReadCapsuleImageIv.setImageResource(R.drawable.read_timecapsule_2)
                3 -> binding.itemReadCapsuleImageIv.setImageResource(R.drawable.read_timecapsule_3)
                4 -> binding.itemReadCapsuleImageIv.setImageResource(R.drawable.read_timecapsule_4)
            }
        }
    }
}