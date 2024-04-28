package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.simplemobiletools.smsmessenger.R
import ru.warr1on.simplesmsforwarding.presentation.core.components.FwdDefaultIconButton
import ru.warr1on.simplesmsforwarding.presentation.core.components.FwdDefaultPresetCard
import ru.warr1on.simplesmsforwarding.presentation.core.components.Spacer
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme
import ru.warr1on.simplesmsforwarding.presentation.shared.PresentationModel
import java.util.UUID

@Composable
fun ForwardingRuleEditorFilterView(
    filter: PresentationModel.ForwardingFilter,
    onEditFilter: (filterID: String) -> Unit,
    onRemoveFilter: (filterID: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filterTypeTextValue = remember(filter) {
        when (filter.filterType) {
            PresentationModel.ForwardingFilter.FilterType.INCLUDE -> "Includes:"
            PresentationModel.ForwardingFilter.FilterType.EXCLUDE -> "Excludes:"
        }
    }
    val ignoresCaseTextValue = remember(filter) {
        when (filter.ignoreCase) {
            true -> "(Ignoring case)"
            false -> "(Respecting case)"
        }
    }

    FwdDefaultPresetCard(
        elevation = 1.dp,
        disableShadow = true,
        useOutlineForDarkTheme = false,
        modifier = modifier
    ) {
        ConstraintLayout(Modifier.fillMaxWidth()) {

            val (
                filterTypeText,
                ignoresCaseText,
                filterText,
                actionButton
            ) = createRefs()

            val startGuideline = createGuidelineFromStart(16.dp)
            val endGuideline = createGuidelineFromEnd(16.dp)
            val topGuideline = createGuidelineFromTop(12.dp)
            val bottomGuideline = createGuidelineFromBottom(12.dp)

            val supportingTextChain = createHorizontalChain(
                filterTypeText,
                ignoresCaseText,
                chainStyle = ChainStyle.SpreadInside
            )

            val supportingTextBarrier = createBottomBarrier(filterTypeText, ignoresCaseText)

            constrain(supportingTextChain) {
                start.linkTo(startGuideline)
                end.linkTo(actionButton.start)
            }

            Text(
                text = filterTypeTextValue,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .constrainAs(filterTypeText) {
                        top.linkTo(topGuideline)
                    }
            )

            Text(
                text = ignoresCaseTextValue,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .constrainAs(ignoresCaseText) {
                        centerVerticallyTo(filterTypeText)
                    }
            )

            Text(
                text = filter.text,
                modifier = Modifier
                    .constrainAs(filterText) {
                        start.linkTo(startGuideline)
                        end.linkTo(endGuideline)
                        bottom.linkTo(bottomGuideline)
                        top.linkTo(supportingTextBarrier, margin = 6.dp)
                        width = Dimension.fillToConstraints
                    }
            )

            FilterActionButtonWithDropdownMenu(
                onEditClicked = { onEditFilter(filter.id) },
                onDeleteClicked = { onRemoveFilter(filter.id) },
                modifier = Modifier
                    .constrainAs(actionButton) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                    }
            )
        }
    }
}

@Composable
private fun FilterActionButtonWithDropdownMenu(
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDropdownMenuExpanded by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
    ) {
        FwdDefaultIconButton(
            painter = painterResource(id = R.drawable.ic_more_vertical),
            contentDescription = "Filter action menu",
            color = MaterialTheme.colorScheme.primary,
            onClick = { isDropdownMenuExpanded = true }
        )

        DropdownMenu(
            expanded = isDropdownMenuExpanded,
            onDismissRequest = { isDropdownMenuExpanded = false }
        ) {
            DropdownMenuItem(
                text = "Edit",
                iconID = R.drawable.ic_edit,
                color = MaterialTheme.colorScheme.primary,
                onClick = onEditClicked
            )
            DropdownMenuItem(
                text = "Delete",
                iconID = R.drawable.ic_delete,
                color = MaterialTheme.colorScheme.error,
                onClick = onDeleteClicked
            )
        }
    }
}

@Composable
private fun DropdownMenuItem(
    text: String,
    iconID: Int,
    color: Color,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = iconID),
                    contentDescription = null
                )

                Spacer(8.dp)

                Text(
                    text = text,
                    color = color
                )
            }
        },
        onClick = onClick
    )
}

//region Previews

@Preview
@Composable
private fun ForwardingRuleEditorFilterView_Preview() {

    val shortFilterText = "Lorem ipsum dolor amet"
    val longFilterText = "Lorem ipsum dolor amet. Dolorem veniam quisquam ut officiis qui nihil et optio"

    val filter = PresentationModel.ForwardingFilter(
        id = UUID.randomUUID().toString(),
        filterType = PresentationModel.ForwardingFilter.FilterType.INCLUDE,
        text = longFilterText,
        ignoreCase = false
    )

    AppTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            ForwardingRuleEditorFilterView(
                filter = filter,
                onEditFilter = {},
                onRemoveFilter = {}
            )
        }
    }
}

//endregion
