package com.tradeasy.ui.selling.userSelling

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.models.SlideModel
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.Snackbar
import com.tradeasy.R
import com.tradeasy.data.product.remote.dto.ProdIdReq
import com.tradeasy.databinding.FragmentUserProductItemBinding
import com.tradeasy.ui.SharedDataViewModel
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
    private val sharedDataViewModel: SharedDataViewModel by activityViewModels()
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
        unlistProd()
        observe()
        deleteProd()
        observeDelete()

    }

    private fun setupView() {

binding.cardView.layoutParams.height = (getScreenSize(binding.root.context).first*0.40).toInt()


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

            binding.unlistProd.text = if(selling) "Unlist Product" else "List Product"
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

                    }
                    .setNegativeButton("No") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()



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
            is UnlistProdState.ShowToast ->{}
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

        binding.unlistProd.showProgress{

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
        binding.deleteProd.showProgress{

            progressColor = Color.WHITE

        }

    }

}