package ar.edu.uade.capturarecibosapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "faq_items")
data class FaqItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val question: String,
    val answer: String,
    //TODO SACAR ESTO, QUE VIVA EN UI
    @ColumnInfo(name = "is_expanded")
    var isExpanded: Boolean = false
)
