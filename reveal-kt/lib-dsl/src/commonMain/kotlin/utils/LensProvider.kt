package dev.limebeck.revealkt.utils

import arrow.optics.Lens
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class LensProvider<T>(private var data: T) {
    inner class LensPropertyProvider<R>(val prop: Lens<T, R>) : ReadWriteProperty<Any?, R> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): R {
            return prop.get(data)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: R) {
            data = prop.set(data, value)
        }
    }

    operator fun <R> invoke(prop: Lens<T, R>) = LensPropertyProvider(prop)

    fun get(): T = data
}

fun <T> T.asLensProvider(): LensProvider<T> = LensProvider(this)