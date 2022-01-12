package com.github.lukpier.techradar.parser

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.io.FileInputStream
import java.nio.charset.Charset

/**
 * Requirements parser. Very simple requirements.txt parser, which drop commented lines and extracts
 * dependency name from each parsable line.
 *
 * TODO: add versions and any other useful requirements txt information
 *
 * @constructor Create empty Requirements parser
 */
class RequirementsParser : DependencyParser<Models.Requirements> {

    private val httpClient = HttpClient()

    override fun parseValue(value: String): Models.Requirements = extractDependencies(value)

    override suspend fun parseRemote(url: String): Models.Requirements? =
        runCatching {
            httpClient.request<HttpResponse>(url) {
                method = HttpMethod.Get
            }
        }.map { response ->
            extractDependencies(response.receive())
        }.getOrNull()

    override fun parse(path: String): Models.Requirements? =
        runCatching { FileInputStream(path).readBytes().toString(Charset.forName("UTF8")) }
            .map(this::extractDependencies)
            .getOrNull()

    private fun extractDependencies(raw: String): Models.Requirements {
        val lines = raw.split('\n')
        return lines.mapNotNull { line -> extractDependency(line) }
            .run { Models.Requirements(this) }
    }

    private fun extractDependency(line: String): Models.Requirement? {
        if (line.startsWith("#")) return null
        val dependencyName = line.split("==")[0]
        return Models.Requirement(dependencyName)
    }

}