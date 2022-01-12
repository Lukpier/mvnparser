package com.github.lukpier.techradar.examples

import com.github.lukpier.techradar.parser.MvnParser
import com.github.lukpier.techradar.parser.RequirementsParser


fun main(args: Array<String>) {
    val parser = RequirementsParser()
    val requirements = parser.parse("src/main/resources/sample-requirements.txt")
    println(requirements)
    println(requirements?.requirements?.size)
}
