package com.hienthai.tungkinhonline

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

class RankAdapter() : ListAdapter<User, RankViewHolder>(RankDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankViewHolder =
        RankViewHolder.create(parent)

    override fun onBindViewHolder(holder: RankViewHolder, position: Int) =
        holder.onBind(getItem(position))

}