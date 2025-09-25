package org.fmz.easyrecipes.support

import android.content.Context

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class DataStoreRepository private constructor(private val dataStore: DataStore<Preferences>) {

    val token: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_TOKEN] ?: ""
    }.distinctUntilChanged()

    private suspend fun saveString(value: String, key: Preferences.Key<String>) {
        dataStore.edit { prefs ->
            prefs[key] = value
        }
    }
    suspend fun saveString(value: String, index: Int) {
        val key: Preferences.Key<String> = when (index) {
            1 -> KEY_TOKEN
            else -> { throw NoSuchFieldException("Invalid input index: $index")}
        }
        this.saveString(value, key)
    }

    companion object {
        private const val DATA_STORE_FILE_NAME = "preferences"
        private val KEY_TOKEN = stringPreferencesKey("token")
        private var INSTANCE: DataStoreRepository? = null
        fun get(): DataStoreRepository {
            return INSTANCE ?: throw IllegalStateException("The DataStoreRepository has been accessed before it was initialized.")
        }
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                val dataStore = PreferenceDataStoreFactory.create {
                    context.preferencesDataStoreFile(DATA_STORE_FILE_NAME)
                }
                INSTANCE = DataStoreRepository(dataStore)
            }
        }
    }

}