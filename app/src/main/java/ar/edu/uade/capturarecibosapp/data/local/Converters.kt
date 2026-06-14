package ar.edu.uade.capturarecibosapp.data.local

import androidx.room.TypeConverter
import ar.edu.uade.capturarecibosapp.data.enums.SyncStatus

class Converters {
    @TypeConverter
    fun fromSyncStatus(status: SyncStatus): String {
        return status.name
    }

    @TypeConverter
    fun toSyncStatus(status: String): SyncStatus {
        return SyncStatus.valueOf(status)
    }
}
