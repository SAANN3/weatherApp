package com.weather.weather.Backend

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.weather.weather.MainActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess
// work with datastore
// nothing more
private val Context.dataStore by preferencesDataStore("user_preferences")
class DataWorker {
    private val dataStore: DataStore<Preferences> = MainActivity.applicationContext().dataStore
    private inline fun<reified T : Any> _getPrefKey(key: String):Preferences.Key<T>{
        val prefKey:Preferences.Key<T> = when(T::class)
        {
            Int::class -> intPreferencesKey(key)
            String::class -> stringPreferencesKey(key)
            Float::class -> floatPreferencesKey(key)
            Boolean::class -> booleanPreferencesKey(key)
            Long::class -> longPreferencesKey(key)
            else ->{
                exitProcess(-1)
            }
        } as Preferences.Key<T>
        return prefKey
    }
    private inline fun<reified T : Any> _setData(key: String, value:T){
        runBlocking {
            dataStore.edit { settings -> settings[_getPrefKey<T>(key)] = value}
        }
    }
    private inline fun<reified T : Any> _getData(key: String): T? {
        return runBlocking{
            ((dataStore.data.map { preferences -> preferences[_getPrefKey<T>(key)] }).first())
        }
    }
    private inline fun<reified T : Any> _removeData(key: String){
        runBlocking {
            dataStore.edit { data -> data.remove(_getPrefKey<T>(key))}
        }
    }
    fun setData(key:String,value:Int){
        _setData<Int>(key,value)
    }
    fun setData(key:String,value: String){
        _setData<String>(key,value)
    }
    fun setData(key:String,value:Float){
        _setData<Float>(key,value)
    }
    fun setData(key:String,value:Boolean){
        _setData<Boolean>(key,value)
    }
    fun setData(key:String,value: Long){
        _setData<Long>(key,value)
    }
    fun getDataInt(key:String):Int?{
        return _getData<Int>(key)
    }
    fun getDataString(key:String):String?{
        return _getData<String>(key)
    }
    fun getDataFloat(key:String):Float?{
        return _getData<Float>(key)
    }
    fun getDataBoolean(key:String):Boolean?{
        return _getData<Boolean>(key)
    }
    fun getDataLong(key:String):Long?{
        return _getData<Long>(key)
    }
    fun removeDataLong(key:String){
        _removeData<Long>(key)
    }
    fun removeDataFloat(key:String){
        _removeData<Float>(key)
    }
    fun removeDataString(key:String){
        _removeData<String>(key)
    }
    fun removeDataInt(key:String){
        _removeData<Int>(key)
    }
    fun removeDataBoolean(key:String){
        _removeData<Boolean>(key)
    }
    fun removeData(){
        runBlocking {
            dataStore.edit {
                it.clear()
            }
        }
    }
}
