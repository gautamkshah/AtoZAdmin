package com.example.atozadmin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.atozadmin.R
import com.example.atozadmin.Utils
import com.example.atozadmin.adapter.AdapterOrders
import com.example.atozadmin.databinding.FragmentOrderBinding
import com.example.atozadmin.model.OrderedItems
import com.example.atozadmin.viewmodels.AdminViewModel
import kotlinx.coroutines.launch
import java.lang.StringBuilder

class OrderFragment : Fragment() {
    private lateinit var adapterOrders: AdapterOrders
    private lateinit var binding: FragmentOrderBinding
    private val viewModel: AdminViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding=FragmentOrderBinding.inflate(layoutInflater)

        getAllOrders()
        return binding.root

    }

    private fun getAllOrders() {
        binding.shimmerViewContainer.visibility=View.VISIBLE
        lifecycleScope.launch {
            viewModel.getAllOrders().collect(){
                    orderList->


                if(orderList.isNotEmpty()){

                    val orderedList=ArrayList<OrderedItems>()
                    for(orders in orderList){

                        val title= StringBuilder()
                        var totalprice=0
                        for (products in orders.orderList!!) {
                            val price = products.productPrice?.substring(1)?.toInt()
                            val itemCount = products.productCount!!
                            totalprice += (price?.times(itemCount)!!)

                            title.append("${products.productCategory}, ")
                        }
                        val orderedItems=OrderedItems(orders.orderId,orders.orderDate,orders.orderStatus,title.toString(),totalprice,orders.userAddress)

                        orderedList.add(orderedItems)

                    }
                    adapterOrders= AdapterOrders(requireContext(),::onOrderItemViewClick)
                    binding.rvOrders.adapter=adapterOrders
                    adapterOrders.differ.submitList(orderedList)
                    binding.shimmerViewContainer.visibility=View.VISIBLE

                }
                else{

                }

            }
        }

    }

    fun onOrderItemViewClick(orderedItems: OrderedItems){
        val bundle=Bundle()
        bundle.putInt("status", orderedItems.itemStatus!!)
        bundle.putString("orderId", orderedItems.orderId)
        bundle.putString("userAddress",orderedItems.userAddress)
        findNavController().navigate(R.id.action_orderFragment_to_orderDetailFragment,bundle)

    }





}