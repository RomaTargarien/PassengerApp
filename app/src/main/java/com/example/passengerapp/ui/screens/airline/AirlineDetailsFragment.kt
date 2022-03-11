package com.example.passengerapp.ui.screens.airline

import android.Manifest
import android.animation.Animator
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
import com.example.passengerapp.R
import com.example.passengerapp.databinding.FragmentAirlineDetailsBinding
import com.example.passengerapp.model.Passenger
import com.example.passengerapp.ui.util.sdk29AnpUp
import com.example.passengerapp.ui.util.slideUpViews
import com.example.passengerapp.ui.util.urlDecode
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class AirlineDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAirlineDetailsBinding
    private val args: AirlineDetailsFragmentArgs by navArgs()
    private lateinit var sharedElementListner: Transition.TransitionListener

    private var writePermissionsGranded = false
    private lateinit var permissonsLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentAirlineDetailsBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val passenger = Gson().fromJson(args.passenger.urlDecode(), Passenger::class.java)
        val airline = passenger.airline[0]
        setSharedElementTransition()
        postponeEnterTransition()
        permissonsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permisions ->
            writePermissionsGranded = permisions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionsGranded
        }
        binding.ivAirline.apply {
            transitionName = passenger.id
            startEnterTransitionAfterLoadingImage(airline.logo, this)
        }
        binding.ivAirline.setOnClickListener {
            try {
                lifecycleScope.launch {
                    val bitmap = getBitmapFromURL(airline.logo)
                    Log.d("TAG",bitmap.toString())
                    //savePhotoToExternalStorage(UUID.randomUUID().toString(),bitmap!!)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        val containerAlphaAnimationListener = object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                binding.tvAirlineTitle.text = airline.name
                binding.tvAirlineSlogan.text = airline.slogan
                slideUpViews(binding.tvAirlineTitle, binding.tvAirlineSlogan)
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {}


        }
        sharedElementListner = object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {}
            override fun onTransitionEnd(transition: Transition) {
                Log.d("TAG", "end")
                binding.imageContainer.animate().alpha(1f).setDuration(700)
                    .setListener(containerAlphaAnimationListener).start()
            }

            override fun onTransitionCancel(transition: Transition) {}
            override fun onTransitionPause(transition: Transition) {}
            override fun onTransitionResume(transition: Transition) {}

        }
        (sharedElementEnterTransition as TransitionSet).addListener(sharedElementListner)
    }

    private fun setSharedElementTransition() {
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.airline_shared_element_transition)
    }

    private fun updateOrRequestPermisions() {
        val hasWritePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        writePermissionsGranded = hasWritePermission || minSdk29
        val permissionsToRequest = mutableListOf<String>()
        if (!writePermissionsGranded) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissonsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun savePhotoToExternalStorage(displayName: String,bmp: Bitmap): Boolean {
        val imageCollection = sdk29AnpUp {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME,"$displayName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg")
            put(MediaStore.Images.Media.WIDTH,"${bmp.width}")
            put(MediaStore.Images.Media.HEIGHT,"${bmp.height}")
        }
        return try {
            activity?.contentResolver?.insert(imageCollection,contentValues)?.also { uri ->
                activity?.contentResolver?.openOutputStream(uri).use { outPutStream ->
                    if (!bmp.compress(Bitmap.CompressFormat.JPEG,95,outPutStream)){
                        throw IOException("cant save")
                    }
                }
            } ?: throw IOException("Couldn't cretae mediastore")
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun startEnterTransitionAfterLoadingImage(imageUrl: String, imageView: ImageView) {
        Picasso.get().load(imageUrl).into(imageView, object : Callback {
            override fun onSuccess() {
                startPostponedEnterTransition()
            }

            override fun onError(e: Exception?) {
                startPostponedEnterTransition()
            }
        })
    }

    suspend fun getBitmapFromURL(src: String?) = withContext(Dispatchers.IO) {
        try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    override fun onStop() {
        super.onStop()
        (sharedElementEnterTransition as TransitionSet).removeListener(sharedElementListner)
    }
}