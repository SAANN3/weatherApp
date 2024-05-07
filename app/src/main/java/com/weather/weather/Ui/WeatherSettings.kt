package com.weather.weather.Ui

import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weather.weather.Controller
import com.weather.weather.TemperatureSymbols
import com.weather.weather.WeatherProviders
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class WeatherSettings(controller: Controller) {
    private val controller:Controller
    init{
        this.controller = controller
    }
    @Composable
    fun Render(
        modifier: Modifier = Modifier
    ) {
        var weatherApiKey by rememberSaveable{mutableStateOf(controller.getWeatherKey()) }
        var weatherProvider by remember {mutableStateOf(controller.getWeatherProvider())}
        var temperatureMetrics by remember {mutableStateOf(controller.getWeatherMetrics())}
        Column(modifier = Modifier) {
            ItemInList(
                text = "Change weather api",
                onSaveFunction = {
                    controller.setWeatherApi(weatherApiKey,weatherProvider)
                },
                onCancelFunction = {
                    weatherApiKey = controller.getWeatherKey()
                    weatherProvider = controller.getWeatherProvider()
                }
            ) {
                Text(text = "Select weather service:",modifier = Modifier.padding(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically){
                    RadioButton(
                        selected =  weatherProvider == WeatherProviders.OPENWEATHER,
                        onClick = { weatherProvider = WeatherProviders.OPENWEATHER }
                    )
                    Text(text = "OpenWeather")
                }
                Row(verticalAlignment = Alignment.CenterVertically){
                    RadioButton(
                        selected = weatherProvider == WeatherProviders.OPENMETEO,
                        onClick = { weatherProvider = WeatherProviders.OPENMETEO }
                    )
                    Text(text = "OpenMeteo")
                }
                if(weatherProvider == WeatherProviders.OPENWEATHER) {
                    OutlinedTextField(
                        value = weatherApiKey,
                        onValueChange = { weatherApiKey = it },
                        label = { Text("Insert api key") },
                        singleLine = true
                    )
                }
            }
            ItemInList(
                text = "Select temperature unit",
                onSaveFunction = {
                    controller.setWeatherMetrics(temperatureMetrics)
                },
                onCancelFunction = {
                    temperatureMetrics = controller.getWeatherMetrics()
                }
            ) {
                Text(text = "Select temperature unit:", modifier = Modifier.padding(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = temperatureMetrics == TemperatureSymbols.CELSIUS,
                        onClick = { temperatureMetrics = TemperatureSymbols.CELSIUS }
                    )
                    Text(text = TemperatureSymbols.CELSIUS.symbol)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = temperatureMetrics == TemperatureSymbols.FAHRENHEIT,
                        onClick = { temperatureMetrics = TemperatureSymbols.FAHRENHEIT }
                    )
                    Text(text = TemperatureSymbols.FAHRENHEIT.symbol)
                }

            }
            ItemInList(text = "Debug info") {
                Column(
                    modifier = Modifier.padding(15.dp)
                ){
                    Text(text = "Longitude : ${controller.getLongitude()}")
                    Text(text = "Latitude : ${controller.getLatitude()}")
                    Button(onClick = {controller.backToFirstTime()}){
                        Text(text = "Back to first time setup")
                    }

                }

            }
        }

    }
    @Composable
    fun ItemInList(
        text:String,
        onCancelFunction: () -> Unit = {},
        onSaveFunction: () -> Unit = {},
        content:@Composable () -> Unit
    ){
        var visible by remember { mutableStateOf(false) }
        val onDismissRequest: () -> Unit = {visible = !visible}
        if(visible)Dialog(onDismissRequest = { onDismissRequest();onCancelFunction()},) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                ){
                    content()
                    Row{
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = {
                            onCancelFunction()
                            onDismissRequest();
                        }) {
                            Text("Cancel")
                        }
                        TextButton(onClick = {
                            onSaveFunction()
                            visible = !visible
                        }) {
                            Text("Save")
                        }
                    }

                }
            }
        }
        Column(modifier = Modifier
            .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = {visible = !visible;},
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(0)
            ) {
                Text(text, modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                    textAlign = TextAlign.Start)

            }
            Divider(modifier = Modifier
                .fillMaxWidth(1f)
                .height(1.dp))
        }
    }
    @Composable
    fun RenderFirstStart(
        innerPadding: PaddingValues,
        currentContext: MutableState<String>
    ){
        val notDetectableCity = remember { mutableStateOf(false)}
        val key = remember { mutableStateOf("") }
        val city = remember { mutableStateOf("") }
        val temperatureSymbol = remember { mutableStateOf(TemperatureSymbols.CELSIUS)}
        val weatherProvider = remember { mutableStateOf(WeatherProviders.OPENMETEO)}
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "First time setup:", fontSize = 32.sp)
            Text(text = "Choose Weather service", fontSize = 24.sp)
            Row(verticalAlignment = Alignment.CenterVertically){
                RadioButton(
                    selected = weatherProvider.value == WeatherProviders.OPENMETEO,
                    onClick = { weatherProvider.value = WeatherProviders.OPENMETEO }
                )
                Text(text = "OpenMeteo")
                RadioButton(
                    selected =  weatherProvider.value == WeatherProviders.OPENWEATHER,
                    onClick = { weatherProvider.value = WeatherProviders.OPENWEATHER }
                )
                Text(text = "OpenWeather")
            }
            if(weatherProvider.value == WeatherProviders.OPENWEATHER){
                OutlinedTextField(
                    value = key.value,
                    onValueChange = {key.value = it},
                    label = {Text("Insert api key")},
                    singleLine = true)
            }
            Text(text = "Select temperature unit", fontSize = 24.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = temperatureSymbol.value == TemperatureSymbols.CELSIUS,
                    onClick = { temperatureSymbol.value = TemperatureSymbols.CELSIUS }
                )
                Text(text = TemperatureSymbols.CELSIUS.symbol)
                RadioButton(
                    selected = temperatureSymbol.value== TemperatureSymbols.FAHRENHEIT,
                    onClick = { temperatureSymbol.value = TemperatureSymbols.FAHRENHEIT }
                )
                Text(text = TemperatureSymbols.FAHRENHEIT.symbol)
            }
            Text(text = "Enter city name", fontSize = 24.sp)
            if(notDetectableCity.value){
                Text(text = "Couldn't find city",fontSize = 18.sp)  
            }
            OutlinedTextField(
                value = city.value,
                onValueChange = {city.value = it;notDetectableCity.value=false},
                label = {Text("Write city name")},
                singleLine = true,
                isError = notDetectableCity.value)
            Button(
                onClick = {
                    GlobalScope.launch {
                        val result: Int = GlobalScope.async {
                            controller.firstTimeRegister(
                                apiKey = key.value,
                                city = city.value,
                                temperatureSymbol = temperatureSymbol.value,
                                weatherProvider = weatherProvider.value
                            )
                        }.await()
                        if (result == 0) {
                            currentContext.value = "main"
                        } else if (result == -1) {
                            notDetectableCity.value = true
                        }
                    }
                },
                enabled = !notDetectableCity.value && city.value != ""
            ) {
                Text(text = "Finish")
            }
        }
    }
}