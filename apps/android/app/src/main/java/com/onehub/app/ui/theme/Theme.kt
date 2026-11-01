package com.onehub.app.ui.theme

import androidx.compose.runtime.Composable
import com.onehub.core.design.OneHubTheme as CoreTheme

/** @deprecated Prefer com.onehub.core.design.OneHubTheme */
@Composable
fun OneHubTheme(content: @Composable () -> Unit) {
    CoreTheme(content = content)
}
