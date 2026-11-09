package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CallOverlay
import com.example.ui.components.StoryViewerDialog
import com.example.ui.screens.*
import com.example.ui.theme.WeChatBlueSecondary
import com.example.ui.theme.WeChatOrangePrimary
import com.example.ui.viewmodel.AppNavDestination
import com.example.ui.viewmodel.WeChatViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun WeChatApp(
    viewModel: WeChatViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()

    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val activeMessages by viewModel.activeMessages.collectAsStateWithLifecycle()
    val selectedConversation by viewModel.selectedConversation.collectAsStateWithLifecycle()
    val selectedPostForComments by viewModel.selectedPostForComments.collectAsStateWithLifecycle()
    val activePostComments by viewModel.activePostComments.collectAsStateWithLifecycle()
    val viewingStory by viewModel.viewingStory.collectAsStateWithLifecycle()

    val communities by viewModel.communities.collectAsStateWithLifecycle()
    val polls by viewModel.polls.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()
    val savedResources by viewModel.savedResources.collectAsStateWithLifecycle()
    val callLogs by viewModel.callLogs.collectAsStateWithLifecycle()

    val activeCall by viewModel.activeCall.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    if (activeSession == null) {
        // Unauthenticated -> Real Registration & Login flow
        AuthScreen(
            onLogin = { ident, pass, onSucc, onErr ->
                viewModel.login(ident, pass, onSucc, onErr)
            },
            onRegister = { name, user, email, phone, pass, dob, bio, onSucc, onErr ->
                viewModel.register(name, user, email, phone, pass, dob, bio, onSucc, onErr)
            },
            modifier = modifier
        )
    } else {
        // Authenticated -> 5-tab WeChat platform
        Scaffold(
            modifier = modifier.testTag("wechat_main_scaffold"),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (selectedConversation == null && activeCall == null) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        tonalElevation = 8.dp,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        // 1. Chats (WhatsApp Primary)
                        val totalUnread = conversations.sumOf { it.unreadCount }
                        NavigationBarItem(
                            selected = currentDestination == AppNavDestination.CHATS,
                            onClick = { viewModel.navigateTo(AppNavDestination.CHATS) },
                            icon = {
                                BadgedBox(badge = {
                                    if (totalUnread > 0) {
                                        Badge(containerColor = WeChatOrangePrimary) {
                                            Text("$totalUnread")
                                        }
                                    }
                                }) {
                                    Icon(
                                        imageVector = if (currentDestination == AppNavDestination.CHATS) Icons.Filled.Chat else Icons.Outlined.Chat,
                                        contentDescription = "Chats"
                                    )
                                }
                            },
                            label = { Text("Chats") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = WeChatOrangePrimary,
                                selectedTextColor = WeChatOrangePrimary,
                                indicatorColor = WeChatOrangePrimary.copy(alpha = 0.15f)
                            )
                        )

                        // 2. Updates (Status & Stories)
                        NavigationBarItem(
                            selected = currentDestination == AppNavDestination.UPDATES,
                            onClick = { viewModel.navigateTo(AppNavDestination.UPDATES) },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == AppNavDestination.UPDATES) Icons.Filled.DonutLarge else Icons.Outlined.DonutLarge,
                                    contentDescription = "Updates"
                                )
                            },
                            label = { Text("Updates") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = WeChatOrangePrimary,
                                selectedTextColor = WeChatOrangePrimary,
                                indicatorColor = WeChatOrangePrimary.copy(alpha = 0.15f)
                            )
                        )

                        // 3. Communities & Decision Center
                        NavigationBarItem(
                            selected = currentDestination == AppNavDestination.COMMUNITIES,
                            onClick = { viewModel.navigateTo(AppNavDestination.COMMUNITIES) },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == AppNavDestination.COMMUNITIES) Icons.Filled.Groups else Icons.Outlined.Groups,
                                    contentDescription = "Communities"
                                )
                            },
                            label = { Text("Communities") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = WeChatOrangePrimary,
                                selectedTextColor = WeChatOrangePrimary,
                                indicatorColor = WeChatOrangePrimary.copy(alpha = 0.15f)
                            )
                        )

                        // 4. Calls
                        NavigationBarItem(
                            selected = currentDestination == AppNavDestination.CALLS,
                            onClick = { viewModel.navigateTo(AppNavDestination.CALLS) },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == AppNavDestination.CALLS) Icons.Filled.Call else Icons.Outlined.Call,
                                    contentDescription = "Calls"
                                )
                            },
                            label = { Text("Calls") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = WeChatOrangePrimary,
                                selectedTextColor = WeChatOrangePrimary,
                                indicatorColor = WeChatOrangePrimary.copy(alpha = 0.15f)
                            )
                        )

                        // 5. Dashboard & Digital ID
                        NavigationBarItem(
                            selected = currentDestination == AppNavDestination.DASHBOARD,
                            onClick = { viewModel.navigateTo(AppNavDestination.DASHBOARD) },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == AppNavDestination.DASHBOARD) Icons.Filled.Widgets else Icons.Outlined.Widgets,
                                    contentDescription = "Dashboard"
                                )
                            },
                            label = { Text("Dashboard") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = WeChatOrangePrimary,
                                selectedTextColor = WeChatOrangePrimary,
                                indicatorColor = WeChatOrangePrimary.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (currentDestination) {
                    AppNavDestination.CHATS -> {
                        MessagesScreen(
                            conversations = conversations,
                            selectedConversation = selectedConversation,
                            messages = activeMessages,
                            onSelectConversation = { viewModel.openConversation(it) },
                            onCloseConversation = { viewModel.closeConversation() },
                            onSendMessage = { content, type -> viewModel.sendMessage(content, type) },
                            onReactMessage = { msgId, react -> viewModel.reactToMessage(msgId, react) },
                            onConvertMessageToTask = { viewModel.convertMessageToTask(it) },
                            onConvertMessageToReminder = { viewModel.convertMessageToReminder(it) },
                            onConvertMessageToResource = { viewModel.convertMessageToResource(it) },
                            onStartCall = { peer, type -> viewModel.startCall(peer, type) },
                            onStartNewChat = { name, isGrp -> viewModel.startNewChat(name, isGrp) }
                        )
                    }
                    AppNavDestination.UPDATES -> {
                        UpdatesScreen(
                            stories = stories,
                            channels = posts,
                            currentUser = currentUser,
                            onStoryClick = { viewModel.openStory(it) },
                            onCreateStory = { caption, bgHex, onDone ->
                                viewModel.createStory(caption, bgHex, onDone)
                            }
                        )
                    }
                    AppNavDestination.COMMUNITIES -> {
                        DiscoverScreen(
                            communities = communities,
                            polls = polls,
                            onToggleJoinCommunity = { viewModel.toggleJoinCommunity(it) },
                            onVotePoll = { pollId, idx -> viewModel.votePoll(pollId, idx) },
                            onCreateCommunityClick = {
                                viewModel.createCommunity(
                                    "WeChat Community Circle",
                                    "Collaborative group space with announcements and polls",
                                    "Technology",
                                    false,
                                    "Be respectful and constructive."
                                ) {}
                            }
                        )
                    }
                    AppNavDestination.CALLS -> {
                        CallsScreen(
                            callLogs = callLogs,
                            onStartCall = { peer, type -> viewModel.startCall(peer, type) }
                        )
                    }
                    AppNavDestination.DASHBOARD -> {
                        ProfileScreen(
                            user = currentUser,
                            tasks = tasks,
                            reminders = reminders,
                            savedResources = savedResources,
                            onToggleTask = { viewModel.toggleTask(it) },
                            onDeleteTask = { viewModel.deleteTask(it) },
                            onAddTask = { title, due, prio -> viewModel.addTask(title, due, prio) },
                            onAddReminder = { title, time -> viewModel.addReminder(title, time) },
                            onDeleteReminder = { viewModel.deleteReminder(it) },
                            onDeleteResource = { viewModel.deleteResource(it) },
                            onChangePassword = { oldPass, newPass, cb -> viewModel.changePassword(oldPass, newPass, cb) },
                            onLogout = { viewModel.logout() },
                            onDeleteAccount = { cb -> viewModel.deleteAccount(cb) }
                        )
                    }
                }

                // Story Viewer Overlay
                if (viewingStory != null) {
                    StoryViewerDialog(
                        story = viewingStory!!,
                        onDismiss = { viewModel.closeStory() },
                        onSendReaction = { reaction ->
                            viewModel.showFeedback("Reacted $reaction to story")
                        }
                    )
                }

                // Comments Bottom Sheet
                if (selectedPostForComments != null) {
                    CommentsBottomSheet(
                        post = selectedPostForComments!!,
                        comments = activePostComments,
                        onAddComment = { text ->
                            selectedPostForComments?.let { p ->
                                viewModel.addComment(p.id, text)
                            }
                        },
                        onDismiss = { viewModel.closeComments() }
                    )
                }

                // Active Audio/Video Call Overlay
                if (activeCall != null) {
                    CallOverlay(
                        callState = activeCall!!,
                        onMuteToggle = { viewModel.toggleMuteCall() },
                        onCameraToggle = { viewModel.toggleCameraCall() },
                        onSpeakerToggle = { viewModel.toggleSpeakerCall() },
                        onEndCall = { viewModel.endCall() }
                    )
                }
            }
        }
    }
}
