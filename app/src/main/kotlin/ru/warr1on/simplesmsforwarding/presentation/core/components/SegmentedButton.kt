package ru.warr1on.simplesmsforwarding.presentation.core.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

@Composable
fun SegmentedButton(
    selection: MutableState<Int>,
    modifier: Modifier = Modifier,
    segments: SegmentedButtonScope.() -> Unit
) {
    SegmentedButton(
        selectedSegment = selection.value,
        onSegmentSelectionChange = { selection.value = it },
        segments = segments,
        modifier = modifier,
    )
}

@Composable
fun SegmentedButton(
    selectedSegment: Int,
    onSegmentSelectionChange: (selectedSegment: Int) -> Unit,
    modifier: Modifier = Modifier,
    segments: SegmentedButtonScope.() -> Unit
) {

    val segmentData = remember(segments) {
        val builder = SegmentedButtonScopeImpl()
        builder.segments()
        return@remember builder.segmentData
    }

    SegmentedButtonLayout(
        segments = {
            segmentData.forEachIndexed { index, segmentData ->
                ButtonSegment(
                    segmentData = segmentData,
                    onClick = { onSegmentSelectionChange(index) }
                )
            }
        },
        selectedSegment = selectedSegment,
        selectionIndicator = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.background)
            )
        },
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(4.dp)
    )
}

//region Layout

@Composable
private fun SegmentedButtonLayout(
    segments: @Composable () -> Unit,
    selectionIndicator: @Composable () -> Unit,
    selectedSegment: Int = 0,
    modifier: Modifier = Modifier
) {
    var numberOfSegments by remember(segments) { mutableIntStateOf(0) }
    var segmentMeasuredWidth by remember(segments) { mutableIntStateOf(0) }

    val selectionIndicatorOffset = remember(segments) {
        Animatable(selectedSegment.toFloat() * segmentMeasuredWidth)
    }
    LaunchedEffect(selectedSegment) {
        selectionIndicatorOffset.animateTo(selectedSegment.toFloat() * segmentMeasuredWidth)
    }

    SubcomposeLayout(
        modifier = modifier
    ) { constraints ->
        val segmentMeasurables = subcompose(1, segments)

        val segmentWidth = when (segmentMeasurables.size) {
            0 -> 0
            else -> constraints.maxWidth / segmentMeasurables.size
        }

        numberOfSegments = segmentMeasurables.size
        segmentMeasuredWidth = segmentWidth

        val segmentPlaceables = segmentMeasurables.map {
            it.measure(constraints.copy(maxWidth = segmentWidth, minWidth = segmentWidth))
        }

        val layoutHeight = when (segmentPlaceables.size) {
            0 -> 0
            else -> segmentPlaceables[0].height
        }

        val selectionIndicatorPlaceable = subcompose(2, selectionIndicator).map {
            it.measure(
                constraints.copy(
                    minWidth = segmentWidth,
                    maxWidth = segmentWidth,
                    minHeight = layoutHeight,
                    maxHeight = layoutHeight
                )
            )
        }.first()

        layout(
            width = constraints.maxWidth,
            height = layoutHeight
        ) {
            segmentPlaceables.forEachIndexed { index, placeable ->
                placeable.place(index * segmentWidth, 0, 1f)
            }

            selectionIndicatorPlaceable.place(
                x = selectionIndicatorOffset.value.toInt(),
                y = 0,
                zIndex = 0.1f
            )
        }
    }
}

//endregion

@Composable
private fun ButtonSegment(
    segmentData: SegmentedButtonSegmentData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        if (segmentData.text != null) {
            Text(
                text = segmentData.text,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

//region Segment builder

interface SegmentedButtonScope {

    fun segmentWithText(text: String)

    fun segmentWithIcon(painter: Painter)

    fun segmentWithIcon(imageVector: ImageVector)

    fun segmentWithIconAndText(painter: Painter, text: String)

    fun segmentWithIconAndText(imageVector: ImageVector, text: String)
}

private data class SegmentedButtonSegmentData(
    val text: String? = null,
    val iconPainter: Painter? = null,
    val iconVector: ImageVector? = null
)

private class SegmentedButtonScopeImpl : SegmentedButtonScope {

    private val _segmentData = mutableListOf<SegmentedButtonSegmentData>()
    val segmentData get() = _segmentData.toList()

    override fun segmentWithText(text: String) {
        _segmentData.add(SegmentedButtonSegmentData(text = text))
    }

    override fun segmentWithIcon(painter: Painter) {
        _segmentData.add(SegmentedButtonSegmentData(iconPainter = painter))
    }

    override fun segmentWithIcon(imageVector: ImageVector) {
        _segmentData.add(SegmentedButtonSegmentData(iconVector = imageVector))
    }

    override fun segmentWithIconAndText(painter: Painter, text: String) {
        _segmentData.add(
            SegmentedButtonSegmentData(
                text = text,
                iconPainter = painter
            )
        )
    }

    override fun segmentWithIconAndText(imageVector: ImageVector, text: String) {
        _segmentData.add(
            SegmentedButtonSegmentData(
                text = text,
                iconVector = imageVector
            )
        )
    }
}

//endregion

//region Previews

@Preview
@Composable
private fun SegmentedButtons_Preview() {

    var selection = remember { mutableIntStateOf(0) }

    AppTheme {
        PreviewBox {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SegmentedButton(
                    selection = selection,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    segmentWithText("Lorem")
                    segmentWithText("Ipsum")
                    segmentWithText("Dolor")
                }

                Spacer(16.dp)

                Text("Selected segment: ${selection.value}")
            }
        }
    }
}

//endregion
