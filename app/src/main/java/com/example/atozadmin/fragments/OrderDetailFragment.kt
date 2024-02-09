package com.example.atozadmin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.atozadmin.R
import com.example.atozadmin.Utils
import com.example.atozadmin.adapter.AdapterCartProducts
import com.example.atozadmin.databinding.FragmentOrderDetailBinding
import com.example.atozadmin.viewmodels.AdminViewModel
import kotlinx.coroutines.launch

class OrderDetailFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private var status = 0
    private var orderId = ""
    var curstatus=0
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var adapterCartProducts: AdapterCartProducts
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater)
        getValues()
        settingStatus(status)
        lifecycleScope.launch {
            getOrderedProducts()

        }
        onBackButtonClick()
        onChangeStatusClick()

        return binding.root
    }

    private fun onChangeStatusClick() {
        binding.btnChangeStatus.setOnClickListener {
            val popupMenu=PopupMenu(requireContext(),it)
            popupMenu.menuInflater.inflate(R.menu.menu_popup,popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.menuReceived->{
                        curstatus=1
                        if(curstatus>status) {
                            status=1
                            settingStatus(1)
                            lifecycleScope.launch {
                                viewModel.sendNotification(orderId, "Order Received", "Your order is received")
                            }

                            viewModel.updateOrderStatus(orderId, 1)
                        }else{
                            Utils.showToast(requireContext(),"Order is already received")
                        }
                        true
                    }
                    R.id.menuDispatched->{

                        curstatus=2
                        if(curstatus>status) {
                            status=2
                            settingStatus(2)
                            lifecycleScope.launch {
                                viewModel.sendNotification(orderId, "Order Dispathed", "Your order is dispathched")
                            }
                            viewModel.updateOrderStatus(orderId, 2)
                        }
                        else{
                            Utils.showToast(requireContext(),"Order is already dispatched")
                        }
                        true
                    }
                    R.id.menuDelivered->{
                        curstatus=3
                        if(curstatus>status) {
                            status=3
                            settingStatus(3)
                            lifecycleScope.launch {
                                viewModel.sendNotification(orderId, "Order Delivered", "Your order is delivered")
                            }
                            viewModel.updateOrderStatus(orderId, 3)
                        }
                        true
                    }
                    else->false
                }
            }
        }
    }

    private fun onBackButtonClick() {
        binding.tbOrderDetailFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_orderDetailFragment_to_orderFragment)
        }
    }

    suspend fun getOrderedProducts() {
        viewModel.getOrderedProducts(orderId).collect{
            adapterCartProducts = AdapterCartProducts()
            binding.rvProductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(it)

        }


    }

    private fun settingStatus(status :Int) {
        val viewMap = mapOf(
            0 to listOf(binding.iv1),
            1 to listOf(binding.iv1, binding.iv2, binding.view1),
            2 to listOf(binding.iv1, binding.iv2, binding.view1, binding.iv3, binding.view2),
            3 to listOf(binding.iv1, binding.iv2, binding.view1, binding.iv3, binding.view2, binding.iv4, binding.view3)
        )

        val viewsTint=viewMap.getOrDefault(status, emptyList())

        for(it in viewsTint){
            it.backgroundTintList= ContextCompat.getColorStateList(requireContext(),R.color.blue)
        }



    }
    private fun getValues() {
        val bundle = arguments
        status = bundle?.getInt("status")!!
        orderId = bundle.getString("orderId").toString()
        binding.tvAddress.text=bundle.getString("userAddress").toString()

    }

}


