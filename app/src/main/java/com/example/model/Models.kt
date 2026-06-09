package com.example.model

import com.google.gson.annotations.SerializedName

data class ExtensionRepository(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("author") val author: String? = null,
    @SerializedName("plugins") val plugins: List<ExtensionPlugin> = emptyList()
)

data class ExtensionPlugin(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("version") val version: String,
    @SerializedName("url") val url: String, // Download URL for the APK/JAR
    @SerializedName("className") val className: String // The entrypoint class implementing MediaProvider
)
