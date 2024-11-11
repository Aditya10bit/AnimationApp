package com.example.animationapp

import android.os.Bundle
import android.view.VelocityTracker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animationapp.ui.theme.AnimationAppTheme
import kotlinx.coroutines.delay
import androidx.compose.animation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimationAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    // Set up the gradient colors
    val gradient1Colors = listOf(Color(0xFF42A5F5), Color(0xFF1E88E5))
    val gradient2Colors = listOf(Color(0xFF8E24AA), Color(0xFFD81B60))

    // Set up animation for gradient
    val animatedProgress = remember { Animatable(0f) }
    var showFishes by remember { mutableStateOf(false) }
    var showContentScreen by remember { mutableStateOf(false) }




    LaunchedEffect(Unit) {
        while (true) {
            animatedProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
            )
            animatedProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
            )
        }
    }

    // Interpolate between the gradients based on the animated progress
    val currentGradientColors = gradient1Colors.zip(gradient2Colors) { start, end ->
        lerp(start, end, animatedProgress.value)
    }

    // Apply the background with animated gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = currentGradientColors,
                    start = androidx.compose.ui.geometry.Offset.Zero,
                    end = androidx.compose.ui.geometry.Offset.Infinite
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Animated Hello Text at the top
        HelloText(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 150.dp)
                .clickable { showFishes = !showFishes }
        )

        // Jellyfish Animation in the center
        AnimatedIconScreen()
        // Animated Fish Swarm
        AnimatedVisibility(
            visible = showFishes,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FishSwarm()
        }
        Click(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            onClickButton = { showContentScreen = true }
        )
        AnimatedVisibility(
            visible = showContentScreen,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight } // Slide from bottom
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight } // Slide to bottom
            ) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ContentScreen()

                AnimatedCloseButton(
                    onClick = { showContentScreen = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )
            }
        }
        // Inside MainScreen's Box content
        DraggableBubble(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (250).dp)
        )
    }
}
@Composable
fun FishSwarm() {
    // Create multiple fish with different starting positions and speeds
    repeat(6) { index ->
        SwimmingFish(
            startFromLeft = index % 2 == 0,
            verticalOffset = Random.nextInt(50, 250).dp,
            delay = index * 500,
            speed = Random.nextInt(2000, 3000)
        )
    }
}

@Composable
fun SwimmingFish(
    startFromLeft: Boolean,
    verticalOffset: Dp,
    delay: Int,
    speed: Int
) {
    var isAnimating by remember { mutableStateOf(false) }

    // Add subtle vertical animation
    val floatingOffset by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    LaunchedEffect(Unit) {
        delay(delay.toLong())
        isAnimating = true
    }

    LaunchedEffect(Unit) {
        delay(delay.toLong())
        isAnimating = true
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val startX = if (startFromLeft) -100.dp else screenWidth + 100.dp
    val endX = if (startFromLeft) screenWidth + 100.dp else -100.dp

    val offsetX by animateFloatAsState(
        targetValue = if (isAnimating) endX.value else startX.value,
        animationSpec = tween(
            durationMillis = 3000,
            easing = LinearEasing
        ),
        finishedListener = {
            isAnimating = false
        }
    )

    Icon(
        imageVector = ImageVector.vectorResource(id = R.drawable.fish),
        contentDescription = "Swimming Fish",
        modifier = Modifier
            .size(60.dp)
            .offset(
                x = offsetX.dp,
                y = verticalOffset + floatingOffset.dp  // Add floating motion
            )
            .graphicsLayer(
                scaleX = if (startFromLeft) 1f else -1f,  // Flip fish based on direction
                alpha = 0.8f  // Slightly increased opacity
            ),
        tint = Color.Unspecified
    )
}


@Composable
fun HelloText(modifier: Modifier = Modifier) {
    var isAnimationPlayed by remember { mutableStateOf(false) }
    var shouldStartAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        shouldStartAnimation = true
    }

    val scale by animateFloatAsState(
        targetValue = if (!shouldStartAnimation) 0f
        else if (!isAnimationPlayed) 2.5f
        else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = { isAnimationPlayed = true }, label = ""
    )

    Text(
        text = "Hello!",
        fontSize = 52.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Black,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    )
}

@Composable
fun AnimatedIconScreen() {
    val translationY = rememberInfiniteTransition(label = "")
    val animatedOffset by translationY.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    var isEyesVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        IconWithAnimation(
            modifier = Modifier.size(200.dp),
            offsetY = animatedOffset,
            onClick = { isEyesVisible = !isEyesVisible }
        )

        JellyfishEyes(
            modifier = Modifier.size(200.dp),
            offsetY = animatedOffset,
            showEyes = isEyesVisible
        )
    }
}

@Composable
fun IconWithAnimation(
    modifier: Modifier = Modifier,
    offsetY: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.jellyfish),
            contentDescription = "Jellyfish Body",
            modifier = Modifier
                .fillMaxSize()
                .offset(y = offsetY.dp)
                .clickable(onClick = onClick)
                .graphicsLayer(
                    scaleX = 1f,
                    scaleY = 1f,
                    alpha = 1f
                ),
            tint = Color.Unspecified
        )
    }
}

@Composable
fun JellyfishEyes(
    modifier: Modifier = Modifier,
    offsetY: Float,
    showEyes: Boolean
) {
    val alpha by animateFloatAsState(
        targetValue = if (showEyes) 1f else 0f,
        animationSpec = tween(durationMillis = 500), label = ""
    )

    if (showEyes) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.jellyfish_eyes),
            contentDescription = "Jellyfish Eyes",
            modifier = modifier
                .offset(y = offsetY.dp)
                .graphicsLayer(alpha = alpha),
            tint = Color.Unspecified
        )
    }
}



@Composable
fun Click(modifier: Modifier = Modifier,
          onClickButton: () -> Unit, ) {
    Button(
        onClick = onClickButton,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        )
    ) {
        Text(
            "Click Me",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun DraggableBubble(
    modifier: Modifier = Modifier,
    bubbleSize: Float = 60f
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // For smooth animation after fling
    val offsetAnimatable = remember { Animatable(0f) }
    val velocityTracker = remember { androidx.compose.ui.input.pointer.util.VelocityTracker() }
    val coroutineScope = rememberCoroutineScope()

    // Bubble floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
            .graphicsLayer { translationY = floatingOffset }
            .draggable(
                state = rememberDraggableState { delta ->
                    offsetX += delta
                },
                orientation = Orientation.Horizontal,
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        // Simulate momentum and friction
                        val targetOffset = offsetX + velocity * 0.1f
                        offsetAnimatable.animateTo(
                            targetValue = targetOffset,
                            initialVelocity = velocity,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                        offsetX = offsetAnimatable.value
                    }
                }
            )
            .draggable(
                state = rememberDraggableState { delta ->
                    offsetY += delta
                },
                orientation = Orientation.Vertical,
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        val targetOffset = offsetY + velocity * 0.1f
                        offsetAnimatable.animateTo(
                            targetValue = targetOffset,
                            initialVelocity = velocity,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                        offsetY = offsetAnimatable.value
                    }
                }
            )
    ) {
        // Bubble content with shadow and gradient effect
        Box(
            modifier = Modifier
                .size(bubbleSize.dp)
                .clip(CircleShape)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF64B5F6).copy(alpha = 0.8f),
                            Color(0xFF1E88E5).copy(alpha = 0.6f)
                        )
                    )
                )
        ) {
            // Optional: Add your bubble icon or content here
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.bubble),
                contentDescription = "Bubble",
                modifier = Modifier
                    .size(bubbleSize.dp * 0.7f)
                    .graphicsLayer(alpha = 0.6f),
                tint = Color.White
            )
        }
    }
}

@Composable
fun AnimatedCloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shake")

    // Create rotation animation
    val rotation by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer {
                rotationZ = rotation // Apply the rotation animation
            }
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    AnimationAppTheme {
        MainScreen()
    }
}