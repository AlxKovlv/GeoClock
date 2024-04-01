package com.example.geoclock.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.geoclock.R
import com.example.geoclock.databinding.FragmentHomeBinding
import com.example.geoclock.databinding.FragmentPhotoBinding
import com.example.geoclock.model.Card
import com.example.geoclock.util.autoCleared
import com.example.geoclock.repos.AuthRepository
import com.example.geoclock.repos.CardRepository
import com.example.geoclock.repos.firebaseImpl.AuthRepositoryFirebase
import com.example.geoclock.repos.firebaseImpl.CardRepositoryFirebase
import java.io.ByteArrayOutputStream


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [photoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class photoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var binding: FragmentPhotoBinding by autoCleared()


    //val viewModel: HomeViewModel by activityViewModels()
    // val key = arguments?.getString("key")
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
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPhotoBinding.inflate(inflater, container, false)
        //get arguments from prev fragment
        val args = this.arguments
        val loc = args?.get("loc")
        val time = args?.get("time")
        val date = args?.get("date")
        val note = args?.get("note")
        val user = args?.get("user")

        try {
            var photo_as_string = args?.get("photo")
            val photo: Bitmap = stringToBitmap(photo_as_string.toString())
            binding.photo.setImageBitmap(photo)
        }catch (e: Exception){
            binding.photo.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.no_photo))
            Toast.makeText(requireContext(), "there is no photo to show", Toast.LENGTH_SHORT).show()
        }



        //show  arguments in UI
        binding.loc.text = ("Location: " +loc.toString())
        binding.time.text = ("Time & Date: " +time.toString()+" "+date.toString())
        binding.user.text=("User: " +user.toString())
        binding.note.text=("note: "+note.toString())



        return binding.root

    }

    fun stringToBitmap(encodedString: String): Bitmap {
        val decodedString: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment photoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            photoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}