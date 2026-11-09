package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.WeChatBlueSecondary
import com.example.ui.theme.WeChatOrangePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: UserEntity?,
    tasks: List<LifeTaskEntity>,
    reminders: List<LifeReminderEntity>,
    savedResources: List<SavedResourceEntity>,
    onToggleTask: (LifeTaskEntity) -> Unit,
    onDeleteTask: (String) -> Unit,
    onAddTask: (String, String, String) -> Unit,
    onAddReminder: (String, String) -> Unit,
    onDeleteReminder: (String) -> Unit,
    onDeleteResource: (String) -> Unit,
    onChangePassword: (String, String, (Boolean, String) -> Unit) -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: (() -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    if (user == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = WeChatOrangePrimary)
        }
        return
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Dashboard", "Digital ID", "Library", "Security")

    Scaffold(
        modifier = modifier.testTag("profile_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sign Out",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Profile Header
            item {
                ProfileHeader(user)
            }

            // Tab bar
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = WeChatOrangePrimary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            when (selectedTab) {
                0 -> {
                    // Life Dashboard Tab (Tasks & Reminders)
                    item {
                        LifeDashboardTab(
                            tasks = tasks,
                            reminders = reminders,
                            onToggleTask = onToggleTask,
                            onDeleteTask = onDeleteTask,
                            onAddTask = onAddTask,
                            onAddReminder = onAddReminder,
                            onDeleteReminder = onDeleteReminder
                        )
                    }
                }
                1 -> {
                    // Digital Identity Card
                    item {
                        Box(modifier = Modifier.padding(16.dp)) {
                            DigitalIdCard(
                                user = user,
                                onShare = { /* Share simulation */ }
                            )
                        }
                    }
                }
                2 -> {
                    // Universal Content Library
                    item {
                        UniversalLibraryTab(
                            savedResources = savedResources,
                            onDeleteResource = onDeleteResource
                        )
                    }
                }
                3 -> {
                    // Security & Settings Tab
                    item {
                        SecuritySettingsTab(
                            user = user,
                            onChangePassword = onChangePassword,
                            onLogout = onLogout,
                            onDeleteAccount = onDeleteAccount
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(user: UserEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserAvatar(
                name = user.fullName,
                size = 72.dp,
                isOnline = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.fullName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Verified",
                    tint = WeChatOrangePrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = "@${user.username}",
                fontSize = 13.sp,
                color = WeChatBlueSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = user.bio,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Trust Score and Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileStat("${user.followersCount}", "Followers")
                Divider(modifier = Modifier.height(24.dp).width(1.dp))
                ProfileStat("${user.followingCount}", "Following")
                Divider(modifier = Modifier.height(24.dp).width(1.dp))
                ProfileStat("${user.trustScore}%", "Trust Score")
            }
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun LifeDashboardTab(
    tasks: List<LifeTaskEntity>,
    reminders: List<LifeReminderEntity>,
    onToggleTask: (LifeTaskEntity) -> Unit,
    onDeleteTask: (String) -> Unit,
    onAddTask: (String, String, String) -> Unit,
    onAddReminder: (String, String) -> Unit,
    onDeleteReminder: (String) -> Unit
) {
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showAddReminderDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Tasks Section
        SectionHeader(
            title = "Personal Life Tasks",
            actionText = "+ Add Task",
            onActionClick = { showAddTaskDialog = true }
        )

        if (tasks.isEmpty()) {
            Text(
                text = "No active tasks. Convert chat messages into tasks or create one directly.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            tasks.forEach { task ->
                TaskItem(task, onToggle = { onToggleTask(task) }, onDelete = { onDeleteTask(task.id) })
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reminders Section
        SectionHeader(
            title = "Scheduled Reminders",
            actionText = "+ Add Reminder",
            onActionClick = { showAddReminderDialog = true }
        )

        if (reminders.isEmpty()) {
            Text(
                text = "No upcoming reminders.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            reminders.forEach { reminder ->
                ReminderItem(reminder, onDelete = { onDeleteReminder(reminder.id) })
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }

    if (showAddTaskDialog) {
        var taskTitle by remember { mutableStateOf("") }
        var dueDate by remember { mutableStateOf("Today") }
        var priority by remember { mutableStateOf("NORMAL") }

        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("New Life Task") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    WeChatTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        label = "Task Description",
                        leadingIcon = Icons.Default.Checklist
                    )
                    WeChatTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = "Due Timeline",
                        leadingIcon = Icons.Default.CalendarToday
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (taskTitle.isNotBlank()) {
                            onAddTask(taskTitle, dueDate, priority)
                            showAddTaskDialog = false
                        }
                    }
                ) {
                    Text("Add Task", color = WeChatOrangePrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showAddReminderDialog) {
        var remTitle by remember { mutableStateOf("") }
        var remTime by remember { mutableStateOf("In 1 hour") }

        AlertDialog(
            onDismissRequest = { showAddReminderDialog = false },
            title = { Text("Schedule Reminder") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    WeChatTextField(
                        value = remTitle,
                        onValueChange = { remTitle = it },
                        label = "Reminder Note",
                        leadingIcon = Icons.Default.Alarm
                    )
                    WeChatTextField(
                        value = remTime,
                        onValueChange = { remTime = it },
                        label = "When",
                        leadingIcon = Icons.Default.Schedule
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (remTitle.isNotBlank()) {
                            onAddReminder(remTitle, remTime)
                            showAddReminderDialog = false
                        }
                    }
                ) {
                    Text("Save Reminder", color = WeChatOrangePrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddReminderDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun TaskItem(task: LifeTaskEntity, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = WeChatOrangePrimary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Due: ${task.dueDate}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (task.origin == "MESSAGE_ACTION") {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• From Chat",
                            fontSize = 11.sp,
                            color = WeChatBlueSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Close, contentDescription = "Delete", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun ReminderItem(reminder: LifeReminderEntity, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Alarm, contentDescription = null, tint = WeChatBlueSecondary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = reminder.title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(text = reminder.reminderTime, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Close, contentDescription = "Delete", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun UniversalLibraryTab(
    savedResources: List<SavedResourceEntity>,
    onDeleteResource: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader(title = "Universal Content Vault")
        Text(
            text = "Encrypted storage for saved messages, shared documents and links.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        if (savedResources.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Library is empty. Save bookmarks from posts or chats.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            savedResources.forEach { res ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (res.type) {
                                "FILE" -> Icons.Default.Description
                                "LINK" -> Icons.Default.Link
                                else -> Icons.Default.Bookmark
                            },
                            contentDescription = null,
                            tint = WeChatOrangePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = res.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = res.summary, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { onDeleteResource(res.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SecuritySettingsTab(
    user: UserEntity,
    onChangePassword: (String, String, (Boolean, String) -> Unit) -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: (() -> Unit) -> Unit
) {
    var showChangePassDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var twoFactorEnabled by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(title = "Account Security & Sessions")

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Two-Factor Authentication", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Protect session login with SMS/App verification", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = twoFactorEnabled,
                        onCheckedChange = { twoFactorEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = WeChatOrangePrimary)
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 10.dp))

                ListItem(
                    headlineContent = { Text("Active Session", fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text("Android Emulator • Current Device • Active Now") },
                    leadingContent = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = WeChatOrangePrimary) }
                )

                Divider(modifier = Modifier.padding(vertical = 10.dp))

                OutlinedButton(
                    onClick = { showChangePassDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Change Password")
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        WeChatButton(
            text = "Sign Out of WeChat",
            isSecondary = true,
            onClick = onLogout,
            leadingIcon = Icons.Default.Logout
        )

        WeChatButton(
            text = "Delete Account Permanently",
            isDanger = true,
            onClick = { showDeleteAccountDialog = true },
            leadingIcon = Icons.Default.DeleteForever
        )
    }

    if (showChangePassDialog) {
        var oldPass by remember { mutableStateOf("") }
        var newPass by remember { mutableStateOf("") }
        var passError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showChangePassDialog = false },
            title = { Text("Change Password") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (passError != null) {
                        Text(passError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                    WeChatTextField(
                        value = oldPass,
                        onValueChange = { oldPass = it },
                        label = "Current Password",
                        isPassword = true
                    )
                    WeChatTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = "New Password (min 6 chars)",
                        isPassword = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onChangePassword(oldPass, newPass) { success, msg ->
                            if (success) {
                                showChangePassDialog = false
                            } else {
                                passError = msg
                            }
                        }
                    }
                ) {
                    Text("Update", color = WeChatOrangePrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePassDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete Account?") },
            text = {
                Text("This will permanently remove your user profile, posts, messages, and cryptographic keys from the WeChat database. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteAccount { showDeleteAccountDialog = false }
                    }
                ) {
                    Text("Delete Permanently", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) { Text("Cancel") }
            }
        )
    }
}
