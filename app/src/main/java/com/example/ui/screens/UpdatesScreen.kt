package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PostEntity
import com.example.data.model.StoryEntity
import com.example.data.model.UserEntity
import com.example.ui.components.UserAvatar
import com.example.ui.components.WeChatTextField
import com.example.ui.theme.WeChatBlueSecondary
import com.example.ui.theme.WeChatOrangePrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatesScreen(
    stories: List<StoryEntity>,
    channels: List<PostEntity>,
    currentUser: UserEntity?,
    onStoryClick: (StoryEntity) -> Unit,
    onCreateStory: (String, String, () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddStatusDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.testTag("updates_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Updates",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showAddStatusDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "New Status",
                            tint = WeChatOrangePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SmallFloatingActionButton(
                    onClick = { showAddStatusDialog = true },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = CircleShape
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Text Status")
                }

                FloatingActionButton(
                    onClick = { showAddStatusDialog = true },
                    containerColor = WeChatOrangePrimary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.testTag("fab_new_status")
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Add Status")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            item {
                Text(
                    text = "Status",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                )
            }

            // My Status Card
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAddStatusDialog = true }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        UserAvatar(name = currentUser?.fullName ?: "Me", size = 52.dp)
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(WeChatOrangePrimary)
                                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "My status",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Tap to add status update",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 6.dp))
            }

            if (stories.isNotEmpty()) {
                item {
                    Text(
                        text = "Recent updates",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                items(stories, key = { it.id }) { story ->
                    val timeStr = remember(story.createdAt) {
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(story.createdAt))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStoryClick(story) }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .border(
                                    2.dp,
                                    Brush.sweepGradient(listOf(WeChatOrangePrimary, WeChatBlueSecondary, WeChatOrangePrimary)),
                                    CircleShape
                                )
                                .padding(3.dp)
                        ) {
                            UserAvatar(name = story.userFullName, size = 48.dp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = story.userFullName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "$timeStr • ${story.caption.take(30)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    Divider(modifier = Modifier.padding(vertical = 6.dp))
                }
            }

            // Channels & Broadcasts
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Channels & Public Moments",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            if (channels.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No channel updates yet. Post in Channels to broadcast updates.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(channels, key = { it.id }) { post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                UserAvatar(name = post.authorFullName, size = 36.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(post.authorFullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("@${post.authorUsername}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(post.content, fontSize = 13.sp, lineHeight = 18.sp)
                        }
                    }
                }
            }
        }
    }

    if (showAddStatusDialog) {
        var statusCaption by remember { mutableStateOf("") }
        var selectedBg by remember { mutableStateOf("#E15712") }
        val colors = listOf("#E15712", "#2FA0E1", "#1A7BB8", "#4A148C", "#1B5E20")

        AlertDialog(
            onDismissRequest = { showAddStatusDialog = false },
            title = { Text("Add Status Update") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    WeChatTextField(
                        value = statusCaption,
                        onValueChange = { statusCaption = it },
                        label = "Type your status...",
                        singleLine = false
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        colors.forEach { hex ->
                            val color = Color(android.graphics.Color.parseColor(hex))
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        if (selectedBg == hex) 3.dp else 1.dp,
                                        if (selectedBg == hex) Color.White else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable { selectedBg = hex }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (statusCaption.isNotBlank()) {
                            onCreateStory(statusCaption, selectedBg) {
                                showAddStatusDialog = false
                            }
                        }
                    }
                ) {
                    Text("Share Status", color = WeChatOrangePrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStatusDialog = false }) { Text("Cancel") }
            }
        )
    }
}
