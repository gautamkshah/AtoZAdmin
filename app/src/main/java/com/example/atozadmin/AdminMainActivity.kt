package com.example.atozadmin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController

class AdminMainActivity : AppCompatActivity() {
    //private lateinit var binding: ActivityAdminMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding= ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_admin_main)
        // NavigationUI.setupWithNavController(binding.bottomMenu, Navigation.findNavController(this,R.id.fragmentContainerView2))
        var navcontroller = Navigation.findNavController(this, R.id.fragmentContainerView2)
        var bootomnav =
            findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_menu)
        bootomnav.setupWithNavController(navcontroller)
    }
}