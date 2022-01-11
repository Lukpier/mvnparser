package com.github.lukpier.techradar.mvnparser

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.nio.file.Path

/**
 * Mvn parser
 * Class Abstraction which can take a filesystem path pointing to pom file, the pom file itself,
 * a string representation of the pom and an HTTP url pointing to that file.
 * @constructor Create empty Mvn parser
 */
class MvnParser {

    private val xmlMapper = XmlMapper(JacksonXmlModule().apply {
        setDefaultUseWrapper(true)
    }).registerKotlinModule()
        .apply {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
        }

    /**
     * Parse takes the string representation of the pom file.
     *
     * @param value
     * @return MavenProject representation of that file
     */
    fun parseValue(value: String): Models.MavenProject {
        return xmlMapper.readValue(value)
    }

    /**
     * Parse takes a path, read the file from local filesystem.
     *
     * @param path
     * @return MavenProject representation of that file
     */
    fun parse(path: String): Models.MavenProject {
        val file = FileInputStream(path).readBytes().toString(Charset.forName("UTF8"))
        return xmlMapper.readValue(file)
    }

    /**
     * Parse takes a pom file and returns its MavenProject representation.
     *
     * @param file
     * @return MavenProject representation of that file
     */
    fun parse(file: File): Models.MavenProject {
        return xmlMapper.readValue(file)
    }

}