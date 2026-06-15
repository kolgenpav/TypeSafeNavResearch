package ua.edu.znu.tsnid

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
import ua.edu.znu.tsnid.nav.Nav
import ua.edu.znu.tsnid.ui.theme.TSNIdTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TSNIdTheme {
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
    TSNIdTheme {
        Nav(innerPadding = PaddingValues(0.dp))
    }
}