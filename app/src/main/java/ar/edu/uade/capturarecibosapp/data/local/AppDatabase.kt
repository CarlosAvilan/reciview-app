package ar.edu.uade.capturarecibosapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import ar.edu.uade.capturarecibosapp.data.local.daos.*
import ar.edu.uade.capturarecibosapp.data.local.seeders.ExpenseSeeder
import ar.edu.uade.capturarecibosapp.data.local.seeders.HelpSeeder
import ar.edu.uade.capturarecibosapp.data.local.seeders.TicketSeeder
import ar.edu.uade.capturarecibosapp.data.local.seeders.UserSeeder
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
        ExpenseItem::class,
        User::class,
        UserCategory::class,
        UserPreferences::class
    ],
    version = 5,
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

                // Precarga de datos
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getDatabase(context)

                            // Precarga de Ayuda
                            val helpDao = database.helpDao()
                            helpDao.insertAdviceItems(HelpSeeder().provideInitialAdvice())
                            helpDao.insertFaqItems(HelpSeeder().provideInitialFaqs())

                            // Precarga de Usuario Mock
                            val userDao = database.userDao()
                            userDao.insertUser(UserSeeder().provideInitialUser())
                            userDao.insertPreferences(UserSeeder().provideInitialPreferences())

                            // Precarga de Ticket Seeders
                            val ticketDao = database.ticketDao()
                            // ticketDao.insertTickets(TicketSeeder().provideInitialTickets())
                            // ticketDao.insertTicketItems(TicketSeeder().provideInitialTicketItems())

                            // Precarga de Expenses para el usuario mock
                            val expenseDao = database.expenseDao()
                            // expenseDao.insertExpenses(ExpenseSeeder().provideInitialExpenses())
                        }
                    }
                })

                .fallbackToDestructiveMigration()
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
