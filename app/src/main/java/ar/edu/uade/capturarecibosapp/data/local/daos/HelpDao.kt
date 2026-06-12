package ar.edu.uade.capturarecibosapp.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ar.edu.uade.capturarecibosapp.data.model.AdviceItem
import ar.edu.uade.capturarecibosapp.data.model.FaqItem
import kotlinx.coroutines.flow.Flow

@Dao
interface HelpDao {
    // Consejos
    @Query("SELECT * FROM advice_items")
    fun getAllAdviceItems(): Flow<List<AdviceItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdviceItems(items: List<AdviceItem>)

    // FAQs
    @Query("SELECT * FROM faq_items")
    fun getAllFaqItems(): Flow<List<FaqItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaqItems(items: List<FaqItem>)
}