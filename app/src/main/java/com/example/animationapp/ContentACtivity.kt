package com.example.animationapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ContentScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Content Screen",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            ImageAnimationBorder()
        }
    }
}

@Composable
fun ImageAnimationBorder() {
    var rotationAngle by remember { mutableFloatStateOf(0f) }

    // Animation coroutine
    LaunchedEffect(Unit) {
        while (true) {
            delay(16) // approximately 60 FPS
            rotationAngle = (rotationAngle + 0.8f) % 360f // Smooth rotation speed
        }
    }

    val rainbowColorsBrush = remember {
        Brush.sweepGradient(
            colors = listOf(
                Color(0xFFF16D6D), // Red
                Color(0xFFFF7F00), // Orange
                Color(0xFFFFFF00), // Yellow
                Color(0xFFAEECAE), // Green
                Color(0xFF6969E6), // Blue
                Color(0xFF4B0082), // Indigo
                Color(0xFF9400D3), // Violet
                Color(0xFFF16D6D)  // Red again to complete the circle
            )
        )
    }

    val borderWidth = 5.dp

    Image(
        painter = painterResource(id = R.drawable.ktlin), // Replace with your image resource
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(200.dp)
            .drawBehind {
                rotate(rotationAngle) {
                    drawCircle(
                        brush = rainbowColorsBrush,
                        style = Stroke(width = borderWidth.toPx()),
                        radius = size.minDimension / 2
                    )
                }
            }
            .padding(borderWidth)
            .clip(CircleShape)
    )
}