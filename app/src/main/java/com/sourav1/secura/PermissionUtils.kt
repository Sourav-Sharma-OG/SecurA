package com.sourav1.secura
import android.Manifest
import android.R
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment


/**
 * Utility class for access to runtime permissions.
 */
object PermissionUtils {
    /**
     * Requests the fine location permission. If a rationale with an additional explanation should
     * be shown to the user, displays a dialog that triggers the request.
     */
    fun requestPermission(
        activity: AppCompatActivity, requestId: Int,
        permission: String, finishActivity: Boolean
    ) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            // Display a dialog with rationale.
            RationaleDialog.newInstance(requestId, finishActivity)
                .show(activity.supportFragmentManager, "dialog")
        } else {
            // Location permission has not been granted yet, request it.
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestId)
        }
    }

    /**
     * Checks if the result contains a [PackageManager.PERMISSION_GRANTED] result for a
     * permission from a runtime permissions request.
     *
     * @see androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
     */
    fun isPermissionGranted(
        grantPermissions: Array<String>, grantResults: IntArray,
        permission: String
    ): Boolean {
        for (i in grantPermissions.indices) {
            if (permission == grantPermissions[i]) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED
            }
        }
        return false
    }

    /**
     * A dialog that displays a permission denied message.
     */
    class PermissionDeniedDialog : DialogFragment() {
        private var finishActivity = false
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            finishActivity = requireArguments().getBoolean(ARGUMENT_FINISH_ACTIVITY)
            return AlertDialog.Builder(activity)
                .setMessage("Location Permission Denied")
                .setPositiveButton(R.string.ok, null)
                .create()
        }

        override fun onDismiss(dialog: DialogInterface) {
            super.onDismiss(dialog)
            if (finishActivity) {
                Toast.makeText(
                    activity, "Permission required..",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }

        companion object {
            private const val ARGUMENT_FINISH_ACTIVITY = "finish"

            /**
             * Creates a new instance of this dialog and optionally finishes the calling Activity
             * when the 'Ok' button is clicked.
             */
            fun newInstance(finishActivity: Boolean): PermissionDeniedDialog {
                val arguments = Bundle()
                arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity)
                val dialog = PermissionDeniedDialog()
                dialog.arguments = arguments
                return dialog
            }
        }
    }

    /**
     * A dialog that explains the use of the location permission and requests the necessary
     * permission.
     *
     *
     * The activity should implement
     * [androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback]
     * to handle permit or denial of this permission request.
     */
    class RationaleDialog : DialogFragment() {
        private var finishActivity = false
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val arguments = arguments
            val requestCode = arguments!!.getInt(ARGUMENT_PERMISSION_REQUEST_CODE)
            finishActivity = arguments.getBoolean(ARGUMENT_FINISH_ACTIVITY)
            return AlertDialog.Builder(activity)
                .setMessage("Permission Required")
                .setPositiveButton(
                    R.string.ok,
                    DialogInterface.OnClickListener { dialog, which -> // After click on Ok, request the permission.
                        ActivityCompat.requestPermissions(
                            requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            requestCode
                        )
                        // Do not finish the Activity while requesting permission.
                        finishActivity = false
                    })
                .setNegativeButton(R.string.cancel, null)
                .create()
        }

        override fun onDismiss(dialog: DialogInterface) {
            super.onDismiss(dialog)
            if (finishActivity) {
                Toast.makeText(
                    activity,
                    "Permission Required",
                    Toast.LENGTH_SHORT
                )
                    .show()
                requireActivity().finish()
            }
        }

        companion object {
            private const val ARGUMENT_PERMISSION_REQUEST_CODE = "requestCode"
            private const val ARGUMENT_FINISH_ACTIVITY = "finish"

            /**
             * Creates a new instance of a dialog displaying the rationale for the use of the location
             * permission.
             *
             *
             * The permission is requested after clicking 'ok'.
             *
             * @param requestCode    Id of the request that is used to request the permission. It is
             * returned to the
             * [androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback].
             * @param finishActivity Whether the calling Activity should be finished if the dialog is
             * cancelled.
             */
            fun newInstance(requestCode: Int, finishActivity: Boolean): RationaleDialog {
                val arguments = Bundle()
                arguments.putInt(ARGUMENT_PERMISSION_REQUEST_CODE, requestCode)
                arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity)
                val dialog = RationaleDialog()
                dialog.arguments = arguments
                return dialog
            }
        }
    }
}