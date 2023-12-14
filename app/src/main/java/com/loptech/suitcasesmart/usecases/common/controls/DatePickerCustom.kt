package com.loptech.suitcasesmart.usecases.common.controls

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.days

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DatePickerCustom(
    onDiss: () -> Unit,
    onSelectDate: (String) -> Unit
) {

    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)


    // set the initial date
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)

    var selectedDate by remember {
        mutableLongStateOf(calendar.timeInMillis)
    }
        DatePickerDialog(
            onDismissRequest = {
                onDiss.invoke()
            },
            confirmButton = {
                TextButton(onClick = {
                    onDiss.invoke()
                    selectedDate = datePickerState.selectedDateMillis!!
                    val c = Calendar.getInstance()
                    var dateFixed = Date(selectedDate)
                    c.time = dateFixed
                    c.add(Calendar.DATE, 1)
                    dateFixed = c.time
                    val date = formatter.format(dateFixed)

                    onSelectDate.invoke(date)
                }) {
                    Text(text = "Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDiss.invoke()
                }) {
                    Text(text = "Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

// For displaying preview in
// the Android Studio IDE emulator
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DatePickerCustom(
       onDiss = {

       },
        onSelectDate = {
            print(it)
        }
    )
}
