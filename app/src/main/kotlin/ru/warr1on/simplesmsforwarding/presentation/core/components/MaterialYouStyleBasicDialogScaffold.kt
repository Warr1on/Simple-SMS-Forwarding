package ru.warr1on.simplesmsforwarding.presentation.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.warr1on.simplesmsforwarding.presentation.core.model.ImmutableWrappedList
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

/**
 * A scaffold container for the Material 3 (Material You) styled
 * basic dialogs. Handles the layout and lets you set up things
 * like title header and action buttons without the boilerplate.
 *
 * @param modifier A modifier for the scaffold container
 * @param title Header title of the dialog
 * @param heroIcon An optional hero icon that would be displayed
 * above the dialog title. Note that if you provide an icon, the
 * header with the title and icon would be center-aligned, while
 * the header with just the title would be start-aligned.
 * @param actions Dialog actions. See [DialogActionBuilderScope]
 * on how to create actions.
 * @param actionPlacement (Optional) Through this you can specify
 * the placement of the action buttons inside the dialog. Without
 * providing the specific action placement, the scaffold will try
 * to determine the action placement by itself: for short-titled
 * actions, it will place them horizontally if there are two or
 * less actions, and will place them vertically if there are more
 * than 2 actions. For long-titled actions, it will place them
 * vertically.
 * @param content The content of the dialog
 */
@Composable
fun MaterialYouStyleBasicDialogScaffold(
    modifier: Modifier = Modifier,
    title: String,
    heroIcon: (@Composable () -> Unit)? = null,
    actions: DialogActionBuilderScope.() -> Unit,
    actionPlacement: BasicDialogActionPlacement? = null,
    content: @Composable () -> Unit
) {
    val resolvedActions = remember(actions) {
        val builder = ActionBuilder()
        builder.actions()
        return@remember builder.resolvedActions
    }

    val bottomPadding = remember(actions) {
        return@remember if (resolvedActions().isEmpty()) {
            24.dp
        } else {
            12.dp
        }
    }

    val headerAlignment: Alignment.Horizontal = remember(heroIcon) {
        when (heroIcon) {
            null -> Alignment.Start
            else -> Alignment.CenterHorizontally
        }
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .widthIn(min = 280.dp, max = 560.dp)
            .padding(
                top = 24.dp,
                start = 24.dp,
                end = 24.dp,
                bottom = bottomPadding
            )
    ) {
        if (heroIcon != null) {
            Box(modifier = Modifier.align(headerAlignment)) {
                heroIcon()
            }

            Spacer(16.dp)
        }

        Text(
            text = title,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(headerAlignment)
        )

        Spacer(16.dp)

        content()

        DialogActionsSection(
            actions = resolvedActions,
            actionPlacementOverride = actionPlacement,
            modifier = Modifier
                .align(Alignment.End)
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
private fun DialogActionsSection(
    actions: ImmutableWrappedList<DialogAction>,
    actionPlacementOverride: BasicDialogActionPlacement?,
    modifier: Modifier = Modifier
) {
    val unwrappedActions = actions()

    val actionPlacement = remember(actions) {
        actionPlacementOverride ?: actionPlacement(unwrappedActions)
    }

    val container: @Composable (content: @Composable () -> Unit) -> Unit = remember(actions) {
        return@remember {
            when (actionPlacement) {
                BasicDialogActionPlacement.HORIZONTAL -> {
                    Row(modifier = modifier) { it() }
                }
                BasicDialogActionPlacement.VERTICAL -> {
                    Column(
                        modifier = modifier,
                        horizontalAlignment = Alignment.End
                    ) { it() }
                }
            }
        }
    }

    val actionSpacer: @Composable () -> Unit = remember(actions) {
        return@remember {
            when (actionPlacement) {
                BasicDialogActionPlacement.HORIZONTAL -> { HorizontalSpacer(8.dp) }
                BasicDialogActionPlacement.VERTICAL -> { VerticalSpacer(8.dp) }
            }
        }
    }

    container {
        unwrappedActions.forEachIndexed { index, action ->

            val color = when (action.isDestructive) {
                false -> MaterialTheme.colorScheme.primary
                true -> MaterialTheme.colorScheme.error
            }

            FwdDefaultTextButton(
                text = action.title,
                color = color,
                onClick = action.onAction
            )

            if (index < unwrappedActions.size - 1) {
                actionSpacer()
            }
        }
    }
}

/**
 * The placement of the action buttons inside a basic dialog.
 * Can either be [HORIZONTAL] or [VERTICAL]
 */
enum class BasicDialogActionPlacement {
    HORIZONTAL, VERTICAL
}

private fun actionPlacement(actions: List<DialogAction>): BasicDialogActionPlacement {

    // If there are 3 or more actions, they should be placed vertically.
    // Generally, according to the Material guidelines, there shouldn't be
    // such a case, but i'll leave the choice there just in case anyway
    if (actions.size > 2) {
        return BasicDialogActionPlacement.VERTICAL
    }

    // If some action's title is too long or contains multiple words,
    // the actions should be placed vertically
    actions.forEach {
        if (it.title.count() > 15 || it.title.contains(" ")) {
            return BasicDialogActionPlacement.VERTICAL
        }
    }

    // Otherwise, actions should be placed horizontally
    return BasicDialogActionPlacement.HORIZONTAL
}

//region Actions builder

private data class DialogAction(
    val title: String,
    val isDestructive: Boolean = false,
    val onAction: () -> Unit
)

/**
 * The scope for the dialog action builder. Through this,
 * you can add actions to the [MaterialYouStyleBasicDialogScaffold]
 * by using [action] and [destructiveAction] functions.
 */
interface DialogActionBuilderScope {

    /**
     * Adds a new action for the [MaterialYouStyleBasicDialogScaffold].
     * For destructive actions, use the [destructiveAction] function.
     *
     * @param title The title text of the action that will be presented
     * on the action button. Use this to describe what the action will do
     * @param onAction The callback that would be called when the action
     * button would be clicked
     */
    fun action(
        title: String,
        onAction: () -> Unit
    )

    /**
     * Adds a new destructive action for the [MaterialYouStyleBasicDialogScaffold].
     * For normal actions, use the [action] function.
     *
     * @param title The title text of the action that will be presented
     * on the action button. Use this to describe what the action will do
     * @param onAction The callback that would be called when the action
     * button would be clicked
     */
    fun destructiveAction(
        title: String,
        onAction: () -> Unit
    )
}

private class ActionBuilder : DialogActionBuilderScope {

    private var _actions = mutableListOf<DialogAction>()
    val resolvedActions: ImmutableWrappedList<DialogAction>
        get() = ImmutableWrappedList.safeInit(_actions)

    override fun action(title: String, onAction: () -> Unit) {
        _actions.add(
            DialogAction(
                title = title,
                isDestructive = false,
                onAction = onAction
            )
        )
    }

    override fun destructiveAction(title: String, onAction: () -> Unit) {
        _actions.add(
            DialogAction(
                title = title,
                isDestructive = true,
                onAction = onAction
            )
        )
    }
}

//endregion

//region Previews

@Preview
@Composable
private fun MaterialYouStyleBasicDialogScaffold_Preview() {
    AppTheme {
        PreviewBox(backgroundColor = Color.Black) {

            MaterialYouStyleBasicDialogScaffold(
                title = "Dialog title",
                modifier = Modifier.clip(MaterialTheme.shapes.extraLarge),
                actions = {
                    destructiveAction("Discard") {}
                    action("Save") {}
                    //action("Long named action") {}
                }
            ) {
                Text(text = "Lorem ipsum dolor amet. Dolorem veniam quisquam ut officiis qui nihil et optio")
            }
        }
    }
}

@Preview
@Composable
private fun MaterialYouStyleBasicDialogScaffold_Preview_HeroIcon() {
    AppTheme {
        PreviewBox(backgroundColor = Color.Black) {

            MaterialYouStyleBasicDialogScaffold(
                title = "Dialog title",
                modifier = Modifier.clip(MaterialTheme.shapes.extraLarge),
                actions = {
                    destructiveAction("Destructive action") {}
                    action("Long named action") {}
                },
                heroIcon = {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            ) {
                Text(text = "Lorem ipsum dolor amet. Dolorem veniam quisquam ut officiis qui nihil et optio")
            }
        }
    }
}

//endregion
