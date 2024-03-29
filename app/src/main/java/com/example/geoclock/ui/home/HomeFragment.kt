package com.example.geoclock.ui.home

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geoclock.R
import com.example.geoclock.databinding.FragmentHomeBinding
import com.example.geoclock.model.Card
import com.example.geoclock.repos.firebaseImpl.AuthRepositoryFirebase
import com.example.geoclock.repos.firebaseImpl.CardRepositoryFirebase
import com.example.geoclock.util.Resource
import com.example.geoclock.util.autoCleared
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding by autoCleared()
    private var deletedCard: Card? = null
    private lateinit var fusedLocationClient : FusedLocationProviderClient

    private var defaultTitle: String = ""
    private var currentDate: String = ""
    private var currentTime: String = ""
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModel.HomeViewModelFactory(AuthRepositoryFirebase(), CardRepositoryFirebase())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        binding.btnStart.setOnClickListener {
            showAddCardDialog()
        }

        binding.btnLogOut.setOnClickListener {
            showLogoutConfirmationDialog()
        }


        return binding.root
    }

//    private fun showAddCardDialog() {
//        viewModel.getDefaultTitle { defaultTitle ->
//            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_card_dialog, null)
//            val alertDialogBuilder = AlertDialog.Builder(requireContext())
//                .setView(dialogView)
//                .setCancelable(false)
//                .setPositiveButton("Confirm") { _, _ ->
//                    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
//                    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
//                    viewModel.addCard(defaultTitle, currentDate, currentTime)
//                }
//                .setNegativeButton("Cancel") { dialog, _ ->
//                    dialog.dismiss()
//                }
//            alertDialogBuilder.show()
//        }
//    }

    //Function to ask for location permission
    private fun requestLocationPermissionIfNeeded() {
        if (!isLocationPermissionGranted()) {
            requestLocationPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    //Function for adding a card with location
    private fun showAddCardDialog() {
        viewModel.getDefaultTitle { defaultTitle ->
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_card_dialog, null)
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.confirm_dialog)) { _, _ ->
                    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

                    // Check if location permission is granted
                    if (isLocationPermissionGranted()) {
                        // If permission granted, fetch location and add card
                        fetchLocationAndAddCard(defaultTitle, currentDate, currentTime)
                    } else {
                        // If permission not granted, show a message or handle it as needed
                        Toast.makeText(requireContext(), getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton(getString(R.string.cancel_dialog)) { dialog, _ ->
                    dialog.dismiss()
                }
            alertDialogBuilder.show()
        }
    }

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission is granted, do nothing here, the location will be fetched when needed
            } else {
                Toast.makeText(requireContext(),
                    getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }

    //Function which checks whether or not the user has permitted the use of location services
    private fun isLocationPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    //Function for fetching the users location which only works if the user granted permission for location services
    private fun fetchLocationAndAddCard(defaultTitle: String, currentDate: String, currentTime: String) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude

                    // Use Geocoder to get the address from latitude and longitude
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    try {
                        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                        if (addresses != null) {
                            if (addresses.isNotEmpty()) {
                                val address = addresses[0].getAddressLine(0)
                                val locationString = getString(R.string.location)+address
                                // Call viewModel.addCard with obtained location
                                viewModel.addCard(defaultTitle, currentDate, currentTime, locationString)
                            } else {
                                Toast.makeText(requireContext(),
                                    getString(R.string.address_not_found), Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: IOException) {
                        Toast.makeText(requireContext(),
                            getString(R.string.error_fetching_address), Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                } ?: run {
                    Toast.makeText(requireContext(),
                        getString(R.string.unable_to_fetch_location), Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(),
                    getString(R.string.location_fetch_failed)+"${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLogoutConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.logout_confirmation))
            .setMessage(getString(R.string.are_you_sure_you_want_to_logout))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.confirm_dialog)) { _, _ ->
                viewModel.signOut()
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
            .setNegativeButton(getString(R.string.cancel_dialog)) { dialog, _ ->
                dialog.dismiss()
            }
        alertDialogBuilder.show()
    }

    private fun showDeleteCardConfirmationDialog(card: Card) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_card_confirmation))
            .setMessage(getString(R.string.delete_card_message))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.confirm_dialog)) { _, _ ->
                deletedCard = card
                viewModel.deleteCard(card.cardId)
            }
            .setNegativeButton(getString(R.string.cancel_dialog)) { dialog, _ ->
                dialog.dismiss()
            }
        alertDialogBuilder.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        binding.recycler.adapter = CardsAdapter(object : CardsAdapter.CardListener {
            override fun onCardClicked(card: Card) {
                //TODO Implement card click action
            }

            override fun onCardLongClicked(card: Card) {
                showDeleteCardConfirmationDialog(card)
            }
        })

        //Interface for adding swipe to delete functionality
        ItemTouchHelper(object : ItemTouchHelper.Callback(){
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) = makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val card = (binding.recycler.adapter as CardsAdapter).getCards()[position]
                showDeleteCardConfirmationDialog(card)
            }
        }).attachToRecyclerView(binding.recycler)

        requestLocationPermissionIfNeeded()

        viewModel.cardStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.btnStart.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    binding.btnStart.isEnabled = true
                    (binding.recycler.adapter as CardsAdapter).setCards(resource.data!!)
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    binding.btnStart.isEnabled = true
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.addCardStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    Snackbar.make(binding.coordinator,
                        getString(R.string.card_updated), Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.deleteCardStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    val snackbar = Snackbar.make(binding.coordinator,
                        getString(R.string.card_deleted), Snackbar.LENGTH_SHORT)
                    snackbar.setAction(getString(R.string.undo)) {
                        deletedCard?.let { restoredCard ->
                            viewModel.addCard(restoredCard.title, restoredCard.date, restoredCard.time,restoredCard.location)
                            deletedCard = null
                        }
                    }
                    snackbar.show()
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}
