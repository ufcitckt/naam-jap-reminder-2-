package com.naamjap.reminder

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    private val viewModel: ReminderViewModel by viewModels()
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
        setContent {
            NaamJapTheme {
                MainScreen(viewModel = viewModel, onSpeak = { speakMantra() })
            }
        }
    }

    private fun speakMantra() {
        tts?.speak("Radha Vallabh Shri Harivansh", TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        tts?.shutdown()
        super.onDestroy()
    }
}

val AmberDark = Color(0xFFB45309)
val AmberMedium = Color(0xFFD97706)
val AmberLight = Color(0xFFFEF3C7)
val Saffron = Color(0xFFF59E0B)
val StoneDark = Color(0xFF1C1917)
val StoneMedium = Color(0xFF44403C)
val StoneLight = Color(0xFFF5F5F4)
val PureWhite = Color(0xFFFFFFFF)

@Composable
fun NaamJapTheme(content: @Composable () -> Unit) {
    val colors = lightColorScheme(
        primary = AmberMedium,
        onPrimary = PureWhite,
        primaryContainer = AmberLight,
        onPrimaryContainer = AmberDark,
        background = StoneLight,
        surface = PureWhite,
        onBackground = StoneDark,
        onSurface = StoneDark,
    )
    MaterialTheme(colorScheme = colors, content = content)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: ReminderViewModel, onSpeak: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Naam Jap Reminder", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = StoneDark)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = AmberMedium, shape = RoundedCornerShape(4.dp), modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)) {
                                Text("OFFLINE", color = PureWhite, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        Text("  | Radhavallabh", color = StoneMedium, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = PureWhite, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = selectedTab == 0, onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Rounded.Home, contentDescription = "Mala") }, label = { Text("Mala") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AmberMedium, selectedTextColor = AmberMedium, unselectedIconColor = StoneMedium, unselectedTextColor = StoneMedium, indicatorColor = AmberLight)
                )
                NavigationBarItem(
                    selected = selectedTab == 1, onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Rounded.Settings, contentDescription = "Settings") }, label = { Text("Settings") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AmberMedium, selectedTextColor = AmberMedium, unselectedIconColor = StoneMedium, unselectedTextColor = StoneMedium, indicatorColor = AmberLight)
                )
                NavigationBarItem(
                    selected = selectedTab == 2, onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Rounded.History, contentDescription = "History") }, label = { Text("History") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AmberMedium, selectedTextColor = AmberMedium, unselectedIconColor = StoneMedium, unselectedTextColor = StoneMedium, indicatorColor = AmberLight)
                )
                NavigationBarItem(
                    selected = selectedTab == 3, onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Rounded.Info, contentDescription = "Guide") }, label = { Text("Guide") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AmberMedium, selectedTextColor = AmberMedium, unselectedIconColor = StoneMedium, unselectedTextColor = StoneMedium, indicatorColor = AmberLight)
                )
            }
        },
        containerColor = StoneLight
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (selectedTab) {
                0 -> MalaScreen(viewModel, onSpeak)
                1 -> SettingsScreen(viewModel)
                2 -> HistoryScreen(viewModel)
                3 -> GuideScreen()
            }
        }
    }
}

@Composable
fun MalaScreen(viewModel: ReminderViewModel, onSpeak: () -> Unit) {
    val completedBeads by viewModel.malaCompletedBeads.collectAsState()
    val totalRounds by viewModel.malaTotalRounds.collectAsState()
    val context = LocalContext.current
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
    var animateTrigger by remember { mutableStateOf(false) }
    val scaleAnimate by animateFloatAsState(
        targetValue = if (animateTrigger) 0.93f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        finishedListener = { animateTrigger = false }
    )
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
        Card(colors = CardDefaults.cardColors(containerColor = PureWhite), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$completedBeads / 108", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = AmberDark)
                    Text(text = "Current Beads", fontSize = 11.sp, color = StoneMedium, fontWeight = FontWeight.Medium)
                }
                Divider(modifier = Modifier.height(36.dp).width(1.dp), color = StoneLight)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$totalRounds", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = AmberDark)
                    Text(text = "Mala Rounds", fontSize = 11.sp, color = StoneMedium, fontWeight = FontWeight.Medium)
                }
            }
        }
        Box(modifier = Modifier.size(280.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(260.dp)) {
                val center = this.center
                val radius = size.minDimension / 2.0f - 12.dp.toPx()
                for (i in 0 until 108) {
                    val angle = (i * 360.0 / 108.0) * (Math.PI / 180.0)
                    val x = (center.x + radius * cos(angle)).toFloat()
                    val y = (center.y + radius * sin(angle)).toFloat()
                    val isCompleted = i < completedBeads
                    val beadColor = if (isCompleted) Color(0xFFD97706) else Color(0xFFE5E7EB)
                    val sizeRadius = if (i % 9 == 0) 5.dp.toPx() else 3.5.dp.toPx()
                    drawCircle(color = beadColor, radius = sizeRadius, center = Offset(x, y))
                }
            }
            Box(
                modifier = Modifier.size(160.dp).graphicsLayer(scaleX = scaleAnimate, scaleY = scaleAnimate)
                    .clip(CircleShape).background(Brush.linearGradient(colors = listOf(Saffron, AmberMedium)))
                    .clickable {
                        animateTrigger = true
                        try { vibrator?.vibrate(50) } catch (e: Exception) { e.printStackTrace() }
                        onSpeak()
                        viewModel.chantBead()
                    }
                    .border(2.dp, PureWhite, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(12.dp)) {
                    Text(text = "ॐ", fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "श्री", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "हरि", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "TAP TO CHANT", color = AmberLight, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { viewModel.triggerTestReminder() }, colors = ButtonDefaults.buttonColors(containerColor = AmberMedium), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Rounded.Notifications, contentDescription = "Alert")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Test Reminder Firing", fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = { viewModel.resetMala() }, colors = ButtonDefaults.textButtonColors(contentColor = StoneMedium)) {
                Icon(Icons.Rounded.Refresh, contentDescription = "Reset")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reset Mala Chanting Count", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: ReminderViewModel) {
    val context = LocalContext.current
    val interval by viewModel.interval.collectAsState()
    val showNotification by viewModel.showNotification.collectAsState()
    val playSound by viewModel.playSound.collectAsState()
    val soundType by viewModel.soundType.collectAsState()
    val quietHoursEnabled by viewModel.quietHoursEnabled.collectAsState()
    val quietHoursStart by viewModel.quietHoursStart.collectAsState()
    val quietHoursEnd by viewModel.quietHoursEnd.collectAsState()
    var showIntervalDialog by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = PureWhite), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = AmberMedium, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Reminder & Sound Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = StoneDark)
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = PureWhite), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Reminder Actions", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = StoneDark)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Show Notifications", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = StoneDark)
                        }
                        Switch(checked = showNotification, onCheckedChange = { viewModel.updateShowNotification(it) })
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Play Sound Alerts", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = StoneDark)
                        }
                        Switch(checked = playSound, onCheckedChange = { viewModel.updatePlaySound(it) })
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(viewModel: ReminderViewModel) {
    val logs by viewModel.chantLogs.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Database Trigger History", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = StoneDark)
            if (logs.isNotEmpty()) {
                TextButton(onClick = { viewModel.clearLogs() }) { Text("Clear All", color = Color.Red) }
            }
        }
        if (logs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Text("No logs generated yet.", fontWeight = FontWeight.Bold, color = StoneDark, fontSize = 14.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(logs) { log ->
                    Card(colors = CardDefaults.cardColors(containerColor = PureWhite), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = log.detail, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StoneDark)
                            Text(text = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date(log.timestamp)), fontSize = 10.sp, color = StoneMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GuideScreen() {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = PureWhite), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Naam Jap Meditative Guide", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = StoneDark)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Naam Jap is an ancient spiritual practice.", fontSize = 12.sp, color = StoneMedium, lineHeight = 18.sp)
                }
            }
        }
    }
}