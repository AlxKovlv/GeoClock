package com.example.geoclock.ui.editdeails
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.geoclock.R
import com.example.geoclock.databinding.FragmentEditDetailsBinding
import com.example.geoclock.util.autoCleared
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.io.ByteArrayOutputStream

class EditDetailsFragment : Fragment() {
    private var binding: FragmentEditDetailsBinding by autoCleared()
    private val db = FirebaseFirestore.getInstance()
    private val REQUEST_IMAGE_CAPTURE = 1
    private var photo: String = ""

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
        val note = args?.get("note")
        val user = args?.get("user")

        //show  arguments in UI
        binding.tvLoc.text = ("Location: " + loc.toString())
        binding.tvTime.text = ("Time & Date: " + time.toString() + " " + date.toString())
        binding.tvUser.text = ("User: " + user.toString())
        binding.tvEditNote.text = (note.toString())

        try {
            var photo_as_string = args?.get("photo")
            val photo: Bitmap = stringToBitmap(photo_as_string.toString())
            binding.editPhoto.setImageBitmap(photo)
        } catch (e: Exception) {  // no photo
            binding.editPhoto.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.no_photo
                )
            )
            Toast.makeText(requireContext(), "there is no photo to show", Toast.LENGTH_SHORT).show()
        }
        binding.btnEditNote.setOnClickListener {
            showEditNoteDialog()
        }

        binding.btnUpdate.setOnClickListener {
            val newNote = binding.tvEditNote.text.toString()
            updateNoteInFirestore(cardId, newNote)
        }

        binding.btnEditPhoto.setOnClickListener {
            dispatchTakePictureIntent()
        }

        return binding.root
    }

    private fun showEditNoteDialog() {

        val builder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.activity_edit_note_dialog, null)
        val editNote = dialogLayout.findViewById<EditText>(R.id.edt_edit_note)

        editNote.setText(binding.tvEditNote.text)
        with(builder) {
            setTitle("Enter some text")
            setView(dialogLayout)
            setPositiveButton("Save") { dialog, which ->
                val newNote = editNote.text.toString()
                binding.tvEditNote.text = newNote
            }
            setNegativeButton("Cancel") { dialog, which ->
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
                    Toast.makeText(
                        requireContext(),
                        "Note updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
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

    fun stringToBitmap(encodedString: String): Bitmap {
        val decodedString: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }


    private fun dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_IMAGE_CAPTURE
            )
        } else {
            openCamera()
        }
    }

    // open camera
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    //Request Permissions Result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            }
        }
    }

    //get photo from camera
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            var photo_As_bitmap: Bitmap = data?.extras?.get("data") as Bitmap
            // convert photo to ARRAY
            var byteArrayOutputStream = ByteArrayOutputStream()
            photo_As_bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream)
            var byteArray: ByteArray = byteArrayOutputStream.toByteArray()

            binding.editPhoto.setImageBitmap(photo_As_bitmap)

            //ARRAY TO STRING
            photo = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
                .toString()
            //get ID
            val args = this.arguments
            val cardId = args?.getString("id")
            //update
            updatePhotoInFirestore(cardId, photo)
        }
    }


    private fun updatePhotoInFirestore(cardId: String?, photo_as_string: String) {
        cardId?.let {
            val cardRef = db.collection("cards").document(it)
            cardRef
                .update("photo", photo_as_string)
                .addOnSuccessListener {
                    Log.d(TAG, "photo updated successfully")
                    Toast.makeText(
                        requireContext(),
                        "photo updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating photo", e)
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()

                }
        }
    }
}


