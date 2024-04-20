package ru.warr1on.simplesmsforwarding.presentation.core.components.modal

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.warr1on.simplesmsforwarding.presentation.core.components.modal.ModalPopupVisibilityState.*
import java.util.*

/**
 * Состояние видимости модального поп-апа
 */
enum class ModalPopupVisibilityState {
    /** Поп-ап скрыт */
    HIDDEN,
    /** Поп-ап видим */
    SHOWN
}

/**
 * Скоуп модального поп-апа, доступный его контенту.
 * Позволяет управлять поп-апом внутри контента
 */
interface ModalPopupScope {

    /**
     * Состояние видимости поп-апа. С помощью него можно
     * делать анимации контента при показе/скрытии поп-апа.
     *
     * Данное состояние отражает targetState, а не currentState,
     * т.е. то, к какому состоянию поп-ап сейчас стремится, а не то,
     * в каком он фактически уже находится.
     */
    val visibilityState: StateFlow<ModalPopupVisibilityState>

    /**
     * Закрывает поп-ап.
     *
     * @param stateSyncRequired Флаг, указывающий на то, необходима ли синхронизация
     * состояний между поп-апом и родительским композаблом при дизмиссе поп-апа.
     * Проще говоря, если этот флаг - true, то родителю в коллбэке onDismissed нужно
     * позаботиться о том, чтобы сменить свое состояние таким образом, чтобы [ModalPopup]
     * вышел из композиции. Если же флаг - false, то значит, что ничего делать не нужно
     * и состояние уже синхронизировано с родителем. Такое может быть в случае, когда, к
     * примеру, внутри поп-апа его контент может совершить какое-либо действие, которое
     * приведет к обновлению состояния прежде чем у [ModalPopup] будет вызван onDismissed,
     * и особенно тогда, когда такое обновление состояния противоречит тому, что будет
     * осуществляться в onDismissed. В таких случаях сюда нужно передать false.
     * True по умолчанию.
     */
    fun dismiss(stateSyncRequired: Boolean = true)
}

private class ModalPopupScopeInstance(
    private val coroutineScope: CoroutineScope,
    private val popupState: ModalPopupState
) : ModalPopupScope {

    override val visibilityState = popupState.currentVisibilityState

    override fun dismiss(stateSyncRequired: Boolean) {
        coroutineScope.launch {
            popupState.requireStateSyncOnDismissal = stateSyncRequired
            popupState.dismiss()
        }
    }
}

/**
 * Модалка-попап для отображения различных диалогов, алертов и подобного.
 * Позволяет отображать контент поверх основного UI приложения.
 *
 * Поп-ап будет показываться до тех пор, пока верно одно из нижеперечисленных условий:
 * - [ModalPopup] остается в композиции
 * - Поп-ап не был закрыт скоуп-функцией [ModalPopupScope.dismiss]
 *
 *
 * Отображение такой модалки должно зависеть от какого-либо состояния,
 * поэтому после дизмисса [ModalPopup] родителю нужно позаботиться о том, чтобы
 * соответственно сменить свое состояние и убрать композабл модалки из композиции,
 * для чего следует использовать коллбэк [onDismissed], вызывающийся после завершения
 * анимаций, в момент, когда модалка готова к окончательному выходу из композиции.
 *
 * Если состояния родителя и поп-апа уже синхронизируются в другом месте
 * (i.e. в композабле родителя или контента поп-апа), то, чтобы [onDismissed] об этом узнал,
 * в [ModalPopupScope.dismiss] при запросе дизмисса следует передать соответствующий
 * флаг. Это позволит коллбэку [onDismissed] понимать, когда ему нужно синхронизировать
 * состояние с родителем поп-апа, а когда - нет.
 *
 * Также возможен вариант с использованием [ModalPopup] без явного вызова
 * [ModalPopupScope.dismiss]. В таком случае, дизмисс будет произведен автоматически
 * как только [ModalPopup] покинет композицию. Если попап покинул композицию по причине
 * смены стейта родителя (как быть и должно), то, чтобы избежать повторной смены стейта
 * в вызывающемся после дизмисса коллбэке [onDismissed], родитель должен смотреть на флаг
 * dismissedInternally, передающийся в коллбэк и несущий информацию о том, что вызвало
 * дизмисс: сам попап или что-либо извне. Это позволит родителю определить, является ли
 * закрытие поп-апа сайд-эффектом для его родителя (в таком случае dismissedInternally = true,
 * а родителю нужно позаботиться о том, чтобы синхронизировать свое состояние с этим
 * сайд-эффектом), либо же закрытие поп-апа является следствием изменения состояния у
 * самого родителя (в таком случае dismissedInternally = false и родитель уже обновил свое
 * состояние)
 *
 *
 * ВАЖНО:
 *
 * Модальные поп-апы отображаются в порядке очереди. Это значит, что если в композиции
 * будут находиться, к примеру, сразу два [ModalPopup], то второй не будет показан пока
 * первый не будет дизмисснут
 *
 * Использование данного компонента предполагает обязательное использование
 * [ModalHostExperimental] на высшем уровне иерархии композаблов
 *
 * @see [ModalHostExperimental]
 *
 * @param onDismissed Блок, вызывающийся после того как поп-ап был закрыт и его UI убран.
 * Внутри вернется флаг dismissedInternally, несущий информацию о том, был ли дизмисс
 * поп-апа вызван самим компонентом [ModalPopup] (либо по тапу за пределами поп-апа, либо
 * через обработчик системной кнопки "назад"), либо же инициирован чем-то за его пределами
 * через [ModalPopupScope].
 * stateSyncRequired, сигнализирующий о том, нужно ли
 * родителю синхронизировать состояние с поп-апом в связи с его дизмиссом
 * из композиции в [ModalHostExperimental]. Здесь нужно менять состояние родителя для того,
 * чтобы родитель окончательно выкинул [ModalPopup] из композиции
 * @param isDismissibleOnOutsideAction Данный флаг определяет, можно ли закрыть поп-ап
 * нажатием за его пределами либо системной кнопкой "назад"
 * @param key Ключ, идентифицирующий конкретно данный поп-ап
 * @param shape Форма поверхности, на которой будет отрисовываться контент поп-апа
 * @param modifier Модификатор, применяемый к поверхности, на которой рисуется поп-ап
 * @param content Контент поп-апа. Имеет доступ к [ModalPopupScope]
 */
@Composable
fun ModalPopup(
    onDismissed: (dismissedInternally: Boolean, stateSyncRequired: Boolean) -> Unit,
    isDismissibleOnOutsideAction: Boolean = true,
    key: String = remember { UUID.randomUUID().toString() },
    shape: Shape = RoundedCornerShape(24.dp),
    modifier: Modifier = Modifier,
    content: @Composable ModalPopupScope.() -> Unit
) {
    val modalHost = LocalModalHost.current
    val popupState = rememberModalPopupState(key)
    val coroutineScope = LocalModalScope.current
    val popupScopeInstance = remember(key) {
        ModalPopupScopeInstance(coroutineScope, popupState)
    }

    // Тут формируется модалка, которая в дальнейшем
    // будет передана в хост для отображения
    val modal: @Composable HostedModalData.() -> Unit = {

        // Текущее состояние видимости поп-апа
        val popupVisibilityState by popupState.currentVisibilityState.collectAsState()

        // Этот стейт используется анимацией показа/скрытия поп-апа
        val transitionState = remember(key) {
            MutableTransitionState(popupVisibilityState == SHOWN)
        }
        transitionState.targetState = popupVisibilityState == SHOWN

        // Этот флаг устанавливается когда поп-ап должен закрыться
        val shouldDismiss = popupState.shouldDismiss
        // Флаг, сигнализирующий о том, что дизмисс был совершен изнутри
        // самим поп-апом (тапом по диммеру или системным хендлером "назад")
        var dismissedInternally by remember { mutableStateOf(false) }

        // При начальной композиции запускаем анимацию всплывания поп-апа
        LaunchedEffect(key) {
            popupState.show()
        }

        // Обработчик нажатий системной кнопки "назад". Нажатие позволит
        // закрыть поп-ап если стоит флаг isDismissibleOnOutsideAction
        BackHandler {
            if (isDismissibleOnOutsideAction) {
                dismissedInternally = true
                popupState.shouldDismiss = true
            }
        }

        // Запускается, когда нужно закрыть поп-ап. Сначала анимированно
        // скрывает его, затем удаляет из хоста и вызывает коллбэк onDismissed,
        // в котором родитель будет должен удалить поп-ап из композиции
        if (shouldDismiss) {
            val modalData = this
            LaunchedEffect(key) {
                popupState.hide()
                modalData.dismiss()
                onDismissed(dismissedInternally, popupState.requireStateSyncOnDismissal)
            }
        }

        // Текущая фракция видимости поп-апа
        val fraction = popupState.visibilityFraction

        val dimmerAlpha = 0.3f * fraction

        // Диммер (скрим). Блокирует контент под поп-апом и, если стоит
        // флаг isDismissibleOnOutsideAction, позволяет по тапу на себя закрыть поп-ап
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(dimmerAlpha)
                .background(Color.Black)
                .clickable(enabled = isDismissibleOnOutsideAction) {
                    coroutineScope.launch {
                        dismissedInternally = true
                        popupScopeInstance.dismiss()
                    }
                }
        )

        // Контейнер поп-апа
        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .alpha(fraction.coerceAtLeast(0.2f))
        ) {
            // Поверхность, на которой будет рисоваться поп-ап
            Surface(
                tonalElevation = 2.dp,
                shadowElevation = 24.dp,
                shape = shape,
                color = MaterialTheme.colorScheme.surface,
                modifier = modifier
                    .requiredSizeIn(
                        minWidth = 0.dp,
                        minHeight = 0.dp,
                        maxWidth = maxWidth * 0.85f,
                        maxHeight = maxHeight * 0.8f
                    )
            ) {
                // Обеспечиваем анимацию поп-апа при показе/скрытии
                AnimatedVisibility(
                    visibleState = transitionState,
                    enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                ) {
                    popupScopeInstance.content()
                }
            }
        }
    }

    // После того, как мы сгенерировали композабл нашей модалки,
    // запускаем DisposableEffect, который отправит его в хост.
    //
    // Тут же, в onDispose, обеспечивается и то, что при покидании
    // композиции поп-ап анимированно задизмиссится
    DisposableEffect(key) {

        coroutineScope.launch {
            modalHost.showModalPopup(modal)
        }

        onDispose {
            popupState.shouldDismiss = true
        }
    }
}

/**
 * Состояние модального поп-апа
 *
 * @param initialValue Начальное значение видимости поп-апа
 */
private class ModalPopupState(
    initialValue: ModalPopupVisibilityState = HIDDEN
) {
    private val _currentVisibilityState = MutableStateFlow(initialValue)
    val currentVisibilityState = _currentVisibilityState.asStateFlow()

    private val visibilityAnimation = Animatable(if (initialValue == HIDDEN) 0f else 1f)
    var visibilityFraction by mutableStateOf(if (initialValue == HIDDEN) 0f else 1f)
        private set

    /**
     * Флаг, устанавливающийся когда поп-апу необходимо закрыться
     */
    var shouldDismiss: Boolean by mutableStateOf(false)

    /**
     * Флаг, говорящий о том, нужна ли синхронизация состояний между
     * поп-апом и его родителем в коллбэке onDismissed
     */
    var requireStateSyncOnDismissal: Boolean = true

    suspend fun show() {
        _currentVisibilityState.emit(SHOWN)
        visibilityAnimation.animateTo(1f) {
            visibilityFraction = this.value
        }
    }

    suspend fun hide() {
        _currentVisibilityState.emit(HIDDEN)
        visibilityAnimation.animateTo(0f) {
            visibilityFraction = this.value
        }
    }

    fun dismiss() {
        shouldDismiss = true
    }

    companion object {
        fun Saver(): Saver<ModalPopupState, *> = Saver(
            save = { it.currentVisibilityState.value },
            restore = { ModalPopupState(it) }
        )
    }
}

/**
 * Создает и запоминает [ModalPopupState]
 */
@Composable
private fun rememberModalPopupState(
    key: String,
    initialValue: ModalPopupVisibilityState = HIDDEN
): ModalPopupState {
    return rememberSaveable(
        inputs = arrayOf(key),
        saver = ModalPopupState.Saver()
    ) {
        ModalPopupState(initialValue = initialValue)
    }
}
