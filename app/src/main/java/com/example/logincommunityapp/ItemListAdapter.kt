package com.example.logincommunityapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.logincommunityapp.databinding.CommunitySampleBinding


class ItemListAdapter(private val itemList: List<Item>, private val onItemLongClickListener: (Item) -> Unit) : ListAdapter<Item, ItemListAdapter.ItemViewHolder>(ItemDiffCallBack()) {
    class ItemDiffCallBack : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListAdapter.ItemViewHolder {
        val binding = CommunitySampleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemListAdapter.ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnLongClickListener {
            onItemLongClickListener(item)
            true
        }
    }

    class ItemViewHolder(private val binding: CommunitySampleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            with(binding) {
                ivProfileImage.setImageResource(item.profileImage)
                textView.text = item.idText
                textMain.text = item.content
                tvHeart.text = item.heartCount
                tvAnswer.text = item.answerCount
            }
        }
    }


}