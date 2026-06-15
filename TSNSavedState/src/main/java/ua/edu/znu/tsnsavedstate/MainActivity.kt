package ua.edu.znu.tsnsavedstate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.edu.znu.tsnsavedstate.nav.Nav
import ua.edu.znu.tsnsavedstate.ui.theme.TSNSavedStateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TSNSavedStateTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Nav(innerPadding = innerPadding)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavPreview() {
    TSNSavedStateTheme {
        Nav(innerPadding = PaddingValues(0.dp))
    }
}