package com.tradeasy.ui.profile.termsAndConditions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tradeasy.databinding.FragmentTermsAndConditionsBinding
import com.tradeasy.ui.MainActivity


class TermsAndConditionsFragment : Fragment() {

private lateinit var binding: FragmentTermsAndConditionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTermsAndConditionsBinding.inflate(inflater, container, false)
        (activity as MainActivity?)?.setupToolBar("Privacy policy", false, false)
        return binding.root
    }


}