package com.hojeda.util

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

object JsonFileLoader {

    fun readFromFileJsonObject(path: String, params: Map<String, String> = mapOf()): JsonObject {
        var json = javaClass.classLoader.getResourceAsStream(path).reader().readText()
        params.forEach { entry -> json = json.replace("%${entry.key}%", entry.value) }
        return JsonObject(json)
    }

    fun readFromFileJsonArray(path: String): JsonArray {
        return JsonArray(
            javaClass.classLoader.getResourceAsStream(path).reader().readText()
        )
    }
}