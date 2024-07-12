package com.example.receipt.recorder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.receipt.recorder.R
import com.example.receipt.recorder.databinding.FragmentReceiptDetailsBinding
import com.example.receipt.recorder.extension.stateFlowCollect
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReceiptDetailsFragment : Fragment() {
    private val args by navArgs<ReceiptDetailsFragmentArgs>()
    private var picker: MaterialDatePicker<Long>? = null

    private val viewModel: ReceiptDetailsViewModel by viewModel {
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
            buttonDate.text = prettyFormat(viewModel.receipt.date)
            textFieldTotal.editText?.setText(viewModel.receipt.total.toString())
            textFieldCurrency.editText?.setText(viewModel.receipt.currency)
            textFieldNotes.editText?.setText(viewModel.receipt.notes)

            textFieldCurrency.editText?.doOnTextChanged { text, _, _, _ ->
                viewModel.setNewCurrency(text.toString())
            }

            textFieldTotal.editText?.doOnTextChanged { text, _, _, _ ->
                viewModel.setNewTotal(text.toString().toDoubleOrNull() ?: .0)
            }

            textFieldNotes.editText?.doOnTextChanged { text, _, _, _ ->
                viewModel.setNewNotes(text.toString())
            }

            fabDone.setOnClickListener {
                viewModel.updateReceipt()
            }

            buttonDate.setOnClickListener {
                picker = MaterialDatePicker.Builder.datePicker()
                    .setSelection(viewModel.receipt.date)
                    .build().also {
                        it.addOnPositiveButtonClickListener { selection ->
                            viewModel.setNewDate(selection)
                        }
                        it.show(parentFragmentManager, "dialog")
                    }
            }

            val items = listOf("Euro", "RON", "USD", "SK")
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
            (textFieldCurrency.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        }
    }

    private fun prettyFormat(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
        return format.format(date)
    }

    private fun setupObservers() {
        stateFlowCollect(viewModel.receiptSaved) {
            if (it) findNavController().popBackStack()
        }

        stateFlowCollect(viewModel.updateDate) {
            binding.buttonDate.text = prettyFormat(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}