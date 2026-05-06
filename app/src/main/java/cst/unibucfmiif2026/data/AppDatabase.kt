package cst.unibucfmiif2026.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cst.unibucfmiif2026.BuildConfig
import cst.unibucfmiif2026.data.dao.AddressDAO
import cst.unibucfmiif2026.data.dao.UserDAO
import cst.unibucfmiif2026.data.entities.AddressEntity
import cst.unibucfmiif2026.data.entities.UserEntity

@Database(
    entities = [UserEntity::class, AddressEntity::class],
    version = 5
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao () : UserDAO
    abstract fun addressDao () : AddressDAO

    companion object {
        private val migration_1_2 = object : Migration(startVersion = 1, endVersion = 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users RENAME COLUMN 'firstName' TO 'first_name'")
                db.execSQL("ALTER TABLE users RENAME COLUMN 'lastName' TO 'last_name'")
            }
        }
        private var instance : AppDatabase? = null
        fun getInstance(context: Context) : AppDatabase {
            return instance ?: synchronized(this) {
                val newInstanceConfig = Room.databaseBuilder(
                    context = context,
                    name = "AppDatabase",
                    klass = AppDatabase::class.java
                )
                    .addMigrations(migration_1_2)

                if (BuildConfig.DEBUG) {
                    newInstanceConfig.fallbackToDestructiveMigration(false)
                }

                val newInstance = newInstanceConfig.build()
                instance = newInstance
                newInstance
            }
        }
    }
}