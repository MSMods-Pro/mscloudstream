package com.example.model

import com.google.gson.annotations.SerializedName

data class ExtensionRepository(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("author") val author: String? = null,
    @SerializedName("plugins") var plugins: List<ExtensionPlugin> = emptyList(),
    @SerializedName("pluginLists") val pluginLists: List<String> = emptyList()
)

data class ExtensionPlugin(
    @SerializedName("internalName") val internalName: String? = null,
    @SerializedName("name") val name: String,
    @SerializedName("version") val version: String? = null,
    @SerializedName("url") val url: String,
    @SerializedName("jarUrl") val jarUrl: String? = null,
    @SerializedName("className") val className: String? = null,
    @SerializedName("description") val description: String? = null
) {
    val id: String
        get() = internalName ?: name
}
