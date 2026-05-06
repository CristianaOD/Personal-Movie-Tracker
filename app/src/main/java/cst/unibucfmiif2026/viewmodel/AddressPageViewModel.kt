package cst.unibucfmiif2026.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cst.unibucfmiif2026.data.AppDatabase
import cst.unibucfmiif2026.data.entities.AddressEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddressPageViewModel(application: Application) : AndroidViewModel(application) {
    private val addressDao = AppDatabase.getInstance(application).addressDao()
    val addresses = addressDao.getAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    fun addAddress(street: String, city: String) {
       viewModelScope.launch {
           addressDao.insert(AddressEntity(street = street, city = city, country = "RO"))
       }
    }
}