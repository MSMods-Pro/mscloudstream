package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.extensions.ExtensionManager
import com.example.model.ExtensionPlugin
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtensionsScreen() {
    val repositories by ExtensionManager.repositories.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var inputUrl by remember { mutableStateOf("") }
    
    // Trigger a recomposition when installing/uninstalling
    var triggerUpdate by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Extensions", fontWeight = FontWeight.Medium) },
                actions = {
                    IconButton(onClick = { /* Add Link Modal potentially */ }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Link")
                    }
                    IconButton(onClick = { /* Sync */ }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Sync")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp)) {
            // Keep Add UI minimal to preserve layout space
            Row(modifier = Modifier.padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = { inputUrl = it },
                    label = { Text("Add Repository URL") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { 
                        coroutineScope.launch {
                            ExtensionManager.addRepository(inputUrl)
                            inputUrl = ""
                        }
                    },
                    shape = CircleShape
                ) {
                    Text("Add")
                }
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                repositories.forEach { repo ->
                    item {
                        // Active Repository Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(24.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(24.dp))
                                .padding(20.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        "ACTIVE REPOSITORY", 
                                        color = MaterialTheme.colorScheme.primary, 
                                        fontSize = 14.sp, 
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 1.sp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.outline, shape = CircleShape)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "PROD-SERVER", 
                                            fontSize = 10.sp, 
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                                Text(
                                    text = repo.name, 
                                    fontSize = 18.sp, 
                                    fontWeight = FontWeight.Medium, 
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = repo.description ?: "github.com/mscloudstream", 
                                    fontSize = 14.sp, 
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {}, 
                                        modifier = Modifier.weight(1f).height(40.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    ) {
                                        Text("Check Updates", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Filled.Settings, 
                                            contentDescription = "Settings", 
                                            modifier = Modifier.size(20.dp), 
                                            tint = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Available Plugins", 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.Medium, 
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
                        )
                    }
                    
                    items(repo.plugins) { plugin ->
                        PluginItem(
                            plugin = plugin, 
                            isInstalled = ExtensionManager.isInstalled(context, plugin.id),
                            onInstall = {
                                coroutineScope.launch {
                                    ExtensionManager.installPlugin(context, plugin)
                                    triggerUpdate++
                                }
                            },
                            onUninstall = {
                                ExtensionManager.uninstallPlugin(context, plugin.id)
                                triggerUpdate++
                            },
                            dummy = triggerUpdate
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PluginItem(plugin: ExtensionPlugin, isInstalled: Boolean, onInstall: () -> Unit, onUninstall: () -> Unit, dummy: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isInstalled) Icons.Filled.PlayArrow else Icons.Filled.Star, 
                        contentDescription = null,
                        tint = if (isInstalled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    )
                }
                Column {
                    Text(
                        text = plugin.name, 
                        fontWeight = FontWeight.Medium, 
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "v${plugin.version} • ${if (isInstalled) "Installed" else "Available"}", 
                        fontSize = 12.sp, 
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
            if (isInstalled) {
                IconButton(
                    onClick = onUninstall,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Uninstall",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onInstall,
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Install", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
    }
}
