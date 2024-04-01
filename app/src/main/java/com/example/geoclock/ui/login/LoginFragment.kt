package com.example.geoclock.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.geoclock.R
import com.example.geoclock.databinding.FragmentLoginBinding
import com.example.geoclock.repos.firebaseImpl.AuthRepositoryFirebase
import com.example.geoclock.util.Resource
import com.example.geoclock.util.autoCleared

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding by autoCleared()
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModel.LoginVieModelFactory(AuthRepositoryFirebase())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.noAccountTv.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.buttonLogin.setOnClickListener {
            viewModel.signInUser(
                binding.editTextLoginEmail.editText?.text.toString(),
                binding.editTextLoginPass.editText?.text.toString()
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userSignInStatus.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Loading -> {
                    binding.loginProgressBar.isVisible = true
                    binding.buttonLogin.isEnabled = false
                }

                is Resource.Success -> {
                    Toast.makeText(requireContext(),
                        getString(R.string.login_successful), Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment) //to login?
                }

                is Resource.Error -> {
                    binding.loginProgressBar.isVisible = false
                    binding.buttonLogin.isEnabled = true
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.currentUser.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.loginProgressBar.isVisible = true
                    binding.buttonLogin.isEnabled = false
                }

                is Resource.Success -> {
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }

                is Resource.Error -> {
                    binding.loginProgressBar.isVisible = false
                    binding.buttonLogin.isEnabled = true
                }
            }
        }
    }
}