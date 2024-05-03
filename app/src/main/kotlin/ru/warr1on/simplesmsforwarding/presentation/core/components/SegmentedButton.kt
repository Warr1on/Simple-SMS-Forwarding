package ru.warr1on.simplesmsforwarding.presentation.core.components

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
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
 * a screen state data class, or if you want to be able to veto the selection change),
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
    colorScheme: SegmentedButtonColorScheme = SegmentedButtonColorScheme.colorScheme(),
    segments: SegmentedButtonScope<SegmentIdType>.() -> Unit
) {
    SegmentedButton(
        selection = selection.value,
        onSegmentSelectionChange = { selection.value = it },
        segments = segments,
        modifier = modifier,
        colorScheme = colorScheme
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
    colorScheme: SegmentedButtonColorScheme = SegmentedButtonColorScheme.colorScheme(),
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

    val density = LocalDensity.current

    SegmentedButtonLayout(
        segments = {
            segmentData.forEach { segmentData ->
                ButtonSegment(
                    segmentData = segmentData,
                    isSelected = segmentData.segmentID == selection,
                    onClick = { onSegmentSelectionChange(segmentData.segmentID) },
                    contentColor = colorScheme.contentColor,
                    selectedContentColor = colorScheme.selectedContentColor
                )
            }
        },
        separators = {
            for (index in 1 until segmentData.size) {
                SegmentSeparator(
                    // The separator should be visible only if it's
                    // not adjacent to the currently selected segment
                    isVisible = (index != selectedSegmentIndex + 1) && (index != selectedSegmentIndex),
                    color = colorScheme.separatorColor
                )
            }
        },
        selectedSegmentIndex = selectedSegmentIndex,
        selectionIndicator = {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        shape = RoundedCornerShape(8.dp)
                        shadowElevation = with(density) { 3.dp.toPx() }
                    }
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.selectionIndicatorColor)
            )
        },
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colorScheme.backgroundColor)
            .padding(4.dp)
    )
}

//endregion

//region Layout / internal composables

@Composable
private fun SegmentedButtonLayout(
    segments: @Composable () -> Unit,
    separators: @Composable () -> Unit,
    selectionIndicator: @Composable () -> Unit,
    selectedSegmentIndex: Int,
    modifier: Modifier = Modifier,
) {
    var numberOfSegments by remember(segments) { mutableIntStateOf(0) }
    var calculatedSegmentWidth by remember(segments) { mutableIntStateOf(0) }

    val selectionIndicatorOffset = remember(segments) {
        Animatable(
            initialValue = selectedSegmentIndex.toFloat() * calculatedSegmentWidth
        )
    }
    LaunchedEffect(selectedSegmentIndex) {
        selectionIndicatorOffset.animateTo(
            targetValue = selectedSegmentIndex.toFloat() * calculatedSegmentWidth
        )
    }

    val density = LocalDensity.current

    SubcomposeLayout(
        modifier = modifier
    ) { constraints ->

        // Get the segment measurables
        val segmentMeasurables = subcompose(1, segments)
        // Calculate the segment width
        val segmentWidth = segmentMeasurables.let { measurables ->
            if (measurables.isEmpty()) return@let 0
            val largestSegmentWidth = measurables.maxOfOrNull { it.minIntrinsicWidth(0) } ?: 0
            largestSegmentWidth.coerceAtLeast(constraints.minWidth / segmentMeasurables.size)
        }
        // Measure the segments
        val segmentPlaceables = segmentMeasurables.map {
            it.measure(constraints.copy(maxWidth = segmentWidth, minWidth = segmentWidth))
        }

        // Set the values necessary for the selection indicator's animation to work
        numberOfSegments = segmentMeasurables.size
        calculatedSegmentWidth = segmentWidth

        // Calculate the whole layout's width and height
        val layoutWidth = (segmentWidth * segmentPlaceables.size)
        val layoutHeight = when (segmentPlaceables.size) {
            0 -> 0
            else -> segmentPlaceables[0].height
        }

        // Get and measure the selection indicator. Selection indicator is assumed
        // to be just a single composable, and it's provided by the parent composable,
        // so we can directly access it by calling `first` on the list of placeables,
        // as we know for sure that it will be there.
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

        // Setting up the separator layout values
        val separatorPadding = 8.dp
        val separatorWidth = with(density) { 1.dp.toPx().toInt() }
        val separatorHeight = with(density) {
            val padding = separatorPadding.toPx().toInt()
            layoutHeight - padding
        }
        val separatorYOffset = with(density) {
            (separatorPadding / 2).toPx().toInt()
        }
        // Measuring the segment separators
        val separatorPlaceables = subcompose(3, separators).map {
            it.measure(
                constraints.copy(
                    minWidth = separatorWidth,
                    maxWidth = segmentWidth,
                    minHeight = separatorHeight,
                    maxHeight = separatorHeight
                )
            )
        }

        // Laying out all of the measured placeables
        layout(
            width = layoutWidth,
            height = layoutHeight
        ) {
            segmentPlaceables.forEachIndexed { index, placeable ->
                placeable.place(
                    x = index * segmentWidth,
                    y = 0,
                    zIndex = 1f
                )
            }

            separatorPlaceables.forEachIndexed { index, placeable ->
                placeable.place(
                    x = (index + 1) * segmentWidth,
                    y = separatorYOffset,
                    zIndex = 0.1f // Separators are on the lowest Z level
                )
            }

            selectionIndicatorPlaceable.place(
                x = selectionIndicatorOffset.value.toInt(),
                y = 0,
                zIndex = 0.2f // Below the segments but above the separators
            )
        }
    }
}

@Composable
private fun <T> ButtonSegment(
    segmentData: SegmentData<T>,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color,
    selectedContentColor: Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    val colorScheme = MaterialTheme.colorScheme

    val hasIconAndText = remember(segmentData) {
        (segmentData.text != null)
            && (segmentData.iconPainter != null || segmentData.iconVector != null)
    }

    val textWeight = remember(isSelected) {
        when (isSelected) {
            true -> FontWeight.Medium
            false -> FontWeight.Normal
        }
    }

    val iconScaleFor: (isSelected: Boolean) -> Float = remember {
        { if (it) 1f else 0.9f }
    }
    val iconScale = remember { Animatable(iconScaleFor(isSelected)) }
    LaunchedEffect(isSelected) {
        iconScale.animateTo(
            targetValue = iconScaleFor(isSelected),
            animationSpec = spring(dampingRatio = 0.7f, stiffness = 200f)
        )
    }

    val contentColorFor: (isSelected: Boolean) -> Color = remember(contentColor, selectedContentColor) {
        { if (it) selectedContentColor else contentColor }
    }
    val animatedContentColor = remember(contentColor, selectedContentColor) {
        Animatable(contentColorFor(isSelected))
    }
    LaunchedEffect(isSelected) {
        animatedContentColor.animateTo(contentColorFor(isSelected))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onClickLabel = segmentData.contentDescription,
                role = Role.Button
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        if (segmentData.iconPainter != null) {
            Icon(
                painter = segmentData.iconPainter,
                contentDescription = null,
                tint = animatedContentColor.value,
                modifier = Modifier
                    .size(24.dp)
                    .scale(iconScale.value)
            )
        } else if (segmentData.iconVector != null) {
            Icon(
                imageVector = segmentData.iconVector,
                contentDescription = null,
                tint = animatedContentColor.value,
                modifier = Modifier
                    .size(24.dp)
                    .scale(iconScale.value)
            )
        }

        if (hasIconAndText) {
            Spacer(4.dp)
        }

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
                    overflow = TextOverflow.Ellipsis,
                    color = animatedContentColor.value
                )
            }
        }
    }
}

@Composable
private fun SegmentSeparator(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    color: Color
) {
    val alphaFor: (isVisible: Boolean) -> Float = remember {
        { if (it) 1f else 0f }
    }
    val alpha = remember { Animatable(alphaFor(isVisible)) }
    LaunchedEffect(isVisible) {
        alpha.animateTo(alphaFor(isVisible))
    }

    Box(
        modifier = modifier
            .alpha(alpha.value)
            .background(color.copy(alpha = 0.3f))
    )
}

//endregion

//region Color scheme

/**
 * A color scheme for the [SegmentedButton].
 *
 * To create an instance with default values from your
 * MaterialTheme, use the [colorScheme] companion function.
 *
 * @property backgroundColor The segmented button's background color.
 * @property selectionIndicatorColor Color of the segmented button's selection indicator.
 * @property separatorColor Color of the separators between segmented button's segments.
 * Do note that the actual color will be less pronounced than the specified color, due
 * to its alpha being reduced to 0.3 of original - that is done to make the separator
 * more subtle and less standing out.
 * @property contentColor Color of the segmented button's segment content when it is not
 * selected. For this value you should use the color that would look good on the [backgroundColor].
 * For setting the selected segment's content color, use the [selectedContentColor] param.
 * @property selectedContentColor Color of the segmented button's segment content when it
 * is selected. For this value you should use the color that would look good on the
 * [selectionIndicatorColor].
 */
@Immutable
data class SegmentedButtonColorScheme(
    val backgroundColor: Color,
    val selectionIndicatorColor: Color,
    val separatorColor: Color,
    val contentColor: Color,
    val selectedContentColor: Color,
) {

    companion object {

        /**
         * Creates a color scheme for the [SegmentedButton].
         *
         * By default, without providing the specific value for a parameter, it will try
         * to get the default value from the Material3's [MaterialTheme]. If you do not
         * use the [MaterialTheme], you should provide your own value for each parameter.
         *
         * @param backgroundColor The segmented button's background color.
         * @param selectionIndicatorColor Color of the segmented button's selection indicator.
         * @param separatorColor Color of the separators between segmented button's segments.
         * Do note that the actual color will be less pronounced than the specified color, due
         * to its alpha being reduced to 0.3 of original - that is done to make the separator
         * more subtle and less standing out.
         * @param contentColor Color of the segmented button's segment content when it is not
         * selected. For this value you should use the color that would look good on the
         * [backgroundColor]. For setting the selected segment's content color, use the
         * [selectedContentColor] param.
         * @param selectedContentColor Color of the segmented button's segment content when it
         * is selected. For this value you should use the color that would look good on the
         * [selectionIndicatorColor].
         */
        @Composable
        fun colorScheme(
            backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
            selectionIndicatorColor: Color = MaterialTheme.colorScheme.background,
            separatorColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
            contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedContentColor: Color = MaterialTheme.colorScheme.onBackground,
        ): SegmentedButtonColorScheme {
            return SegmentedButtonColorScheme(
                backgroundColor = backgroundColor,
                selectionIndicatorColor = selectionIndicatorColor,
                separatorColor = separatorColor,
                contentColor = contentColor,
                selectedContentColor = selectedContentColor
            )
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
     * @param contentDescription Content description that is used for accessibility
     * purposes
     * @param segmentID The unique identifier that is used for this segment.
     * Can be of any type, but all of the segments must share the same ID type.
     * Usually you'd want an enum value as the ID for the segment, but you can
     * also use something like a simple integer to identify your segments.
     */
    fun segmentWithIcon(
        painter: Painter,
        contentDescription: String?,
        segmentID: SegmentIdType
    )

    /**
     * Adds a segment with the provided icon as its content. This variant
     * takes an [ImageVector] as the param to draw the icon, for [Painter]-based
     * icons, use the another overload.
     *
     * @param imageVector The image vector that will be used to draw the icon that
     * will be displayed in that segment
     * @param contentDescription Content description that is used for accessibility
     * purposes
     * @param segmentID The unique identifier that is used for this segment.
     * Can be of any type, but all of the segments must share the same ID type.
     * Usually you'd want an enum value as the ID for the segment, but you can
     * also use something like a simple integer to identify your segments.
     */
    fun segmentWithIcon(
        imageVector: ImageVector,
        contentDescription: String?,
        segmentID: SegmentIdType
    )

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
    fun segmentWithIconAndText(
        painter: Painter,
        text: String,
        segmentID: SegmentIdType
    )

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
    fun segmentWithIconAndText(
        imageVector: ImageVector,
        text: String,
        segmentID: SegmentIdType
    )
}

private data class SegmentData<SegmentIdType>(
    val text: String? = null,
    val iconPainter: Painter? = null,
    val iconVector: ImageVector? = null,
    val contentDescription: String? = text,
    val segmentID: SegmentIdType,
    val segmentIndex: Int
)

private class SegmentedButtonScopeImpl<SegmentIdType> : SegmentedButtonScope<SegmentIdType> {

    private val _segmentData = mutableListOf<SegmentData<SegmentIdType>>()
    val segmentData get() = _segmentData.toList()

    override fun segmentWithText(text: String, segmentID: SegmentIdType) {
        addSegment(segmentID = segmentID, text = text)
    }

    override fun segmentWithIcon(
        painter: Painter,
        contentDescription: String?,
        segmentID: SegmentIdType
    ) {
        addSegment(
            segmentID = segmentID,
            painter = painter,
            contentDescription = contentDescription
        )
    }

    override fun segmentWithIcon(
        imageVector: ImageVector,
        contentDescription: String?,
        segmentID: SegmentIdType
    ) {
        addSegment(
            segmentID = segmentID,
            imageVector = imageVector,
            contentDescription = contentDescription
        )
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
        imageVector: ImageVector? = null,
        contentDescription: String? = null,
    ) {
        val segmentIndex = _segmentData.size

        _segmentData.add(
            SegmentData(
                segmentIndex = segmentIndex,
                segmentID = segmentID,
                text = text,
                iconPainter = painter,
                iconVector = imageVector,
                contentDescription = contentDescription
            )
        )
    }
}

//endregion

//region Previews

//--- Int-typed segment button preview ---//

@Preview
@Composable
private fun SegmentedButton_Preview_IntTyped() {

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
private fun SegmentedButton_Preview_EnumTyped() {

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


//--- Icon-based segment button preview ---//

@Preview
@Composable
private fun SegmentedButton_Preview_IconSegments() {

    val selection = remember { mutableIntStateOf(0) }

    val firstIcon = remember { Icons.Filled.Favorite }
    val secondIcon = remember { Icons.Filled.Email }
    val thirdIcon = remember { Icons.Filled.Home }

    AppTheme {
        PreviewBox {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SegmentedButton(
                    selection = selection,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    segmentWithIcon(firstIcon, null, 0)
                    segmentWithIcon(secondIcon, null, 1)
                    segmentWithIcon(thirdIcon, null, 2)
                    //segmentWithIconAndText(Icons.Outlined.AccountCircle, "Home", 3)
                }

                Spacer(16.dp)

                Text("Selected segment: ${selection.value}")
            }
        }
    }
}


//--- Compact segment button preview ---//

@Preview
@Composable
private fun SegmentedButton_Preview_Compact() {

    val selection = remember { mutableIntStateOf(0) }

    val firstIcon = remember { Icons.Filled.Favorite }
    val secondIcon = remember { Icons.Filled.Email }
    val thirdIcon = remember { Icons.Filled.Home }

    AppTheme {
        PreviewBox {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SegmentedButton(
                    selection = selection,
                    modifier = Modifier
                ) {
                    segmentWithIcon(firstIcon, null, 0)
                    segmentWithIcon(secondIcon, null, 1)
                    segmentWithIcon(thirdIcon, null, 2)
                }

                Spacer(16.dp)

                Text("Selected segment: ${selection.value}")
            }
        }
    }
}

@Preview
@Composable
private fun SegmentedButton_Preview_CustomColors() {

    val selection = remember { mutableIntStateOf(0) }

    AppTheme {
        PreviewBox {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SegmentedButton(
                    selection = selection,
                    modifier = Modifier.fillMaxWidth(),
                    colorScheme = SegmentedButtonColorScheme(
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                        selectionIndicatorColor = MaterialTheme.colorScheme.tertiary,
                        separatorColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        selectedContentColor = MaterialTheme.colorScheme.onTertiary
                    )
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

//endregion
