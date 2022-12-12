package com.tradeasy

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.chaos.view.PinView
import com.tradeasy.databinding.FragmentNewPasswordBinding


class NewPasswordFragment : Fragment() {


    private lateinit var binding:FragmentNewPasswordBinding
    private lateinit var pinView: PinView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewPasswordBinding.inflate(inflater, container, false)


        pinView = binding.pinView
        pinView.requestFocus()
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY)

        pinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length == 6) {
                    Toast.makeText(context, "PinView: $s", Toast.LENGTH_SHORT).show()
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })
        return binding.root

    }


}