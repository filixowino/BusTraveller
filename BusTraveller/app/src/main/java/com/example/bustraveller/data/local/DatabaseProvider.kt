package com.example.bustraveller.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var database: TrackingDatabase? = null
    
    fun getDatabase(context: Context): TrackingDatabase {
        return database ?: Room.databaseBuilder(
            context.applicationContext,
            TrackingDatabase::class.java,
            "tracking_database"
        ).build().also {
            database = it
        }
    }
}

