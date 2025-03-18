package ru.vasiliiostapenko.randomcoffee.ui.screens.MainScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.StateFlow
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.ProductData
import ru.vasiliiostapenko.randomcoffee.DomainLayer.MainScreen.MainScreenViewModel
import ru.vasiliiostapenko.randomcoffee.R
import ru.vasiliiostapenko.randomcoffee.data.CategoryWithProducts

@Composable
fun ProductLazyList(
    categoryList: ArrayList<CategoryWithProducts>,
    cart: Map<Int, Int>,
    mainLazyListState: LazyListState,
    viewModel: MainScreenViewModel,
    onClickToCart: (product: ProductData) -> Unit,
    currentPageFlow: StateFlow<Int>,
    setCurrentPage: (Int) -> Unit
) {
    val currentPage by currentPageFlow.collectAsState()
    LazyColumn(state = mainLazyListState) {
        categoryList.forEach { category ->
            item {
                Text(
                    category.category.slug.toString(),
                    fontSize = 30.sp,
                    modifier = Modifier.padding(top = 32.dp, bottom = 16.dp, start = 16.dp)
                )
            }
            var productPairs = category.products.chunked(2)
            items(productPairs) { productPair ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    productPair.forEach { product ->
                        Box(modifier = Modifier.weight(1f)) {
                            ProductCard(
                                product = product,
                                countInCart = cart.getOrDefault(product.id, 0),
                                addToCart = {
                                    viewModel.addProductToCart(product.id ?: -1)
                                },
                                removeFromCart = {
                                    viewModel.removeProductFromCart(product.id ?: -1)
                                },
                                onClickToCart
                            )
                        }
                    }
                    if (productPair.size < 2) {
                        Box(modifier = Modifier.weight(1f)) {}
                    }
                }

            }
        }
        item {
            Row(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    enabled = currentPage > 0,
                    onClick = { if (currentPage > 0) setCurrentPage(currentPage - 1) },
                    modifier = Modifier.padding(5.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_keyboard_arrow_left_24),
                        contentDescription = null
                    )
                }
                Text(
                    text = "${currentPage + 1}",
                    fontSize = 20.sp
                )
                IconButton(
                    enabled = true,
                    onClick = { setCurrentPage(currentPage + 1) },
                    modifier = Modifier.padding(5.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_keyboard_arrow_right_24),
                        contentDescription = null
                    )
                }
            }
        }
        item {
            //save zone
            Box(modifier = Modifier.padding(40.dp))
        }
    }
}