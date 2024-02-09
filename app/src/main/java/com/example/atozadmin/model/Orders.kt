package com.example.atozadmin.model

import com.example.atozadmin.CartProducts


data class Orders(
    val orderId: String? = null,
    val orderList: List<CartProducts>? = null,
    val userAddress: String ? = null,
    val orderStatus: Int ? = 0,
    val orderDate: String ? = null,
    val orderingUserId: String ? = null,

    )
