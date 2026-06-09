package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.extensions.ExtensionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val loadedProviders by ExtensionManager.loadedProviders.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Home") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            item {
                Text(
                    "Providers Loaded: ${loadedProviders.size}",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(loadedProviders) { provider ->
                Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = provider.name, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}
