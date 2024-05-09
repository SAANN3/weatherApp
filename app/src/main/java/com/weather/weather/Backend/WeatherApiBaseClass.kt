package com.weather.weather.Backend

import android.util.Log
import com.weather.weather.DaysOfTheWeek
import com.weather.weather.TemperatureSymbols
import com.weather.weather.WeatherCondition
import com.weather.weather.WeatherProviders
import kotlin.math.round

data class SettingsData(
    val latitude: Float,
    val longitude: Float,
    val city: String,
    val temperatureSymbol: TemperatureSymbols,
    val weatherProvider: WeatherProviders,
)
data class ResponseRaw(
    val rawResponse:String,
    val dateResponse:Long,
)
open class WeatherApiBaseClass(
    weatherApiKey:String? = null,
    settingsData: SettingsData,
    ) {
    data class HourForecast(
        val weatherCondition: WeatherCondition,
        val temperature: Float,
        val hour:Int,
        val dayOfMonth:Int,
        val dayOfWeek: DaysOfTheWeek
    )
    data class DailyForecast(
        var minTemperature: Float,
        var maxTemperature: Float,
        var condition: WeatherCondition,
        val dayOfMonth:Int,
        val dayOfWeek:DaysOfTheWeek,
    )
    data class LatNLong(
        val latitude: Float,
        val longitude: Float,
        val city: String
    )
    protected val weatherApiKey:String?
    protected val latitude:Float
    protected val longitude:Float
    protected val temperatureSymbol:TemperatureSymbols
    protected val city:String
    protected val weatherProvider: WeatherProviders
    protected val hourlyForecast: MutableList<HourForecast> = mutableListOf()
    protected val dailyForecast: MutableList<DailyForecast> = mutableListOf()
    protected val listeners: MutableList<() -> Unit> = mutableListOf()
    init{
        this.weatherApiKey = weatherApiKey
        this.latitude = settingsData.latitude
        this.longitude = settingsData.longitude
        this.temperatureSymbol = settingsData.temperatureSymbol
        this.city = settingsData.city
        this.weatherProvider = settingsData.weatherProvider
    }
    companion object{
        suspend fun getLatLong(city: String):LatNLong?{
            return null
        }
        suspend fun getLatLong(city: String,length: Int):Array<LatNLong>?{
            return null
        }
    }
    protected fun celsiusToKelvin(celsius:Float):Float{
        return round(((celsius * 9/5) + 32)*10)/10
    }
    protected fun kelvinToCelsius(kelvin:Float):Float{
        return kelvin - 273.1f
    }
    protected fun notifyListeners(){
        listeners.forEach {
            it()
        }
    }
    fun subscribe(listener:() -> Unit){
        listeners.add(listener)
    }
    // using g because kotlin generates its own getters and they clashing
    open fun gRawResponse():ResponseRaw?{
        return null
    }
    fun gMessage():String{
        return "Weather & Geocoding provided by \n ${weatherProvider.site}"
    }
    fun gTemperatureSymbol():TemperatureSymbols{
        return temperatureSymbol
    }
    fun gCity():String{
        return city
    }
    fun gWeatherProvider():WeatherProviders{
        return weatherProvider
    }
    fun gWeatherKey():String {
        return weatherApiKey ?: ""
    }
    fun gHourlyForecast():MutableList<HourForecast>{
        return hourlyForecast
    }
    fun gDailyForecast():MutableList<DailyForecast>{
        return dailyForecast
    }
    fun gLatitude():Float{
        return latitude
    }
    fun gLongitude():Float{
        return longitude
    }
}