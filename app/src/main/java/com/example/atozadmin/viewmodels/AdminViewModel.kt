package com.example.atozadmin.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.atozadmin.CartProducts
import com.example.atozadmin.Utils
import com.example.atozadmin.api.ApiUtilities
import com.example.atozadmin.model.Notification
import com.example.atozadmin.model.NotificationData
import com.example.atozadmin.model.Orders
import com.example.atozadmin.model.Product
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class AdminViewModel : ViewModel() {
    private val _isImageUploaded = MutableStateFlow(false)
    var isImageUploaded: StateFlow<Boolean> = _isImageUploaded
    private val _isProductSaved = MutableStateFlow(false)
    var isProductSaved: StateFlow<Boolean> = _isProductSaved
    private val _downloadUrls = MutableStateFlow<ArrayList<String?>>(arrayListOf())
    var downloadUrls: StateFlow<ArrayList<String?>> = _downloadUrls
    fun saveImagesInDB(imageUri: ArrayList<Uri>) {
        val downloadUrls = ArrayList<String?>()

        imageUri.forEach { uri ->
            val imageRef = FirebaseStorage.getInstance().reference.child(Utils.getCurrentUserId())
                .child("images").child(UUID.randomUUID().toString())
            imageRef.putFile(uri).continueWithTask {
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                val url = task.result
                downloadUrls.add(url.toString())

                if (downloadUrls.size == imageUri.size) {
                    _isImageUploaded.value = true
                    _downloadUrls.value = downloadUrls
                }
            }

        }
    }

    fun saveProduct(product: Product) {
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("AllProducts/${product.productRandomId}").setValue(product)
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference("Admins")
                    .child("ProductCategory/${product.productCategory}/${product.productRandomId}")
                    .setValue(product).addOnSuccessListener {
                        FirebaseDatabase.getInstance().getReference("Admins")
                            .child("ProductType/${product.productType}/${product.productRandomId}")
                            .setValue(product).addOnSuccessListener {
                                _isProductSaved.value = true
                            }
                    }
            }
    }


    fun fetchAllTheProduct(category: String): Flow<List<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children) {
                    val prod = product.getValue(Product::class.java)
                    if (category == "All" || prod?.productCategory == category) {
                        products.add(prod!!)
                    }

                }
                trySend(products)

            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        db.addValueEventListener(eventListener)
        awaitClose {
            db.removeEventListener(eventListener)
        }
    }

    fun savingUpdatedProduct(product: Product) {
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("AllProducts/${product.productRandomId}").setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("ProductCategory/${product.productCategory}/${product.productRandomId}")
            .setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("ProductType/${product.productType}/${product.productRandomId}")
            .setValue(product)


    }


    fun getAllOrders(): Flow<List<Orders>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders")
            .orderByChild("orderStatus")
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Orders>()
                for (orders in snapshot.children) {
                    val order = orders.getValue(Orders::class.java)
                    if (order != null) {
                        orderList.add(order)
                    }

                }
                // Utils.showToast(getApplication(),"No  Found${orderList.size}dn")
                trySend(orderList)
            }

            override fun onCancelled(error: DatabaseError) {


            }

        }
        db.addValueEventListener(eventListener)
        awaitClose() {
            db.removeEventListener(eventListener)
        }


    }

    fun getOrderedProducts(orderId: String): Flow<List<CartProducts>> = callbackFlow {
        val db = Firebase.database.getReference("Admins").child("Orders").child(orderId)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Orders::class.java)
                trySend(order?.orderList!!)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
        db.addValueEventListener(eventListener)
        awaitClose {
            db.removeEventListener(eventListener)
        }
    }

    fun updateOrderStatus(orderId: String, status: Int) {
        FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orderId)
            .child("orderStatus").setValue(status)
    }


    suspend fun sendNotification(orderId: String, title: String, message: String) {
        val gettoken =
            FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orderId)
                .child("orderingUserId").get()
        Log.d("iam", "sendNotification: ${gettoken.toString()}")

        gettoken.addOnCompleteListener {

            val userid = it.result.getValue(String::class.java)

            val userToken=FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(userid!!)
                .child("userToken").get()
            userToken.addOnCompleteListener {
                val notification = Notification(it.result.getValue(String::class.java), NotificationData(title, message))

                ApiUtilities.notificationApi.onSendNotification(notification)
                    .enqueue(object : Callback<Notification> {
                        override fun onResponse(
                            call: Call<Notification>,
                            response: Response<Notification>,
                        ) {
                            if (response.isSuccessful) {
                                Log.d("GG", "onResponse:")

                            } else {
                                Log.d("GGG", "nopResponse: ")
                            }
                        }

                        override fun onFailure(call: Call<Notification>, t: Throwable) {
                        }

                    })

            }



        }


    }


}