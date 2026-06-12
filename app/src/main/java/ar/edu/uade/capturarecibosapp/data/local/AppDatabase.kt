package ar.edu.uade.capturarecibosapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ar.edu.uade.capturarecibosapp.data.local.daos.HelpDao
import ar.edu.uade.capturarecibosapp.data.local.daos.ReportDao
import ar.edu.uade.capturarecibosapp.data.local.daos.TicketDao
import ar.edu.uade.capturarecibosapp.data.local.daos.UserDao
import ar.edu.uade.capturarecibosapp.data.model.*

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

    // Declaración de los métodos abstractos para obtener los DAOs
    abstract fun helpDao(): HelpDao
    abstract fun userDao(): UserDao
    abstract fun ticketDao(): TicketDao
    abstract fun reportDao(): ReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Si la instancia ya existe, la retorna directamente.
            // Si no, abre un bloque sincronizado para que dos hilos no la creen a la vez.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "captura_recibos_database"
                )
                    // .fallbackToDestructiveMigration() // Descomentar solo en desarrollo si cambian las tablas frecuentemente y no quieren hacer migraciones manuales todavía
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}