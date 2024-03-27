package com.example.geoclock.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.geoclock.R
import com.example.geoclock.databinding.FragmentHomeAdminBinding
import com.example.geoclock.repos.firebaseImpl.AuthRepositoryFirebase
import com.example.geoclock.util.autoCleared

class AdminHomeFragment : Fragment() {

    private var binding : FragmentHomeAdminBinding by autoCleared()
    private val viewModel : AdminHomeViewModel by viewModels{
        AdminHomeViewModel.AdminHomeViewModelFactory(AuthRepositoryFirebase())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        binding.btnLogOut.setOnClickListener{
            viewModel.sighOut()
            findNavController().navigate(R.id.action_aminHomeFragment_to_loginFragment)
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}
