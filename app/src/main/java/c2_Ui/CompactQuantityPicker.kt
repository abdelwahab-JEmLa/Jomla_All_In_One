package c2_Ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun CompactQuantityPickerPC(
    initialQuantity: Int = 1,
    height: Dp
) {
    var isRed by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while(true) {
            if(isRed) {
                delay(700)
                isRed = false
            }  else {
                delay(6000)
                isRed = true
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = animateColorAsState(
                if (isRed) Color.Red else Color.White,
                label = "backgroundColor"
            ).value
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            val values = remember {
                (1..15).map { it.toString() } +
                        (20..25).map { it.toString() } +
                        listOf("30", "40", "50")
            }

            val valuesPickerState = rememberPickerState()

            LaunchedEffect(Unit) {
                valuesPickerState.selectedItem = initialQuantity.toString()
            }

            Picker(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .size(height),
                items = values,
                state = valuesPickerState,
                visibleItemsCount = 15,
                textModifier = Modifier.padding(4.dp),
                textStyle = TextStyle(
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = animateColorAsState(
                        if (isRed) Color.White else Color.Red,
                        label = "textColor"
                    ).value
                ),
                dividerColor = animateColorAsState(
                    if (isRed) Color.White else Color.Red,
                    label = "dividerColor"
                ).value,
                startIndex = values.indexOfFirst { it == initialQuantity.toString() }.coerceAtLeast(0),
                onItemStat = { }
            )
        }
    }
}

@Composable
fun Picker(
    modifier: Modifier = Modifier,
    items: List<String>,
    state: PickerState = rememberPickerState(),
    startIndex: Int = 0,
    visibleItemsCount: Int ,
    textModifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    dividerColor: Color = LocalContentColor.current,
    onItemStat: (String) -> Unit,
) {
    var minusToReglePosition by remember { mutableStateOf(7.0) }

    val centerPosition = 3
    val listScrollCount = Integer.MAX_VALUE
    val listScrollMiddle = listScrollCount / 2
    val listStartIndex = listScrollMiddle - listScrollMiddle % items.size - centerPosition + startIndex

    fun getItem(index: Int) = items[index % items.size]

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = listStartIndex)

    // Simplified fling behavior
    val flingBehavior = rememberSnapFlingBehavior(listState)

    val itemHeightPixels = remember { mutableStateOf(0) }
    val itemHeightDp = pixelsToDp(itemHeightPixels.value)

    val fadingEdgeGradient = remember {
        Brush.verticalGradient(
            0f to Color.Transparent,
            0.3f to Color.Black,
            0.7f to Color.Black,
            1f to Color.Transparent
        )
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> getItem(index + centerPosition) }
            .distinctUntilChanged()
            .collect { item ->
                state.selectedItem = item
                onItemStat(item)
                minusToReglePosition = -50.0
            }
    }

    val scope = rememberCoroutineScope()
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()

    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightDp * visibleItemsCount)
                .fadingEdge(fadingEdgeGradient)
                .pointerInput(Unit) {
                    var lastDragPosition = Offset.Zero
                    var totalDragDistance = 0f

                    detectDragGestures(
                        onDragStart = { lastDragPosition = it },
                        onDragEnd = {
                            scope.launch {
                                val velocity = totalDragDistance * 1.5f
                                val targetOffset = decayAnimationSpec.calculateTargetValue(0f, velocity)
                                val targetIndex = (targetOffset / itemHeightPixels.value).roundToInt()
                                listState.animateScrollBy(
                                    targetIndex * itemHeightPixels.value.toFloat(),
                                    spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                                totalDragDistance = 0f
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            lastDragPosition += dragAmount
                            totalDragDistance += dragAmount.y
                            scope.launch {
                                listState.scrollBy(-dragAmount.y * 1.5f)
                            }
                        }
                    )
                }
        ) {
            items(listScrollCount) { index ->
                Text(
                    text = getItem(index),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = textStyle,
                    modifier = Modifier
                        .onSizeChanged { size -> itemHeightPixels.value = size.height }
                        .then(textModifier)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.offset(y = (itemHeightDp- minusToReglePosition.dp) * 1),
            color = dividerColor
        )

        HorizontalDivider(
            modifier = Modifier.offset(y = (itemHeightDp- minusToReglePosition.dp) * 2),
            color = dividerColor
        )
    }
}

private fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

@Composable
private fun pixelsToDp(pixels: Int) = with(LocalDensity.current) { pixels.toDp() }




@Composable
fun rememberPickerState() = remember { PickerState() }

class PickerState {
    var selectedItem by mutableStateOf("")
}

