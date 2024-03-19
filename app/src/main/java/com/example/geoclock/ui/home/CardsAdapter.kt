package com.example.geoclock.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.geoclock.databinding.CardLayoutBinding
import com.example.geoclock.model.Card

class CardsAdapter(private val callBack:TaskListener) : RecyclerView.Adapter<CardsAdapter.CardViewHolder>() {

    private val cards = ArrayList<Card>()

    fun setCards(cards: Collection<Card>){
        this.cards.clear()
        this.cards.addAll(cards)
        notifyDataSetChanged()
    }
    interface TaskListener {
        fun onCardClicked (index:Int)
        fun onCardLongClicked(index: Int)
    }

    inner class CardViewHolder(private val binding: CardLayoutBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener, View.OnLongClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)

        }

        fun bind(card: Card){
            binding.textUserName.text = card.userName
            binding.textDate.text = card.date.toString()

        }

        override fun onClick(v: View?) {
            callBack.onCardClicked(adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            callBack.onCardLongClicked(adapterPosition)
            return false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CardViewHolder(
        CardLayoutBinding.inflate(LayoutInflater.from(parent.context),
        parent,
        false)
    )

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) = holder.bind(cards[position])

    override fun getItemCount() = cards.size

}