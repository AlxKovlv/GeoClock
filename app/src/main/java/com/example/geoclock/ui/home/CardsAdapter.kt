package com.example.geoclock.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.geoclock.databinding.CardLayoutBinding
import com.example.geoclock.model.Card
import java.text.SimpleDateFormat
import java.util.*

class CardsAdapter(private val callBack: CardListener) : RecyclerView.Adapter<CardsAdapter.CardViewHolder>() {

    private val cards = ArrayList<Card>()

    fun setCards(cards: Collection<Card>) {
        this.cards.clear()
        this.cards.addAll(cards)
        notifyDataSetChanged()
    }

    interface CardListener {
        fun onCardClicked(card: Card)
        fun onCardLongClicked(card: Card)
    }

    inner class CardViewHolder(private val binding: CardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)
        }

        fun bind(card: Card) {
            binding.textUserName.text = card.userName

            // Convert timestamp string to a formatted date string
            val dateInMillis = card.date.toLongOrNull()
            val formattedDate = if (dateInMillis != null) {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = dateInMillis
                sdf.format(calendar.time)
            } else {
                "Invalid Date"
            }
            binding.textDate.text = formattedDate
        }

        override fun onClick(v: View?) {
            callBack.onCardClicked(cards[adapterPosition])
        }

        override fun onLongClick(v: View?): Boolean {
            callBack.onCardLongClicked(cards[adapterPosition])
            return false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardLayoutBinding.inflate(inflater, parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount(): Int {
        return cards.size
    }
}
