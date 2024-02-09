package com.example.atozadmin

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.example.atozadmin.databinding.ProgressDialogBinding
import com.google.firebase.auth.FirebaseAuth


object Utils {
    private var dialog: AlertDialog? = null
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    fun showDialog(context: Context, message:String){
        val progress= ProgressDialogBinding.inflate(LayoutInflater.from(context))
        progress.tvMessage.text=message
        val builder= AlertDialog.Builder(context)
        builder.setView(progress.root)
        dialog=builder.create()
        dialog!!.show()

    }
    fun hideDialog(){
        dialog!!.dismiss()
    }


    private var firebaseAuthInstance: FirebaseAuth?=null
    fun getAuthInstance(): FirebaseAuth {
        if (firebaseAuthInstance==null){
            firebaseAuthInstance= FirebaseAuth.getInstance()
        }
        return firebaseAuthInstance!!
    }
    fun getCurrentUserId():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun getRandomId():String{
       return (List(26) { ('A'..'Z').random() } + List(26) { ('a'..'z').random() } + List(10) { ('0'..'9').random() }).shuffled().joinToString("").take(26)
    }
}