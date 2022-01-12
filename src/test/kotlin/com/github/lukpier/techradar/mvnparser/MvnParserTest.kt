package com.github.lukpier.techradar.mvnparser

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertNull

internal class MvnParserTest {

    @Test
    fun parseValue() {

        val raw = """
            <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
                <modelVersion>4.0.0</modelVersion>
                <groupId>org.openjfx</groupId>
                <artifactId>hellofx</artifactId>
                <packaging>jar</packaging>
                <version>1.0-SNAPSHOT</version>
                <name>demo</name>
                <url>http://maven.apache.org</url>

                <properties>
                    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                    <javafx.version>17.0.1</javafx.version>
                    <javafx.maven.plugin.version>0.0.8</javafx.maven.plugin.version>
                </properties>

                <dependencies>
                    <dependency>
                        <groupId>org.openjfx</groupId>
                        <artifactId>javafx-controls</artifactId>
                        <version>11.7</version>
                    </dependency>
                </dependencies>

                <build>
                    <plugins>
                        <plugin>
                            <groupId>org.openjfx</groupId>
                            <artifactId>javafx-maven-plugin</artifactId>
                            <version>11.7</version>
                            <configuration>
                                <mainClass>HelloFX</mainClass>
                            </configuration>
                        </plugin>
                    </plugins>
                </build>

            </project>
        """.trimIndent()
        val parser = MvnParser()
        val pom = parser.parseValue(raw)
        assertEquals(pom?.name, "demo")
        assertEquals(pom?.dependencies?.size, 1)

    }

    @Test
    fun parse() {
        val parser = MvnParser()
        val pom = parser.parse("src/test/resources/sample-pom.xml")
        assertEquals(pom?.name, "demo")
        assertEquals(pom?.dependencies?.size, 1)
    }

    @Test
    fun parseFile() {
        val parser = MvnParser()
        val pom = parser.parse(File("src/test/resources/sample-pom.xml"))
        assertEquals(pom?.name, "demo")
        assertEquals(pom?.dependencies?.size, 1)
    }

    @Test
    fun parseRemote() {
        val parser = MvnParser()
        runBlocking {
                parser.parseRemote("https://raw.githubusercontent.com/openjfx/samples/master/HelloFX/Maven/hellofx/pom.xml")
                    .apply {
                        assertEquals(this?.name, "demo")
                        assertEquals(this?.dependencies?.size, 1)
                    }
            }
    }

    @Test
    fun parseRemote_whenNotFound_returnNull() {
        val parser = MvnParser()
        runBlocking {
            parser.parseRemote("https://raw.githubusercontent.com/openjfx/samples/fake")
                .apply {
                    assertNull(this)
                }
        }
    }

}