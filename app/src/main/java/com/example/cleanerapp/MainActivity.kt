package com.example.cleanerapp

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.activity.compose.setContent
import androidx.activity.ComponentActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.os.Bundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CleanerAppTheme {
                CleanerScreen(this)
            }
        }
    }
}

@Composable
fun CleanerScreen(context: Context) {
    var isCleaning by remember { mutableStateOf(false) }
    var spaceCleaned by remember { mutableStateOf(0L) }
    var ramFreed by remember { mutableStateOf(0L) }
    var totalSpaceFreed by remember { mutableStateOf(0L) }
    var cleaningProgress by remember { mutableStateOf("") }
    var showResults by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0F0F0F)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Cleaner Pro",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(20.dp)
            )

            Text(
                "Nettoyez votre telephone en un clic",
                fontSize = 14.sp,
                color = Color(0xFFB0B0B0),
                modifier = Modifier.padding(bottom = 30.dp)
            )

            StatsRow(context)

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    isCleaning = true
                    spaceCleaned = 0L
                    ramFreed = 0L
                    cleaningProgress = "Demarrage du nettoyage..."
                    showResults = false

                    scope.launch {
                        val result = withContext(Dispatchers.IO) {
                            performCleaning(context)
                        }
                        spaceCleaned = result.first
                        ramFreed = result.second
                        totalSpaceFreed = spaceCleaned + ramFreed
                        cleaningProgress = "Nettoyage termine !"
                        showResults = true
                        isCleaning = false
                    }
                },
                enabled = !isCleaning,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .animateContentSize(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6366F1),
                    disabledContainerColor = Color(0xFF4F46E5)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isCleaning) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Nettoyage en cours...", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("NETTOYER MAINTENANT", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (cleaningProgress.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    cleaningProgress,
                    fontSize = 14.sp,
                    color = if (showResults) Color(0xFF4ADE80) else Color(0xFFB0B0B0),
                    fontWeight = FontWeight.Medium
                )
            }

            if (showResults) {
                Spacer(modifier = Modifier.height(30.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1F1F1F)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Nettoyage Reussi !",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        ResultItem(
                            icon = Icons.Default.Folder,
                            label = "Cache Nettoye",
                            value = formatBytes(spaceCleaned),
                            color = Color(0xFF60A5FA)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ResultItem(
                            icon = Icons.Default.Memory,
                            label = "RAM Liberee",
                            value = formatBytes(ramFreed),
                            color = Color(0xFF34D399)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Divider(color = Color(0xFF2F2F2F), thickness = 1.dp)

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Total Libere",
                                fontSize = 16.sp,
                                color = Color(0xFFB0B0B0)
                            )
                            Text(
                                formatBytes(totalSpaceFreed),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4ADE80)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1F1F1F)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Informations",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "- Nettoie le cache de TOUTES les applications\n" +
                        "- Supprime les fichiers temporaires\n" +
                        "- Libere la RAM en tuant les processus inutiles\n" +
                        "- 100% sur - Aucune donnee personnelle supprimee",
                        fontSize = 12.sp,
                        color = Color(0xFFB0B0B0),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ResultItem(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A2A2A), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, fontSize = 14.sp, color = Color(0xFFB0B0B0))
        }
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun StatsRow(context: Context) {
    var totalStorage by remember { mutableStateOf(0L) }
    var freeStorage by remember { mutableStateOf(0L) }
    var usedStorage by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        val stats = withContext(Dispatchers.Default) {
            getStorageStats(context)
        }
        totalStorage = stats.first
        freeStorage = stats.second
        usedStorage = totalStorage - freeStorage
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard("Espace Total", formatBytes(totalStorage), Color(0xFF6366F1))
        StatCard("Utilise", formatBytes(usedStorage), Color(0xFFEF4444))
        StatCard("Libre", formatBytes(freeStorage), Color(0xFF4ADE80))
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, fontSize = 12.sp, color = Color(0xFFB0B0B0))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun CleanerAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF6366F1),
            secondary = Color(0xFF34D399),
            tertiary = Color(0xFFEF4444)
        )
    ) {
        content()
    }
}

suspend fun performCleaning(context: Context): Pair<Long, Long> {
    return withContext(Dispatchers.IO) {
        var totalCleaned = 0L
        val cacheSize = cleanAppCache(context)
        totalCleaned += cacheSize
        val tempSize = cleanTempFiles(context)
        totalCleaned += tempSize
        val ramFreed = freeRam(context)
        Pair(totalCleaned, ramFreed)
    }
}

fun cleanAppCache(context: Context): Long {
    var totalSize = 0L
    val pm = context.packageManager
    val packages = pm.getInstalledApplications(0)

    for (app in packages) {
        try {
            val parent = context.cacheDir.parent
            if (parent != null) {
                val cacheDir = File(parent, app.packageName + "/cache")
                if (cacheDir.exists()) {
                    totalSize += deleteDir(cacheDir)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return totalSize
}

fun cleanTempFiles(context: Context): Long {
    var totalSize = 0L
    try {
        totalSize += deleteDir(context.cacheDir)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    try {
        val tmpDir = File(context.getExternalFilesDir(null), "tmp")
        if (tmpDir.exists()) {
            totalSize += deleteDir(tmpDir)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return totalSize
}

fun freeRam(context: Context): Long {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val before = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(before)

    val myPackage = context.packageName
    val packages = context.packageManager.getInstalledApplications(0)
    for (app in packages) {
        if (app.packageName != myPackage) {
            try {
                activityManager.killBackgroundProcesses(app.packageName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    System.gc()
    try {
        Thread.sleep(1500)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }

    val after = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(after)
    val freed = after.availMem - before.availMem
    return if (freed > 0) freed else 0L
}

fun deleteDir(dir: File?): Long {
    var size = 0L
    if (dir != null && dir.isDirectory) {
        val children = dir.listFiles()
        if (children != null) {
            for (child in children) {
                size += deleteDir(child)
            }
        }
    }
    if (dir != null) {
        val fileSize = if (dir.isFile) dir.length() else 0L
        if (dir.delete()) {
            size += fileSize
        }
    }
    return size
}

fun getStorageStats(context: Context): Pair<Long, Long> {
    val stat = StatFs(Environment.getDataDirectory().path)
    val blockSize = stat.blockSizeLong
    val totalBlocks = stat.blockCountLong
    val availableBlocks = stat.availableBlocksLong

    val totalStorage = totalBlocks * blockSize
    val freeStorage = availableBlocks * blockSize

    return Pair(totalStorage, freeStorage)
}

fun formatBytes(bytes: Long): String {
    return when {
        bytes >= 1024 * 1024 * 1024 -> String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))
        bytes >= 1024 * 1024 -> String.format("%.2f MB", bytes / (1024.0 * 1024.0))
        bytes >= 1024 -> String.format("%.2f KB", bytes / 1024.0)
        else -> "$bytes B"
    }
}
