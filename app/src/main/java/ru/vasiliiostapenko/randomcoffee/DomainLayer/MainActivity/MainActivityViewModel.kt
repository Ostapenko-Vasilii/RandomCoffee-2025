package ru.vasiliiostapenko.randomcoffee.DomainLayer.MainActivity

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.vasiliiostapenko.randomcoffee.DataLayer.models.ProductData

class MainActivityViewModel(darkTheme: Boolean) : ViewModel() {
    private val _alertState = MutableStateFlow(true)
    val alertState: StateFlow<Boolean> = _alertState.asStateFlow()
    fun setAlertState(state: Boolean) {
        _alertState.value = state
    }

    private val _isDarkTheme = MutableStateFlow(darkTheme)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()
    fun setIsDarkThemeState(state: Boolean) {
        _isDarkTheme.value = state
    }

    private val _snackBarHostState = MutableStateFlow(SnackbarHostState())
    val snackBarHostState: StateFlow<SnackbarHostState> = _snackBarHostState.asStateFlow()

    private val _currentPage = MutableStateFlow(DEFAULT_PAGE)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()
    fun setCurrentPage(page: Int) {
        _currentPage.value = page
    }

    private val _appState = MutableStateFlow(AppState.START)
    val appState: StateFlow<Int> = _appState.asStateFlow()
    fun setAppState(state: Int) {
        _appState.value = state
    }

    private val _productData = MutableStateFlow(ProductData())
    val productData: StateFlow<ProductData> = _productData.asStateFlow()
    fun setProductData(data: ProductData) {
        _productData.value = data
    }

    companion object {
        const val DEFAULT_PAGE = 0
    }
}

class AppState {
    companion object {
        const val START = 0
        const val PAUSE = 1
    }
}