package ru.vasiliiostapenko.randomcoffee.ui.screens.ProductInfoScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.ProductData
import ru.vasiliiostapenko.randomcoffee.R

@Composable
fun ProductInfoScreen(
    innerPadding: PaddingValues,
    isDarkTheme: Boolean,
    setTheme: () -> Unit,
    currentProductData: ProductData,
    back: () -> Unit
) {
    val isAlreadyBack = remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.padding(innerPadding),
        floatingActionButton = FloatingProductInfoButton(isDarkTheme, setTheme),
        topBar = {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically

            ) {

                IconButton(
                    onClick = {
                        if (!isAlreadyBack.value) {
                            isAlreadyBack.value = true
                            back()
                        }
                    }, enabled = !isAlreadyBack.value
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "back",

                        )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item {
                AsyncImage(
                    model = currentProductData.imageUrl,
                    contentDescription = currentProductData.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    placeholder = painterResource(R.drawable.coffee_placeholder)
                )
                Text(
                    text = currentProductData.name.toString(),
                    fontSize = 30.sp,
                    modifier = Modifier.padding(5.dp)
                )
            }
            item {
                Text(
                    text = currentProductData.description.toString(),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(5.dp)
                )
            }
            item {
                //save zone
                Box(modifier = Modifier.padding(20.dp))
            }
        }
    }

}

@Composable
fun FloatingProductInfoButton(
    isDarkTheme: Boolean,
    changeTheme: () -> Unit,
): @Composable () -> Unit {

    return {
        Row(
            modifier = Modifier
                .padding(start = 30.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
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
        }
    }
}



