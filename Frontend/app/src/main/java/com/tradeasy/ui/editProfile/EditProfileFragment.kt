package com.tradeasy.ui.editProfile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradeasy.databinding.FragmentEditProfileBinding
import com.tradeasy.ui.home.HomeFragment
import com.tradeasy.ui.navigation.editProfileToUpdateEmail
import com.tradeasy.ui.navigation.editProfileToUpdatePassword
import com.tradeasy.ui.navigation.editProfileToUpdatePn
import com.tradeasy.ui.navigation.editProfileToUpdateUsername
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding

    private val requestGallery = 2121
    private var file: File? = null
    private var link: Uri? = null
    @Inject
    lateinit var sharedPrefs: SharedPrefs


// make this fragment a child of ProfileFragment


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        HomeFragment().setupToolBar("Edit Profile")
        constraintsNavigation()
        sharedPrefs.getUser()?.let {
            binding.usernameView.text = it.username
            binding.emailView.text = it.email
            binding.phoneNumberView.text = it.phoneNumber.toString()

        }
        //  binding.cropView.setBitmap(requireContext().getDrawable(com.tradeasy.R.drawable.app_logo_48)!!.toBitmap() )

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.rootView.findViewById<BottomNavigationView>(com.tradeasy.R.id.bottomNavigationView).visibility =
            View.VISIBLE
        binding.uploadPorfilePic.setOnClickListener {


            ImagePicker.with(this).start(requestGallery)
        }


    }


    private fun constraintsNavigation() {

        binding.updatePasswordConstraint.setOnClickListener {
            editProfileToUpdatePassword(requireView())
        }
        binding.updateUsernameConstraint.setOnClickListener {


            editProfileToUpdateUsername(requireView())
        }
        binding.updateEmailConstraint.setOnClickListener {

            editProfileToUpdateEmail(requireView())

        }
        binding.updatePnConstraint.setOnClickListener {

            editProfileToUpdatePn(requireView())

        }


    }

// pop this fragment from backstack when back button is pressed

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == requestGallery && data != null) {
                val fileUri = data.data
                ImagePicker.getFile(data)?.let {
                    file = it
                    link = fileUri
                    println("aaaa $file")
                    println("aaaa $link")
                    println("aaaa $fileUri")
                    val action= EditProfileFragmentDirections.actionEditProfileFragmentToCropProfilePicFragment(
                        fileUri.toString(),
                        file.toString(),
                    )



                    findNavController().navigate(action)
                }
            }
        }
    }


//    override fun onPause() {
//        super.onPause()
//        val toolbar: TextView = requireActivity().findViewById(com.tradeasy.R.id.toolbar_title)
//        // get drawable as bitmap
//
//
//// change selected item in bottom navigation bar to profile fragment
//        toolbar.text = "Edit profile"
//    }

    override fun onResume() {
        super.onResume()

    }



}