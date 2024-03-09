package com.example.geoclock.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.geoclock.R
import com.example.geoclock.databinding.FragmentHomeBinding
import com.example.geoclock.repos.FirebaseImpl.AuthRepositoryFirebase
import com.example.geoclock.util.autoCleared

class HomeFragment : Fragment() {

    private var binding : FragmentHomeBinding by autoCleared()
    private val viewModel : HomeViewModel by viewModels{
        HomeViewModel.HomeViewModelFactory(AuthRepositoryFirebase())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.btnLogOut.setOnClickListener{
            viewModel.sighOut()
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }



}
