package com.example.user.gambling.game

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.user.gambling.R

class SetPlayerNameDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View? = inflater.inflate(R.layout.dialog_player_name, container, false)
        val btnCancelDialog: Button = view!!.findViewById(R.id.buttonCancelDialog)
        val btnConfirmDialog: Button = view.findViewById(R.id.buttonConfirmDialog)

        btnCancelDialog.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(context, "No new player name set!", Toast.LENGTH_SHORT).show()
        }

        btnConfirmDialog.setOnClickListener {
            val eTUserName: TextView = view.findViewById(R.id.username)
            val newPlayerName = eTUserName.text.toString()
            if (isPlayerNameNotValid(newPlayerName)) {
                eTUserName.error = "Cannot be empty!"
            } else {
                updateObserver(newPlayerName)
                dialog.dismiss()
                Toast.makeText(context, "Set $newPlayerName as new player name.", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private fun updateObserver(newPlayerName: String) {
        activity?.let { it2 ->
            val userNameViewModel = ViewModelProviders.of(it2).get(UserNameViewModel::class.java)
            userNameViewModel.userName.postValue(newPlayerName)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS)
        return dialog
    }

    private fun isPlayerNameNotValid(playerName: String): Boolean {
        return playerName.isEmpty()
    }


}