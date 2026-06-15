package ua.edu.znu.tsnparcelize

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
import ua.edu.znu.tsnparcelize.nav.Nav
import ua.edu.znu.tsnparcelize.ui.theme.TSNParcelizeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TSNParcelizeTheme {
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
    TSNParcelizeTheme {
        Nav(innerPadding = PaddingValues(0.dp))
    }
}