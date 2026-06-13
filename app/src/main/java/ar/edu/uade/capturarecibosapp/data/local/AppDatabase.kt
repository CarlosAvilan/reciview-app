package ar.edu.uade.capturarecibosapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ar.edu.uade.capturarecibosapp.data.local.daos.HelpDao
import ar.edu.uade.capturarecibosapp.data.local.daos.ReportDao
import ar.edu.uade.capturarecibosapp.data.local.daos.TicketDao
import ar.edu.uade.capturarecibosapp.data.local.daos.UserDao
import ar.edu.uade.capturarecibosapp.data.local.seeders.HelpSeeder
import ar.edu.uade.capturarecibosapp.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AdviceItem::class,
        FaqItem::class,
        MonthlyReport::class,
        Ticket::class,
        TicketItem::class,
        User::class,
        UserCategory::class,
        UserPreferences::class
    ],
    version = 1,
    exportSchema = false // Evita generar reportes de esquema en json durante la compilación básica
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun helpDao(): HelpDao
    abstract fun userDao(): UserDao
    abstract fun ticketDao(): TicketDao
    abstract fun reportDao(): ReportDao

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

                // Precarga de datos
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getDatabase(context).helpDao()
                            // Consejos y Faqs
                            dao.insertAdviceItems(HelpSeeder().provideInitialAdvice())
                            dao.insertFaqItems(HelpSeeder().provideInitialFaqs())
                        }
                    }
                })

                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}