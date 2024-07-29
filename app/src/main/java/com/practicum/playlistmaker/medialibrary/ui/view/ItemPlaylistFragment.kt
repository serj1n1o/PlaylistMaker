package com.practicum.playlistmaker.medialibrary.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.R.attr.colorOnSecondary
import com.google.android.material.R.attr.colorSecondary
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentItemPlaylistBinding
import com.practicum.playlistmaker.medialibrary.ui.viewmodel.PlaylistViewModel
import com.practicum.playlistmaker.util.FragmentWithBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ItemPlaylistFragment : FragmentWithBinding<FragmentItemPlaylistBinding>() {

    private val viewModel by viewModel<PlaylistViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentItemPlaylistBinding {
        return FragmentItemPlaylistBinding.inflate(inflater, container, false)
    }

    private val confirmDialog by lazy {
        MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialogTheme)
            .setTitle(getString(R.string.allert_dialog_txt_finish_creating))
            .setMessage(getString(R.string.allert_dialog_txt_lost_data))
            .setNeutralButton(getString(R.string.cancel_txt)) { dialog, which ->
            }
            .setPositiveButton(getString(R.string.finish_txt)) { dialog, which ->
                findNavController().popBackStack()
            }
    }

    private var coverUri: Uri? = null
    private var descriptionPlaylist: String? = null
    private var namePlaylist: String? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val requester = PermissionRequester.instance()

        binding.btnCreatePlaylist.isEnabled = false

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    coverUri = uri
                    binding.coverPlaylist.apply {
                        setImageURI(uri)
                        this.tag = requireContext().getString(R.string.uploaded_tag_image)
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                }
            }

        binding.iconBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.coverPlaylist.setOnClickListener {
            lifecycleScope.launch {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requester.request(Manifest.permission.READ_MEDIA_IMAGES).collect { result ->
                        when (result) {
                            is PermissionResult.Granted -> {
                                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }

                            is PermissionResult.Denied.NeedsRationale -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.rationale_the_request),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            is PermissionResult.Denied.DeniedPermanently -> {
                                viewModel.openSettingsPermission()
                            }

                            PermissionResult.Cancelled -> return@collect
                        }
                    }
                } else {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }
        }

        with(binding) {
            namePlaylist.setBoxStrokeColorStateList(
                setColorState(
                    hasText = false,
                    isText = !IS_TEXT
                )
            )
            descriptionPlaylist.setBoxStrokeColorStateList(
                setColorState(
                    hasText = false,
                    isText = !IS_TEXT
                )
            )
        }


        binding.editName.doOnTextChanged { text, _, _, _ ->
            namePlaylist = text?.toString()
            val hasText = text?.isNotBlank() == true
            binding.btnCreatePlaylist.isEnabled = hasText

            binding.namePlaylist.apply {
                setBoxStrokeColorStateList(setColorState(hasText, !IS_TEXT))
                defaultHintTextColor = setColorState(hasText, IS_TEXT)
            }

        }

        binding.editDescription.doOnTextChanged { text, _, _, _ ->
            descriptionPlaylist = text?.toString()
            val hasText = text?.isNotBlank() == true

            binding.descriptionPlaylist.apply {
                setBoxStrokeColorStateList(setColorState(hasText, !IS_TEXT))
                defaultHintTextColor = setColorState(hasText, IS_TEXT)
            }

        }


        binding.btnCreatePlaylist.setOnClickListener {
            if (namePlaylist != null) {
                coverUri?.let { uri ->
                    viewModel.saveImageToStorage(artworkUri = uri, namePlaylist = namePlaylist!!)
                }
                viewModel.createPlaylist(namePlaylist, descriptionPlaylist, coverUri)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.playlist_created, namePlaylist), Toast.LENGTH_SHORT
                ).show()
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val hasTextName = binding.editName.text?.isNotEmpty() == true
            val hasTextDescription = binding.editDescription.text?.isNotEmpty() == true
            val imageView = binding.coverPlaylist
            if (hasTextName || hasTextDescription || isImageViewNotEmpty(imageView)) {
                confirmDialog.show()
            } else {
                findNavController().popBackStack()
            }
        }

    }


    private fun isImageViewNotEmpty(imageView: ImageView): Boolean {
        return imageView.tag == requireContext().getString(R.string.uploaded_tag_image)
    }

    private fun setColorState(hasText: Boolean, isText: Boolean): ColorStateList {
        val colorBlue = requireContext().getColor(R.color.yp_blue)
        val defColor =
            getColorFromAttr(requireContext(), if (isText) colorSecondary else colorOnSecondary)
        when (hasText) {
            true -> {
                return ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_focused),
                        intArrayOf(android.R.attr.state_focused)
                    ),
                    intArrayOf(colorBlue, colorBlue)
                )
            }

            false -> {
                return ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_focused),
                        intArrayOf(android.R.attr.state_focused)
                    ),
                    intArrayOf(defColor, colorBlue)
                )
            }
        }
    }


    private fun getColorFromAttr(context: Context, attr: Int): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    companion object {
        private const val IS_TEXT = true
    }

}