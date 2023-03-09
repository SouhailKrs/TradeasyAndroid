package com.tradeasy.ui.selling.userSelling

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.models.SlideModel
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.Snackbar
import com.gurutouchlabs.kenneth.elegantdialog.ElegantActionListeners
import com.gurutouchlabs.kenneth.elegantdialog.ElegantDialog
import com.tradeasy.R
import com.tradeasy.data.product.remote.dto.ProdIdReq
import com.tradeasy.databinding.FragmentUserProductItemBinding
import com.tradeasy.domain.user.entity.User
import com.tradeasy.ui.SharedDataViewModel
import com.tradeasy.ui.selling.userSelling.bidWinner.BidWinnerState
import com.tradeasy.ui.selling.userSelling.bidWinner.BidWinnerViewModel
import com.tradeasy.ui.selling.userSelling.deleteProd.DeleteProdState
import com.tradeasy.ui.selling.userSelling.deleteProd.DeleteProdViewModel
import com.tradeasy.ui.selling.userSelling.unlistProd.UnlistProdState
import com.tradeasy.ui.selling.userSelling.unlistProd.UnlistProdtViewModel
import com.tradeasy.utils.WrappedResponse
import com.tradeasy.utils.getScreenSize
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

@AndroidEntryPoint
class UserProductItemFragment : Fragment() {
    private lateinit var binding: FragmentUserProductItemBinding
    private val unlistProdVM: UnlistProdtViewModel by viewModels()
    private val deleteProdVM: DeleteProdViewModel by viewModels()
    private val getBidWinnder: BidWinnerViewModel by viewModels()
    private val sharedDataViewModel: SharedDataViewModel by activityViewModels()
    private lateinit var bidderUsername: String
    private lateinit var bidderProfilePic: String
    private lateinit var bidderPhoneNumber: String
    private var PERMISSION_CODE = 100
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserProductItemBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        unlistProd()
        observe()
        deleteProd()
        observeDelete()
        getBidWinner()
        observeBidWinner()
        setupBidWinner()
    }

    private fun setupView() {

        binding.cardView.layoutParams.height =
            (getScreenSize(binding.root.context).first * 0.40).toInt()


        binding.editProd.setOnClickListener {

            findNavController().navigate(R.id.action_userProductItemFragment_to_editProductFragment)
        }

        binding.apply {
            val imageList = ArrayList<SlideModel>()
            sharedDataViewModel.prodImages.observe(viewLifecycleOwner) { prodImages ->

                for (i in prodImages.indices) {
                    imageList.add(SlideModel(prodImages[i].toString()))
                }
                userProdImgSlider.setImageList(imageList)
            }
            sharedDataViewModel.prodName.observe(viewLifecycleOwner) { prodName ->
                userProdName.text = prodName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else it.toString()
                }
            }
            sharedDataViewModel.prodPrice.observe(viewLifecycleOwner) { prodPrice ->
                userProdPrice.text = "$prodPrice TND"

            }
            sharedDataViewModel.forBid.observe(viewLifecycleOwner) { forBid ->

                binding.editProd.visibility = if (forBid) View.GONE else View.VISIBLE

            }
            sharedDataViewModel.selling.observe(viewLifecycleOwner) { selling ->

                binding.editProd.visibility = if (selling) View.GONE else View.VISIBLE

            }
            sharedDataViewModel.prodDesc.observe(viewLifecycleOwner) { prodDesc ->
                binding.prodDesc.text = prodDesc
            }



        }
// PLACE BID BTN VISIBILITY

        sharedDataViewModel.selling.observe(viewLifecycleOwner) { selling ->

            binding.unlistProd.text = if (selling) "Unlist Product" else "List Product"
        }


    }

    private fun deleteProd() {

        binding.deleteProd.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton("Yes") { dialog, which ->
                    sharedDataViewModel.prodId.observe(viewLifecycleOwner) { prodId ->

                        deleteProdVM.deleteProd(ProdIdReq(prodId.toString()))
                    }

                }.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }.show()


        }
    }

    private fun unlistProd() {
        binding.unlistProd.setOnClickListener {
            sharedDataViewModel.prodId.observe(viewLifecycleOwner) { prodId ->

                unlistProdVM.unlistProd(ProdIdReq(prodId))
            }

        }

    }

    private fun observe() {

        unlistProdVM.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }.launchIn(lifecycleScope)

    }

    private fun handleStateChange(state: UnlistProdState) {
        when (state) {
            is UnlistProdState.Init -> Unit
            is UnlistProdState.ErrorUnlist -> handleErrorUnlist(state.rawResponse)
            is UnlistProdState.SuccessUnlist -> handleSuccessUnlist()
            is UnlistProdState.ShowToast -> {}
            is UnlistProdState.IsLoading -> handleUnlistLoading(state.isLoading)
        }
    }

    private fun handleErrorUnlist(response: WrappedResponse<String>) {
        binding.unlistProd.hideProgress("Unlist product")
        // snackbar

        Snackbar.make(binding.root, response.message, Snackbar.LENGTH_SHORT).show()

    }

    private fun handleSuccessUnlist() {

        findNavController().popBackStack(R.id.sellingFragment, true)
        findNavController().navigate(R.id.sellingFragment)
        Snackbar.make(requireView(), "Product Unlisted Successfully", Snackbar.LENGTH_LONG).show()

    }

    private fun handleUnlistLoading(isLoading: Boolean) {

        binding.unlistProd.showProgress {

            progressColor = Color.WHITE

        }

    }


    private fun observeDelete() {

        deleteProdVM.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> deleteStateChange(state) }.launchIn(lifecycleScope)

    }

    private fun deleteStateChange(state: DeleteProdState) {
        when (state) {
            is DeleteProdState.Init -> Unit
            is DeleteProdState.ErrorDelete -> deleteErrorUnlist(state.rawResponse)
            is DeleteProdState.SuccessDelete -> deleteSuccessUnlist()
            is DeleteProdState.ShowToast -> {


            }
            is DeleteProdState.IsLoading -> deleteUnlistLoading(state.isLoading)
        }
    }

    private fun deleteErrorUnlist(response: WrappedResponse<String>) {
        binding.deleteProd.hideProgress("Delete product")
        // snackbar
        Snackbar.make(binding.root, response.message, Snackbar.LENGTH_SHORT).show()

    }

    private fun deleteSuccessUnlist() {
        findNavController().popBackStack(R.id.sellingFragment, true)
        findNavController().navigate(R.id.sellingFragment)
        Snackbar.make(requireView(), "Product deleted Successfully", Snackbar.LENGTH_LONG).show()

    }

    private fun deleteUnlistLoading(isLoading: Boolean) {
        binding.deleteProd.showProgress {

            progressColor = Color.WHITE

        }

    }


    private fun getBidWinner() {

        sharedDataViewModel.prodId.observe(viewLifecycleOwner) { prodId ->

            getBidWinnder.getBidWinner((ProdIdReq(prodId)))

        }


    }

    private fun observeBidWinner() {
        getBidWinnder.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }.launchIn(lifecycleScope)
    }

    private fun handleStateChange(state: BidWinnerState) {
        when (state) {
            is BidWinnerState.Init -> Unit
            is BidWinnerState.ErrorGet -> handleErrorGet(state.rawResponse)
            is BidWinnerState.SuccessGet -> handleSuccessGet(state.user)
            is BidWinnerState.ShowToast -> {}
            is BidWinnerState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handleErrorGet(response: WrappedResponse<User>) {
        sharedDataViewModel.bade.observe(viewLifecycleOwner) { bade ->
            binding.bidWinnerConstraint.visibility = if (bade) View.VISIBLE else View.GONE
        }

    }

    private fun handleLoading(isLoading: Boolean) {
        sharedDataViewModel.bade.observe(viewLifecycleOwner) { bade ->
            binding.bidWinnerConstraint.visibility = if (bade) View.VISIBLE else View.GONE
        }
      //  binding.bidWinnerConstraint.visibility = View.GONE
    }

    // IF LOGGED IN SUCCESSFULLY
    private fun handleSuccessGet(user: User) {
        sharedDataViewModel.bade.observe(viewLifecycleOwner) { bade ->
            binding.bidWinnerConstraint.visibility = if (bade) View.VISIBLE else View.GONE
        }
        binding.bidWinner.text = user.username
        bidderUsername = user.username!!
        bidderProfilePic = user.profilePicture!!
        bidderPhoneNumber = user.phoneNumber!!
    }
    private fun checkPhonePermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), PERMISSION_CODE
            )
            false
        }
    }


    private fun setupBidWinner(){
        sharedDataViewModel.bade.observe(viewLifecycleOwner) { bade ->
            println("baade $bade")
            binding.bidWinnerConstraint.visibility = if (bade==true) View.VISIBLE else View.GONE
            binding.bidWinner.paint.isUnderlineText = true
            binding.bidWinner.setOnClickListener {
                val dialog = ElegantDialog(context).setTitleIcon(
                    resources.getDrawable(R.drawable.app_logo_48)
                )//Set title icon drawable if your not loading with Glide or Picasso
                    .setTitleIconBackgroundColor(Color.WHITE) //Set title icon drawable background color
                    .setBackgroundTopColor(
                        // set a custom color hex value for the top background
                        Color.parseColor("#EEF5F2")


                    )// Set top color
                    .setBackgroundBottomColor(Color.WHITE) // Set bottom color
                    // .setCustomView(R.layout.custom_image_layout)//Set custom layout
                    .setCornerRadius(50f) //Set dialog corner radius
                    .setCanceledOnTouchOutside(true) // Dismiss on tap outside
                    .setTitleHidden(false)
                    .setElegantActionClickListener(object :
                        ElegantActionListeners {
                        override fun onPositiveListener(dialog: ElegantDialog) {

                            if (checkPhonePermission()) {
                                val i = Intent(Intent.ACTION_CALL)
                                val phoneNumber = bidderPhoneNumber.replace("\\s".toRegex(), "")
                                i.data = Uri.parse("tel:$phoneNumber")
                                startActivity(i)
                            }
                        }

                        override fun onNegativeListener(dialog: ElegantDialog) {
                            dialog.dismiss()
                        }

                        override fun onGotItListener(dialog: ElegantDialog) {



                        }

                        override fun onCancelListener(dialog: DialogInterface) {
                            dialog.dismiss()
                        }
                    })
                    .show()// Hide title
                val contentView: View? = dialog.getCustomView()
                if (dialog.getTitleIconView() != null) {
                    if(bidderProfilePic.isNotEmpty()){
                        Glide.with(requireContext()).load(bidderProfilePic).into(dialog.getTitleIconView()!!)
                    }else{
                        Glide.with(requireContext()).load(R.drawable.default_profile_picture).into(dialog.getTitleIconView()!!)
                    }

                    dialog.getTitleTextView()!!.text = bidderUsername
                    dialog.getTitleTextView()!!.setTextColor(
                        resources.getColor(R.color.black)
                    )
                    dialog.getContentTextView()!!.text = bidderPhoneNumber //Set content text
                    dialog.getContentTextView()!!.setTextColor(
                        resources.getColor(R.color.black)
                    )
                    dialog.getPositiveButtonIconView()!!.setImageDrawable(
                        resources.getDrawable(R.drawable.phone_filled)
                    ) //Set positive button icon drawable
                    dialog.getPositiveButtonIconView()!!.setColorFilter(
                        resources.getColor(R.color.black)
                    )
                    dialog.getPositiveButtonTextView()!!.text = "Call"
              dialog.getNegativeButton()!!.visibility = View.GONE
              dialog.getGotItButton()!!.visibility = View.GONE
                dialog.getPositiveButton()!!.visibility=View.VISIBLE//
                }
            }
        }
    }
}