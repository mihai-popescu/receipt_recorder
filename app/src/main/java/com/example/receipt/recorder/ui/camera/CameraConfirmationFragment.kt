package com.example.receipt.recorder.ui.camera

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.receipt.recorder.databinding.FragmentCameraConfirmationBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CameraConfirmationFragment : Fragment() {
    private val args by navArgs<CameraConfirmationFragmentArgs>()
    private val viewModel: CameraConfirmationViewModel by viewModel {
        parametersOf(args.path)
    }

    private var _binding: FragmentCameraConfirmationBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCameraConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            confirmButton.setOnClickListener {
                savePhoto()
            }

            viewModel.loadImage(capturePreview)
        }
    }

    private fun setupObservers() = Unit

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun savePhoto() {
        viewModel.usePhoto(requireContext()) { savedMediaUri ->
            savedMediaUri?.let {
                setFragmentResult(
                    CameraFragment.resultConfirmationKey, bundleOf(
                        CameraFragment.resultBundleKey to it)
                )
                findNavController().popBackStack()
            }
        }
    }
}