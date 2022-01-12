package com.github.lukpier.techradar.parser

import io.ktor.client.*
import java.io.File

interface DependencyParser<T> {

    fun parseValue(value: String): T?
    suspend fun parseRemote(url: String): T?
    fun parse(path: String): T?

}