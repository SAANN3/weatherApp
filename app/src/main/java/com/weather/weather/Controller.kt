package com.weather.weather

import android.util.Log
import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.weather.weather.Backend.DataWorker
import com.weather.weather.Backend.OpenMeteoApi
import com.weather.weather.Backend.OpenWeatherApi
import com.weather.weather.Backend.ResponseRaw
import com.weather.weather.Backend.SettingsData
import com.weather.weather.Backend.WeatherApiBaseClass
import com.weather.weather.Ui.DaysWeatherLong
import com.weather.weather.Ui.HourWeatherShort
import com.weather.weather.Ui.MainScreenWeather
import com.weather.weather.Ui.WeatherBar
import com.weather.weather.Ui.WeatherSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import okhttp3.internal.notifyAll
import kotlin.system.exitProcess

class Controller {
    private val firstStart:Boolean
    private val dataWorker:DataWorker = DataWorker()
    private lateinit var weatherApi:WeatherApiBaseClass
    private val weatherBar: WeatherBar = WeatherBar()
    private val weatherSettings: WeatherSettings = WeatherSettings(this)
    private val mainScreenWeather: MainScreenWeather = MainScreenWeather(this)
    private val hourWeatherShort: HourWeatherShort = HourWeatherShort(this)
    private val daysWeatherLong: DaysWeatherLong = DaysWeatherLong(this)
    init{
        firstStart = dataWorker.getDataBoolean("firstStart")?:true
        if(!firstStart){
            createWeatherApi()
        }
    }
    private fun resetResponse(){
        daysWeatherLong.resetForecast()
        hourWeatherShort.resetForecast()
        mainScreenWeather.resetForecast()
        dataWorker.removeDataString("previousResponse")
        dataWorker.removeDataString("previousDateResponse")
    }
    private fun createWeatherApi(){
        //called only if everything set up
        val key:String? = dataWorker.getDataString("apiKey")
        val rawResponse:String? = dataWorker.getDataString("previousResponse")
        val dateResponse:Long? = dataWorker.getDataLong("previousDateResponse")
        val previousResponse: ResponseRaw?
        if(rawResponse != null && dateResponse != null){
            previousResponse = ResponseRaw(
                rawResponse = rawResponse,
                dateResponse = dateResponse
            )
        }
        else {
            previousResponse = null
        }
        val settingsData:SettingsData = SettingsData(
            latitude = dataWorker.getDataFloat("latitude")!!,
            longitude = dataWorker.getDataFloat("longitude")!!,
            city = dataWorker.getDataString("city")!!,
            temperatureSymbol = TemperatureSymbols.valueOf(dataWorker.getDataString("temperatureSymbol")!!),
            weatherProvider = WeatherProviders.valueOf(dataWorker.getDataString("weatherProvider")!!),
        )
        when(settingsData.weatherProvider){
            WeatherProviders.OPENMETEO -> {
                weatherApi = OpenMeteoApi(
                    settingsData = settingsData,
                    previousResponse = previousResponse
                )
            }
            WeatherProviders.OPENWEATHER -> {
                weatherApi = OpenWeatherApi(
                    weatherApiKey = key!!,
                    settingsData = settingsData,
                    previousResponse = previousResponse,

                )
            }
        }
        weatherBar.onComplete()
        weatherApi.subscribeError(this::onError)
        weatherApi.subscribeError(weatherBar::onError)
        weatherApi.subscribe(this::saveLastResponse)
        weatherApi.subscribe(hourWeatherShort::onForecastChange)
        weatherApi.subscribe(daysWeatherLong::onForecastChange)
        weatherApi.subscribe(mainScreenWeather::onForecastChange)
        weatherApi.start()
    }
    private fun onError(error: WeatherErrors){
        when(error){
            WeatherErrors.UnknownHost -> {
                weatherApi.start(true)
            }
            WeatherErrors.CacheForceLoadFailed -> {

            }
            WeatherErrors.IOError -> {
                weatherApi.start(true)
            }
            WeatherErrors.ApiKeyInvalid -> {
                weatherApi.start(true)
            }
            else -> {
                Log.e("weatherError", "[cauht]:$error")
            }
        }
    }
    private fun saveLastResponse(){
        val requestRaw:ResponseRaw? = weatherApi.gRawResponse()
        if(requestRaw != null){
            dataWorker.setData("previousResponse",requestRaw.rawResponse)
            dataWorker.setData("previousDateResponse",requestRaw.dateResponse)
        }
    }
    //setters and getters
    fun getWeatherKey():String{
        return weatherApi.gWeatherKey()
    }
    suspend fun getCityFromNet(city:String, weatherProvider: WeatherProviders,weatherApiKey:String? = null):Array<WeatherApiBaseClass.LatNLong>?{
        return when(weatherProvider){
            WeatherProviders.OPENMETEO -> {
                GlobalScope.async {OpenMeteoApi.getLatLong(city,weatherApiKey,5)}.await()
            }
            WeatherProviders.OPENWEATHER -> {
                GlobalScope.async {OpenWeatherApi.getLatLong(city,weatherApiKey,5)}.await()
            }
        }
    }
    fun getWeatherProvider():WeatherProviders{
        return weatherApi.gWeatherProvider()
    }
    fun getWeatherMetrics():TemperatureSymbols{
        return weatherApi.gTemperatureSymbol()
    }
    fun getCity():String{
        return weatherApi.gCity()
    }
    fun getLatitude():Float{
        return weatherApi.gLatitude()
    }
    fun getLongitude():Float{
        return weatherApi.gLongitude()
    }
    fun setWeatherApi(apiKey:String,weatherProvider: WeatherProviders){
        if(dataWorker.getDataString("weatherProvider") != weatherProvider.toString()){
            resetResponse()
        }
        dataWorker.setData("apiKey",apiKey)
        dataWorker.setData("weatherProvider",weatherProvider.toString())
        dataWorker.removeDataLong("previousDateResponse")
        dataWorker.removeDataString("previousResponse")
        createWeatherApi()
    }
    fun setWeatherMetrics(temperatureSymbol: TemperatureSymbols){
        dataWorker.setData("temperatureSymbol",temperatureSymbol.toString())
        createWeatherApi()
    }
    suspend fun setCity(city:String){
        resetResponse()
        val latNlong:WeatherApiBaseClass.LatNLong? = when(weatherApi.gWeatherProvider()){
            WeatherProviders.OPENMETEO -> {
                GlobalScope.async {OpenMeteoApi.getLatLong(city,weatherApi.gWeatherKey())}.await()
            }
            WeatherProviders.OPENWEATHER -> {
                GlobalScope.async {OpenWeatherApi.getLatLong(city,weatherApi.gWeatherKey())}.await()
            }
            else -> { null }
        }
        if(latNlong != null) {
            dataWorker.setData("latitude", latNlong.latitude)
            dataWorker.setData("longitude", latNlong.longitude)
            dataWorker.setData("city", latNlong.city)
            createWeatherApi()
        }
    }
    fun getHourlyForecast():MutableList<WeatherApiBaseClass.HourForecast>{
        return weatherApi.gHourlyForecast()
    }
    fun getDailyForecast():MutableList<WeatherApiBaseClass.DailyForecast>{
        return weatherApi.gDailyForecast()
    }
    suspend fun firstTimeRegister(
        city:String,
        temperatureSymbol: TemperatureSymbols,
        weatherProvider: WeatherProviders,
        apiKey: String?
    ):Int{
        dataWorker.setData("temperatureSymbol",temperatureSymbol.toString())
        dataWorker.setData("apiKey",apiKey?:"")
        dataWorker.setData("weatherProvider",weatherProvider.toString())
        val latNlong:WeatherApiBaseClass.LatNLong? = when(weatherProvider){
            WeatherProviders.OPENMETEO -> {
                GlobalScope.async {OpenMeteoApi.getLatLong(city,apiKey)}.await()
            }
            WeatherProviders.OPENWEATHER -> {
                GlobalScope.async {OpenWeatherApi.getLatLong(city,apiKey)}.await()
            }
        }
        if(latNlong != null){
            dataWorker.setData("latitude",latNlong.latitude)
            dataWorker.setData("longitude",latNlong.longitude)
            dataWorker.setData("city",latNlong.city)
            createWeatherApi()
            dataWorker.setData("firstStart",false)
        } else {
            return -1
        }
        return 0
    }
    fun backToFirstTime(){
        dataWorker.removeData()
        exitProcess(0)
    }
    //composables
    @Composable
    fun ContextMain(innerPadding: PaddingValues) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(innerPadding)
                .padding(20.dp, 0.dp, 20.dp, 0.dp)
                .verticalScroll(scrollState)

        ) {
            mainScreenWeather.Render()
            hourWeatherShort.Render()
            daysWeatherLong.Render()
            Spacer(modifier = Modifier.weight(1f))
            Text(weatherApi.gMessage(), modifier = Modifier.fillMaxWidth(1f), textAlign = TextAlign.Center)
        }
    }
    @Composable
    fun SettingsMenu(innerPadding: PaddingValues){
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            weatherSettings.Render()
        }
    }
    @Composable
    fun MainView(){
        val currentContext = remember { mutableStateOf(if(firstStart)"firstStart" else "main") }
        Scaffold(
            topBar = {
                 if(currentContext.value!="firstStart"){weatherBar.Render(currentContext)}
            },
            content = { innerPadding -> when(currentContext.value) {
                "firstStart" -> weatherSettings.RenderFirstStart(innerPadding,currentContext)
                "settings" -> SettingsMenu(innerPadding)
                "main" -> ContextMain(innerPadding)
                }
            }
        )
    }

}