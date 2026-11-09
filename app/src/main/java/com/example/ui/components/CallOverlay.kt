package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WeChatBlueSecondary
import com.example.ui.theme.WeChatOrangePrimary
import com.example.ui.viewmodel.ActiveCallState

@Composable
fun CallOverlay(
    callState: ActiveCallState,
    onMuteToggle: () -> Unit,
    onCameraToggle: () -> Unit,
    onSpeakerToggle: () -> Unit,
    onEndCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val durationMinutes = callState.durationSeconds / 60
    val durationSecs = callState.durationSeconds % 60
    val timerString = String.format("%02d:%02d", durationMinutes, durationSecs)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F1216),
                        Color(0xFF181C22),
                        Color(0xFF090A0C)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("call_overlay")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Call Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = WeChatBlueSecondary.copy(alpha = 0.2f),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (callState.callType == "VIDEO") Icons.Default.Videocam else Icons.Default.Call,
                            contentDescription = null,
                            tint = WeChatBlueSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "WECHAT ${callState.callType} ENCRYPTED",
                            color = WeChatBlueSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Text(
                    text = callState.peerName,
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (callState.isConnected) timerString else "Connecting cryptographic stream...",
                    color = if (callState.isConnected) Color(0xFF00E676) else WeChatOrangePrimary,
                    fontSize = 16.sp,
                    fontFamily = if (callState.isConnected) FontFamily.Monospace else FontFamily.Default,
                    fontWeight = FontWeight.Medium
                )
            }

            // Central Visual / Video Simulation
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .border(3.dp, Brush.sweepGradient(listOf(WeChatOrangePrimary, WeChatBlueSecondary, WeChatOrangePrimary)), CircleShape)
                    .background(Color(0xFF1E232B))
            ) {
                if (callState.callType == "VIDEO" && callState.isCameraOn && callState.isConnected) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    listOf(WeChatBlueSecondary.copy(alpha = 0.35f), Color.Transparent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = WeChatBlueSecondary,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                } else {
                    UserAvatar(
                        name = callState.peerName,
                        size = 130.dp,
                        isOnline = callState.isConnected
                    )
                }
            }

            // Bottom Actions Bar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Button
                    IconButton(
                        onClick = onMuteToggle,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (callState.isMuted) Color.White else Color(0xFF2A2E38))
                    ) {
                        Icon(
                            imageVector = if (callState.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = if (callState.isMuted) Color.Black else Color.White
                        )
                    }

                    // Camera Button (for video call)
                    if (callState.callType == "VIDEO") {
                        IconButton(
                            onClick = onCameraToggle,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (!callState.isCameraOn) Color.White else Color(0xFF2A2E38))
                        ) {
                            Icon(
                                imageVector = if (callState.isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                contentDescription = "Camera",
                                tint = if (!callState.isCameraOn) Color.Black else Color.White
                            )
                        }
                    }

                    // Speaker Button
                    IconButton(
                        onClick = onSpeakerToggle,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (callState.isSpeakerOn) WeChatBlueSecondary else Color(0xFF2A2E38))
                    ) {
                        Icon(
                            imageVector = if (callState.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                            contentDescription = "Speaker",
                            tint = Color.White
                        )
                    }

                    // Hangup Button
                    IconButton(
                        onClick = onEndCall,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD32F2F))
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}
