package cst.unibucfmiif2026.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cst.unibucfmiif2026.R
import cst.unibucfmiif2026.data.entities.AddressEntity
import cst.unibucfmiif2026.ui.theme.UniBucFMIIF2026Theme
import cst.unibucfmiif2026.utils.isValidName
import cst.unibucfmiif2026.viewmodel.AddressPageViewModel

@Composable
fun AddressesPage(viewModel: AddressPageViewModel = viewModel(), gotoAddressDetails: (id:Long) -> Unit = {}) {

    val addresses = viewModel.addresses.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AddressesPageHeader(viewModel::addAddress)
        }

        if (addresses.value.isNotEmpty()) {
            items(addresses.value) { address ->
                AddressListItem(address, gotoAddressDetails)
            }
        } else {
            item {
                Text(text = stringResource(R.string.no_addresses_found),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun AddressesPageHeader(onAddAddress: (street: String, city: String) -> Unit) {

    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var streetError by remember { mutableStateOf<String?>(null) }
    var cityError by remember { mutableStateOf<String?>(null) }
    val invalidStreetMessage = stringResource(R.string.invalid_street)
    val invalidCityMessage = stringResource(R.string.invalid_city)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.addresses_page_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = street,
            onValueChange = { newValue ->
                street = newValue
                streetError = null

            },
            label = {
                Text(stringResource(R.string.street))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.PinDrop,
                    contentDescription = null
                )
            },
            singleLine = true,
            isError = streetError != null,
            supportingText = streetError?.let { errorMessage ->
                {
                    Text(errorMessage)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { newValue ->
                city = newValue
                cityError = null
            },
            label = {
                Text(stringResource(R.string.city))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationCity,
                    contentDescription = null
                )
            },
            isError = cityError != null,
            supportingText = cityError?.let { errorMessage ->
                {
                    Text(errorMessage)
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                var isValid = true
                if (!street.isValidName()) {
                    streetError = invalidStreetMessage
                    isValid = false
                }

                if (!city.isValidName()) {
                    cityError = invalidCityMessage
                    isValid = false
                }

                if (isValid) onAddAddress(street, city)
            },
            modifier = Modifier.fillMaxWidth(),
            // enabled = !isLoading
        ) {
//            when (isLoading) {
//                true -> CircularProgressIndicator(
//                    modifier = Modifier.size(24.dp),
//                    strokeWidth = 2.dp
//                )
//
//                false ->
//            }
            Text(stringResource(R.string.add_address))
        }

//        errorMessage?.let { errMsg ->
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                color = MaterialTheme.colorScheme.error,
//                style = MaterialTheme.typography.bodySmall,
//                fontWeight = FontWeight.Bold,
//                text = errMsg
//            )
//        }
    }
}

@Composable
fun AddressListItem(address: AddressEntity, onClick: (id: Long) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick(address.id) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = address.street,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = address.city,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )

            Text(
                text = address.country,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddressesPagePreview() {
    UniBucFMIIF2026Theme {
        AddressesPage()
    }
}