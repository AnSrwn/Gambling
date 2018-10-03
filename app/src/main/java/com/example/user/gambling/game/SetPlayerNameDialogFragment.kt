package com.example.user.gambling.game

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog

class SetPlayerNameDialogFragment : DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        /*return activity?.let {
            val builder  = AlertDialog.Builder(it)
            builder.setView(layoutInflater.inflate(R.layout.dialog_player_name, null))


                    // Add action buttons
                    .setPositiveButton(R.string.dialog_confirm,
                            DialogInterface.OnClickListener { dialog, id ->
                                // set username

                            })
                    .setNegativeButton(R.string.dialog_cancel,
                            DialogInterface.OnClickListener { dialog, id ->
                                getDialog().cancel()
                            })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")*/
        return activity.let {
            val builder = AlertDialog.Builder(context!!)
            builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->

            })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                        dialog.cancel()
                    })
                    .setMessage("ZTest")


            builder.create()
        }
    }
}