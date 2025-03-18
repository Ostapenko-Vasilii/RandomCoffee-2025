import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.ProductData
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.ProductInCardModel
import ru.vasiliiostapenko.randomcoffee.DomainLayer.MainActivity.AppState
import ru.vasiliiostapenko.randomcoffee.DomainLayer.MainScreen.MainScreenViewModel
import ru.vasiliiostapenko.randomcoffee.R
import ru.vasiliiostapenko.randomcoffee.ui.screens.CartScreen.CartScreen
import ru.vasiliiostapenko.randomcoffee.ui.screens.MainScreen.components.CategoryTitleCard
import ru.vasiliiostapenko.randomcoffee.ui.screens.MainScreen.components.FloatingButton
import ru.vasiliiostapenko.randomcoffee.ui.screens.MainScreen.components.ProductLazyList


@Composable
fun MainScreen(

    padding: PaddingValues,
    isDarkTheme: Boolean,
    changeTheme: () -> Unit,
    showNotification: (String) -> Unit,
    currentPage: StateFlow<Int>,
    setCurrentPage: (Int) -> Unit,
    flowAppState: StateFlow<Int>,
    navigateToProductScreen: (product: ProductData) -> Unit,

    ) {

    val context = LocalContext.current
    val viewModel: MainScreenViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainScreenViewModel(context, currentPage) as T
            }
        }
    )
    val appState by flowAppState.collectAsState()
    LaunchedEffect(appState) {
        viewModel.setLocalAppState(appState)
        if (appState == AppState.START) {
            viewModel.getProductListFromStorage(currentPage.value)
            viewModel.initApiCall(currentPage.value)
        }
    }

    val categoryList by viewModel.categoryList.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val cartSum by viewModel.cartSum.collectAsState()
    val cartMap by viewModel.cartCountMap.collectAsState()
    val mainLazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val orderState by viewModel.orderState.collectAsState()
    val categoryToHeaderIndex = remember(categoryList) {
        var currentIndex = 0
        categoryList.associateWith { category ->
            val headerIndex = currentIndex
            currentIndex += 1 + (category.products.size + 1) / 2
            headerIndex
        }
    }
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentHandlerIndex by remember { mutableIntStateOf(0) }
    val categoryLazyRowState = rememberLazyListState()

    LaunchedEffect(currentPage.collectAsState().value) {
        if (appState == AppState.START) {
            viewModel.getProductListFromStorage(currentPage.value)
            viewModel.initApiCall(currentPage.value)
            viewModel.cashNewProductListFromApiAndSaveToStorage(currentPage.value + 1)
            viewModel.setApiDataState(0)
            viewModel.clearCategoryList()
        }
    }

    Scaffold(
        modifier = Modifier.padding(padding),
        floatingActionButton = FloatingButton(isDarkTheme, changeTheme, viewModel) {
            showBottomSheet = true
        },
        topBar = {
            LazyRow(state = categoryLazyRowState) {
                items(categoryList) { category ->
                    val handlerIndex = categoryToHeaderIndex[category] ?: 0
                    CategoryTitleCard(
                        category.category.slug.toString(),
                        currentHandlerIndex == handlerIndex
                    ) {
                        scope.launch {
                            mainLazyListState.animateScrollToItem(handlerIndex)
                            currentHandlerIndex = handlerIndex

                        }
                    }
                }
            }
        }
    )
    { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            if (viewModel.apiDataState.collectAsState().value == 1 && categoryList.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Red)
                ) {
                    Text(
                        stringResource(R.string.server_connection_error),
                        color = Color.White,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }

            if (categoryList.isNotEmpty()) {

                LaunchedEffect(
                    mainLazyListState.firstVisibleItemIndex,
                    mainLazyListState.firstVisibleItemScrollOffset
                ) {
                    scope.launch {
                        var isNotIn = true
                        categoryToHeaderIndex.forEach { (key, value) ->
                            if (mainLazyListState.firstVisibleItemIndex == value) {
                                currentHandlerIndex = mainLazyListState.firstVisibleItemIndex
                                isNotIn = false
                                if (key.category.id != null) {
                                    scope.launch {
                                        try {
                                            categoryLazyRowState.animateScrollToItem(key.category.id!! - 1)

                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                                }
                            }

                        }
                        if (isNotIn) {
                            currentHandlerIndex = -1
                        }
                    }

                }
                val successOrderText = stringResource(R.string.successful_order)
                LaunchedEffect(orderState) {
                    if (orderState == 3) {
                        showNotification(successOrderText)
                    }
                }
                ProductLazyList(
                    categoryList,
                    cartMap,
                    mainLazyListState,
                    viewModel,
                    { product -> navigateToProductScreen(product) },
                    currentPage,
                    { page ->
                        setCurrentPage(page)
                        try {
                            scope.launch {
                                mainLazyListState.scrollToItem(0)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                )

                CartBottomSheet(
                    showBottomSheet = showBottomSheet,
                    cart,
                    viewModel,
                    { showBottomSheet = false },
                    cartSum,
                )
            } else if (viewModel.apiDataState.collectAsState().value == 0) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        stringResource(R.string.please_waite),
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            } else if (categoryList.isEmpty() && viewModel.apiDataState.collectAsState().value == 2) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.coffee_placeholder),
                        contentDescription = "empty cart",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    if (currentPage.collectAsState().value != 0) {
                        Text(
                            stringResource(R.string.empt_prd_list),
                            fontSize = 25.sp,
                            modifier = Modifier.padding(10.dp),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            stringResource(R.string.empt_prd_lis_dick),
                            fontSize = 20.sp,
                            modifier = Modifier.padding(10.dp),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Button(
                            onClick = { setCurrentPage(currentPage.value - 1) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text(
                                text = stringResource(R.string.priv_page),
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }

                }
            } else if (viewModel.apiDataState.collectAsState().value == 1) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.connection_error),
                        contentDescription = "connection error",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(stringResource(R.string.server_connection_error), fontSize = 25.sp)
                }
            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartBottomSheet(
    showBottomSheet: Boolean,
    cart: ArrayList<ProductInCardModel>,
    viewModel: MainScreenViewModel,
    closeSheet: () -> Unit,
    cartSum: Float,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    if (showBottomSheet) {
        ModalBottomSheet(

            sheetState = sheetState,
            onDismissRequest = {
                closeSheet()
            },
        ) {
            CartScreen(
                cart,
                closeSheet,
                { viewModel.clearCart() },
                cartSum,
                { viewModel.createOrder() },
                { orderState: Int -> viewModel.setOrderState(orderState) },
                viewModel.orderState
            )
        }
    }
}