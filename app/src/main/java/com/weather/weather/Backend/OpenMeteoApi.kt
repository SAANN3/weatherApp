package com.weather.weather.Backend

import android.util.Log
import com.weather.weather.DaysOfTheWeek
import com.weather.weather.TemperatureSymbols
import com.weather.weather.WeatherCondition
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess
val JSONSTRING = "{\"latitude\":55.125,\"longitude\":61.4375,\"generationtime_ms\":0.05793571472167969,\"utc_offset_seconds\":0,\"timezone\":\"GMT\",\"timezone_abbreviation\":\"GMT\",\"elevation\":232.0,\"hourly_units\":{\"time\":\"iso8601\",\"temperature_2m\":\"°C\",\"weather_code\":\"wmo code\"},\"hourly\":{\"time\":[\"2024-05-07T00:00\",\"2024-05-07T01:00\",\"2024-05-07T02:00\",\"2024-05-07T03:00\",\"2024-05-07T04:00\",\"2024-05-07T05:00\",\"2024-05-07T06:00\",\"2024-05-07T07:00\",\"2024-05-07T08:00\",\"2024-05-07T09:00\",\"2024-05-07T10:00\",\"2024-05-07T11:00\",\"2024-05-07T12:00\",\"2024-05-07T13:00\",\"2024-05-07T14:00\",\"2024-05-07T15:00\",\"2024-05-07T16:00\",\"2024-05-07T17:00\",\"2024-05-07T18:00\",\"2024-05-07T19:00\",\"2024-05-07T20:00\",\"2024-05-07T21:00\",\"2024-05-07T22:00\",\"2024-05-07T23:00\",\"2024-05-08T00:00\",\"2024-05-08T01:00\",\"2024-05-08T02:00\",\"2024-05-08T03:00\",\"2024-05-08T04:00\",\"2024-05-08T05:00\",\"2024-05-08T06:00\",\"2024-05-08T07:00\",\"2024-05-08T08:00\",\"2024-05-08T09:00\",\"2024-05-08T10:00\",\"2024-05-08T11:00\",\"2024-05-08T12:00\",\"2024-05-08T13:00\",\"2024-05-08T14:00\",\"2024-05-08T15:00\",\"2024-05-08T16:00\",\"2024-05-08T17:00\",\"2024-05-08T18:00\",\"2024-05-08T19:00\",\"2024-05-08T20:00\",\"2024-05-08T21:00\",\"2024-05-08T22:00\",\"2024-05-08T23:00\",\"2024-05-09T00:00\",\"2024-05-09T01:00\",\"2024-05-09T02:00\",\"2024-05-09T03:00\",\"2024-05-09T04:00\",\"2024-05-09T05:00\",\"2024-05-09T06:00\",\"2024-05-09T07:00\",\"2024-05-09T08:00\",\"2024-05-09T09:00\",\"2024-05-09T10:00\",\"2024-05-09T11:00\",\"2024-05-09T12:00\",\"2024-05-09T13:00\",\"2024-05-09T14:00\",\"2024-05-09T15:00\",\"2024-05-09T16:00\",\"2024-05-09T17:00\",\"2024-05-09T18:00\",\"2024-05-09T19:00\",\"2024-05-09T20:00\",\"2024-05-09T21:00\",\"2024-05-09T22:00\",\"2024-05-09T23:00\",\"2024-05-10T00:00\",\"2024-05-10T01:00\",\"2024-05-10T02:00\",\"2024-05-10T03:00\",\"2024-05-10T04:00\",\"2024-05-10T05:00\",\"2024-05-10T06:00\",\"2024-05-10T07:00\",\"2024-05-10T08:00\",\"2024-05-10T09:00\",\"2024-05-10T10:00\",\"2024-05-10T11:00\",\"2024-05-10T12:00\",\"2024-05-10T13:00\",\"2024-05-10T14:00\",\"2024-05-10T15:00\",\"2024-05-10T16:00\",\"2024-05-10T17:00\",\"2024-05-10T18:00\",\"2024-05-10T19:00\",\"2024-05-10T20:00\",\"2024-05-10T21:00\",\"2024-05-10T22:00\",\"2024-05-10T23:00\",\"2024-05-11T00:00\",\"2024-05-11T01:00\",\"2024-05-11T02:00\",\"2024-05-11T03:00\",\"2024-05-11T04:00\",\"2024-05-11T05:00\",\"2024-05-11T06:00\",\"2024-05-11T07:00\",\"2024-05-11T08:00\",\"2024-05-11T09:00\",\"2024-05-11T10:00\",\"2024-05-11T11:00\",\"2024-05-11T12:00\",\"2024-05-11T13:00\",\"2024-05-11T14:00\",\"2024-05-11T15:00\",\"2024-05-11T16:00\",\"2024-05-11T17:00\",\"2024-05-11T18:00\",\"2024-05-11T19:00\",\"2024-05-11T20:00\",\"2024-05-11T21:00\",\"2024-05-11T22:00\",\"2024-05-11T23:00\",\"2024-05-12T00:00\",\"2024-05-12T01:00\",\"2024-05-12T02:00\",\"2024-05-12T03:00\",\"2024-05-12T04:00\",\"2024-05-12T05:00\",\"2024-05-12T06:00\",\"2024-05-12T07:00\",\"2024-05-12T08:00\",\"2024-05-12T09:00\",\"2024-05-12T10:00\",\"2024-05-12T11:00\",\"2024-05-12T12:00\",\"2024-05-12T13:00\",\"2024-05-12T14:00\",\"2024-05-12T15:00\",\"2024-05-12T16:00\",\"2024-05-12T17:00\",\"2024-05-12T18:00\",\"2024-05-12T19:00\",\"2024-05-12T20:00\",\"2024-05-12T21:00\",\"2024-05-12T22:00\",\"2024-05-12T23:00\",\"2024-05-13T00:00\",\"2024-05-13T01:00\",\"2024-05-13T02:00\",\"2024-05-13T03:00\",\"2024-05-13T04:00\",\"2024-05-13T05:00\",\"2024-05-13T06:00\",\"2024-05-13T07:00\",\"2024-05-13T08:00\",\"2024-05-13T09:00\",\"2024-05-13T10:00\",\"2024-05-13T11:00\",\"2024-05-13T12:00\",\"2024-05-13T13:00\",\"2024-05-13T14:00\",\"2024-05-13T15:00\",\"2024-05-13T16:00\",\"2024-05-13T17:00\",\"2024-05-13T18:00\",\"2024-05-13T19:00\",\"2024-05-13T20:00\",\"2024-05-13T21:00\",\"2024-05-13T22:00\",\"2024-05-13T23:00\"],\"temperature_2m\":[4.8,4.5,5.2,6.6,7.8,8.8,9.5,10.5,11.5,11.6,11.6,11.8,12.4,12.2,11.9,11.6,10.7,9.8,8.6,8.1,7.9,8.0,8.2,7.9,7.5,7.7,8.8,10.8,13.1,15.2,17.2,18.5,19.5,19.6,20.1,19.8,19.4,18.9,18.3,17.6,17.1,16.2,15.3,14.3,13.2,12.8,12.5,12.4,12.4,12.5,12.3,10.9,8.3,7.5,6.2,5.9,7.0,8.1,9.7,10.5,10.5,9.9,8.9,8.0,7.4,6.5,5.6,4.8,4.1,3.6,3.1,2.8,2.1,2.1,2.9,3.8,5.4,6.3,6.4,6.5,7.9,9.8,9.9,10.1,10.2,9.6,9.0,8.2,7.2,6.1,5.3,4.6,4.0,3.5,2.9,2.3,2.2,2.7,3.7,4.8,6.2,7.7,8.9,9.8,10.5,11.0,11.6,12.1,12.3,12.3,12.1,11.6,10.6,9.4,8.4,7.6,7.1,6.6,6.2,5.8,5.8,6.1,6.8,7.8,9.6,11.9,13.8,15.3,16.4,17.0,16.7,15.8,15.1,15.1,14.9,14.4,13.5,12.5,11.5,10.9,10.3,9.8,9.2,8.5,8.1,8.2,8.6,8.9,9.2,9.5,9.8,10.2,10.6,10.9,11.4,11.9,12.0,11.7,11.2,10.5,9.8,8.9,8.3,7.9,7.8,7.5,7.0,6.5],\"weather_code\":[0,0,1,0,2,2,2,2,1,2,2,3,3,1,2,3,1,2,2,2,3,3,2,1,1,0,0,1,1,3,3,3,3,3,3,3,3,3,3,3,3,3,61,61,61,61,61,80,3,3,61,61,61,61,61,61,61,3,3,0,0,1,1,1,2,1,0,0,0,0,0,0,0,3,3,3,61,2,80,1,3,3,3,2,2,2,1,1,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3,3,3,3,3,3,3,0,0,0,2,2,2,2,2,2,61,61,61,2,2,2,3,3,3,2,2,2,3,3,3,2,2,2,2,2,2,3,3,3,3,3,3,80,80,80,61,61,61,3,3,3,3,3,3,3,3,3,61,61,61,2,2]}}"
class OpenMeteoApi(settingsData:SettingsData, previousRequest : ResponseRaw? = null)
    : WeatherApiBaseClass(null,settingsData) {
    private var responseRaw: ResponseRaw? = null
    init{
        this.responseRaw = previousRequest
        GlobalScope.launch { start() }
    }
    companion object{
        suspend fun getLatLong(city: String):LatNLong?{
            try{
                val httpClient:OkHttpClient = OkHttpClient().newBuilder()
                    .callTimeout(5000,TimeUnit.SECONDS)
                    .readTimeout(5000,TimeUnit.SECONDS)
                    .build()
                val httpUrl:String = "https://geocoding-api.open-meteo.com/v1/search?name=${city}&count=1&language=en&format=json"
                val request = Request.Builder().url(httpUrl).build()
                val response:String = httpClient.newCall(request).execute().use {
                    it.body!!.string()
                }
                Log.e("EEE",response)
                val parsedResponse:JsonObject = Json.parseToJsonElement(response).jsonObject
                Log.e("EEE",parsedResponse.toString())
                val firstOccurrence:JsonObject = parsedResponse["results"]!!.jsonArray[0].jsonObject
                val latitude:Float = firstOccurrence["latitude"]!!.jsonPrimitive.float
                val longitude:Float = firstOccurrence["longitude"]!!.jsonPrimitive.float
                val findedCity:String = firstOccurrence["name"]!!.jsonPrimitive.content
                return LatNLong(latitude, longitude,findedCity)
            }catch (e:Exception){
                Log.e("EEE",e.stackTraceToString())
                //cant
            }
            return null
        }
    }
    override fun gRawResponse():ResponseRaw?{
        return responseRaw
    }
    private suspend fun start(){
        val prevResponse:ResponseRaw? = responseRaw
        val newResponse:ResponseRaw
        if(prevResponse != null && prevResponse.dateResponse > (ZonedDateTime.now().toEpochSecond() - 3600)){
            newResponse  = prevResponse
        } else{
            val httpClient:OkHttpClient = OkHttpClient().newBuilder()
                .callTimeout(5000,TimeUnit.SECONDS)
                .readTimeout(5000,TimeUnit.SECONDS)
                .build()
            val httpUrl:String = "https://api.open-meteo.com/v1/forecast?latitude=${latitude}&longitude=${longitude}&hourly=temperature_2m,weather_code"
            val request = Request.Builder().url(httpUrl).build()
            val response:String = httpClient.newCall(request).execute().use {
                it.body!!.string()
            }
            Log.e("EEE",response)
            newResponse  = ResponseRaw(
                rawResponse = response,
                dateResponse = ZonedDateTime.now().toEpochSecond()
            )
        }
        processData(newResponse)
    }
    private fun processData(responseRaw: ResponseRaw){
        val parsedResponse:JsonObject = Json.parseToJsonElement(responseRaw.rawResponse).jsonObject
        try{
            var mostlyWeatherIs:IntArray = IntArray(WeatherCondition.entries.size)
            val hourly:JsonObject = parsedResponse["hourly"]!!.jsonObject
            val hourlyTemperature:JsonArray = hourly["temperature_2m"]!!.jsonArray
            val hourlyWeatherCodes:JsonArray = hourly["weather_code"]!!.jsonArray
            val hourlyTime:JsonArray = hourly["time"]!!.jsonArray
            for (i in hourlyTemperature.indices){
                val timeZone:ZonedDateTime = LocalDateTime.parse(hourlyTime[i].jsonPrimitive.content).toInstant(ZoneOffset.UTC).atZone(TimeZone.getDefault().toZoneId())
                if((timeZone.hour != LocalDateTime.now().hour ||
                    timeZone.dayOfMonth != LocalDateTime.now().dayOfMonth) &&
                    hourlyForecast.size == 0){
                    continue
                }
                val hourWeather: HourForecast = HourForecast(
                    temperature = when(temperatureSymbol){
                        TemperatureSymbols.CELSIUS -> hourlyTemperature[i].jsonPrimitive.float
                        TemperatureSymbols.FAHRENHEIT -> celsiusToKelvin(hourlyTemperature[i].jsonPrimitive.float)
                    } ,
                    weatherCondition = formatWeatherCode(hourlyWeatherCodes[i].jsonPrimitive.int),
                    hour = timeZone.hour,
                    dayOfMonth = timeZone.dayOfMonth,
                    dayOfWeek =  DaysOfTheWeek.entries[timeZone.dayOfWeek.value-1]
                )
                hourlyForecast.add(hourWeather)
                if(dailyForecast.size == 0 || dailyForecast.last().dayOfMonth != hourWeather.dayOfMonth ){
                    mostlyWeatherIs = IntArray(WeatherCondition.entries.size)
                    val dayWeather:DailyForecast = DailyForecast(
                        minTemperature = hourWeather.temperature,
                        maxTemperature = hourWeather.temperature,
                        condition = hourWeather.weatherCondition,
                        dayOfMonth = hourWeather.dayOfMonth,
                        dayOfWeek = hourWeather.dayOfWeek,
                    )
                    mostlyWeatherIs[hourWeather.weatherCondition.ordinal] += 1
                    dailyForecast.add(dayWeather)
                }
                else{
                    if(dailyForecast.last().minTemperature > hourWeather.temperature){
                        dailyForecast.last().minTemperature = hourWeather.temperature
                    }
                    if(dailyForecast.last().maxTemperature < hourWeather.temperature){
                        dailyForecast.last().maxTemperature = hourWeather.temperature
                    }
                    mostlyWeatherIs[hourWeather.weatherCondition.ordinal] += 1
                    dailyForecast.last().condition = WeatherCondition.entries[mostlyWeatherIs.indexOf(mostlyWeatherIs.max())]
                }
            }
            this.responseRaw = responseRaw
            notifyListeners()
        }catch (e:Exception){
            //cant
            Log.e("EE",e.stackTraceToString())
        }
    }
    private fun formatWeatherCode(code:Int):WeatherCondition{
        return when(code){
            0 -> WeatherCondition.CLEAR
            in (1..2) -> WeatherCondition.PARTLYCLOUDY
            3 -> WeatherCondition.CLOUDY
            in (4..67) -> WeatherCondition.RAIN
            in (68..86) -> WeatherCondition.SNOW
            in (95..99) -> WeatherCondition.THUNDERSTORM
            else -> WeatherCondition.CLEAR
        }
    }

}