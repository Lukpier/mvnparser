package com.github.lukpier.techradar.parser

import com.fasterxml.jackson.annotation.JsonProperty

object Models {

    data class MavenProject(
        var xmlName: String = "",
        var modelVersion: String = "",
        var parent: Parent?,
        var groupId: String = "",
        var artifactId: String = "",
        var version: String = "",
        var packaging: String = "",
        var name: String = "",
        var repositories: List<Repository> = emptyList(),
        var properties: Map<String, String>,
        var dependencyManagement: DependencyManagement?,
        var dependencies: List<Dependency> = emptyList(),
        var profiles: List<Profile> = emptyList(),
        var build: Build?,
        var pluginRepositories: List<PluginRepository> = emptyList(),
        var modules: List<String> = emptyList()
    ) {

        /**
         * Remove placeholder versions iterate on pom properties that contains a "version" suffix and try to
         * substitute each version to the corresponding dependency version placeholder.
         * It might be extended also to plugin versions, since usually both the version placeholders follow this convention.
         * Example:
         *   <javafx.version>17.0.1</javafx.version>
         *   <javafx.maven.plugin.version>0.0.8</javafx.maven.plugin.version>
         * @return cleaned MavenProject
         */
        fun removePlaceHolderVersions(): MavenProject =
            properties.filterKeys { it.endsWith("version") }
                .entries.fold(this) { acc, (key, value) ->
                    val withoutPlaceholder = acc.dependencies
                        .filterNot { it.version.contains(key) }
                    val fixed = (acc.dependencies - withoutPlaceholder.toSet()).map { it.copy(version = value) }
                    if (dependencies.isNotEmpty()) {
                        acc.copy(dependencies = withoutPlaceholder + fixed)
                    } else {
                        acc
                    }
                }

    }

    data class Parent(
        var groupId: String = "",
        var artifactId: String = "",
        var version: String = ""
    )

    data class Dependency(
        @JsonProperty("dependency")
        var xmlName: String = "",
        var groupId: String = "",
        var artifactId: String = "",
        var version: String = "",
        var classifier: String = "",
        var type: String = "",
        var scope: String = "",
        var exclusions: List<Exclusion> = emptyList()
    )

    data class Exclusion(
        @JsonProperty("exclusion")
        var xmlName: String = "",
        var groupId: String = "",
        var artifactId: String = "",
    )

    data class DependencyManagement(
        var dependencies: List<Dependency> = emptyList()
    )

    data class Repository(
        var id: String = "",
        var name: String = "",
        var url: String = ""
    )

    data class Profile(
        var id: String = "",
        var build: Build
    )

    data class Build(
        var plugins: List<Plugin> = emptyList()
    )

    data class Plugin(
        @JsonProperty("plugin")
        var xmlName: String = "",
        var groupId: String = "",
        var artifactId: String = "",
        var version: String = ""
    )

    data class PluginRepository(
        var id: String = "",
        var name: String = "",
        var url: String = "",
    )


    data class Requirements(
        val requirements: List<Requirement>
    )

    data class Requirement(
        val name: String // TODO: add version
    )

}


