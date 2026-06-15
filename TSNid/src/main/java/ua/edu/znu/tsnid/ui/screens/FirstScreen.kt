package ua.edu.znu.tsnid.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ua.edu.znu.tsnid.data.Category
import ua.edu.znu.tsnid.data.Subject

/**
 * The first screen of the app, allowing the user to input details for a Subject and navigate forward.
 * The navigation callback accepts a Subject object, demonstrating type-safe data passing.
 */
@Composable
fun FirstScreen(
    /* Callback to navigate forward, accepting the user-type complex argument */
    onNavigateForward: (Subject) -> Unit
) {
    val subjectName = rememberSaveable { mutableStateOf("") }
    val isSubjectChecked = rememberSaveable { mutableStateOf(false) }
    val categoryName = rememberSaveable { mutableStateOf("") }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(text = "First Screen")
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            modifier = Modifier.testTag("subjectNameInput"),
            value = subjectName.value,
            onValueChange = { subjectName.value = it },
            label = { Text("Enter the Subject name") }
        )
        Checkbox(
            modifier = Modifier.testTag("subjectCheckedInput"),
            checked = isSubjectChecked.value,
            onCheckedChange = { isSubjectChecked.value = it }
        )
        OutlinedTextField(
            modifier = Modifier.testTag("categoryNameInput"),
            value = categoryName.value,
            onValueChange = { categoryName.value = it },
            label = { Text("Enter the Category name") }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.testTag("goToSecondButton"),
            onClick = {
                onNavigateForward(
                    Subject(
                        id = 1, // It will be replaced by the repository, but we need to provide something here
                        name = subjectName.value,
                        isChecked = isSubjectChecked.value,
                        // Should not affect the laten
                        data = ByteArray(10000), // Simulate some payload data
                        category = Category(id = 1, name = categoryName.value) // The same for id as above
                    )
                )
            }) {
            Text(text = "Go to Second Screen")
        }
    }
}
