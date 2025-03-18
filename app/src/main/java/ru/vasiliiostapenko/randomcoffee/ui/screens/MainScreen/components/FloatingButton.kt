package ru.vasiliiostapenko.randomcoffee.ui.screens.MainScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.vasiliiostapenko.randomcoffee.DomainLayer.MainScreen.MainScreenViewModel
import ru.vasiliiostapenko.randomcoffee.R

@Composable
fun FloatingButton(
    isDarkTheme: Boolean,
    changeTheme: () -> Unit,
    viewModel: MainScreenViewModel,
    showBottomSheet: () -> Unit
): @Composable () -> Unit {
    val cartSum by viewModel.cartSum.collectAsState()
    val cart by viewModel.cart.collectAsState()
    return {
        Row(
            modifier = Modifier
                .padding(start = 30.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ExtendedFloatingActionButton(
                content = {
                    Icon(
                        if (isDarkTheme) {
                            painterResource(R.drawable.night_ico)
                        } else {
                            painterResource(R.drawable.sun_ico)
                        }, "theme"
                    )
                },
                onClick = {
                    changeTheme()
                },
                modifier = Modifier.size(60.dp)
            )
            if (cart.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    content = {
                        Icon(
                            painterResource(R.drawable.cart_ico),
                            "cart",
                            modifier = Modifier.padding(5.dp)
                        )
                        Text("$cartSum ₽", fontWeight = FontWeight.Bold)
                    },
                    onClick = showBottomSheet
                )
            }
        }
    }
}