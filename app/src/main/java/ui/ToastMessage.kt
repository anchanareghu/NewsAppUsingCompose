package ui

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable

@Composable
fun ShowToastMessage(context: Context, message: String?) {
    message?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
}