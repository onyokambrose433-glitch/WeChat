package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.theme.WeChatBlueSecondary
import com.example.ui.theme.WeChatOrangePrimary

@Composable
fun DigitalIdCard(
    user: UserEntity,
    modifier: Modifier = Modifier,
    onShare: () -> Unit = {}
) {
    var showQrCode by remember { mutableStateOf(false) }
    var nfcSimulated by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("digital_id_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.linearGradient(listOf(WeChatOrangePrimary, WeChatBlueSecondary))
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Card Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(WeChatOrangePrimary, Color(0xFFC74306), WeChatBlueSecondary)
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "WECHAT DIGITAL ID",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp
                            )
                        }
                        Text(
                            text = "SECURE PROTOCOL v2.4",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = "TRUST ${user.trustScore}%",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Card Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserAvatar(
                        name = user.fullName,
                        size = 54.dp,
                        isOnline = true
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.fullName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = WeChatOrangePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "@${user.username}",
                            fontSize = 13.sp,
                            color = WeChatBlueSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "ID: ${user.id.take(12).uppercase()}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = user.bio,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Metadata items
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoPill(Icons.Default.Email, user.email, Modifier.weight(1f))
                    if (user.phone.isNotBlank()) {
                        InfoPill(Icons.Default.Phone, user.phone, Modifier.weight(1f))
                    }
                }

                // QR Code Matrix Simulation
                AnimatedVisibility(visible = showQrCode) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                            color = Color.White
                        ) {
                            SimulatedQrCode(content = "wechat://user/${user.username}")
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Scan to add contact or verify cryptographic credentials",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // NFC Simulation Indicator
                AnimatedVisibility(visible = nfcSimulated) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = WeChatOrangePrimary.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Nfc,
                                contentDescription = null,
                                tint = WeChatOrangePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NFC Digital Beam ready. Hold devices together to exchange identity.",
                                fontSize = 11.sp,
                                color = WeChatOrangePrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Card Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showQrCode = !showQrCode },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = if (showQrCode) Icons.Default.Close else Icons.Default.QrCode,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showQrCode) "Hide QR" else "View QR", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { nfcSimulated = !nfcSimulated },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Nfc,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Beam NFC", fontSize = 12.sp)
                    }

                    IconButton(
                        onClick = onShare,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(WeChatOrangePrimary.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share ID",
                            tint = WeChatOrangePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoPill(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = WeChatOrangePrimary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SimulatedQrCode(content: String) {
    Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        val size = size.width
        val cols = 15
        val cellSize = size / cols

        // Draw corner targets
        fun drawCorner(x: Float, y: Float) {
            drawRect(
                color = Color.Black,
                topLeft = Offset(x, y),
                size = Size(cellSize * 3, cellSize * 3)
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(x + cellSize * 0.6f, y + cellSize * 0.6f),
                size = Size(cellSize * 1.8f, cellSize * 1.8f)
            )
            drawRect(
                color = Color.Black,
                topLeft = Offset(x + cellSize * 1.1f, y + cellSize * 1.1f),
                size = Size(cellSize * 0.8f, cellSize * 0.8f)
            )
        }

        drawCorner(0f, 0f)
        drawCorner(size - cellSize * 3, 0f)
        drawCorner(0f, size - cellSize * 3)

        // Draw pseudo random matrix bits
        val hash = content.hashCode()
        for (i in 0 until cols) {
            for (j in 0 until cols) {
                val inCorner1 = i < 4 && j < 4
                val inCorner2 = i > cols - 5 && j < 4
                val inCorner3 = i < 4 && j > cols - 5
                if (!inCorner1 && !inCorner2 && !inCorner3) {
                    val bit = ((hash xor (i * 31 + j * 17)) and 1) == 0
                    if (bit) {
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(i * cellSize, j * cellSize),
                            size = Size(cellSize * 0.9f, cellSize * 0.9f)
                        )
                    }
                }
            }
        }
    }
}
