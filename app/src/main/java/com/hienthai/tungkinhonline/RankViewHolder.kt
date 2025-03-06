package com.hienthai.tungkinhonline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hienthai.tungkinhonline.databinding.ItemRankBinding

class RankViewHolder(
    private val binding: ItemRankBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var item: User? = null

    fun onBind(item: User) {
        this.item = item

        binding.run {
            tvStt.text = (bindingAdapterPosition + 1).toString()
            tvUsername.text = item.username
            tvCount.text = item.count.toString()
            if (item.myPosition == true) {
                tvUsername.setTextColor(ContextCompat.getColor(tvUsername.context, R.color.red))
            } else {
                tvUsername.setTextColor(ContextCompat.getColor(tvUsername.context, R.color.black))
            }
            if (item.myPosition == true) {
                tvStt.setTextColor(ContextCompat.getColor(tvUsername.context, R.color.red))
            } else {
                tvStt.setTextColor(ContextCompat.getColor(tvUsername.context, R.color.black))
            }
            if (item.myPosition == true) {
                tvCount.setTextColor(ContextCompat.getColor(tvUsername.context, R.color.red))
            } else {
                tvCount.setTextColor(ContextCompat.getColor(tvUsername.context, R.color.black))
            }

            tvTextRank.text = tvTextRank.context.getString(Rank.getRank(item.count ?: 0L))
        }
    }
    companion object {
        fun create(parent: ViewGroup) = ItemRankBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let { RankViewHolder(it) }
    }
}