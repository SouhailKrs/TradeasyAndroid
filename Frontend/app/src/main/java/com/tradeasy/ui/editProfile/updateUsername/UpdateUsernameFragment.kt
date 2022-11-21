package com.tradeasy.ui.editProfile.updateUsername

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tradeasy.databinding.FragmentUpdateUsernameBinding


class UpdateUsernameFragment : Fragment() {
private lateinit var binding: FragmentUpdateUsernameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateUsernameBinding.inflate(inflater, container, false)
        //(activity as AppCompatActivity?)!!.supportActionBar!!.hide()
       // (activity as AppCompatActivity?)!!.supportActionBar!!.title = Html.fromHtml("<font color='#000000'>Update username</font>")
        //val toolbar: Toolbar = requireActivity().findViewById(com.tradeasy.R.id.toolbar)
       // toolbar.setTitleTextColor(R.color.black)


        // change the custom toolbar title
        val toolbar: TextView = requireActivity().findViewById(com.tradeasy.R.id.toolbar_title)
        toolbar.text = "Update username"
        val toolbarTxt: TextView = requireActivity().findViewById(com.tradeasy.R.id.toolbarRightText)

        toolbarTxt.text = "Done"
        toolbarTxt.visibility = View.VISIBLE


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


}