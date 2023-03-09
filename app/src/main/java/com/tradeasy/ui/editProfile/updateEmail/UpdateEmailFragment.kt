package com.tradeasy.ui.editProfile.updateEmail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tradeasy.databinding.FragmentUpdateEmailBinding


class UpdateEmailFragment : Fragment() {
    private lateinit var binding: FragmentUpdateEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateEmailBinding.inflate(inflater, container, false)


        val toolbar: TextView = requireActivity().findViewById(com.tradeasy.R.id.toolbar_title)

        toolbar.text = "Update email"

        return binding.root
    }


}