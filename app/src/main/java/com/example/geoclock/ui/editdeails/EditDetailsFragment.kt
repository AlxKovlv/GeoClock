package com.example.geoclock.ui.editdeails

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.geoclock.R
import com.example.geoclock.databinding.FragmentEditDetailsBinding
import com.example.geoclock.databinding.FragmentPhotoBinding
import com.example.geoclock.util.autoCleared
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class EditDetailsFragment : Fragment() {
    private var binding: FragmentEditDetailsBinding by autoCleared()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditDetailsBinding.inflate(inflater, container, false)

        // Define data base of Firestore in Firebase
        val db = Firebase.firestore

        //get arguments from prev fragment
        val args = this.arguments
        val cardId = args?.getString("id")
        val loc = args?.get("loc")
        val time = args?.get("time")
        val date = args?.get("date")
        val title = args?.get("title")
        val note = args?.get("note")
        val user = args?.get("user")

        //show  arguments in UI
        binding.tvLoc.text = ("Location: " +loc.toString())
        binding.tvTime.text = ("Time & Date: " +time.toString()+" "+date.toString())
        binding.tvUser.text=("User: " +user.toString())
        binding.tvEditNote.text=(note.toString())

        binding.btnEditNote.setOnClickListener{
            showEditNoteDialog()
        }

        binding.btnUpdate.setOnClickListener {
            val newNote = binding.tvEditNote.text.toString()
            updateNoteInFirestore(cardId, newNote)
        }

        return binding.root
    }

    private fun showEditNoteDialog(){

        val builder = AlertDialog.Builder(requireContext())
        val inflater:LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.activity_edit_note_dialog,null)
        val editNote = dialogLayout.findViewById<EditText>(R.id.edt_edit_note)

        editNote.setText(binding.tvEditNote.text)
        with(builder){
            setTitle("Enter some text")
            setView(dialogLayout)
            setPositiveButton("Save"){dialog , which ->
                val newNote = editNote.text.toString()
                binding.tvEditNote.text = newNote
            }
            setNegativeButton("Cancel"){ dialog, which ->
                Log.d("Main", "Negative buttub clicked")
            }
        }.show()

    }

    private fun updateNoteInFirestore(cardId: String?, newNote: String) {
        cardId?.let {
            val cardRef = db.collection("cards").document(it)
            cardRef
                .update("note", newNote)
                .addOnSuccessListener {
                    Log.d(TAG, "Note updated successfully")
                    Toast.makeText(requireContext(), "Note updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating note", e)
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()

                }
        }
    }

    companion object {
        private const val TAG = "EditDetailsFragment"
    }




}