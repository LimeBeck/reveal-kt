package dev.limebeck.revealkt.utils

import kotlinx.html.HtmlBlockTag
import kotlinx.html.classes

fun HtmlBlockTag.conditionalClass(classname: String, block: () -> Boolean) {
    if (block()) {
        addClass(classname)
    }
}

fun HtmlBlockTag.conditionalAttribute(attributeName: String, value: String = "", block: () -> Boolean) {
    if (block()) {
        addAttribute(attributeName, value)
    }
}

fun HtmlBlockTag.conditionalAttribute(attributeName: String, block: () -> Boolean) {
    if (block()) {
        addAttribute(attributeName)
    }
}

fun HtmlBlockTag.addClass(classname: String) {
    classes = classes + classname
}

fun HtmlBlockTag.addAttribute(attributeName: String, value: String = "") {
    attributes[attributeName] = value
}

fun HtmlBlockTag.removeAttribute(attributeName: String) {
    attributes.remove(attributeName)
}