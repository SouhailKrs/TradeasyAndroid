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
import com.tradeasy.ui.MainActivity
import com.tradeasy.ui.navigation.editProfileToUpdateEmail
import com.tradeasy.ui.navigation.editProfileToUpdatePassword
import com.tradeasy.ui.navigation.editProfileToUpdatePn
import com.tradeasy.ui.navigation.editProfileToUpdateUsername
import com.tradeasy.utils.ImageLoader
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
        val activity: Activity? = activity
        constraintsNavigation()
    fragmentSetup()
        return binding.root
    }


private fun fragmentSetup(){

    sharedPrefs.getUser()?.let {
        binding.usernameView.text = it.username
        binding.emailView.text = it.email
        binding.phoneNumberView.text = it.phoneNumber.toString()
        if (!sharedPrefs.getUser()?.profilePicture.isNullOrEmpty()) {
            ImageLoader(sharedPrefs.getUser()!!.profilePicture!!, binding.editProfilePicture)
        } else if (sharedPrefs.getUser()?.profilePicture.isNullOrEmpty()) {
            binding.editProfilePicture.setImageResource(com.tradeasy.R.drawable.default_profile_picture)
        }
    }


}
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.rootView.findViewById<BottomNavigationView>(com.tradeasy.R.id.bottomNavigationView).visibility =
            View.VISIBLE
        (activity as MainActivity?)?.setupToolBar("Profile", false, false)
//        HomeFragment().setupToolBar("Edit Profile")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == requestGallery && data != null) {
                val fileUri = data.data
                ImagePicker.getFile(data)?.let {
                    file = it
                    link = fileUri
                    val action= EditProfileFragmentDirections.actionEditProfileFragmentToCropProfilePicFragment(
                        fileUri.toString(),
                        file.toString(),
                    )
                    findNavController().navigate(action)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }



}