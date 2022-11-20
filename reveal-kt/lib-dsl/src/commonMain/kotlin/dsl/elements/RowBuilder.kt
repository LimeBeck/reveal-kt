package dev.limebeck.revealkt.dsl

import dev.limebeck.revealkt.core.elements.Column
import dev.limebeck.revealkt.core.elements.Row

class RowBuilder {
    internal val columns = mutableListOf<Column>()
    fun column(
        colBuilder: ColumnBuilder.() -> Unit,
    ) = ColumnBuilder()
        .apply(colBuilder)
        .build().also {
            columns.add(it)
        }

    fun build(): Row = Row(columns = columns)
}

