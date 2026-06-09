package com.example.extensions

import android.content.Context
import com.example.api.RepositoryApi
import com.example.model.ExtensionPlugin
import com.example.model.ExtensionRepository
import dalvik.system.DexClassLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream

object ExtensionManager {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://localhost/") // Base URL is ignored since we use @Url
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val repositoryApi = retrofit.create(RepositoryApi::class.java)
    private val httpClient = OkHttpClient()

    private val _repositories = MutableStateFlow<List<ExtensionRepository>>(emptyList())
    val repositories: StateFlow<List<ExtensionRepository>> = _repositories.asStateFlow()

    private val _loadedProviders = MutableStateFlow<List<MediaProvider>>(emptyList())
    val loadedProviders: StateFlow<List<MediaProvider>> = _loadedProviders.asStateFlow()

    suspend fun addRepository(url: String): Boolean {
        return try {
            val httpsUrl = url.replace("cloudstreamrepo://", "https://")
            val repo = repositoryApi.getRepository(httpsUrl)
            _repositories.value = _repositories.value + repo
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun installPlugin(context: Context, plugin: ExtensionPlugin) = withContext(Dispatchers.IO) {
        try {
            val pluginsDir = File(context.filesDir, "plugins")
            if (!pluginsDir.exists()) pluginsDir.mkdirs()

            val pluginFile = File(pluginsDir, "${plugin.id}.apk")
            
            val request = Request.Builder().url(plugin.url).build()
            val response = httpClient.newCall(request).execute()
            
            if (response.isSuccessful) {
                val body = response.body
                if (body != null) {
                    val inputStream = body.byteStream()
                    val outputStream = FileOutputStream(pluginFile)
                    inputStream.copyTo(outputStream)
                    outputStream.close()
                    inputStream.close()
                    loadPlugin(context, plugin, pluginFile)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadAllInstalled(context: Context) {
        // Load configurations if they were persisted. 
        // For simplicity, we just look at installed APKs from the repositories that are in memory.
        // In a real app we'd save repository JSON locally too.
        val pluginsDir = File(context.filesDir, "plugins")
        if (!pluginsDir.exists()) return
        
        _repositories.value.forEach { repo ->
            repo.plugins.forEach { plugin ->
                val file = File(pluginsDir, "${plugin.id}.apk")
                if (file.exists()) {
                    // Avoid double load
                    if (_loadedProviders.value.none { it.javaClass.name == plugin.className }) {
                        loadPlugin(context, plugin, file)
                    }
                }
            }
        }
    }

    private fun loadPlugin(context: Context, plugin: ExtensionPlugin, file: File) {
        try {
            val optimizedDir = context.getDir("dex", Context.MODE_PRIVATE)
            val classLoader = DexClassLoader(
                file.absolutePath,
                optimizedDir.absolutePath,
                null,
                context.classLoader
            )
            
            val pluginClass = classLoader.loadClass(plugin.className)
            val providerInstance = pluginClass.getDeclaredConstructor().newInstance() as? MediaProvider
            
            if (providerInstance != null) {
                _loadedProviders.value = _loadedProviders.value + providerInstance
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isInstalled(context: Context, pluginId: String): Boolean {
        val pluginFile = File(File(context.filesDir, "plugins"), "${pluginId}.apk")
        return pluginFile.exists()
    }
    
    fun uninstallPlugin(context: Context, pluginId: String) {
         val pluginFile = File(File(context.filesDir, "plugins"), "${pluginId}.apk")
         if (pluginFile.exists()) {
             pluginFile.delete()
         }
         // Clear from loaded providers (naive implementation)
         _loadedProviders.value = _loadedProviders.value.filter { 
            // In a real implementation we'd track pluginId on the MediaProvider
            !it.javaClass.name.contains(pluginId, ignoreCase = true) 
         }
    }
}
