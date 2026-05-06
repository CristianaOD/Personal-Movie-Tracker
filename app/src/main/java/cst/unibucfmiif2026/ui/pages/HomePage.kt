package cst.unibucfmiif2026.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cst.unibucfmiif2026.R
import cst.unibucfmiif2026.ui.theme.UniBucFMIIF2026Theme
import cst.unibucfmiif2026.viewmodel.HomeViewModel

@Composable
fun HomePage(viewModel: HomeViewModel = viewModel(), onLogout: () -> Unit = {}, gotoAddresses: () -> Unit = {}) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.welcome),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Button(onClick = gotoAddresses) { Text(stringResource(R.string.goto_addresses_btn)) }

        Button(onClick = onLogout) { Text(stringResource(R.string.logout_btn)) }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    UniBucFMIIF2026Theme {
        HomePage()
    }
}