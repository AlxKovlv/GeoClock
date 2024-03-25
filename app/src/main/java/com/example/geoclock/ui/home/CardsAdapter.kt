package com.example.geoclock.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.geoclock.databinding.CardLayoutBinding
import com.example.geoclock.model.Card
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CardsAdapter(private val callBack: CardListener) : RecyclerView.Adapter<CardsAdapter.CardViewHolder>() {

    private val cards = ArrayList<Card>()

    fun getCards(): List<Card> {
        return cards
    }

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

            // Convert the date string to a Date object
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = try {
                dateFormat.parse(card.date)
            } catch (e: ParseException) {
                null
            }

            // Format the date
            val formattedDate = if (date != null) {
                val formattedDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formattedDateFormat.format(date)
            } else {
                "Invalid Date"
            }

            // Bind
            binding.textDate.text = "Date: $formattedDate"
            binding.textTime.text = "Time: ${card.time}"
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
