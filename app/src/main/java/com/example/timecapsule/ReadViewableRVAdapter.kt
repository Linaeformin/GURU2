package com.example.timecapsule

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.timecapsule.databinding.ItemTimecapsuleViewableBinding

class ReadViewableRVAdapter (private val capsuleList: ArrayList<ViewableCapsule>):RecyclerView.Adapter<ReadViewableRVAdapter.ViewHolder>() {

    interface MyItemClickListener{
        fun onItemViewableClick(viewableCapsule: ViewableCapsule)
    }

    private lateinit var mItemClickListener: MyItemClickListener

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
            Log.d("제목",capsule.title)
            //binding.itemReadCapsuleImageIv.setImageResource(capsule.coverImg!!)
        }
    }


}