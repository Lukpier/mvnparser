package com.github.lukpier.techradar.parser

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.cio.*
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset

/**
 * Mvn parser
 * Class Abstraction which can take a filesystem path pointing to pom file, the pom file itself,
 * a string representation of the pom and an HTTP url pointing to that file.
 * @constructor Create empty Mvn parser
 */
class MvnParser : DependencyParser<Models.MavenProject> {

    private val xmlMapper = XmlMapper(JacksonXmlModule().apply {
        setDefaultUseWrapper(true)
    }).registerKotlinModule()
        .apply {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
        }

    private val httpClient = HttpClient()

    /**
     * Parse takes the string representation of the pom file.
     *
     * @param value
     * @return MavenProject representation of that file
     */
    override fun parseValue(value: String): Models.MavenProject? =
        runCatching { xmlMapper.readValue<Models.MavenProject>(value) }.getOrNull()

    /**
     * Parse takes a path, read the file from local filesystem.
     *
     * @param path
     * @return MavenProject representation of that file
     */
    override fun parse(path: String): Models.MavenProject? =
        runCatching { FileInputStream(path).readBytes().toString(Charset.forName("UTF8")) }
            .mapCatching { file -> xmlMapper.readValue<Models.MavenProject>(file) }
            .getOrNull()

    /**
     * Parse takes a URL pointing to a pom file and returns its MavenProject representation.
     *
     * @param file
     * @return MavenProject representation of that file
     */
    override suspend fun parseRemote(url: String): Models.MavenProject? =
        runCatching {
            httpClient.request<HttpResponse>(url) {
                method = HttpMethod.Get
            }
        }.mapCatching { response ->
            xmlMapper.readValue<Models.MavenProject>(response.receive<String>())
        }.getOrNull()

}