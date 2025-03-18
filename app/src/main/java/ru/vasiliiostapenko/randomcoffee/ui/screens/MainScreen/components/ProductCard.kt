package ru.vasiliiostapenko.randomcoffee.ui.screens.MainScreen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.ProductData
import ru.vasiliiostapenko.randomcoffee.R

@Composable
fun ProductCard(
    product: ProductData,
    countInCart: Int,
    addToCart: (id: Int) -> Unit,
    removeFromCart: (id: Int) -> Unit,
    click: (product: ProductData) -> Unit
) {
    val textColor = remember { mutableStateOf(Color.Black) }
    textColor.value = MaterialTheme.colorScheme.tertiary
    val priceRub = remember(product) {
        product.prices.find { it.currency == "RUB" }?.value?.toString() ?: "-"
    }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        onClick = { click(product) }
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                placeholder = painterResource(R.drawable.coffee_placeholder),
                modifier = Modifier
                    .size(140.dp)
                    .padding(vertical = 8.dp)
                    .clickable { click(product) },

                )


            Text(
                product.name.toString(),
                fontSize = 20.sp,
                color = textColor.value,
                modifier = Modifier.clickable { click(product) }
            )
            if (countInCart == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$priceRub ₽",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 20.sp
                    )
                    IconButton(
                        onClick = {
                            product.id?.let { addToCart(it) }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.pluse_ico),
                            contentDescription = "Add to cart"
                        )
                    }
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (product.id != 0) {
                                removeFromCart(product.id!!)
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceTint)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.minus_ico),
                            contentDescription = "Delete once from cart"
                        )
                    }
                    Text(
                        text = countInCart.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(horizontal = 5.dp)
                    )
                    IconButton(
                        onClick = {
                            if (product.id != 0) {
                                addToCart(product.id!!)
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceTint),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.pluse_ico),
                            contentDescription = "Add once to cart"
                        )
                    }
                }
            }
        }
    }
}

