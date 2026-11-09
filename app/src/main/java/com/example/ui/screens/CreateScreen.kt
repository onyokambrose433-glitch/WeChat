package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WeChatButton
import com.example.ui.components.WeChatTextField
import com.example.ui.theme.WeChatBlueSecondary
import com.example.ui.theme.WeChatOrangePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    onCreatePost: (String, String, String, () -> Unit) -> Unit,
    onCreateStory: (String, String, () -> Unit) -> Unit,
    onCreateCommunity: (String, String, String, Boolean, String, () -> Unit) -> Unit,
    onCreatePoll: (String, String, List<String>, () -> Unit) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Post", "Story", "Community", "Poll")

    Scaffold(
        modifier = modifier.testTag("create_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Studio",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
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
            // Mode Tabs
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                when (selectedTab) {
                    0 -> item { CreatePostTab(onCreatePost, onNavigateBack) }
                    1 -> item { CreateStoryTab(onCreateStory, onNavigateBack) }
                    2 -> item { CreateCommunityTab(onCreateCommunity, onNavigateBack) }
                    3 -> item { CreatePollTab(onCreatePoll, onNavigateBack) }
                }
            }
        }
    }
}

@Composable
private fun CreatePostTab(
    onCreatePost: (String, String, String, () -> Unit) -> Unit,
    onSuccess: () -> Unit
) {
    var content by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("#WeChat,#Connect") }
    var mediaType by remember { mutableStateOf("NONE") }
    var visibility by remember { mutableStateOf("PUBLIC") }
    var isPosting by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Share to Personal Social Timeline",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            placeholder = { Text("What's on your mind? Share thoughts, articles or project updates...", fontSize = 14.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .testTag("create_post_content_input"),
            shape = RoundedCornerShape(12.dp)
        )

        WeChatTextField(
            value = tags,
            onValueChange = { tags = it },
            label = "Tags (comma-separated)",
            leadingIcon = Icons.Default.Tag,
            testTag = "create_post_tags_input"
        )

        // Media Type Selector
        Text("Attachment Type", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("NONE" to "Text Only", "IMAGE" to "Photo", "DOCUMENT" to "Document").forEach { (type, label) ->
                FilterChip(
                    selected = mediaType == type,
                    onClick = { mediaType = type },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WeChatOrangePrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        WeChatButton(
            text = if (isPosting) "Publishing..." else "Publish Post",
            enabled = content.isNotBlank() && !isPosting,
            onClick = {
                isPosting = true
                onCreatePost(content, tags, mediaType) {
                    isPosting = false
                    onSuccess()
                }
            },
            testTag = "submit_post_button"
        )
    }
}

@Composable
private fun CreateStoryTab(
    onCreateStory: (String, String, () -> Unit) -> Unit,
    onSuccess: () -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var selectedBg by remember { mutableStateOf("#E15712") }
    val bgColors = listOf("#E15712", "#2FA0E1", "#1A7BB8", "#4A148C", "#1B5E20", "#212121")
    var isPosting by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Create 24-Hour Story / Moment",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        OutlinedTextField(
            value = caption,
            onValueChange = { caption = it },
            placeholder = { Text("Write your story caption or daily moment update...", fontSize = 14.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .testTag("create_story_caption_input"),
            shape = RoundedCornerShape(12.dp)
        )

        Text("Select Background Theme", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            bgColors.forEach { hex ->
                val color = Color(android.graphics.Color.parseColor(hex))
                val isSelected = selectedBg == hex
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { selectedBg = hex }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        WeChatButton(
            text = if (isPosting) "Sharing..." else "Share Story",
            enabled = caption.isNotBlank() && !isPosting,
            onClick = {
                isPosting = true
                onCreateStory(caption, selectedBg) {
                    isPosting = false
                    onSuccess()
                }
            },
            testTag = "submit_story_button"
        )
    }
}

@Composable
private fun CreateCommunityTab(
    onCreateCommunity: (String, String, String, Boolean, String, () -> Unit) -> Unit,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Technology") }
    var isPrivate by remember { mutableStateOf(false) }
    var rules by remember { mutableStateOf("Be respectful and constructive.") }
    var isPosting by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Launch an Interactive Community", fontWeight = FontWeight.Bold, fontSize = 15.sp)

        WeChatTextField(
            value = name,
            onValueChange = { name = it },
            label = "Community Name",
            leadingIcon = Icons.Default.Groups,
            testTag = "comm_name_input"
        )

        WeChatTextField(
            value = desc,
            onValueChange = { desc = it },
            label = "Community Description & Mission",
            singleLine = false,
            leadingIcon = Icons.Default.Description,
            testTag = "comm_desc_input"
        )

        Text("Category", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Technology", "Creative", "Business", "Education", "Local").forEach { cat ->
                FilterChip(
                    selected = category == cat,
                    onClick = { category = cat },
                    label = { Text(cat, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WeChatOrangePrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Private Community", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("Members require moderator approval", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked = isPrivate,
                onCheckedChange = { isPrivate = it },
                colors = SwitchDefaults.colors(checkedThumbColor = WeChatOrangePrimary)
            )
        }

        WeChatTextField(
            value = rules,
            onValueChange = { rules = it },
            label = "Community Guidelines & Rules",
            leadingIcon = Icons.Default.Gavel,
            testTag = "comm_rules_input"
        )

        Spacer(modifier = Modifier.height(8.dp))

        WeChatButton(
            text = if (isPosting) "Creating..." else "Create Community",
            enabled = name.isNotBlank() && desc.isNotBlank() && !isPosting,
            onClick = {
                isPosting = true
                onCreateCommunity(name, desc, category, isPrivate, rules) {
                    isPosting = false
                    onSuccess()
                }
            },
            testTag = "submit_community_button"
        )
    }
}

@Composable
private fun CreatePollTab(
    onCreatePoll: (String, String, List<String>, () -> Unit) -> Unit,
    onSuccess: () -> Unit
) {
    var question by remember { mutableStateOf("") }
    var opt1 by remember { mutableStateOf("") }
    var opt2 by remember { mutableStateOf("") }
    var opt3 by remember { mutableStateOf("") }
    var isPosting by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Propose Group Decision / Poll", fontWeight = FontWeight.Bold, fontSize = 15.sp)

        WeChatTextField(
            value = question,
            onValueChange = { question = it },
            label = "Decision Question / Topic",
            leadingIcon = Icons.Default.HelpOutline,
            testTag = "poll_question_input"
        )

        WeChatTextField(
            value = opt1,
            onValueChange = { opt1 = it },
            label = "Option 1",
            leadingIcon = Icons.Default.RadioButtonUnchecked,
            testTag = "poll_opt1_input"
        )

        WeChatTextField(
            value = opt2,
            onValueChange = { opt2 = it },
            label = "Option 2",
            leadingIcon = Icons.Default.RadioButtonUnchecked,
            testTag = "poll_opt2_input"
        )

        WeChatTextField(
            value = opt3,
            onValueChange = { opt3 = it },
            label = "Option 3 (Optional)",
            leadingIcon = Icons.Default.RadioButtonUnchecked,
            testTag = "poll_opt3_input"
        )

        Spacer(modifier = Modifier.height(8.dp))

        WeChatButton(
            text = if (isPosting) "Publishing Poll..." else "Launch Community Vote",
            enabled = question.isNotBlank() && opt1.isNotBlank() && opt2.isNotBlank() && !isPosting,
            onClick = {
                val opts = listOfNotNull(opt1, opt2, opt3.ifBlank { null })
                isPosting = true
                onCreatePoll("comm_tech", question, opts) {
                    isPosting = false
                    onSuccess()
                }
            },
            testTag = "submit_poll_button"
        )
    }
}
