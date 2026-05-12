package com.loptech.suitcasesmart.usecases.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.loptech.suitcasesmart.model.domain.UserData
import com.loptech.suitcasesmart.ui.theme.AviationNavy
import com.loptech.suitcasesmart.ui.theme.AviationNavyLight
import com.loptech.suitcasesmart.ui.theme.SkyLight
import java.lang.RuntimeException

@Composable
fun ProfileScreen(
    userData: UserData,
    maletaCount: Int,
    itemCount: Int,
    packedCount: Int,
    onSignOut: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    var showErrorButton by remember { mutableStateOf(false) }
    var isSigningOut by remember { mutableStateOf(false) }

    val percentListo = if (itemCount > 0) (packedCount * 100 / itemCount) else 0

    LaunchedEffect(Unit) {
        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showErrorButton = Firebase.remoteConfig.getBoolean("show_error_btn")
            }
        }
    }

    Scaffold(contentWindowInsets = WindowInsets(0)) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Navy header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AviationNavy)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(AviationNavyLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (userData.profilePictureUrl != null) {
                        AsyncImage(
                            model = userData.profilePictureUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Initials or default icon
                        val initials = userData.username
                            ?.split(" ")
                            ?.take(2)
                            ?.joinToString("") { it.take(1).uppercase() }
                            ?.ifEmpty { null }
                        if (initials != null) {
                            Text(
                                text = initials,
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                modifier = Modifier.size(44.dp),
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (userData.username != null) {
                    Text(
                        text = userData.username,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (userData.email != null) {
                    Text(
                        text = userData.email,
                        color = SkyLight.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stats row
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                        .padding(vertical = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem(label = "Maletas", value = "$maletaCount", modifier = Modifier.weight(1f))
                    StatDivider()
                    StatItem(label = "Items", value = "$itemCount", modifier = Modifier.weight(1f))
                    StatDivider()
                    StatItem(label = "Listo", value = "$percentListo%", modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Preferencias
            SectionLabel("Preferencias")
            MenuRow(
                icon = Icons.Filled.DarkMode,
                label = "Modo oscuro",
                iconBg = Color(0xFFFFF7ED),
                iconTint = Color(0xFFF59E0B),
                showArrow = false,
                onClick = onToggleTheme,
                trailing = {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleTheme() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AviationNavy
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section: Sesión
            SectionLabel("Sesión")
            MenuRow(
                icon = Icons.AutoMirrored.Filled.Logout,
                label = "Cerrar sesión",
                iconBg = Color(0xFFFFECEC),
                iconTint = Color(0xFFE53935),
                labelColor = Color(0xFFE53935),
                enabled = !isSigningOut,
                onClick = {
                    if (!isSigningOut) {
                        isSigningOut = true
                        onSignOut()
                    }
                },
                trailing = {
                    if (isSigningOut) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFFE53935)
                        )
                    }
                }
            )

            if (showErrorButton) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { throw RuntimeException("Forzando el error") },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text("Crash")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, color = SkyLight.copy(alpha = 0.8f), fontSize = 12.sp)
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(32.dp)
            .background(Color.White.copy(alpha = 0.25f))
    )
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        text = title.uppercase(),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 1.sp
    )
}

@Composable
private fun MenuRow(
    icon: ImageVector,
    label: String,
    iconBg: Color,
    iconTint: Color,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    enabled: Boolean = true,
    showArrow: Boolean = false,
    onClick: () -> Unit,
    trailing: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(iconBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = labelColor
        )
        trailing()
        if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
