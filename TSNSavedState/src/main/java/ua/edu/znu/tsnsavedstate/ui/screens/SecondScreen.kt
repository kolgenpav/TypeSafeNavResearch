package ua.edu.znu.tsnsavedstate.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ua.edu.znu.tsnsavedstate.data.Subject

/**
 * The second screen of the app, receiving a Subject object from the first screen and displaying its details.
 * Also includes a button to navigate back to the first screen.
 */
@Composable
fun SecondScreen(
    /* User-type complex object received from the first screen */
    subject: Subject?,
    onNavigateBack: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Second Screen"
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (subject == null) {
            Text(text = "Subject is missing (process restored or no handoff).")
        } else {
            Text(text = "Subject Details:")
            Text(
                text = "  ID: ${subject.id}"
            )
            Text(
                text = "  Name: ${subject.name}"
            )
            Text(
                text = "  Checked: ${subject.isChecked}"
            )
            Text(
                text = "  Category: ${subject.category.name}"
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNavigateBack
        ) {
            Text(text = "Go back to First Screen")
        }
    }
}