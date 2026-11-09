package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConversationEntity
import com.example.data.model.MessageEntity
import com.example.ui.components.UserAvatar
import com.example.ui.components.WeChatTextField
import com.example.ui.theme.WeChatBlueSecondary
import com.example.ui.theme.WeChatOrangePrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    conversations: List<ConversationEntity>,
    selectedConversation: ConversationEntity?,
    messages: List<MessageEntity>,
    onSelectConversation: (ConversationEntity) -> Unit,
    onCloseConversation: () -> Unit,
    onSendMessage: (String, String) -> Unit,
    onReactMessage: (String, String) -> Unit,
    onConvertMessageToTask: (MessageEntity) -> Unit,
    onConvertMessageToReminder: (MessageEntity) -> Unit,
    onConvertMessageToResource: (MessageEntity) -> Unit,
    onStartCall: (String, String) -> Unit,
    onStartNewChat: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedConversation != null) {
        ChatDetailScreen(
            conversation = selectedConversation,
            messages = messages,
            onBack = onCloseConversation,
            onSendMessage = onSendMessage,
            onReactMessage = onReactMessage,
            onConvertMessageToTask = onConvertMessageToTask,
            onConvertMessageToReminder = onConvertMessageToReminder,
            onConvertMessageToResource = onConvertMessageToResource,
            onStartCall = onStartCall,
            modifier = modifier
        )
    } else {
        ConversationListScreen(
            conversations = conversations,
            onSelectConversation = onSelectConversation,
            onStartNewChat = onStartNewChat,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversationListScreen(
    conversations: List<ConversationEntity>,
    onSelectConversation: (ConversationEntity) -> Unit,
    onStartNewChat: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showNewChatDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.testTag("conversation_list_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Messages",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showNewChatDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.AddComment,
                            contentDescription = "New Conversation",
                            tint = WeChatOrangePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search messages and contacts...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = WeChatOrangePrimary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            val filtered = conversations.filter {
                searchQuery.isBlank() || it.title.contains(searchQuery, true) || it.lastMessage.contains(searchQuery, true)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                if (filtered.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No active conversations",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(filtered, key = { it.id }) { conv ->
                        ConversationItem(
                            conversation = conv,
                            onClick = { onSelectConversation(conv) }
                        )
                        Divider(
                            modifier = Modifier.padding(start = 76.dp, end = 16.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                        )
                    }
                }
            }
        }
    }

    if (showNewChatDialog) {
        var newChatName by remember { mutableStateOf("") }
        var isGroup by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showNewChatDialog = false },
            title = { Text("New Conversation") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    WeChatTextField(
                        value = newChatName,
                        onValueChange = { newChatName = it },
                        label = "Contact or Group Name",
                        leadingIcon = Icons.Default.Person
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Group Conversation", fontSize = 14.sp)
                        Switch(checked = isGroup, onCheckedChange = { isGroup = it })
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newChatName.isNotBlank()) {
                            onStartNewChat(newChatName, isGroup)
                            showNewChatDialog = false
                        }
                    }
                ) {
                    Text("Start Chat", color = WeChatOrangePrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewChatDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ConversationItem(
    conversation: ConversationEntity,
    onClick: () -> Unit
) {
    val timeFormatted = remember(conversation.lastMessageTime) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(conversation.lastMessageTime))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(
            name = conversation.title,
            size = 50.dp,
            isOnline = conversation.peerOnline
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = conversation.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (conversation.isPinned) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = WeChatOrangePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(
                    text = timeFormatted,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conversation.lastMessage,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (conversation.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(WeChatOrangePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${conversation.unreadCount}",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    conversation: ConversationEntity,
    messages: List<MessageEntity>,
    onBack: () -> Unit,
    onSendMessage: (String, String) -> Unit,
    onReactMessage: (String, String) -> Unit,
    onConvertMessageToTask: (MessageEntity) -> Unit,
    onConvertMessageToReminder: (MessageEntity) -> Unit,
    onConvertMessageToResource: (MessageEntity) -> Unit,
    onStartCall: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var inputText by remember { mutableStateOf("") }
    var selectedMessageForActions by remember { mutableStateOf<MessageEntity?>(null) }
    var showAttachmentMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.testTag("chat_detail_screen"),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UserAvatar(
                            name = conversation.title,
                            size = 36.dp,
                            isOnline = conversation.peerOnline
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = conversation.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = if (conversation.peerOnline) "Online • End-to-End Encrypted" else "Offline",
                                fontSize = 11.sp,
                                color = if (conversation.peerOnline) Color(0xFF00C853) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { onStartCall(conversation.title, "AUDIO") }) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = "Voice Call", tint = WeChatOrangePrimary)
                    }
                    IconButton(onClick = { onStartCall(conversation.title, "VIDEO") }) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = "Video Call", tint = WeChatBlueSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Messages Stream
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    MessageBubble(
                        message = msg,
                        onLongClick = { selectedMessageForActions = msg },
                        onReact = { reaction -> onReactMessage(msg.id, reaction) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Attachment Options Menu
            AnimatedVisibility(visible = showAttachmentMenu) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        AttachmentOption(Icons.Default.Image, "Photo") {
                            onSendMessage("Shared photo asset", "IMAGE")
                            showAttachmentMenu = false
                        }
                        AttachmentOption(Icons.Default.Description, "Document") {
                            onSendMessage("Shared document specification", "DOC")
                            showAttachmentMenu = false
                        }
                        AttachmentOption(Icons.Default.LocationOn, "Location") {
                            onSendMessage("Live location: Tech Campus Core", "LOCATION")
                            showAttachmentMenu = false
                        }
                        AttachmentOption(Icons.Default.Mic, "Voice Note") {
                            onSendMessage("Voice note (0:18)", "VOICE")
                            showAttachmentMenu = false
                        }
                    }
                }
            }

            // Input Row
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showAttachmentMenu = !showAttachmentMenu }) {
                        Icon(
                            imageVector = if (showAttachmentMenu) Icons.Default.Close else Icons.Default.AddCircleOutline,
                            contentDescription = "Attach",
                            tint = WeChatOrangePrimary
                        )
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Message...", fontSize = 14.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        shape = RoundedCornerShape(20.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WeChatOrangePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                onSendMessage(inputText, "TEXT")
                                inputText = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(WeChatOrangePrimary)
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // Smart Actions Bottom Sheet
    if (selectedMessageForActions != null) {
        val targetMsg = selectedMessageForActions!!
        ModalBottomSheet(
            onDismissRequest = { selectedMessageForActions = null }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "Smart Message Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "\"${targetMsg.content.take(60)}\"",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                // Quick Reactions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("👍", "❤️", "🔥", "💡", "👏", "🎯").forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    onReactMessage(targetMsg.id, emoji)
                                    selectedMessageForActions = null
                                }
                                .padding(4.dp)
                        )
                    }
                }

                Divider()

                ListItem(
                    headlineContent = { Text("Convert to Task (Life Dashboard)", fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text("Add directly to personal to-do list with high priority") },
                    leadingContent = { Icon(Icons.Default.Checklist, contentDescription = null, tint = WeChatOrangePrimary) },
                    modifier = Modifier.clickable {
                        onConvertMessageToTask(targetMsg)
                        selectedMessageForActions = null
                    }
                )

                ListItem(
                    headlineContent = { Text("Schedule as Reminder", fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text("Trigger reminder notification in 2 hours") },
                    leadingContent = { Icon(Icons.Default.Alarm, contentDescription = null, tint = WeChatBlueSecondary) },
                    modifier = Modifier.clickable {
                        onConvertMessageToReminder(targetMsg)
                        selectedMessageForActions = null
                    }
                )

                ListItem(
                    headlineContent = { Text("Save to Universal Content Library", fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text("Store in personal encrypted resource vault") },
                    leadingContent = { Icon(Icons.Default.Bookmark, contentDescription = null, tint = WeChatOrangePrimary) },
                    modifier = Modifier.clickable {
                        onConvertMessageToResource(targetMsg)
                        selectedMessageForActions = null
                    }
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: MessageEntity,
    onLongClick: () -> Unit,
    onReact: (String) -> Unit
) {
    val isOut = message.isOutgoing
    val alignment = if (isOut) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isOut) 16.dp else 4.dp,
                bottomEnd = if (isOut) 4.dp else 16.dp
            ),
            color = if (isOut) WeChatOrangePrimary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clickable { onLongClick() }
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                when (message.messageType) {
                    "DOC" -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = if (isOut) Color.White else WeChatOrangePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = message.content,
                                color = if (isOut) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    "VOICE" -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = if (isOut) Color.White else WeChatBlueSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "••••|••••|•••• (0:18)",
                                color = if (isOut) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    "LOCATION" -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (isOut) Color.White else Color(0xFFD32F2F),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = message.content,
                                color = if (isOut) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp
                            )
                        }
                    }
                    else -> {
                        Text(
                            text = message.content,
                            color = if (isOut) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            lineHeight = 19.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
                    Text(
                        text = time,
                        fontSize = 10.sp,
                        color = (if (isOut) Color.White else MaterialTheme.colorScheme.onSurfaceVariant).copy(alpha = 0.7f)
                    )

                    if (isOut) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = when (message.status) {
                                "READ" -> Icons.Default.DoneAll
                                "DELIVERED" -> Icons.Default.Done
                                else -> Icons.Default.Schedule
                            },
                            contentDescription = message.status,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }

        // Reaction or Smart Action Note badge
        if (message.reaction.isNotBlank() || message.smartActionNote.isNotBlank()) {
            Row(
                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (message.reaction.isNotBlank()) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp
                    ) {
                        Text(
                            text = message.reaction,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                if (message.smartActionNote.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = WeChatBlueSecondary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = message.smartActionNote,
                            fontSize = 10.sp,
                            color = WeChatBlueSecondary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AttachmentOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(WeChatBlueSecondary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = WeChatBlueSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
