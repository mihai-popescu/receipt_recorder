package com.example.receipt.recorder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.receipt.recorder.databinding.FragmentReceiptDetailsBinding
import com.example.receipt.recorder.extension.stateFlowCollect
import com.example.receipt.recorder.ui.camera.CameraConfirmationFragmentArgs
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class ReceiptDetailsFragment : Fragment() {
    private val args by navArgs<ReceiptDetailsFragmentArgs>()

    private val viewModel: ReceiptDetailsViewModel by inject() {
        parametersOf(args.receipt)
    }

    private var _binding: FragmentReceiptDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentReceiptDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            viewModel.loadImage(receiptImage)
            textFieldDate.editText?.setText(viewModel.receipt.date.toString())
            textFieldTotal.editText?.setText(viewModel.receipt.total.toString())
            textFieldCurrency.editText?.setText(viewModel.receipt.currency)
            textFieldNotes.editText?.setText(viewModel.receipt.notes)

            fabDone.setOnClickListener {
                viewModel.updateReceipt(textFieldDate.editText?.text.toString().toLong(),
                    textFieldTotal.editText?.text.toString().toDouble(),
                    textFieldCurrency.editText?.text.toString(),
                    textFieldNotes.editText?.text.toString())
            }
        }
    }
    private fun setupObservers() {
        stateFlowCollect(viewModel.receiptSaved) {
            if (it) findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}