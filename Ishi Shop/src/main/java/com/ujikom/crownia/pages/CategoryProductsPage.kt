package com.ujikom.crownia.pages

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.ujikom.crownia.components.ProductItemView
import com.ujikom.crownia.model.ProductModel

@Composable
fun CategoryProductsPage(modifier: Modifier = Modifier,categoryid : String,navController: NavController) {
    val productsList = remember {
        mutableStateOf<List<ProductModel>>(emptyList())
    }

    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("data").document("stock")
            .collection("products")
            .whereEqualTo("category",categoryid)
            .get().addOnCompleteListener(){
                if(it.isSuccessful){
                    val resultList = it.result.documents.mapNotNull { doc ->
                        doc.toObject(ProductModel::class.java)
                    }
                    productsList.value = resultList.plus(resultList).plus(resultList)
                }
            }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(productsList.value.chunked(2)){rowItems->
           Row {
               rowItems.forEach {
                   ProductItemView(product = it, modifier = Modifier.weight(1f), navController = navController)
               }
               if(rowItems.size==1){
                   Spacer(modifier = Modifier.weight(1f))

               }
           }
        }
    }

}