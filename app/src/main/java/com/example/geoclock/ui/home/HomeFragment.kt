package com.example.geoclock.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geoclock.R
import com.example.geoclock.databinding.FragmentHomeBinding
import com.example.geoclock.model.Card
import com.example.geoclock.repos.firebaseImpl.AuthRepositoryFirebase
import com.example.geoclock.repos.firebaseImpl.CardRepositoryFirebase
import com.example.geoclock.util.Resource
import com.example.geoclock.util.autoCleared
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var binding : FragmentHomeBinding by autoCleared()
    private val viewModel : HomeViewModel by viewModels{
        HomeViewModel.HomeViewModelFactory(AuthRepositoryFirebase(), CardRepositoryFirebase())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.btnStart.setOnClickListener {
            showAddCardDialog()
        }

        binding.btnLogOut.setOnClickListener{
            showLogoutConfirmationDialog()
        }

        return binding.root
    }

    private fun showAddCardDialog() {
        viewModel.getDefaultTitle { defaultTitle ->
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_card_dialog, null)
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("Confirm") { _, _ ->
                    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    viewModel.addCard(defaultTitle, currentDate, currentTime)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
            alertDialogBuilder.show()
        }
    }

    private fun showLogoutConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Logout Confirmation")
            .setMessage("Are you sure you want to logout?")
            .setCancelable(false)
            .setPositiveButton("Confirm") { _, _ ->
                viewModel.sighOut()
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        alertDialogBuilder.show()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        binding.recycler.adapter = CardsAdapter(object : CardsAdapter.CardListener{
            override fun onCardClicked(card : Card) {

            }

            override fun onCardLongClicked(card : Card) {
                viewModel.deleteCard(card.cardId)
            }
        }

        )

        viewModel.cardStatus.observe(viewLifecycleOwner) {
            when(it){
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.btnStart.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    binding.btnStart.isEnabled = true
                    (binding.recycler.adapter as CardsAdapter).setCards(it.data!!)
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    binding.btnStart.isEnabled = true
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.addCardStatus.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    Snackbar.make(binding.coordinator,"Card updated", Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.deleteCardStatus.observe(viewLifecycleOwner) {
            when(it){
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    Snackbar.make(binding.coordinator,"Card deleted", Snackbar.LENGTH_SHORT).setAction("Undo"){
                        Toast.makeText(requireContext(), "To implement", Toast.LENGTH_SHORT).show()
                    }.show()
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                }
            }
        }

    }



}
