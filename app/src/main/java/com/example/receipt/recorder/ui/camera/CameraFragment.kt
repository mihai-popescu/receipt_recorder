package com.example.receipt.recorder.ui.camera

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.receipt.recorder.R
import com.example.receipt.recorder.databinding.FragmentCameraBinding
import com.example.receipt.recorder.extension.checkPermissions
import com.example.receipt.recorder.extension.preparePermissionsRequest
import com.example.receipt.recorder.extension.stateFlowCollect
import com.example.receipt.recorder.model.AppPermission
import com.example.receipt.recorder.ui.dialog.SimpleAlertDialog
import com.example.receipt.recorder.ui.dialog.SimpleAlertDialogListener
import com.example.receipt.recorder.util.ifNotNull
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.androidx.viewmodel.ext.android.viewModel

class CameraFragment : Fragment() {
    private val viewModel: CameraViewModel by viewModel()

    private var _binding: FragmentCameraBinding? = null

    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var useCaseCapture: UseCase? = null

    private val cameraExecutor by lazy { ContextCompat.getMainExecutor(requireContext()) }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var cameraPermissionRequest: ActivityResultLauncher<Array<String>>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraPermissionRequest = preparePermissionsRequest(viewModel.cameraPermissionsNeeded, {
            onCameraPermissionGranted()
        }, {
            onCameraPermissionDenied()
        }, {
            onCameraPermissionRationaleNeeded(it)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            buttonCapture.setOnClickListener {
                capture()
            }
        }
    }

    private fun setupObservers() {
        with(viewModel) {
            stateFlowCollect(saveSuccessful) { event ->
                event.getContentIfNotConsumed()?.let { path ->
                    if (path.isNotBlank()) {
                        navigateTo(
                            CameraFragmentDirections.actionCameraFragmentToCameraConfirmationFragment(
                                path
                            )
                        )
                    }
                }
            }

            stateFlowCollect(isCapturing) {
                showCameraControls(!it)
            }
        }

        setFragmentResultListener(resultConfirmationKey) { _, bundle ->
            getParcelable<Uri>(
                bundle,
                resultBundleKey
            )?.let {
                viewModel.onCaptureResultReceived(CaptureResult(uri = it))
            }
            onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        getCameraPermissions()

    }

    override fun onStop() {
        super.onStop()
        cameraProvider?.unbindAll()
        cameraProvider = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun capture() {
        if (!viewModel.isCapturing.value) {
            viewModel.startPhotoCapture()
            takePhoto()
        }
    }

    private fun showCameraControls(isVisible: Boolean) {
        with(binding) {
            buttonCapture.isVisible = isVisible
        }
    }

    private fun onCameraPermissionGranted() {
        lifecycleScope.launch {
            cameraProvider = ProcessCameraProvider.getInstance(requireContext()).await()
            startCamera()
        }
    }

    private fun onCameraPermissionDenied() {
//        onBackPressed()
    }

    private fun onCameraPermissionRationaleNeeded(rationalNeeded: List<AppPermission>) {
        showPermissionsDialog(rationalNeeded)
    }

    private fun showPermissionsDialog(rationalNeeded: List<AppPermission>) {
        val (title, msg) = with(rationalNeeded) {
            when {
                listOf(
                    AppPermission.ReadExternalStorage,
                    AppPermission.WriteExternalStorage,
                    AppPermission.ReadMediaImages,
                    AppPermission.ReadMediaVideo
                ).any(this::contains) -> R.string.storage_permission_title to R.string.storage_permission_denied

                contains(AppPermission.Camera) -> R.string.camera_permission_title to R.string.camera_permission_denied
                else -> null to null
            }
        }

        ifNotNull(title, msg) { notNullTitle, notNullMessage ->
            SimpleAlertDialog.newInstance(
                title = notNullTitle,
                message = notNullMessage,
                positiveButtonText = R.string.permission_request,
                negativeButtonText = R.string.cancel,
                listener = object : SimpleAlertDialogListener {
                    override fun onPositiveClick(dialog: DialogInterface) {
                        requireContext().let(::goToAppSettings)
                    }

                    override fun onNegativeClick(dialog: DialogInterface) {
                        onBackPressed()
                    }
                }
            ).apply {
                isCancelable = false
            }.show(childFragmentManager, null)
        }
    }

    private fun goToAppSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        })
    }

    private fun onBackPressed() {
        findNavController().popBackStack()
    }

    private fun startCamera() {
        cameraProvider?.let { bindPreview(it) }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder()
            .build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(binding.preview.getSurfaceProvider())
        useCaseCapture = captureImageUseCase()

        camera = cameraProvider.bindToLifecycle(
            this as LifecycleOwner,
            cameraSelector,
            preview,
            useCaseCapture
        )
    }

    private fun captureImageUseCase(): UseCase {
        return ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    private fun getCameraPermissions() {
        cameraPermissionRequest?.let {
            checkPermissions(viewModel.cameraPermissionsNeeded, it, onGranted = {
                onCameraPermissionGranted()
            }, onRationaleNeeded = { appPermissions ->
                onCameraPermissionRationaleNeeded(appPermissions)
            })
        }
    }

    private fun takePhoto() {
        val imageCapture = useCaseCapture as? ImageCapture ?: return

        val outputOptions =
            ImageCapture.OutputFileOptions.Builder(viewModel.createOutputFile()).build()

        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            viewModel.onImageSavedCallback
        )
    }

    private fun navigateTo(directions: NavDirections) {
        with(findNavController()) {
            currentDestination?.getAction(directions.actionId)?.let {
                navigate(directions)
            }
        }
    }


    private inline fun <reified T : Parcelable> getParcelable(bundle: Bundle, bundleKey: String): T? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(bundleKey, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelable(bundleKey)
        }


    @Parcelize
    data class CaptureFragmentResult(
        val path: String,
        val sensorOrientation: Int
    ) : Parcelable


    @Parcelize
    data class CaptureResult(
        val uri: Uri
    ) : Parcelable

    @Parcelize
    data class CaptureCancel(val error: String) : Parcelable

    companion object {
        const val resultConfirmationKey = "CameraConfirmationResultKey"
        const val resultBundleKey = "CameraResultBundleKey"
    }
}