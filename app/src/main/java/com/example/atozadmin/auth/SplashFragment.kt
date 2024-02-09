package com.example.atozadmin.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.atozadmin.AdminMainActivity
import com.example.atozadmin.R
import com.example.atozadmin.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {
 private lateinit var auth : FirebaseAuth
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setStatusBarColor()

        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                viewModel.isACurrentUser.collect{
                    if(it){

                        Log.d("SplashFragment", "onCreateView: ")
                        startActivity(Intent(requireContext(), AdminMainActivity::class.java))
                        requireActivity().finish()
                    }else{
                        findNavController().navigate(R.id.action_splashFragment_to_signInFragment)
                    }
                }
            }

        },3000)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
    private fun setStatusBarColor(){
        activity?.window?.apply{
            val statusBarColors= ContextCompat.getColor(context, R.color.yellow)
            statusBarColor=statusBarColors
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

}