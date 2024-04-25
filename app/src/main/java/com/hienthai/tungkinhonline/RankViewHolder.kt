package com.hienthai.tungkinhonline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hienthai.tungkinhonline.databinding.ItemRankBinding

class RankViewHolder(
    private val binding: ItemRankBinding
) : RecyclerView.ViewHolder(binding.root) {

    var item: User? = null
        private set

    fun onBind(item: User) {
        this.item = item

        binding.run {
            tvStt.text = (bindingAdapterPosition + 1).toString()
            tvUsername.text = item.username
            tvCount.text = item.count.toString()

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