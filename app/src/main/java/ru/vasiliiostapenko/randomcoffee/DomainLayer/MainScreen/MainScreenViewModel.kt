package ru.vasiliiostapenko.randomcoffee.DomainLayer.MainScreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.vasiliiostapenko.randomcoffee.DataLayer.CartManager
import ru.vasiliiostapenko.randomcoffee.DataLayer.NameProductsManager
import ru.vasiliiostapenko.randomcoffee.DataLayer.PricesManager
import ru.vasiliiostapenko.randomcoffee.DataLayer.ProductsDataManager
import ru.vasiliiostapenko.randomcoffee.DataLayer.ProductsImageUrlsManager
import ru.vasiliiostapenko.randomcoffee.DataLayer.api.RandomCoffeeAPI
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.OrderModel
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.ProductInCardModel
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.ProductList
import ru.vasiliiostapenko.randomcoffee.DomainLayer.MainActivity.AppState
import ru.vasiliiostapenko.randomcoffee.data.CategoryWithProducts
import java.lang.Thread.sleep


class MainScreenViewModel(private val current: Context, private val currentPageId: StateFlow<Int>) :
    ViewModel() {

    private val _categoryList = MutableStateFlow<ArrayList<CategoryWithProducts>>(arrayListOf())
    val categoryList: StateFlow<ArrayList<CategoryWithProducts>> = _categoryList.asStateFlow()
    fun clearCategoryList() {
        _categoryList.value = arrayListOf()
    }

    private val _cart = MutableStateFlow<ArrayList<ProductInCardModel>>(arrayListOf())
    var cart: StateFlow<ArrayList<ProductInCardModel>> = _cart.asStateFlow()

    private var _cartSum: MutableStateFlow<Float> = MutableStateFlow<Float>(0f)
    var cartSum: StateFlow<Float> = _cartSum.asStateFlow()

    private val _apiDataState = MutableStateFlow(0)
    val apiDataState: StateFlow<Int> = _apiDataState.asStateFlow()
    fun setApiDataState(newState: Int) {
        _apiDataState.value = newState
    }

    private val _cartCountMap = MutableStateFlow<Map<Int, Int>>(mapOf())
    var cartCountMap: StateFlow<Map<Int, Int>> = _cartCountMap.asStateFlow()

    private val _orderState = MutableStateFlow<Int>(0)
    var orderState: StateFlow<Int> = _orderState.asStateFlow()
    fun setOrderState(newState: Int) {
        _orderState.value = newState
    }

    private val _localAppState = MutableStateFlow<Int>(AppState.START)
    fun setLocalAppState(newState: Int) {
        _localAppState.value = newState
    }


    init {
        getCart()
    }

    fun initApiCall(pageId: Int) {
        getNewProductListFromApiAndSaveToStorage(pageId)
        val errCorutineScope = CoroutineScope(Dispatchers.IO).launch {
            sleep(4800)
            if (_apiDataState.value != 2) {
                _apiDataState.value = 1
            }
            while (_apiDataState.value == 1 && _localAppState.value == AppState.START) {
                Log.d("ApiUpdate", "Try again ")
                getNewProductListFromApiAndSaveToStorage(pageId)
                sleep(3000)
            }
        }
        errCorutineScope.start()
    }

    private fun updateCategoryList(
        newCategoryWithProducts: ArrayList<CategoryWithProducts>,
        currentPage: Int
    ) {
        if (!_categoryList.equals(newCategoryWithProducts) && currentPage == currentPageId.value) {
            _categoryList.value = newCategoryWithProducts
            saveActualDataToStorage(newCategoryWithProducts)

        }
    }


    private fun saveActualDataToStorage(categorysWithProducts: ArrayList<CategoryWithProducts>) {
        val namesMap = mutableMapOf<Int, String>()
        val rubPricesMap = mutableMapOf<Int, Float>()
        val imageUrlsMap = mutableMapOf<Int, String>()
        val saveNewNamesScope = CoroutineScope(Dispatchers.IO).launch {
            categorysWithProducts.forEach { category ->
                category.products.forEach { product ->
                    namesMap.put(product.id ?: -1, product.name.toString())
                    rubPricesMap.put(
                        product.id ?: -1,
                        product.prices.find { it.currency == "RUB" }?.value?.toFloat() ?: -1f
                    )
                    imageUrlsMap.put(product.id ?: -1, product.imageUrl.toString())
                }
            }
            NameProductsManager().setActualNamesToDataStore(current, namesMap)
            PricesManager().setActualRubPricesToDataStore(current, rubPricesMap)
            ProductsImageUrlsManager().setActualImageUrlToDataStore(current, imageUrlsMap)
        }
        saveNewNamesScope.start()
    }


    fun getProductListFromStorage(currentPage: Int) {
        var getProductListFromStorage = CoroutineScope(Dispatchers.IO).launch {
            try {
                var products = ProductsDataManager().getProductsDataStore(current, currentPage)
                if (products != null) {
                    var sortedData = toCategoryWithProductsData(products = products)
                    updateCategoryList(sortedData, currentPage)

                }
                Log.d("Get Product List", "$products")

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        getProductListFromStorage.start()

    }


    private fun updateCart(newCart: ArrayList<ProductInCardModel>) {
        _cart.value = newCart
        viewModelScope.launch {
            var cartTempSum = 0f
            newCart.forEach { product ->
                cartTempSum += product.count * product.price
            }
            _cartSum.value = cartTempSum
            var newCartTempMap = mutableMapOf<Int, Int>()
            newCart.forEach { product ->
                newCartTempMap.put(product.productId, product.count ?: 0)
            }
            Log.d("New Cart Sum", newCart.toString())
            _cartCountMap.value = newCartTempMap
        }
    }

    fun getCart() {
        var getCartFromStage = CoroutineScope(Dispatchers.IO).launch {
            val idsWithCount = CartManager().getCartFromData(current)
            var cardModelList: ArrayList<ProductInCardModel> = arrayListOf()
            idsWithCount.forEach { (id, count) ->
                val name = NameProductsManager().getNamesByIdDataStore(current, id)
                val price = PricesManager().getRubPriceByIdDataStore(current, id)
                val image = ProductsImageUrlsManager().getUrlByIdDataStore(current, id)
                cardModelList.add(ProductInCardModel(id, name, count, price, image))
            }
            updateCart(cardModelList)
        }
        getCartFromStage.start()

    }

    fun addProductToCart(productId: Int) {
        if (productId != -1) {
            val addOneToCart = CoroutineScope(Dispatchers.IO).launch {
                var newCart: ArrayList<ProductInCardModel> = arrayListOf()
                var isIn = false
                CartManager().addOneProductToCart(current, productId)
                cart.value.forEach { product ->
                    if ((product.productId == productId)) {
                        if (product.count < 10) {
                            newCart.add(
                                ProductInCardModel(
                                    product.productId,
                                    product.productName,
                                    product.count + 1,
                                    product.price,
                                    product.imageUrl
                                )
                            )
                        } else {
                            newCart.add(product)
                        }
                        isIn = true
                    } else {
                        newCart.add(product)
                    }
                }
                if (!isIn) {
                    newCart.add(
                        ProductInCardModel(
                            productId,
                            NameProductsManager().getNamesByIdDataStore(current, productId),
                            1,
                            PricesManager().getRubPriceByIdDataStore(current, productId),
                            ProductsImageUrlsManager().getUrlByIdDataStore(current, productId)
                        )
                    )
                }
                updateCart(newCart)
            }
            addOneToCart.start()
        }
    }

    fun removeProductFromCart(productId: Int) {
        if (productId != -1) {
            val addOneToCart = CoroutineScope(Dispatchers.IO).launch {
                CartManager().removeOneProductFromCart(current, productId)
                var newCart: ArrayList<ProductInCardModel> = arrayListOf()
                cart.value.forEach { product ->
                    if (product.productId == productId) {
                        if (product.count > 1) {
                            newCart.add(
                                ProductInCardModel(
                                    product.productId,
                                    product.productName,
                                    product.count - 1,
                                    product.price,
                                    product.imageUrl
                                )
                            )
                        }
                    } else {
                        newCart.add(product)
                    }
                }
                updateCart(newCart)
            }
            addOneToCart.start()
        }
    }

    private fun getNewProductListFromApiAndSaveToStorage(pageId: Int) {
        val apiResult = CoroutineScope(Dispatchers.IO).launch {
            try {
                var res = RandomCoffeeAPI().getProductsByPage(
                    pageId,
                    100,
                    { respounse, pageId -> saveProductListToStorage(respounse, pageId) },
                    { _apiDataState.value = 1 })
                if (res != null) {
                    _apiDataState.value = 2
                    var sortedData = toCategoryWithProductsData(products = res)
                    updateCategoryList(sortedData, pageId)

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        apiResult.start()
    }

    fun cashNewProductListFromApiAndSaveToStorage(pageId: Int) {
        val apiResult = CoroutineScope(Dispatchers.IO).launch {
            try {
                var res = RandomCoffeeAPI().getProductsByPage(
                    pageId,
                    100,
                    { respounse, pageId -> saveProductListToStorage(respounse, pageId) },
                    { _apiDataState.value = 1 })
                if (res != null) {
                    _apiDataState.value = 2
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        apiResult.start()
    }

    fun saveProductListToStorage(requestBody: String, pageId: Int) {
        val coroutineScope = CoroutineScope(Dispatchers.IO).launch {

            ProductsDataManager().setProductsDataStore(current, requestBody, pageId)
        }
        coroutineScope.start()
    }

    fun clearCart() {
        updateCart(arrayListOf())
        viewModelScope.launch {
            CartManager().removeAllProductsFromCart(current)
        }
    }

    fun createOrder() {
        val createOrderApi = CoroutineScope(Dispatchers.IO).launch {
            _orderState.value = 1
            val res = RandomCoffeeAPI().createOrderRequest(
                OrderModel(
                    cartCountMap.value,
                    REGISTRATION_TOKEN
                )
            )
            if (res) {
                _orderState.value = 3
                sleep(2011)
                _orderState.value = 0
            } else {
                _orderState.value = 2
                sleep(1611)
                _orderState.value = 0
            }
        }
        createOrderApi.start()
    }

    companion object {
        const val REGISTRATION_TOKEN = "<FCM Registration Token>"
    }
}


fun toCategoryWithProductsData(products: ProductList): ArrayList<CategoryWithProducts> {
    var categoriesList: ArrayList<CategoryWithProducts> = arrayListOf()
    var categoriesIds = arrayListOf<Int>()
    for (product in products.data) {
        if (!(product.category?.id in categoriesIds)) {
            categoriesList.add(CategoryWithProducts(product.category!!))
            categoriesIds.add(product.category!!.id!!)
        }
        for (i in 0..categoriesIds.size) {
            if (product.category?.id == categoriesIds[i]) {
                categoriesList[i].products.add(product)
                break
            }
        }
    }

    val comparator = compareBy<CategoryWithProducts> { it.category.id ?: Int.MIN_VALUE }
    categoriesList = ArrayList(categoriesList.sortedWith(comparator))

    return categoriesList
}

