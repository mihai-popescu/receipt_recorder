package com.example.receipt.recorder.ui

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.receipt.recorder.databinding.FragmentReceiptListBinding
import com.example.receipt.recorder.extension.stateFlowCollect
import com.example.receipt.recorder.model.Receipt
import com.example.receipt.recorder.util.addMediaToGallery
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A fragment representing a list of Items.
 */
class ReceiptListFragment : Fragment() {
    private val viewModel: ReceiptListViewModel by viewModel()

    private var _binding: FragmentReceiptListBinding? = null

    private var columnCount = 2

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var sourceAdapter: ReceiptSourceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentReceiptListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            // Set the adapter
            with(list) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                sourceAdapter = ReceiptSourceAdapter { receipt ->
                    onEditReceipt(receipt)
                }
                adapter = sourceAdapter
            }

            fabaddReceipt.setOnClickListener {
                startMediaCapture()
            }
        }
    }

    private fun setupObservers() {
        with (viewModel) {
            stateFlowCollect(confirmCapturedContent) {
                it.getContentIfNotConsumed()?.let { uri ->
                    if (uri != Uri.EMPTY) {
                        onConfirmCapture(uri)
                    }
                }
            }

            stateFlowCollect(receipts) {
                sourceAdapter.updateData(it)
            }
        }
    }

    private fun startMediaCapture() {
        viewModel.camera.startMediaCapture(
            parent = this@ReceiptListFragment,
            navDirections =
            ReceiptListFragmentDirections.actionReceiptListFragmentToCameraNav(),
            onMediaCaptureSuccess = ::onMediaCaptureSuccess,
            onMediaCaptureFailure = ::onMediaCaptureFailure)
    }

    private fun onMediaCaptureSuccess(uri: Uri) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            addMediaToGallery(uri, context)
            onConfirmCapture(uri)
        }

        viewModel.onMediaCaptureSuccess(requireContext(), uri)
    }

    private fun onMediaCaptureFailure() {
        viewModel.onMediaCaptureFailure()
    }


    private fun onConfirmCapture(uri: Uri) {
        onEditReceipt(Receipt(uri = uri))
    }

    private fun onEditReceipt(receipt: Receipt) {
        navigateTo(
            ReceiptListFragmentDirections.actionReceiptListFragmentToReceiptDetailsFragment(receipt))
    }


    private fun navigateTo(directions: NavDirections) {
        with(findNavController()) {
            currentDestination?.getAction(directions.actionId)?.let {
                navigate(directions)
            }
        }
    }
}