package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.extensions.ExtensionManager
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var pendingAddRepoUrl by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        ExtensionManager.loadAllInstalled(this)
        
        handleIntent(intent)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()

                    pendingAddRepoUrl?.let { url ->
                        AlertDialog(
                            onDismissRequest = { pendingAddRepoUrl = null },
                            title = { Text("Add Repository?") },
                            text = { Text("Do you want to add the extension repository at $url?") },
                            confirmButton = {
                                Button(onClick = {
                                    lifecycleScope.launch {
                                        ExtensionManager.addRepository(url)
                                        pendingAddRepoUrl = null
                                    }
                                }) {
                                    Text("Yes")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { pendingAddRepoUrl = null }) {
                                    Text("No")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (Intent.ACTION_VIEW == intent?.action) {
            intent.data?.let { uri ->
                if (uri.scheme == "cloudstreamrepo") {
                    pendingAddRepoUrl = uri.toString()
                }
            }
        }
    }
}
