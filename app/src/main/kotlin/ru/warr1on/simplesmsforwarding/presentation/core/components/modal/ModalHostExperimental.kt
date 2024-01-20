package ru.warr1on.simplesmsforwarding.presentation.core.components.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

interface HostedModalData {
    
    val modalContent: @Composable HostedModalData.() -> Unit
    
    var mainContentDownsizeFraction: Float // 0f (min) - 1f (max)
    
    fun dismiss()
}

val LocalModalHost = compositionLocalOf<ModalHostStateExperimental> {
    error("No modal host provided")
}

val LocalModalScope = compositionLocalOf<CoroutineScope> {
    error("No modal scope provided")
}

class ModalHostStateExperimental {
    
    private val bottomSheetMutex = Mutex()
    private val popupMutex = Mutex()
    
    var currentBottomSheetData by mutableStateOf<HostedModalData?>(null)
        private set
    var currentPopupData by mutableStateOf<HostedModalData?>(null)
    
    suspend fun showModalBottomSheet(
        content: @Composable HostedModalData.() -> Unit
    ) = bottomSheetMutex.withLock {
        try {
            suspendCancellableCoroutine<Unit> { continuation ->
                currentBottomSheetData = HostedModalDataImpl(content, continuation)
            }
        } finally {
            currentBottomSheetData = null
        }
    }
    
    suspend fun showModalPopup(
        content: @Composable HostedModalData.() -> Unit
    ) = popupMutex.withLock {
        try {
            suspendCancellableCoroutine<Unit> { continuation ->
                currentPopupData = HostedModalDataImpl(content, continuation)
            }
        } finally {
            currentPopupData = null
        }
    }
    
    private class HostedModalDataImpl(
        override val modalContent: @Composable HostedModalData.() -> Unit,
        private val continuation: CancellableContinuation<Unit>
    ) : HostedModalData {
    
        override var mainContentDownsizeFraction by mutableStateOf(0f)
        
        override fun dismiss() {
            if (continuation.isActive) { continuation.resume(Unit) }
        }
    }
}

@Composable
private fun rememberModalHostStateExperimental(): ModalHostStateExperimental {
    return remember { ModalHostStateExperimental() }
}

@Composable
fun ModalHostExperimental(
    backgroundColor: Color = Color.Black,
    content: @Composable () -> Unit
) {
    val state = rememberModalHostStateExperimental()
    val modalScope = rememberCoroutineScope()
    
    CompositionLocalProvider(
        LocalModalHost provides state,
        LocalModalScope provides modalScope
    ) {
        // Контейнер модального хоста
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            ModalBottomSheetHostExperimental(
                currentBottomSheetData = state.currentBottomSheetData,
                modifier = Modifier.fillMaxSize(),
                underlyingContent = content
            )

            ModalPopupHost(
                currentPopupData = state.currentPopupData,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ModalBottomSheetHostExperimental(
    currentBottomSheetData: HostedModalData?,
    modifier: Modifier = Modifier,
    underlyingContent: @Composable () -> Unit
) {
    val downsizeFraction = currentBottomSheetData?.mainContentDownsizeFraction ?: 0f
    val mainContentCornerRadius = (10 * downsizeFraction).dp
    val mainContentScaleFactor = 1f - (0.05f * downsizeFraction)
    val mainContentYOffset = 5 * downsizeFraction
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .offset(y = mainContentYOffset.dp)
            .scale(mainContentScaleFactor)
            .clip(RoundedCornerShape(mainContentCornerRadius))
    ) {
        underlyingContent()
    }
    
    if (currentBottomSheetData != null) {
        val sheetContent = currentBottomSheetData.modalContent
        currentBottomSheetData.sheetContent()
    }
}

@Composable
private fun ModalPopupHost(
    currentPopupData: HostedModalData?,
    modifier: Modifier
) {
    if (currentPopupData != null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
        ) {
            val popupContent = currentPopupData.modalContent
            currentPopupData.popupContent()
        }
    }
}
