package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.extensions.ExtensionManager
import com.example.extensions.MainPageItem
import com.example.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val loadedProviders by ExtensionManager.loadedProviders.collectAsState()
    var selectedProvider by remember { mutableStateOf<com.example.extensions.MediaProvider?>(null) }
    var mainPages by remember { mutableStateOf<List<MainPageItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(loadedProviders) {
        if (selectedProvider == null && loadedProviders.isNotEmpty()) {
            selectedProvider = loadedProviders.first()
        }
    }

    LaunchedEffect(selectedProvider) {
        if (selectedProvider != null) {
            isLoading = true
            try {
                mainPages = selectedProvider!!.getMainPage()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Home", fontWeight = FontWeight.Medium) }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (loadedProviders.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = loadedProviders.indexOf(selectedProvider).coerceAtLeast(0),
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.background,
                    divider = {}
                ) {
                    loadedProviders.forEach { provider ->
                        Tab(
                            selected = selectedProvider == provider,
                            onClick = { selectedProvider = provider },
                            text = { Text(provider.name, fontWeight = if (selectedProvider == provider) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(mainPages) { pageItem ->
                        Text(
                            text = pageItem.title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(pageItem.items) { result ->
                                Card(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(180.dp)
                                        .clickable {
                                            selectedProvider?.name?.let { providerName ->
                                                navController.navigate(Screen.Detail.createRoute(result.name, result.posterUrl ?: "", result.url, providerName))
                                            }
                                        }
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        if (result.posterUrl != null) {
                                            AsyncImage(
                                                model = result.posterUrl,
                                                contentDescription = result.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                        Box(
                                            modifier = Modifier.fillMaxSize().padding(8.dp),
                                            contentAlignment = androidx.compose.ui.Alignment.BottomStart
                                        ) {
                                            Text(
                                                text = result.name,
                                                color = androidx.compose.ui.graphics.Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 2
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
