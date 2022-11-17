package com.tradeasy.ui.editProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradeasy.R
import com.tradeasy.databinding.FragmentEditProfileBinding
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    @Inject
    lateinit var sharedPrefs: SharedPrefs



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        sharedPrefs.getUser()?.let {
            binding.usernameView.text = it.username
            binding.emailView.text = it.email
            binding.phoneNumberView.text = it.phoneNumber.toString()

        }

        return binding.root
    }


    override fun onResume() {
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.rootView.findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
binding.updatePasswordConstraint.setOnClickListener{
    findNavController().navigate(R.id.action_editProfileFragment_to_updatePasswordFragment)


}

    }







}