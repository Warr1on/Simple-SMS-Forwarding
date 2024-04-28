@file:OptIn(ExperimentalMaterialApi::class)

package ru.warr1on.simplesmsforwarding.presentation.core.components.modal

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.warr1on.simplesmsforwarding.presentation.core.components.modal.BottomSheetVisibilityState.*
import java.util.*
import kotlin.math.roundToInt

/** Состояние видимости боттом шита */
enum class BottomSheetVisibilityState {
    /** Шит закрыт (полностью невидим) */
    CLOSED,
    /** Шит полностью раскрыт и показан */
    OPEN
}

/**
 * Скоуп модального боттом шита, доступный его контенту. Позволяет управлять шитом внутри контента
 * и получать доступ к текущей фракции видимости шита.
 */
interface ModalBottomSheetScope {
    
    /**
     * Текущая фракция видимости боттом шита. Может принимать значения от 0f до 1f,
     * где 0f - шит полностью скрыт, 1f - шит полностью видим. Данное значение может
     * использоваться его контентом для изменения UI в зависимости от степени видимости
     * шита.
     */
    var visibilityFraction: Float
    
    /** Анимированно разворачивает и показывает боттом шит */
    fun showBottomSheet()

    /** Анимированно скрывает боттом шит */
    fun hideBottomSheet()
    
    /** Закрывает боттом шит */
    fun dismiss()
}

private class ModalBottomSheetScopeInstance(
    private val coroutineScope: CoroutineScope,
    private val bottomSheetState: ModalBottomSheetStateExperimental
) : ModalBottomSheetScope {
    
    override var visibilityFraction: Float = bottomSheetState.visibilityFraction
    
    override fun showBottomSheet() {
        coroutineScope.launch { bottomSheetState.show() }
    }
    
    override fun hideBottomSheet() {
        coroutineScope.launch { bottomSheetState.close() }
    }
    
    override fun dismiss() {
        coroutineScope.launch { bottomSheetState.close() }
    }
    
}

/**
 * Экспериментальная версия модального боттом шита.
 * Позволяет показывать анимированный и свайпабельный боттом шит поверх основного контента
 * приложения.
 *
 * Боттом шит будет показываться до тех пор, пока:
 * - [ModalBottomSheetExperimental] остается в композиции
 * - шит не будет закрыт скоуп-функцией [ModalBottomSheetScope.dismiss] или действием юзера
 *
 * Отображение такой модалки должно зависеть от какого-либо состояния,
 * поэтому после дизмисса [ModalBottomSheetExperimental] родителю нужно позаботиться о том, чтобы
 * соответственно сменить свое состояние и убрать композабл модалки из композиции,
 * для чего следует использовать [onDismissed], вызывающийся после завершения
 * анимаций, в момент, когда модалка готова к окончательному выходу из композиции
 *
 * Использование данного компонента предполагает обязательное использование
 * [ModalHostExperimental] на высшем уровне иерархии композаблов
 *
 * @see [ModalHostExperimental]
 *
 * @param onDismissed Блок, вызывающийся после того как боттом шит был закрыт и его UI убран
 * из композиции в [ModalHostExperimental]. Здесь нужно менять состояние родителя для того,
 * чтобы родитель окончательно выкинул [ModalBottomSheetExperimental] из композиции
 * @param modifier Модификатор, применяемый к поверхности, на которой рисуется боттом шит
 * @param key Ключ, идентифицирующий конкретно данный боттом шит
 * @param title Текст, используемый в качестве заголовка модалки
 * @param header Шапка боттом шита. Создается шапка по умолчанию если задан [title],
 * в данном случае шапка будет содержать заголовок и кнопку для закрытия шита
 * @param sheetContent Контент, отображаемый в боттом шите
 */
@Composable
fun ModalBottomSheetExperimental(
    onDismissed: () -> Unit,
    modifier: Modifier = Modifier,
    key: String = remember { UUID.randomUUID().toString() },
    title: String? = null,
    header: (@Composable ModalBottomSheetScope.() -> Unit)? =
        if (title != null) {
            { ModalBottomSheetGenericHeader(title = title, closeAction = { this.dismiss() }) }
        } else { null },
    sheetContent: @Composable ModalBottomSheetScope.() -> Unit
) {
    val modalHost = LocalModalHost.current
    val bottomSheetState = rememberModalBottomSheetStateExperimental(CLOSED, key = key)
    val coroutineScope = LocalModalScope.current
    val sheetScopeInstance = remember(key) {
        ModalBottomSheetScopeInstance(coroutineScope, bottomSheetState)
    }
    
    val modal: @Composable HostedModalData.() -> Unit = {
    
        // Этот флаг отвечает за возможность закрыть шит и перейти по иерархии назад
        var canBePopped by remember(key) { mutableStateOf(false) }
        // Когда этот флаг устанавливается, происходит рекомпозиция и запускается лонч-эффект,
        // закрывающий боттом шит
        var shouldCloseAndPop by remember(key) { mutableStateOf(false) }
    
        // текущая фракция видимости боттом шита
        val fraction = bottomSheetState.visibilityFraction
    
        // Добавляем обработчик нажатий системной кнопки "назад"
        BackHandler(enabled = (fraction > 0f)) {
            shouldCloseAndPop = true
        }
    
        if (shouldCloseAndPop) {
            LaunchedEffect(key) {
                bottomSheetState.close()
            }
        }
    
        if (fraction > 0f) canBePopped = true
        
        // Если шит закрылся и установлен флаг, говорящий о том, что его можно выкидывать из
        // иерархии, то делаем это и, таким образом, возвращаемся по иерархии назад
        if (fraction == 0f && canBePopped) {
            this.dismiss()
            onDismissed()
        }

        this.mainContentDownsizeFraction = fraction
    
        val dimmerAlpha = 0.3f * fraction
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(dimmerAlpha)
                .background(Color.Black)
                .clickable { shouldCloseAndPop = true }
        )
        
        ModalBottomSheetLayout(
            sheetScopeInstance = sheetScopeInstance,
            sheetState = bottomSheetState,
            modifier = modifier,
            header = header,
            content = sheetContent
        )
    }
    
    DisposableEffect(key) {
        coroutineScope.launch { modalHost.showModalBottomSheet(modal) }
        
        onDispose {
            sheetScopeInstance.dismiss()
        }
    }
}

@Composable
private fun ModalBottomSheetLayout(
    sheetScopeInstance: ModalBottomSheetScopeInstance,
    sheetState: ModalBottomSheetStateExperimental,
    modifier: Modifier,
    header: @Composable (ModalBottomSheetScope.() -> Unit)?,
    content: @Composable ModalBottomSheetScope.() -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        
        // Максимальная высота контейнера в пикселях
        val fullHeight = constraints.maxHeight.toFloat()
        // Текущая высота bottom sheet'а
        val sheetHeightState = remember { mutableStateOf<Float?>(null) }
        // Флаг, устанавливающийся во время установления якорей соответственно
        // значениям высоты; и нужный для того, чтобы обеспечить изначальную
        // установку оффсета шита на положение "скрыт", а в дальнейшем при
        // рекомпозиции привязать оффсет шита к оффсету из ModalBottomSheetState
        var setAnchorsFlag by remember { mutableStateOf(false) }
        
        Surface(
            elevation = 16.dp,
            shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
            modifier = modifier
                .fillMaxWidth()
                .requiredHeightIn(min = 100.dp, max = maxHeight * 0.9f)
                .nestedScroll(sheetState.nestedScrollConnection)
                .offset {
                    // Если это первый прогон композиции и якоря еще не установлены,
                    // то ставим оффсет по высоте во всю высоту контейнера, чтобы
                    // шит был полностью скрыт. Если же якоря уже установлены и
                    // происходит рекомпозиция, то привязываем оффсет к sheetState
                    val y = if (!setAnchorsFlag) {
                        fullHeight.roundToInt()
                    } else {
                        sheetState.offset.value.roundToInt()
                    }
                    IntOffset(0, y)
                }
                .modalBottomSheetSwipeable(sheetState, fullHeight, sheetHeightState) {
                    // Эта лямбда вызовется во время установки якорей,
                    // что приведет к рекомпозиции с привязкой оффсета
                    // шита к sheetState
                    setAnchorsFlag = true
                }
                .onGloballyPositioned {
                    // Когда позиция шита меняется, обновляем стейт высоты шита
                    // и пересчитываем актуальную фракцию видимости в sheetState
                    sheetHeightState.value = it.size.height.toFloat()
                    sheetState.recalculateVisibilityFraction(
                        sheetHeight = it.size.height.toFloat(),
                        containerHeight = fullHeight
                    )
                }
        ) {
            if (header != null) {
                ModalBottomSheetContentWithHeader(
                    header = { sheetScopeInstance.header() },
                    content = { sheetScopeInstance.content() }
                )
            } else {
                sheetScopeInstance.content()
            }
        }
    }
    
    // Сразу анимированно показываем шит при начальной композации
    LaunchedEffect(key1 = sheetState) {
        sheetState.show()
    }
}

/**
 * Состояние модального боттом шита.
 *
 * Через объект состояния можно показывать или закрывать bottom sheet
 * (с помощью функций [show] и [close]); и получать информацию о состоянии
 * видимости шита через свойство [visibilityFraction].
 */
@Stable
private class ModalBottomSheetStateExperimental(
    initialValue: BottomSheetVisibilityState = CLOSED,
    animationSpec: AnimationSpec<Float>,
    val confirmStateChange: (BottomSheetVisibilityState) -> Boolean = { true }
) : SwipeableState<BottomSheetVisibilityState>(
    initialValue = initialValue,
    animationSpec = animationSpec,
    confirmStateChange = confirmStateChange
) {
    
    /**
     * Состояние видимости bottom sheet'а. Имеет значение в пределах 0f - 1f,
     * коррелирующее с тем, насколько видим в данный момент шит.
     * 0 - полностью скрыт, 1 - полностью видим.
     *
     * Данное свойство полезно в первую очередь для создания анимаций, связанных
     * с bottom sheet'ом, и привязанных к его состоянию видимости.
     */
    var visibilityFraction by mutableStateOf(0f)
    
    /**
     * [NestedScrollConnection], обеспечивающий работу свайпов управления боттом шитом вместе
     * со скролл-контейнерами, могущими присутствовать на самом боттом шите.
     */
    val nestedScrollConnection: NestedScrollConnection
        get() = object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.toFloat()
                return if (delta < 0 && source == NestedScrollSource.Drag) {
                    performDrag(delta).toOffset()
                } else {
                    Offset.Zero
                }
            }
            
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                return if (source == NestedScrollSource.Drag) {
                    performDrag(available.toFloat()).toOffset()
                } else {
                    Offset.Zero
                }
            }
            
            override suspend fun onPreFling(available: Velocity): Velocity {
                val toFling = Offset(available.x, available.y).toFloat()
                return if (
                    toFling < 0
                    && this@ModalBottomSheetStateExperimental.currentValue == OPEN
                ) {
                    performFling(velocity = toFling)
                    available
                } else {
                    Velocity.Zero
                }
            }
            
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                performFling(velocity = Offset(available.x, available.y).toFloat())
                return available
            }
            
            private fun Float.toOffset(): Offset = Offset(0f, this)
            
            private fun Offset.toFloat(): Float = this.y
        }
    
    /** Анимированно показывает bottom sheet */
    suspend fun show() = animateTo(
        targetValue = OPEN,
        anim = spring(1.3f)
    )
    
    /** Анимированно закрывает bottom sheet */
    suspend fun close() = animateTo(
        targetValue = CLOSED,
        anim = spring(1.3f)
    )
    
    /**
     * Пересчитывает [visibilityFraction] в соответствиии с текущими значениями прогресса
     * свайпа/анимации, текущего и конечного состояния. Этот метод должен вызываться тогда,
     * когда bottom sheet меняет свои координаты, чтобы в соответствии с изменениями обновлять
     * [visibilityFraction].
     */
    @OptIn(ExperimentalMaterialApi::class)
    fun recalculateVisibilityFraction(sheetHeight: Float, containerHeight: Float) {
        // Чтобы получить фракцию видимости, от полной высоты шита нужно отнять
        // положительный оффсет шита по высоте относительно своей самой верхней позиции
        // (чем он больше, тем, соответственно, меньше фракция видимости), и затем поделить
        // результат на высоту шита. Итоговый результат вгоняем в рамки от 0 до 1
        val fraction = (sheetHeight - (offset.value - (containerHeight - sheetHeight)).coerceAtLeast(0f)) / sheetHeight
        visibilityFraction = when {
            currentValue == CLOSED && targetValue == CLOSED -> 0f
            else -> fraction
        }.coerceIn(0f, 1f)
        visibilityFraction
    }
    
    companion object {
        
        fun Saver(
            animationSpec: AnimationSpec<Float>,
            confirmStateChange: (BottomSheetVisibilityState) -> Boolean
        ): Saver<ModalBottomSheetStateExperimental, *> = Saver(
            save = { it.currentValue },
            restore = {
                ModalBottomSheetStateExperimental(
                    initialValue = it,
                    animationSpec = animationSpec,
                    confirmStateChange = confirmStateChange
                )
            }
        )
    }
}

/**
 * Создает и запоминает [ModalBottomSheetStateExperimental]
 */
@Composable
private fun rememberModalBottomSheetStateExperimental(
    initialValue: BottomSheetVisibilityState,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    confirmStateChange: (BottomSheetVisibilityState) -> Boolean = { true },
    key: String = remember { UUID.randomUUID().toString() }
): ModalBottomSheetStateExperimental {
    return rememberSaveable(
        animationSpec,
        saver = ModalBottomSheetStateExperimental.Saver(
            animationSpec = animationSpec,
            confirmStateChange = confirmStateChange
        ),
        key = key
    ) {
        ModalBottomSheetStateExperimental(
            initialValue = initialValue,
            animationSpec = animationSpec,
            confirmStateChange = confirmStateChange
        )
    }
}

/**
 * Свайп-модификатор для модальных боттом шитов
 */
private fun Modifier.modalBottomSheetSwipeable(
    sheetState: ModalBottomSheetStateExperimental,
    fullHeight: Float,
    sheetHeightState: State<Float?>,
    onAnchorsSet: () -> Unit
): Modifier {
    val sheetHeight = sheetHeightState.value
    val modifier = if (sheetHeight != null) {
        
        val anchors = mapOf(
            fullHeight to CLOSED,
            fullHeight - sheetHeight to OPEN
        )
        
        onAnchorsSet()
        
        Modifier.swipeable(
            state = sheetState,
            anchors = anchors,
            orientation = Orientation.Vertical,
            enabled = sheetState.currentValue != CLOSED,
            resistance = null,
            thresholds = { _, _ -> FractionalThreshold(0.4f) }
        )
    } else {
        Modifier
    }
    
    return this.then(modifier)
}
