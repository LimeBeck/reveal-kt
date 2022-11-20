package dev.limebeck.revealkt.core

import dev.limebeck.revealkt.utils.ID
import kotlinx.html.HtmlBlockTag
import kotlinx.html.classes
import kotlinx.html.style
import kotlin.reflect.KProperty

interface RevealKtElement {
    val id: ID
    fun render(tag: HtmlBlockTag)
}

abstract class AttributedElement(
    override val id: ID,
) : RevealKtElement {
    val elementAttributes: MutableMap<String, String> = mutableMapOf()
    val elementClasses: MutableSet<String> = mutableSetOf()
    var extraStyles: MutableMap<String, String> = mutableMapOf()

    fun conditionalClass(classname: String, block: () -> Boolean) {
        if (block()) {
            elementClasses.add(classname)
        }
    }

    fun conditionalAttribute(attributeName: String, value: String = "", block: () -> Boolean) {
        if (block()) {
            elementAttributes[attributeName] = value
        }
    }

    fun conditionalAttribute(attributeName: String, block: () -> Boolean) {
        if (block()) {
            elementAttributes[attributeName] = ""
        }
    }

    fun addClass(classname: String) {
        elementClasses.add(classname)
    }

    fun addAttribute(attributeName: String, value: String = "") {
        elementAttributes[attributeName] = value
    }

    fun addStyle(property: String, value: String) {
        extraStyles[property] = value
    }

    fun addStyles(vararg propToValue: Pair<String, String>) {
        propToValue.forEach { (property, value) ->
            extraStyles[property] = value
        }
    }

    fun removeStyle(property: String) {
        extraStyles.remove(property)
    }

    abstract fun HtmlBlockTag.tagBodyProvider(renderAttributes: HtmlBlockTag.() -> Unit): Unit

    override fun render(tag: HtmlBlockTag) = with(tag) {
        tagBodyProvider {
            addAttribute("data-id", id.id)
            classes = classes + elementClasses
            attributes.putAll(elementAttributes)
            style = extraStyles.map { "${it.key}:${it.value}" }.joinToString("; ")
        }
    }
}

class ConditionalStringAttributeDelegate(
    val attributeName: String,
) {
    operator fun getValue(thisRef: AttributedElement, property: KProperty<*>): String? {
        return thisRef.elementAttributes[attributeName]
    }

    operator fun setValue(thisRef: AttributedElement, property: KProperty<*>, value: String?) {
        if (value != null)
            thisRef.elementAttributes[attributeName] = value
        else
            thisRef.elementAttributes.remove(attributeName)
    }
}

class BooleanAttributeDelegate(
    val attributeName: String? = null,
) {
    operator fun getValue(thisRef: AttributedElement, property: KProperty<*>): Boolean {
        return thisRef.elementAttributes.containsValue(attributeName ?: property.name)
    }

    operator fun setValue(thisRef: AttributedElement, property: KProperty<*>, value: Boolean) {
        if (value)
            thisRef.elementAttributes[attributeName ?: property.name] = ""
        else
            thisRef.elementAttributes.remove(attributeName ?: property.name)
    }
}

//
//interface FragmentedElement {
//    val fragmented: Boolean
//}
//
//class FragmentedElementMixin(
//    override val elementAttributes: MutableMap<String, String>,
//    override val elementClasses: MutableSet<String>,
//) : RevealKtHtmlBaseElement(), FragmentedElement {
//    override var fragmented: Boolean = false
//
//    init {
//        conditionalClass("fragment", ::fragmented)
//    }
//}
//
//abstract class RevealKtHtmlBaseElement : RevealKtElement, AttributedElement {
//    override val elementAttributes: MutableMap<String, String> = mutableMapOf()
//    override val elementClasses: MutableSet<String> = mutableSetOf()
//
//    protected abstract fun HtmlBlockTag.htmlElementProvider(block: HtmlBlockTag.() -> Unit)
//
//    protected fun HtmlBlockTag.renderAttributes() {
//        addAttribute("data-id", id.id)
//        classes = classes + elementClasses
//        attributes.putAll(elementAttributes)
//    }
//
//    override fun render(tag: HtmlBlockTag): Unit = with(tag) {
//        htmlElementProvider {
//            renderAttributes()
//        }
//    }
//}
//
//class Title(
//    override val id: ID,
//    val title: String,
//) : RevealKtHtmlBaseElement(), FragmentedElement by FragmentedElementMixin(elementAttributes) {
//    override fun HtmlBlockTag.htmlElementProvider(block: HtmlBlockTag.() -> Unit) {
//        h2 {
//            block()
//            +title
//        }
//    }
//}