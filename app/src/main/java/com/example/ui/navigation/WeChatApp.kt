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
                        // 1. Home
                        NavigationBarItem(
                            selected = currentDestination == AppNavDestination.HOME,
                            onClick = { viewModel.navigateTo(AppNavDestination.HOME) },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == AppNavDestination.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text("Home") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = WeChatOrangePrimary,
                                selectedTextColor = WeChatOrangePrimary,
                                indicatorColor = WeChatOrangePrimary.copy(alpha = 0.15f)
                            )
                        )

                        // 2. Discover
                        NavigationBarItem(
                            selected = currentDestination == AppNavDestination.DISCOVER,
                            onClick = { viewModel.navigateTo(AppNavDestination.DISCOVER) },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == AppNavDestination.DISCOVER) Icons.Filled.Explore else Icons.Outlined.Explore,
                                    contentDescription = "Discover"
                                )
                            },
                            label = { Text("Discover") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = WeChatOrangePrimary,
                                selectedTextColor = WeChatOrangePrimary,
                                indicatorColor = WeChatOrangePrimary.copy(alpha = 0.15f)
                            )
                        )

                        // 3. Create Studio
                        NavigationBarItem(
                            selected = currentDestination == AppNavDestination.CREATE,
                            onClick = { viewModel.navigateTo(AppNavDestination.CREATE) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.AddCircle,
                                    contentDescription = "Create",
                                    tint = WeChatOrangePrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            },
                            label = { Text("Create") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = WeChatOrangePrimary,
                                selectedTextColor = WeChatOrangePrimary,
                                indicatorColor = WeChatOrangePrimary.copy(alpha = 0.15f)
                            )
                        )

                        // 4. Messages
                        val totalUnread = conversations.sumOf { it.unreadCount }
                        NavigationBarItem(
                            selected = currentDestination == AppNavDestination.MESSAGES,
                            onClick = { viewModel.navigateTo(AppNavDestination.MESSAGES) },
                            icon = {
                                BadgedBox(badge = {
                                    if (totalUnread > 0) {
                                        Badge(containerColor = WeChatOrangePrimary) {
                                            Text("$totalUnread")
                                        }
                                    }
                                }) {
                                    Icon(
                                        imageVector = if (currentDestination == AppNavDestination.MESSAGES) Icons.Filled.Chat else Icons.Outlined.Chat,
                                        contentDescription = "Messages"
                                    )
                                }
                            },
                            label = { Text("Messages") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = WeChatOrangePrimary,
                                selectedTextColor = WeChatOrangePrimary,
                                indicatorColor = WeChatOrangePrimary.copy(alpha = 0.15f)
                            )
                        )

                        // 5. Profile
                        NavigationBarItem(
                            selected = currentDestination == AppNavDestination.PROFILE,
                            onClick = { viewModel.navigateTo(AppNavDestination.PROFILE) },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == AppNavDestination.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "Profile"
                                )
                            },
                            label = { Text("Profile") },
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
                    AppNavDestination.HOME -> {
                        HomeScreen(
                            posts = posts,
                            stories = stories,
                            currentUser = currentUser,
                            onStoryClick = { viewModel.openStory(it) },
                            onAddStoryClick = { viewModel.navigateTo(AppNavDestination.CREATE) },
                            onLikePost = { viewModel.toggleLikePost(it) },
                            onBookmarkPost = { viewModel.toggleBookmarkPost(it) },
                            onOpenComments = { viewModel.openComments(it) },
                            onDeletePost = { viewModel.deletePost(it) },
                            onCreatePostClick = { viewModel.navigateTo(AppNavDestination.CREATE) }
                        )
                    }
                    AppNavDestination.DISCOVER -> {
                        DiscoverScreen(
                            communities = communities,
                            polls = polls,
                            onToggleJoinCommunity = { viewModel.toggleJoinCommunity(it) },
                            onVotePoll = { pollId, idx -> viewModel.votePoll(pollId, idx) },
                            onCreateCommunityClick = { viewModel.navigateTo(AppNavDestination.CREATE) }
                        )
                    }
                    AppNavDestination.CREATE -> {
                        CreateScreen(
                            onCreatePost = { content, tags, type, onDone ->
                                viewModel.createPost(content, tags, type) {
                                    onDone()
                                    viewModel.navigateTo(AppNavDestination.HOME)
                                }
                            },
                            onCreateStory = { caption, bgHex, onDone ->
                                viewModel.createStory(caption, bgHex) {
                                    onDone()
                                    viewModel.navigateTo(AppNavDestination.HOME)
                                }
                            },
                            onCreateCommunity = { name, desc, cat, priv, rules, onDone ->
                                viewModel.createCommunity(name, desc, cat, priv, rules) {
                                    onDone()
                                    viewModel.navigateTo(AppNavDestination.DISCOVER)
                                }
                            },
                            onCreatePoll = { commId, q, opts, onDone ->
                                viewModel.createPoll(commId, q, opts) {
                                    onDone()
                                    viewModel.navigateTo(AppNavDestination.DISCOVER)
                                }
                            },
                            onNavigateBack = { viewModel.navigateTo(AppNavDestination.HOME) }
                        )
                    }
                    AppNavDestination.MESSAGES -> {
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
                    AppNavDestination.PROFILE -> {
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
