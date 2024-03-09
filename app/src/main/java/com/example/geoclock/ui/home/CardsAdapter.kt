package com.example.geoclock.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.geoclock.databinding.CardLayoutBinding
import com.example.geoclock.model.Card

class CardsAdapter(private val callBack:TaskListener) : RecyclerView.Adapter<CardsAdapter.JobViewHolder>() {

    private val jobs = ArrayList<Card>()

    fun setJobs(jobs: Collection<Card>){
        this.jobs.clear()
        this.jobs.addAll(jobs)
        notifyDataSetChanged()
    }
    interface TaskListener {
        fun onJobClicked (index:Int)
        fun onJobLongClicked(index: Int)
    }

    inner class JobViewHolder(private val binding: CardLayoutBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener, View.OnLongClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)

        }

        fun bind(card: Card){
            binding.textUserName.text = card.userName
            binding.textDate.text = card.dateTime.toString()

        }

        override fun onClick(v: View?) {
            callBack.onJobClicked(adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            callBack.onJobLongClicked(adapterPosition)
            return false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = JobViewHolder(
        CardLayoutBinding.inflate(LayoutInflater.from(parent.context),
        parent,
        false)
    )

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) = holder.bind(jobs[position])

    override fun getItemCount() = jobs.size

}