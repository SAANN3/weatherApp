package com.weather.weather.Ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.weather.weather.R

class WeatherBar {

    @Composable
    fun Render(currentContext: MutableState<String>, modifier: Modifier = Modifier) {
        var expanded by remember { mutableStateOf(false) }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if(currentContext.value != "main"){
                    TextButton(
                        onClick = { currentContext.value = "main" }
                    ) {
                        Image(
                            painterResource(id = R.drawable.baseline_keyboard_backspace_24), "",
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
                TextButton(
                    onClick = { expanded = !expanded  }
                ) {
                    Image(
                        painterResource(id = R.drawable.baseline_more_vert_24),"",
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary))
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                expanded = !expanded
                                currentContext.value = "settings"
                            }
                        )
                    }


                }
            }
            Divider(modifier = Modifier
                .fillMaxWidth(1f)
                .height(1.dp))
        }
    }
}