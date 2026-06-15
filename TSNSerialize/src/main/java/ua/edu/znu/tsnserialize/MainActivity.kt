package ua.edu.znu.tsnserialize

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
import ua.edu.znu.tsnserialize.nav.Nav
import ua.edu.znu.tsnserialize.ui.theme.TSNSerializeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TSNSerializeTheme {
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
    TSNSerializeTheme {
        Nav(innerPadding = PaddingValues(0.dp))
    }
}