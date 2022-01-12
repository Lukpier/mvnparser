package com.github.lukpier.techradar.examples

import com.github.lukpier.techradar.parser.MvnParser

fun main(args: Array<String>) {

    val path = args[0]
    val parser = MvnParser()
    val project = parser.parse(path)
    println(project)
    println(project?.dependencies)
    println(project?.removePlaceHolderVersions()?.dependencies)

}