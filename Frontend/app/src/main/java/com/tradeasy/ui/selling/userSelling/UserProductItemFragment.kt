package com.tradeasy.ui.selling.userSelling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.denzcoskun.imageslider.models.SlideModel
import com.tradeasy.databinding.FragmentUserProductItemBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProductItemFragment : Fragment() {
private lateinit var binding: FragmentUserProductItemBinding
    private val args: UserProductItemFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserProductItemBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {



        binding.apply {
            val imageList = ArrayList<SlideModel>()
            for (i in args.userProdImg.indices) {
                imageList.add(SlideModel(args.userProdImg[i].toString()))
            }

            userProdImgSlider.setImageList(imageList)
            userProdName.text = args.userProdName
            userProdPrice.text = args.userProdPrice.toString() + " TND"

        }
// PLACE BID BTN VISIBILITY
  binding.removeProdFromSelling.visibility = if (args.selling) View.VISIBLE else View.GONE
    }
}