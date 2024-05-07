package ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebView(url: String) {
    AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
        android.webkit.WebView(context).apply { loadUrl(url) }
    })
}
