package com.example.atozadmin.fragments

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.atozadmin.Constants
import com.example.atozadmin.R
import com.example.atozadmin.Utils
import com.example.atozadmin.adapter.AdapterProduct
import com.example.atozadmin.adapter.CategoriesAdapter
import com.example.atozadmin.databinding.EditProductLayoutBinding
import com.example.atozadmin.databinding.FragmentHomeBinding
import com.example.atozadmin.model.Categories
import com.example.atozadmin.model.Product
import com.example.atozadmin.viewmodels.AdminViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    val viewModel: AdminViewModel by viewModels()
    private lateinit var adapterProduct: AdapterProduct

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding= FragmentHomeBinding.inflate(layoutInflater)
        setStatusBarColor()
        setCategories()


        getAlltheProducts("All")
        searchProducts()


        return binding.root
    }

    private fun searchProducts() {
        binding.searchEt.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query=s.toString().trim()
                Log.d("TAG", "onTextChanged: $query")
                adapterProduct.filter?.filter(query)
             //   Log.d("taf" , "${adapterProduct.filter}")

                Utils.showToast(requireContext(), query)



             }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun getAlltheProducts(category: String) {
        binding.shimmerViewContainer.visibility=View.VISIBLE


        lifecycleScope.launch {
            viewModel.fetchAllTheProduct(category).collect {
                if(it.isEmpty()){
                    binding.rvProducts.visibility=View.GONE
                    binding.tvtext.visibility=View.VISIBLE

                }
                else{
                    binding.rvProducts.visibility=View.VISIBLE
                    binding.tvtext.visibility=View.GONE
                }
                adapterProduct=AdapterProduct(::onEditButtonClicked)
                binding.rvProducts.adapter=adapterProduct
                adapterProduct.differ.submitList(it)
                adapterProduct.originalList=it as ArrayList<Product>
                Log.d("TAAG", "getAlltheProducts: ${adapterProduct.originalList.size}")
                binding.shimmerViewContainer.visibility=View.GONE

            }
        }

    }

    private fun setCategories() {
        val categoryList=ArrayList<Categories>()
        for (i in 0 until Constants.allProductsCategoryIcon.size){
            categoryList.add(Categories(
                Constants.allProductsCategory[i],
                Constants.allProductsCategoryIcon[i]))
        }
        binding.rvCategories.adapter=CategoriesAdapter(categoryList, ::onCategoryClicked)
    }
   private fun onCategoryClicked(categories: Categories){
       getAlltheProducts(categories.category)

    }
   private fun onEditButtonClicked(product: Product){
       val editProduct=EditProductLayoutBinding.inflate(LayoutInflater.from(requireContext()))
       editProduct.apply {
           etProductTitle.setText(product.productTitle)
           etProductQuantity.setText (product.productQuantity.toString())
           etProductUnit.setText(product.productUnit)
           etProductPrice.setText(product.productPrice.toString() )
           etProductStock. setText(product.productStock.toString())
           etProductCategory.setText(product.productCategory)
           etProductType.setText(product.productType)
       }
       val alertDialog=AlertDialog.Builder(requireContext())
           .setView(editProduct.root)
           .create()
       alertDialog.show()

       editProduct.btnEdit.setOnClickListener {
           editProduct.apply {
               etProductTitle.isEnabled=true
                etProductQuantity.isEnabled=true
                etProductUnit.isEnabled=true
                etProductPrice.isEnabled=true
                etProductStock.isEnabled=true
                etProductCategory.isEnabled=true
                etProductType.isEnabled=true
           }
           setAutoCompleteTextView(editProduct)
           editProduct.btnSave.setOnClickListener {
               lifecycleScope.launch {
                   product.productTitle=editProduct.etProductTitle.text.toString()
                   product.productQuantity=editProduct.etProductQuantity.text.toString().toInt()
                   product.productUnit=editProduct.etProductUnit.text.toString()
                   product.productPrice=editProduct.etProductPrice.text.toString().toInt()
                   product.productStock=editProduct.etProductStock.text.toString().toInt()
                   product.productCategory=editProduct.etProductCategory.text.toString()
                   product.productType=editProduct.etProductType.text.toString()
                   viewModel.savingUpdatedProduct(product)
               }
               Utils.showToast(requireContext(), "Product Updated")
               alertDialog.dismiss()
           }
       }
    }

    private fun setAutoCompleteTextView(editProduct: EditProductLayoutBinding){
        val units= ArrayAdapter(requireContext(), R.layout.show_list, Constants.allUnitsofproducts)
        val category= ArrayAdapter(requireContext(),
            R.layout.show_list,
            Constants.allProductsCategory
        )
        val productType= ArrayAdapter(requireContext(),
            R.layout.show_list,
            Constants.allProductType
        )
        editProduct.apply {
            etProductUnit.setAdapter(units)
            etProductCategory.setAdapter(category)
            etProductType.setAdapter(productType)
        }

    }

    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(context, R.color.red)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }



}
