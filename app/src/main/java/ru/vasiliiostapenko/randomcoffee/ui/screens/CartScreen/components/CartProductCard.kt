package ru.vasiliiostapenko.randomcoffee.ui.screens.CartScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.ProductInCardModel

@Composable
fun CartProductCard(product: ProductInCardModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.productName,
                modifier = Modifier.size(40.dp)
            )
            Text(buildAnnotatedString {
                append(product.productName)
                if (product.count > 1) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(" ${product.count}")
                    }
                }
            }, modifier = Modifier.padding(start = 16.dp))
        }
        Text(text = "${product.price} ₽", fontWeight = FontWeight.Bold)
    }
}