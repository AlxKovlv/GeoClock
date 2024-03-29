package com.example.geoclock.ui.reports

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geoclock.R
import com.example.geoclock.databinding.FragmentHomeBinding
import com.example.geoclock.databinding.FragmentReportsBinding
import com.example.geoclock.model.Card
import com.example.geoclock.repos.CardRepository
import com.example.geoclock.repos.firebaseImpl.AuthRepositoryFirebase
import com.example.geoclock.repos.firebaseImpl.CardRepositoryFirebase
import com.example.geoclock.ui.home.CardsAdapter
import com.example.geoclock.ui.home.HomeViewModel
import com.example.geoclock.util.Resource
import com.example.geoclock.util.autoCleared
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReportsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReportsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var binding:FragmentReportsBinding by autoCleared()

    private val viewModel: ReportsViewModel by viewModels {
        ReportsViewModel.ReportsViewModelFactory(CardRepositoryFirebase())
    }
    private var selectedButtonId: Int = 0
    private var startDate: String = ""
    private var endDate: String = ""

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReportsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReportsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReportsBinding.inflate(inflater, container, false)

        binding.btnStartDate.setOnClickListener {
            selectedButtonId = R.id.btn_start_date
            showDatePickerDialog()
        }

        binding.btnEndDate.setOnClickListener {
            selectedButtonId = R.id.btn_end_date
            showDatePickerDialog()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_reportsFragment_to_homeFragment)
        }

        binding.btnShowCards.setOnClickListener {
           if(startDate != "" && endDate != ""){
                viewModel.getFilteredCards(startDate, endDate)
            }else{
                Toast.makeText(requireContext(),"Please enter both start and end date",Toast.LENGTH_SHORT).show()
            }
            Log.d("TAG", "start date: $startDate, end date: $endDate")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        binding.recycler.adapter = CardsAdapter(object : CardsAdapter.CardListener {
            override fun onCardClicked(card: Card) {

            }

            override fun onCardLongClicked(card: Card) {

            }
        })

        // Observe filtered cards
        viewModel.filteredCards.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    // Update RecyclerView with filtered cards
                    (binding.recycler.adapter as? CardsAdapter)?.setCards(resource.data ?: emptyList())
                }
                is Resource.Error -> {
                    // Handle error
                    Toast.makeText(requireContext(), resource.message ?: "Filtered card error", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Show loading indicator if needed
                }
            }
        }
    }

    fun showDatePickerDialog() {
        val calendarInstance = Calendar.getInstance()
        val listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val selectedDate = "${formatTwoDigits(dayOfMonth)}/${formatTwoDigits(month + 1)}/$year"
            when (selectedButtonId) {
                R.id.btn_start_date -> {
                    binding.btnStartDate.text = selectedDate
                    startDate = selectedDate
                }
                R.id.btn_end_date -> {
                    binding.btnEndDate.text = selectedDate
                    endDate = selectedDate
                }
            }
        }
        val datePickerDialog = DatePickerDialog(
            requireContext(), listener,
            calendarInstance.get(Calendar.YEAR),
            calendarInstance.get(Calendar.MONTH),
            calendarInstance.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun formatTwoDigits(number: Int): String {
        return if (number < 10) "0$number" else number.toString()
    }

}