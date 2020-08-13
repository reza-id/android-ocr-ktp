package dev.juara.ocrktp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.otaliastudios.cameraview.CameraException
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Audio
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.controls.Mode
import dev.juara.ocrktp.databinding.ActivityTakeBinding
import dev.juara.ocrktp.di.EventObserver
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class TakeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTakeBinding
    private val vm by viewModel<TakeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_take)
        binding.lifecycleOwner = this
        binding.vm = vm

        initCameraView()
        initListeners()
    }

    private fun initCameraView() {
        binding.cameraView.setLifecycleOwner(this)
        binding.cameraView.audio = Audio.OFF
        binding.cameraView.playSounds = true
        binding.cameraView.mode = Mode.PICTURE
        binding.cameraView.useDeviceOrientation = false
    }

    private fun initListeners() {
        binding.cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                Timber.d("rotation: ${result.rotation} size: ${result.size.height},${result.size.width}")
                binding.cameraView.flash = Flash.OFF
                result.toBitmap { bitmap ->
                    if (bitmap == null) return@toBitmap
                    binding.imageView.setImageBitmap(bitmap)
                    binding.cameraView.visibility = View.GONE
                    binding.imageView.visibility = View.VISIBLE
                    binding.modeTake.visibility = View.GONE
                    val image = FirebaseVisionImage.fromBitmap(bitmap)
                    val textRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
                    textRecognizer.processImage(image)
                        .addOnSuccessListener { vm.processTextResult(it) }
                        .addOnFailureListener { Timber.e(it) }
                }
            }

            override fun onCameraError(exception: CameraException) {
                Timber.e(exception)
                super.onCameraError(exception)
            }
        })

        vm.gotResult.observe(this, EventObserver {
            if (it) onResultSuccess() else onResultFailure()
        })

        binding.buttonTakePicture.setOnClickListener {
            vm.takePicture()
            binding.cameraView.takePicture()
        }
        binding.btnClose.setOnClickListener { onBackPressed() }
        binding.btnLamp.setOnClickListener {
            if (binding.cameraView.flash == Flash.TORCH)
                binding.cameraView.flash = Flash.OFF
            else
                binding.cameraView.flash = Flash.TORCH
        }
    }

    private fun onResultSuccess() {
        Timber.d("oke")
        binding.btnSave.visibility = View.VISIBLE
        binding.btnSave.setOnClickListener { saveResult() }
    }

    private fun onResultFailure() {
        Timber.d("fail")
        binding.btnSave.background = ContextCompat.getDrawable(this, R.drawable.ic_retake)
        binding.btnSave.visibility = View.VISIBLE
        binding.tvError.visibility = View.VISIBLE
        binding.btnSave.setOnClickListener {
            binding.imageView.visibility = View.GONE
            binding.cameraView.visibility = View.VISIBLE
            binding.btnSave.background = ContextCompat.getDrawable(this, R.drawable.ic_save)
            binding.btnSave.visibility = View.GONE
            binding.modeTake.visibility = View.VISIBLE
            binding.tvError.visibility = View.GONE
        }
    }

    private fun saveResult() {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra("nik", vm.nik)
            putExtra("gender", vm.gender)
            putExtra("name", vm.name)
            putExtra("birthDate", vm.birthDate)
            putExtra("birthPlace", vm.birthPlace)
            putExtra("address", vm.address)
        })
        finish()
    }
}