package com.duoc.securitysjapp.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Usuario::class, Caso::class], version = 7, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun casoDao(): CasoDao

    companion object {
        // Agrega los identificadores remotos sin borrar las cuentas ni los casos guardados.
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE usuarios ADD COLUMN xanoId INTEGER")
                database.execSQL("ALTER TABLE casos ADD COLUMN xanoId INTEGER")
                database.execSQL("ALTER TABLE casos ADD COLUMN xanoUsuarioId INTEGER")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    // Etapa de desarrollo: si cambia el esquema, se recrea la base
                    .addMigrations(MIGRATION_6_7)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
