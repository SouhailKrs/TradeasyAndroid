package com.tradeasy.ui.editProfile.updateProfilePic

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.takusemba.cropme.OnCropListener
import com.tradeasy.R
import com.tradeasy.databinding.FragmentCropProfilePicBinding
import com.tradeasy.domain.user.entity.User
import com.tradeasy.utils.SharedPrefs
import com.tradeasy.utils.getScreenSize
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class CropProfilePicFragment : Fragment() {
    private lateinit var binding: FragmentCropProfilePicBinding
    private val args: com.tradeasy.ui.editProfile.updateProfilePic.CropProfilePicFragmentArgs by navArgs()
    private val viewModel: UpdateProfilePicViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCropProfilePicBinding.inflate(inflater, container, false)
// hide the toolbar

        // hide toolbar
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        binding.profilePicLoading.visibility = View.GONE
        // hide bottom navigation view
        val bottomNavigationView = requireActivity().findViewById<View>(R.id.bottomNavigationView)
        bottomNavigationView.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cropViewSetup()
        uploadProfilePic()
        observe()
    }

    private fun observe() {
        viewModel.mState.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleState(state) }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleState(state: UploadProfilePicSate) {
        when (state) {
            is UploadProfilePicSate.IsLoading -> handleLoading(state.isLoading)
            is UploadProfilePicSate.SuccessCreate -> handleSuccessUpdate(state.user)
            is UploadProfilePicSate.ShowToast -> {

            }
            is UploadProfilePicSate.Init -> Unit
        }
    }

    private fun handleLoading(isLoading: Boolean) {
binding.doneCrop.visibility = View.GONE
        binding.profilePicLoading.visibility = View.VISIBLE


    }

    private fun handleSuccessUpdate(user: User) {
        sharedPrefs.setUser(user)
        Snackbar.make(requireView(), "Profile Picture  Updated Successfully", Snackbar.LENGTH_LONG)
            .show()
        // make toolbar visible
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        findNavController().navigateUp()
    }

    private fun uploadProfilePic() {
        binding.doneCrop.setOnClickListener {
            val uri: Uri = Uri.parse(args.profilePic)
            binding.cropView.crop()
            binding.cropView.addOnCropListener(object : OnCropListener {
                override fun onSuccess(bitmap: Bitmap) {
                    // convert the bitmap file to multipart file
                    //  val file = File(args.file)
                    val file = bitmapToFile(bitmap)
                    val imageFile = file.asRequestBody(
                        requireContext().contentResolver.getType(uri)?.toMediaTypeOrNull()
                    )
                    val image = MultipartBody.Part.createFormData("image", file.name, imageFile)
                    viewModel.uploadProfilePic(
                        image
                    )
                }

                override fun onFailure(e: Exception) {
                    // do error handling.
                }
            })
        }

    }

    // function to convert bitmap to file and return the file
    private fun bitmapToFile(bitmap: Bitmap): File {
        val file = File(requireContext().cacheDir, "image.jpg")
        file.createNewFile()
        val bos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos)
        bos.flush()
        bos.close()
        return file
    }

    private fun cropViewSetup() {
        val uri: Uri = Uri.parse(args.profilePic)
        binding.goBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // set crop view height and width
        binding.cropView.layoutParams.height = getScreenSize(requireContext()).second.toInt()
        binding.cropView.layoutParams.width = getScreenSize(requireContext()).second.toInt()
        binding.cropView.setUri(uri)
    }
}