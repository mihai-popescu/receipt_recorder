package com.example.receipt.recorder.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

interface SimpleAlertDialogListener {

	fun onPositiveClick(dialog: DialogInterface) = Unit
	fun onNegativeClick(dialog: DialogInterface) = Unit
	fun onNeutralClick(dialog: DialogInterface) = Unit
}

class SimpleAlertDialog(private val listener: SimpleAlertDialogListener?) : DialogFragment() {

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val titleRes = requireArguments().getInt(titleKey).takeIf { it != -1 }
		val messageRes = requireArguments().getInt(messageKey).takeIf { it != -1 }
		val positiveButtonTextRes = requireArguments().getInt(positiveButtonKey).takeIf { it != -1 }
		val negativeButtonTextRes = requireArguments().getInt(negativeButtonKey).takeIf { it != -1 }
		val neutralButtonTextRes = requireArguments().getInt(neutralButtonKey).takeIf { it != -1 }

		return MaterialAlertDialogBuilder(requireContext()).apply {
			titleRes?.let { setTitle(it) }
			messageRes?.let { setMessage(it) }
			positiveButtonTextRes?.let {
				setPositiveButton(it) { dialog, _ ->
					listener?.onPositiveClick(dialog) ?: dialog.dismiss()
				}
			}
			negativeButtonTextRes?.let {
				setNegativeButton(it) { dialog, _ ->
					listener?.onNegativeClick(dialog) ?: dialog.dismiss()
				}
			}
			neutralButtonTextRes?.let {
				setNeutralButton(it) { dialog, _ ->
					listener?.onNeutralClick(dialog) ?: dialog.dismiss()
				}
			}
		}.create()
	}

	companion object {
		const val TAG = "SimpleAlertDialog"
		private const val titleKey = "title"
		private const val messageKey = "message"
		private const val positiveButtonKey = "positiveButton"
		private const val negativeButtonKey = "negativeButton"
		private const val neutralButtonKey = "neutralButton"

		fun newInstance(
			@StringRes title: Int?,
			@StringRes message: Int?,
			@StringRes positiveButtonText: Int? = null,
			@StringRes negativeButtonText: Int? = null,
			@StringRes neutralButtonText: Int? = null,
			listener: SimpleAlertDialogListener?,
		): SimpleAlertDialog {
			return SimpleAlertDialog(listener).apply {
				arguments = Bundle().apply {
					putInt(titleKey, title ?: -1)
					putInt(messageKey, message ?: -1)
					putInt(positiveButtonKey, positiveButtonText ?: -1)
					putInt(negativeButtonKey, negativeButtonText ?: -1)
					putInt(neutralButtonKey, neutralButtonText ?: -1)
				}
			}
		}
	}
}