package com.tradeasy.ui.selling.userSelling

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.denzcoskun.imageslider.models.SlideModel
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.Snackbar
import com.tradeasy.R
import com.tradeasy.data.product.remote.dto.ProdIdReq
import com.tradeasy.databinding.FragmentUserProductItemBinding
import com.tradeasy.ui.selling.userSelling.unlistProd.DeleteProdState
import com.tradeasy.ui.selling.userSelling.unlistProd.DeleteProdViewModel
import com.tradeasy.ui.selling.userSelling.unlistProd.UnlistProdState
import com.tradeasy.ui.selling.userSelling.unlistProd.UnlistProdtViewModel
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class UserProductItemFragment : Fragment() {
    private lateinit var binding: FragmentUserProductItemBinding
    private val args: UserProductItemFragmentArgs by navArgs()
    private val unlistProdVM: UnlistProdtViewModel by viewModels()
    private val deleteProdVM: DeleteProdViewModel by viewModels()
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
        deleteProd()
        observe()
        observeDelete()
        goToEdit()
    }

    private  fun goToEdit(){
        binding.editProd.setOnClickListener {
            val action = UserProductItemFragmentDirections.actionUserProductItemFragmentToEditProdFragment(args.userProdId,args.category)
            findNavController().navigate(action)
        }
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
        binding.unlistProd.text = if(args.selling) "Unlist Product" else "List Product"
    }

    private fun deleteProd() {
        binding.deleteProd.setOnClickListener {
            binding.deleteProd.setOnClickListener {
                deleteProdVM.deleteProd(ProdIdReq(args.userProdId))
            }

        }
    }
    private fun unlistProd() {
        binding.unlistProd.setOnClickListener {
            unlistProdVM.unlistProd(ProdIdReq(args.userProdId))
            println("id is " + args.userProdId)
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
            is UnlistProdState.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()
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
        Toast.makeText(requireActivity(), "Loading", Toast.LENGTH_SHORT).show()
    }



    private fun observeDelete() {

        deleteProdVM.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> deleteStateChange(state) }.launchIn(lifecycleScope)

    }

    private fun deleteStateChange(state: DeleteProdState) {
        when (state) {
            is DeleteProdState.Init -> Unit
            is DeleteProdState.ErrorUnlist -> deleteErrorUnlist(state.rawResponse)
            is DeleteProdState.SuccessUnlist -> deleteSuccessUnlist()
            is DeleteProdState.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()
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
        Toast.makeText(requireActivity(), "Loading", Toast.LENGTH_SHORT).show()
    }

}