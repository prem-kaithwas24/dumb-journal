package com.dumpjournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dumpjournal.ui.theme.DumpJournalTheme
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.Canvas
import kotlin.random.Random
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.delay
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.drawscope.DrawScope
import android.media.MediaPlayer
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import android.media.ToneGenerator
import android.media.AudioManager

data class FireParticle(
    var x: Float,
    var y: Float,
    var size: Float,
    var alpha: Float,
    var velocityY: Float,
    var velocityX: Float,
    var life: Float = 1f
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by rememberSaveable { mutableStateOf(false) }
            
            var text by rememberSaveable { mutableStateOf("") }
            var triggerFireAnimation by remember { mutableStateOf(false) }
            
            DumpJournalTheme(darkTheme = isDarkMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        NoteBook(
                            modifier = Modifier.weight(1f),
                            isDarkMode = isDarkMode,
                            text = text,
                            onTextChange = { text = it },
                            triggerFireAnimation = triggerFireAnimation,
                            onAnimationComplete = { 
                                text = ""
                                triggerFireAnimation = false
                            }
                        )
                        Footer(
                            isDarkMode = isDarkMode,
                            onToggleTheme = { isDarkMode = !isDarkMode },
                            onFireClick = { 
                                if (text.isNotEmpty()) {
                                    triggerFireAnimation = true
                                }
                            },
                            triggerSound = triggerFireAnimation
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppHeader(modifier: Modifier = Modifier){
    Text(
        text = "Dump Journal",
        modifier = modifier
    )
}

@Composable
fun NoteBook(
    modifier: Modifier = Modifier, 
    isDarkMode: Boolean = false,
    text: String,
    onTextChange: (String) -> Unit,
    triggerFireAnimation: Boolean = false,
    onAnimationComplete: () -> Unit = {}
){
    val backgroundColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF3F3F3)
    val textColor = if (isDarkMode) Color(0xFFE0E0E0) else Color.Black
    val placeholderColor = if (isDarkMode) Color(0xFF808080) else Color.Gray

    // Fire animation: fade out with scale and color change
    val animationProgress = animateFloatAsState(
        targetValue = if (triggerFireAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000),
        finishedListener = {
            if (it == 1f) {
                onAnimationComplete()
            }
        },
        label = "fireAnimation"
    )

    val alpha = 1f - animationProgress.value
    val scale = 1f + (animationProgress.value * 0.2f)
    val fireColorProgress = animationProgress.value
    
    // Interpolate from original color to orange/red fire color
    val animatedTextColor = if (triggerFireAnimation) {
        Color(
            red = textColor.red + (1f - textColor.red) * fireColorProgress,
            green = textColor.green + (0.5f - textColor.green) * fireColorProgress * 0.7f,
            blue = textColor.blue * (1f - fireColorProgress * 0.8f),
            alpha = alpha
        )
    } else {
        textColor
    }

    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp)
                .graphicsLayer(
                    alpha = alpha,
                    scaleX = scale,
                    scaleY = scale,
                    translationY = -animationProgress.value * 20f
                ),
            textStyle = TextStyle(
                fontSize = 18.sp,
                lineHeight = 26.sp,
                color = animatedTextColor
            ),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxSize()) {
                    if (text.isEmpty()) {
                        Text(
                            text= "Let your thoughts flow freely... nobody is watching, and we're not keeping track.",
                            color = placeholderColor,
                            fontSize = 18.sp
                        )
                    }

                    innerTextField()
                }
            }
        )
        
        // Fire animation overlay
        if (triggerFireAnimation) {
            FireAnimation(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun Footer(
    isDarkMode: Boolean = false, 
    onToggleTheme: () -> Unit = {}, 
    onFireClick: () -> Unit = {},
    triggerSound: Boolean = false
): Unit {
    val toneGenerator = remember { 
        ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80) 
    }
    
    // Play fire sound effect - whoosh + crackle
    LaunchedEffect(triggerSound) {
        if (triggerSound) {
            try {
                // Whoosh sound (like fire igniting/splash)
                toneGenerator.startTone(ToneGenerator.TONE_SUP_DIAL, 200)
                delay(150)
                
                // Quick crackle bursts
                toneGenerator.startTone(ToneGenerator.TONE_DTMF_S, 50)
                delay(80)
                toneGenerator.startTone(ToneGenerator.TONE_DTMF_D, 40)
                delay(60)
                toneGenerator.startTone(ToneGenerator.TONE_DTMF_S, 50)
                delay(100)
                
                // Final whoosh fade
                toneGenerator.startTone(ToneGenerator.TONE_SUP_RADIO_NOTAVAIL, 300)
            } catch (e: Exception) {
                // Silently fail if sound can't be played
            }
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            toneGenerator.release()
        }
    }
    
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = { onFireClick() }) {
            Icon(
                imageVector = Icons.Default.Whatshot,
                contentDescription = "Fire Note Animation",
                tint = Color.Gray
            )
        }
        Text(
            text="Dump Journal",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(end = 8.dp)
        )
        IconButton(onClick = onToggleTheme) {

            Icon(
                painter = painterResource(
                    id = if (isDarkMode)
                        R.drawable.mode_night_24px
                    else
                        R.drawable.light_mode_24px
                ),
                contentDescription = "Toggle theme",
                tint = Color.Gray
            )
        }

    }
}

@Composable
fun FireAnimation(modifier: Modifier = Modifier) {
    val particles = remember { mutableStateListOf<FireParticle>() }
    
    LaunchedEffect(Unit) {
        // Animation loop
        while (true) {
            // Add new particles from bottom
            repeat(8) {
                particles.add(
                    FireParticle(
                        x = Random.nextFloat(),
                        y = 1f,
                        size = Random.nextFloat() * 40f + 20f,
                        alpha = 1f,
                        velocityY = -(Random.nextFloat() * 0.015f + 0.01f),
                        velocityX = (Random.nextFloat() - 0.5f) * 0.003f,
                        life = 1f
                    )
                )
            }
            
            // Update particles
            particles.forEach { particle ->
                particle.y += particle.velocityY
                particle.x += particle.velocityX
                particle.life -= 0.015f
                particle.alpha = particle.life.coerceIn(0f, 1f)
                particle.size *= 0.99f
            }
            
            // Remove dead particles
            particles.removeAll { it.life <= 0f || it.y < -0.1f }
            
            delay(16) // ~60 FPS
        }
    }
    
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        particles.forEach { particle ->
            val x = particle.x * width
            val y = particle.y * height
            
            // Create flame shape (pointy at top, wider at bottom)
            val flamePath = Path().apply {
                val baseWidth = particle.size * 0.8f
                val flameHeight = particle.size * 1.5f
                
                // Bottom center
                moveTo(x, y + flameHeight * 0.3f)
                
                // Left side curve
                cubicTo(
                    x - baseWidth * 0.6f, y + flameHeight * 0.2f,
                    x - baseWidth * 0.4f, y - flameHeight * 0.3f,
                    x, y - flameHeight * 0.7f  // Pointy top
                )
                
                // Right side curve
                cubicTo(
                    x + baseWidth * 0.4f, y - flameHeight * 0.3f,
                    x + baseWidth * 0.6f, y + flameHeight * 0.2f,
                    x, y + flameHeight * 0.3f  // Back to bottom
                )
                
                close()
            }
            
            // Draw flame with gradient
            val flameGradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFFFFF).copy(alpha = particle.alpha * 0.9f), // White hot at tip
                    Color(0xFFFFDD00).copy(alpha = particle.alpha * 0.95f), // Yellow
                    Color(0xFFFF8800).copy(alpha = particle.alpha * 0.8f), // Orange
                    Color(0xFFFF4400).copy(alpha = particle.alpha * 0.6f), // Red-orange
                    Color(0xFFFF0000).copy(alpha = particle.alpha * 0.3f), // Red at base
                ),
                startY = y - particle.size,
                endY = y + particle.size
            )
            
            // Add slight rotation for more organic look
            rotate(
                degrees = (particle.x * 30f - 15f) + (particle.velocityX * 1000f),
                pivot = Offset(x, y)
            ) {
                drawPath(
                    path = flamePath,
                    brush = flameGradient
                )
            }
            
            // Add inner glow
            val glowGradient = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFFFFF).copy(alpha = particle.alpha * 0.6f),
                    Color(0xFFFFDD00).copy(alpha = particle.alpha * 0.4f),
                    Color.Transparent
                ),
                center = Offset(x, y),
                radius = particle.size * 0.5f
            )
            
            drawCircle(
                brush = glowGradient,
                radius = particle.size * 0.5f,
                center = Offset(x, y)
            )
        }
    }
}