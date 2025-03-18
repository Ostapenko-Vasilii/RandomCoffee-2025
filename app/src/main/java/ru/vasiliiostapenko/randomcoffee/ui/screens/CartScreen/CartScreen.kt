package ru.vasiliiostapenko.randomcoffee.ui.screens.CartScreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.ProductInCardModel
import ru.vasiliiostapenko.randomcoffee.R
import ru.vasiliiostapenko.randomcoffee.ui.screens.CartScreen.components.CartProductCard
import ru.vasiliiostapenko.randomcoffee.ui.theme.TransparentColor
import ru.vasiliiostapenko.randomcoffee.ui.theme.snackBarColor

@Composable
fun CartScreen(
    cart: ArrayList<ProductInCardModel>,
    closeSheet: () -> Unit,
    clearCart: () -> Unit,
    cartSum: Float,
    createOrder: () -> Unit,
    setOrderState: (Int) -> Unit,
    orderState: StateFlow<Int>
) {
    val scope = rememberCoroutineScope()
    val cartSnackBarHostState = remember { SnackbarHostState() }
    val orderState2 by orderState.collectAsState()
    Box() {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight(0.9f)
        ) {
            Column(modifier = Modifier.weight(11f)) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.your_order), fontSize = 25.sp)
                    IconButton(
                        onClick = {
                            clearCart()
                            closeSheet()
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = TransparentColor)
                    ) {
                        Image(painterResource(R.drawable.bin_ico), contentDescription = "cart")
                    }
                }
                SimpleSpaseBox()
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(cart) { index, product ->
                        CartProductCard(product)
                    }
                    item {
                        SimpleSpaseBox()
                        Row(
                            modifier = Modifier
                                .padding(15.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = stringResource(R.string.sum_order))
                            Text(text = "$cartSum ₽", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }


            Button(
                onClick = {
                    scope.launch {
                        createOrder()
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.5f)
                    .padding(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                enabled = orderState2 == 0
            ) {
                Text(
                    text = stringResource(R.string.create_order),
                    color = Color.White,
                    fontSize = 20.sp
                )
            }


        }

        SnackbarHost(
            hostState = cartSnackBarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
            snackbar = { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = snackBarColor,
                    contentColor = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        )
    }
    val unsuccessfulOrderText = stringResource(R.string.error_order)

    LaunchedEffect(orderState2) {
        if (orderState2 == 2) {
            cartSnackBarHostState.showSnackbar(
                message = unsuccessfulOrderText,
                duration = SnackbarDuration.Long
            )
        } else if (orderState2 == 3) {
            clearCart()
            closeSheet()
        }
    }
}


@Composable
fun SimpleSpaseBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .height(2.dp)
            .background(color = MaterialTheme.colorScheme.primary)
    )

}