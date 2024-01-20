package ru.warr1on.simplesmsforwarding.presentation.core.components.modal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

@Composable
fun ModalBottomSheetGenericHeader(
    title: String,
    closeAction: () -> Unit
) {
    val headerHeight = 60.dp
    
    Surface(
        elevation = 0.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(headerHeight)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 10.dp)
                    .align(Alignment.Center)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                
                Spacer(Modifier.width(16.dp))
                
                ModalBottomSheetGenericHeaderCloseButton(closeAction)
            }
            
            Divider(
                color = Color.Gray.copy(alpha = 0.18f),
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun ModalBottomSheetContentWithHeader(
    header: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val bottomSheetMinTopOffset = 40
    
    BoxWithConstraints(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = (this.maxHeight.value - bottomSheetMinTopOffset).dp)
        ) {
            header()
            content()
        }
    }
}

@Composable
private fun ModalBottomSheetGenericHeaderCloseButton(closeAction: () -> Unit) {
    
    val buttonSize = 22
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(buttonSize.dp)
            .clip(CircleShape)
            .background(Color.Gray.copy(alpha = 0.5f))
            .clickable { closeAction() }
    ) {
        Image(
            imageVector = Icons.Filled.Close,
            contentDescription = "Close",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size((buttonSize - 8).dp)
        )
    }
}

// Previews

@Preview
@Composable
private fun ModalBottomSheetGenericHeader_Preview() {
    AppTheme {
        ModalBottomSheetGenericHeader(title = "Header") {}
    }
}
