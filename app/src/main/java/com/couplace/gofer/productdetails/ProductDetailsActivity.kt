package com.couplace.gofer.productdetails

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.net.Uri
import android.os.*
import android.provider.Settings
import androidx.lifecycle.ViewModelProvider
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.afollestad.materialdialogs.MaterialDialog
import com.couplace.gofer.BuildConfig
import com.couplace.gofer.R
import com.couplace.gofer.model.Product
import com.couplace.gofer.product.ProductActivity
import com.couplace.gofer.product.ProductViewModel
import com.couplace.gofer.utils.Utils
import com.couplace.gofer.utils.drawableToBitmapDescriptor
import com.couplace.gofer.utils.lifecycleOwner
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import timber.log.Timber
import java.util.*
import android.content.Intent

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.DialogFragment

import com.google.android.gms.common.GooglePlayServicesUtil
import android.widget.Toast
import com.couplace.gofer.services.ForegroundOnlyLocationService


class ProductDetailsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener, SeekBar.OnSeekBarChangeListener,
    GoogleMap.OnGroundOverlayClickListener {

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var location: Location
    private lateinit var product_details_description: TextView
    private lateinit var product_details_title: TextView
    private lateinit var buy_product: Button
    private lateinit var navController: NavController

    lateinit var product: Product
    private val TAG = ProductDetailsActivity::class.java.simpleName
    private lateinit var map: GoogleMap
    private lateinit var viewModel: ProductViewModel

    companion object {
        val CONNECTION_FAILURE_RESOLUTION_REQUEST = 21
        val ERROR_DIALOG_REQUEST = 17
        val USER_LOCATION = "User"
        const val TAG = "MainActivity"
        const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        const val MSG_LOCATION = 7
    }


    private val REQUEST_LOCATION_PERMISSION: Int=1

    private var foregroundOnlyLocationServiceBound = false

    // Provides location updates for while-in-use feature.
    private var foregroundOnlyLocationService: ForegroundOnlyLocationService? = null

    // Listens for location broadcasts from ForegroundOnlyLocationService.
    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var foregroundOnlyLocationButton: Button


    //It keeps the activity updated with changes via a Messenger.
    var mActivityMessenger: Messenger? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        AndroidInjection.inject(this)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)




        navController = Navigation.findNavController(this, R.id.navigation_graph)

        product_details_title = findViewById(R.id.product_details_title)
        product_details_title.text = product!!.name

        product_details_description = findViewById(R.id.product_details_description)
        product_details_description.text = getString(R.string.lorem_ipsum)

        buy_product = findViewById(R.id.buy_product)

        buy_product.setOnClickListener{
            MaterialDialog(this).show {
                title(R.string.buy_product_dialog)
                message(R.string.buy_product)
                positiveButton(R.string.confirm) { _ ->
                    startActivity(Intent(this@ProductDetailsActivity, ProductActivity::class.java))
//                    Navigation.findNavController(view).navigate(R.id.action_productDetailsFragment_to_productFragment)

                }
                negativeButton(android.R.string.cancel) {
                    dismiss()
                }
                lifecycleOwner(this@ProductDetailsActivity)
            }
        }


        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        val enabled = sharedPreferences.getBoolean(
            Utils.KEY_FOREGROUND_ENABLED, false)

        if (enabled) {
            foregroundOnlyLocationService?.unsubscribeToLocationUpdates()
        } else {
            // TODO: Step 1.0, Review Permissions: Checks and requests if needed.
            if (foregroundPermissionApproved()) {
                foregroundOnlyLocationService?.subscribeToLocationUpdates()
                    ?: Timber.d(TAG, "Service Not Bound")
            } else {
                requestForegroundPermissions()
            }
        }

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw IllegalStateException("You forgot to supply a Google Maps API key")
        }

        if (savedInstanceState != null && savedInstanceState.keySet().contains(USER_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            location = savedInstanceState.getParcelable(USER_LOCATION)!!
        }

        mapFragment = (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!

        mapFragment.getMapAsync { map -> loadMap(map) }


    }

    // Monitors connection to the while-in-use service.
    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as ForegroundOnlyLocationService.LocalBinder
            foregroundOnlyLocationService = binder.service
            foregroundOnlyLocationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            foregroundOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }


    override fun onStart() {
        super.onStart()


        sharedPreferences.getBoolean(Utils.KEY_FOREGROUND_ENABLED, false)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val serviceIntent = Intent(this, ForegroundOnlyLocationService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                ForegroundOnlyLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
        )
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            foregroundOnlyBroadcastReceiver
        )
        super.onPause()
    }

    override fun onStop() {
        if (foregroundOnlyLocationServiceBound) {
            unbindService(foregroundOnlyServiceConnection)
            foregroundOnlyLocationServiceBound = false
        }
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)

        super.onStop()
    }


    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putParcelable(USER_LOCATION, location)
        super.onSaveInstanceState(savedInstanceState)
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        // Check that Google Play services is available
        val resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)
        // If Google Play services is available
        return if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            true
        } else {
            // Get the error dialog from Google Play services
            val errorDialog = GooglePlayServicesUtil.getErrorDialog(
                resultCode, this,
                CONNECTION_FAILURE_RESOLUTION_REQUEST
            )

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                val errorFragment = ErrorDialogFragment()
                errorFragment.setDialog(errorDialog)
                errorFragment.show(supportFragmentManager, "Location Updates")
            }
            false
        }
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        // Updates button states if new while in use location is added to SharedPreferences.
        if (key == Utils.KEY_FOREGROUND_ENABLED) {
            sharedPreferences.getBoolean(Utils.KEY_FOREGROUND_ENABLED, false)

        }
    }

    // TODO: Step 1.0, Review Permissions: Method checks if permissions approved.
    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    // TODO: Step 1.0, Review Permissions: Method requests permissions.
    private fun requestForegroundPermissions() {
        val provideRationale = foregroundPermissionApproved()

        // If the user denied a previous request, but didn't check "Don't ask again", provide
        // additional rationale.
        if (provideRationale) {
            Snackbar.make(
                findViewById(R.id.activity_product_details),
                R.string.permission_rationale,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.ok) {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this@ProductDetailsActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
                    )
                }
                .show()
        } else {
            Timber.d(TAG, "Request foreground only permission")
            ActivityCompat.requestPermissions(
                this@ProductDetailsActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    // TODO: Step 1.0, Review Permissions: Handles permission result.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Timber.d(TAG, "onRequestPermissionResult")

        when (requestCode) {
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE -> when {
                grantResults.isEmpty() ->
                    // If user interaction was interrupted, the permission request
                    // is cancelled and you receive empty arrays.
                    Timber.d(TAG, "User interaction was cancelled.")
                grantResults.contains(PackageManager.PERMISSION_GRANTED) ->
                    // Permission was granted.
                    foregroundOnlyLocationService?.subscribeToLocationUpdates()
                else -> {
                    // Permission denied.

                    Snackbar.make(
                        findViewById(R.id.activity_product_details),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.settings) {
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .show()
                }
            }
        }
    }




    /**
     * Receiver for location broadcasts from [ForegroundOnlyLocationService].
     */
    inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            location = intent.getParcelableExtra<Location>(
                ForegroundOnlyLocationService.EXTRA_LOCATION
            )!!


                showMyLocationMap(location, product)

        }
    }

    fun showGooglePlayErrorDialog(googlePlayStatus: Int) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(googlePlayStatus)) {
                //an error occured but we can resolve it
                val dialog: Dialog = GoogleApiAvailability.getInstance().getErrorDialog(
                    this,
                    googlePlayStatus,
                    ERROR_DIALOG_REQUEST
                )!!
                dialog.setCancelable(true)
                dialog.setOnDismissListener { }
                dialog.show()
            }
        }



    // Checks that users have given permission
    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // Checks if users have given their location and sets location enabled if so.




    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        TODO("Not yet implemented")
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        TODO("Not yet implemented")
    }

    override fun onGroundOverlayClick(p0: GroundOverlay) {
        TODO("Not yet implemented")
    }

    fun showMyLocationMap(location: Location, product: Product){

        val zoomLevel = 16f
        val overlaySize = 100f

        val latLng = LatLng(location.latitude, location.longitude)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))

        val poi = map.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(product.name)
                .snippet(product.description)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        )
        poi!!.showInfoWindow()

        val googleOverlay = GroundOverlayOptions()
            .image(drawableToBitmapDescriptor(product.image)!!).anchor(0f, 1f)
            .position(latLng, overlaySize)
            .bearing(30f)

        map.addGroundOverlay(googleOverlay)
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this,
                    R.raw.map_style_light
                )
            )

            if (!success) {
                Timber.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Timber.e(e, "Can't find style. Error: ")
        }
    }

    protected fun loadMap(googleMap: GoogleMap) {
        map = googleMap
        if(isGooglePlayServicesAvailable()){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            map.isMyLocationEnabled = true
        }

    }

    // Define a DialogFragment that displays the error dialog
    class ErrorDialogFragment     // Default constructor. Sets the dialog field to null
        : DialogFragment() {
        // Global field to contain the error dialog
        private var mDialog: Dialog? = null

        // Set the dialog to display
        fun setDialog(dialog: Dialog?) {
            mDialog = dialog
        }

        // Return a Dialog to the DialogFragment.
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return mDialog!!
        }
    }

}