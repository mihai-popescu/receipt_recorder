package com.example.receipt.recorder.document.scanner

import android.app.Activity
import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.receipt.recorder.databinding.FragmentCameraBinding
import com.example.receipt.recorder.databinding.FragmentDocumentScannerBinding
import com.example.receipt.recorder.extension.stateFlowCollect
import com.example.receipt.recorder.ui.camera.CameraFragment
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import org.koin.androidx.viewmodel.ext.android.viewModel

class DocumentScannerFragment: Fragment() {

    private val viewModel: DocumentScannerViewModel by viewModel()

    private var _binding: FragmentDocumentScannerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var scannerLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        scannerLauncher =
            registerForActivityResult(StartIntentSenderForResult()) { result ->
                handleActivityResult(result)
            }

        val options =
            GmsDocumentScannerOptions.Builder()
                .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_BASE)
                .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
                .setGalleryImportAllowed(false)

        options.setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)

        GmsDocumentScanning.getClient(options.build())
            .getStartScanIntent(this.requireActivity())
            .addOnSuccessListener { intentSender: IntentSender ->
                scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
            .addOnFailureListener { ex: Exception ->
                viewModel.onCaptureCancelled(CameraFragment.CaptureCancel(ex.message ?: ex.toString()))
            }
    }


    private fun handleActivityResult(activityResult: ActivityResult) {
        val resultCode = activityResult.resultCode
        val result = GmsDocumentScanningResult.fromActivityResultIntent(activityResult.data)
        if (resultCode == Activity.RESULT_OK && result != null) {
            val pages = result.pages
            if (!pages.isNullOrEmpty()) {
                viewModel.usePhoto(requireContext(), pages[0].imageUri.toFile()) { uri: Uri? ->
                    uri?.let {
                        viewModel.onCaptureResultReceived(CameraFragment.CaptureResult(it))
                    } ?: viewModel.onCaptureCancelled(CameraFragment.CaptureCancel(""))
                    findNavController().popBackStack()
                }
            }
        } else viewModel.onCaptureCancelled(CameraFragment.CaptureCancel("")).also { findNavController().popBackStack() }
    }
}