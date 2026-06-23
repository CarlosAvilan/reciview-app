package ar.edu.uade.capturarecibosapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ar.edu.uade.capturarecibosapp.data.local.daos.*
import ar.edu.uade.capturarecibosapp.data.model.*

@Database(
    entities = [
        AdviceItem::class,
        FaqItem::class,
        MonthlyReport::class,
        Ticket::class,
        TicketItem::class,
        ExpenseItem::class,
        User::class,
        UserCategory::class,
        UserPreferences::class,
    ],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun helpDao(): HelpDao
    abstract fun userDao(): UserDao
    abstract fun ticketDao(): TicketDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun reportDao(): ReportDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Si la instancia ya existe, la retorna directamente
                return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "captura_recibos_database"
                )
                .fallbackToDestructiveMigration(true)
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
