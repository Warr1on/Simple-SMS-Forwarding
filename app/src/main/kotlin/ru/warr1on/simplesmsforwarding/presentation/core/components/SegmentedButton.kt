package ru.warr1on.simplesmsforwarding.presentation.core.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

//region Main composables

/**
 * A single-select segmented button that is akin in design to iOS's segmented Picker.
 *
 * This variant takes a [MutableState] as a param for the button's current [selection]
 * and updates its value automatically when the selection changes, so it's very
 * straightforward to use; but this may not always be the preferred way of doing
 * things, and if you need more direct control over the selection (for example, if
 * your segmented button selection state is hoisted in the view model as a part of
 * a screen state data class, or if you want to be able to veto a selection change),
 * use the overload with the selectedSegment and onSegmentSelectionChange parameters.
 *
 * @param selection The selection param represents the currently selected section's ID
 * that is wrapped in a [MutableState]. The section ID could be of any type, but all of
 * the sections should have the same ID type. Usually you'd want something like an enum
 * for the section identifiers, but something as simple as an integer could work as well.
 * The [MutableState]'s value will be updated automatically when the current selection changes.
 * @param modifier The segmented button's modifier
 * @param segments Here you should define your segments by using the [SegmentedButtonScope]'s
 * functions
 */
@Composable
fun <SegmentIdType> SegmentedButton(
    selection: MutableState<SegmentIdType>,
    modifier: Modifier = Modifier,
    segments: SegmentedButtonScope<SegmentIdType>.() -> Unit
) {
    SegmentedButton(
        selection = selection.value,
        onSegmentSelectionChange = { selection.value = it },
        segments = segments,
        modifier = modifier,
    )
}

/**
 * A single-select segmented button that is akin in design to iOS's segmented Picker.
 *
 * This variant takes a selected segment's ID ([selection]) and an [onSegmentSelectionChange]
 * callback as parameters to give you control over the selection. Alternatively, if you
 * don't need that extra control, you can use the other overload with the [MutableState] as
 * the param for the selection, which value would be updated automatically whenever the
 * selection would change.
 *
 * @param selection The selection param represents the currently selected section's ID.
 * The section ID could be of any type, but all of the sections should have the same ID type.
 * Usually you'd want something like an enum for the section identifiers, but something as
 * simple as an integer could work as well. You're responsible for updating the selected
 * section's ID manually whenever it would change, which you can track with [onSegmentSelectionChange]
 * callback.
 * @param onSegmentSelectionChange This callback will be called whenever the selection in the
 * segmented button would want to change. Will have a proposed segment's ID for the new selection
 * as the param. Here, usually you'd want to update the selection state, but if you want to veto
 * this change, then you can also just do nothing with the new value.
 * @param modifier The segmented button's modifier
 * @param segments Here you should define your segments by using the [SegmentedButtonScope]'s
 * functions
 */
@Composable
fun <SegmentIdType> SegmentedButton(
    selection: SegmentIdType,
    onSegmentSelectionChange: (selectedSegment: SegmentIdType) -> Unit,
    modifier: Modifier = Modifier,
    segments: SegmentedButtonScope<SegmentIdType>.() -> Unit
) {

    val segmentData = remember(segments) {
        val builder = SegmentedButtonScopeImpl<SegmentIdType>()
        builder.segments()
        return@remember builder.segmentData
    }

    val selectedSegmentIndex = remember(selection, segments) {
        val index = segmentData
            .find { it.segmentID == selection }
            ?.segmentIndex
         return@remember index ?: 0
    }

    SegmentedButtonLayout(
        segments = {
            segmentData.forEach { segmentData ->
                ButtonSegment(
                    segmentData = segmentData,
                    isSelected = segmentData.segmentID == selection,
                    onClick = { onSegmentSelectionChange(segmentData.segmentID) }
                )
            }
        },
        selectedSegmentIndex = selectedSegmentIndex,
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

//endregion

//region Layout / internal composables

@Composable
private fun SegmentedButtonLayout(
    segments: @Composable () -> Unit,
    selectionIndicator: @Composable () -> Unit,
    selectedSegmentIndex: Int,
    modifier: Modifier = Modifier
) {
    var numberOfSegments by remember(segments) { mutableIntStateOf(0) }
    var segmentMeasuredWidth by remember(segments) { mutableIntStateOf(0) }

    val selectionIndicatorOffset = remember(segments) {
        Animatable(
            initialValue = selectedSegmentIndex.toFloat() * segmentMeasuredWidth
        )
    }
    LaunchedEffect(selectedSegmentIndex) {
        selectionIndicatorOffset.animateTo(
            targetValue = selectedSegmentIndex.toFloat() * segmentMeasuredWidth
        )
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

@Composable
private fun <T> ButtonSegment(
    segmentData: SegmentData<T>,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val textWeight = remember(isSelected) {
        return@remember when (isSelected) {
            true -> FontWeight.Medium
            false -> FontWeight.Normal
        }
    }

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
            AnimatedContent(
                targetState = textWeight,
                transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                label = "segbtn_segment_text_anim"
            ) { weight ->
                Text(
                    text = segmentData.text,
                    fontSize = 14.sp,
                    fontWeight = weight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

//endregion

//region Segment builder

/**
 * A receiver scope for the [SegmentedButton]. Through this, you can
 * add segments to the segmented button.
 */
interface SegmentedButtonScope<SegmentIdType> {

    /**
     * Adds a segment with the provided [text] as its content.
     *
     * @param text The text that will be displayed in that segment
     * @param segmentID The unique identifier that is used for this segment.
     * Can be of any type, but all of the segments must share the same ID type.
     * Usually you'd want an enum value as the ID for the segment, but you can
     * also use something like a simple integer to identify your segments.
     */
    fun segmentWithText(text: String, segmentID: SegmentIdType)

    /**
     * Adds a segment with the provided icon as its content. This variant
     * takes a [Painter] as the param to draw the icon, for [ImageVector]-based
     * icons, use the another overload.
     *
     * @param painter The painter that will be used to draw the icon that will
     * be displayed in that segment
     * @param segmentID The unique identifier that is used for this segment.
     * Can be of any type, but all of the segments must share the same ID type.
     * Usually you'd want an enum value as the ID for the segment, but you can
     * also use something like a simple integer to identify your segments.
     */
    fun segmentWithIcon(painter: Painter, segmentID: SegmentIdType)

    /**
     * Adds a segment with the provided icon as its content. This variant
     * takes an [ImageVector] as the param to draw the icon, for [Painter]-based
     * icons, use the another overload.
     *
     * @param imageVector The image vector that will be used to draw the icon that
     * will be displayed in that segment
     * @param segmentID The unique identifier that is used for this segment.
     * Can be of any type, but all of the segments must share the same ID type.
     * Usually you'd want an enum value as the ID for the segment, but you can
     * also use something like a simple integer to identify your segments.
     */
    fun segmentWithIcon(imageVector: ImageVector, segmentID: SegmentIdType)

    /**
     * Adds a segment with the provided icon and text as its content. This variant
     * takes a [Painter] as the param to draw the icon, for [ImageVector]-based
     * icons, use the another overload.
     *
     * @param painter The painter that will be used to draw the icon that will
     * be displayed in that segment
     * @param text The text that will be displayed in that segment
     * @param segmentID The unique identifier that is used for this segment.
     * Can be of any type, but all of the segments must share the same ID type.
     * Usually you'd want an enum value as the ID for the segment, but you can
     * also use something like a simple integer to identify your segments.
     */
    fun segmentWithIconAndText(painter: Painter, text: String, segmentID: SegmentIdType)

    /**
     * Adds a segment with the provided icon and text as its content. This variant
     * takes an [ImageVector] as the param to draw the icon, for [Painter]-based
     * icons, use the another overload.
     *
     * @param imageVector The image vector that will be used to draw the icon that
     * will be displayed in that segment
     * @param text The text that will be displayed in that segment
     * @param segmentID The unique identifier that is used for this segment.
     * Can be of any type, but all of the segments must share the same ID type.
     * Usually you'd want an enum value as the ID for the segment, but you can
     * also use something like a simple integer to identify your segments.
     */
    fun segmentWithIconAndText(imageVector: ImageVector, text: String, segmentID: SegmentIdType)
}

private data class SegmentData<SegmentIdType>(
    val text: String? = null,
    val iconPainter: Painter? = null,
    val iconVector: ImageVector? = null,
    val segmentID: SegmentIdType,
    val segmentIndex: Int
)

private class SegmentedButtonScopeImpl<SegmentIdType> : SegmentedButtonScope<SegmentIdType> {

    private val _segmentData = mutableListOf<SegmentData<SegmentIdType>>()
    val segmentData get() = _segmentData.toList()

    override fun segmentWithText(text: String, segmentID: SegmentIdType) {
        addSegment(segmentID = segmentID, text = text)
    }

    override fun segmentWithIcon(painter: Painter, segmentID: SegmentIdType) {
        addSegment(segmentID = segmentID, painter = painter)
    }

    override fun segmentWithIcon(imageVector: ImageVector, segmentID: SegmentIdType) {
        addSegment(segmentID = segmentID, imageVector = imageVector)
    }

    override fun segmentWithIconAndText(
        painter: Painter,
        text: String,
        segmentID: SegmentIdType
    ) {
        addSegment(
            segmentID = segmentID,
            text = text,
            painter = painter
        )
    }

    override fun segmentWithIconAndText(
        imageVector: ImageVector,
        text: String,
        segmentID: SegmentIdType
    ) {
        addSegment(
            segmentID = segmentID,
            text = text,
            imageVector = imageVector
        )
    }

    private fun addSegment(
        segmentID: SegmentIdType,
        text: String? = null,
        painter: Painter? = null,
        imageVector: ImageVector? = null
    ) {
        val segmentIndex = _segmentData.size

        _segmentData.add(
            SegmentData(
                segmentIndex = segmentIndex,
                segmentID = segmentID,
                text = text,
                iconPainter = painter,
                iconVector = imageVector
            )
        )
    }
}

//endregion

//region Previews

//--- Int-typed segment button preview ---//

@Preview
@Composable
private fun SegmentedButtons_Preview_IntTyped() {

    val selection = remember { mutableIntStateOf(0) }

    AppTheme {
        PreviewBox {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SegmentedButton(
                    selection = selection,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    segmentWithText("Lorem", 0)
                    segmentWithText("Ipsum", 1)
                    segmentWithText("Dolor", 2)
                }

                Spacer(16.dp)

                Text("Selected segment: ${selection.value}")
            }
        }
    }
}


//--- Enum-typed segment button preview ---//

private enum class PreviewSegment(val textDescription: String) {
    FIRST_SEGMENT("First"),
    SECOND_SEGMENT("Second")
}

@Preview
@Composable
private fun SegmentedButtons_Preview_EnumTyped() {

    val selection = remember { mutableStateOf(PreviewSegment.FIRST_SEGMENT) }

    AppTheme {
        PreviewBox {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SegmentedButton(
                    selection = selection,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    segmentWithText("Lorem", PreviewSegment.FIRST_SEGMENT)
                    segmentWithText("Ipsum", PreviewSegment.SECOND_SEGMENT)
                }

                Spacer(16.dp)

                Text("Selected segment: ${selection.value.textDescription}")
            }
        }
    }
}

//endregion
