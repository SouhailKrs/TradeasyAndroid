package com.tradeasy.ui.editProfile.updatePn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tradeasy.databinding.FragmentUpdatePnBinding


class UpdatePnFragment : Fragment() {
private lateinit var binding: FragmentUpdatePnBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentUpdatePnBinding.inflate(inflater, container, false)


        val toolbar: TextView = requireActivity().findViewById(com.tradeasy.R.id.toolbar_title)

         toolbar.text = "Update Phone Number"


        return binding.root
    }


}