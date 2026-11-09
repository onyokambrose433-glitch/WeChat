package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WeChatButton
import com.example.ui.components.WeChatTextField
import com.example.ui.theme.WeChatBlueSecondary
import com.example.ui.theme.WeChatOrangePrimary

@Composable
fun AuthScreen(
    onLogin: (String, String, () -> Unit, (String) -> Unit) -> Unit,
    onRegister: (String, String, String, String, String, String, String, () -> Unit, (String) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var isRegisterMode by remember { mutableStateOf(false) }

    // Form fields
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var emailOrIdentifier by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    fun submitRegistration() {
        if (fullName.isBlank()) {
            errorMessage = "Please enter your full name."
            return
        }
        if (username.isBlank()) {
            errorMessage = "Please enter a unique username."
            return
        }
        if (username.trim().length < 3) {
            errorMessage = "Username must be at least 3 characters."
            return
        }
        if (emailOrIdentifier.isBlank() || !emailOrIdentifier.contains("@")) {
            errorMessage = "Please enter a valid email address."
            return
        }
        if (password.length < 6) {
            errorMessage = "Password must be at least 6 characters."
            return
        }
        if (password != confirmPassword) {
            errorMessage = "Passwords do not match."
            return
        }

        isLoading = true
        errorMessage = null
        onRegister(
            fullName.trim(),
            username.trim().removePrefix("@"),
            emailOrIdentifier.trim(),
            phone.trim(),
            password,
            dob.trim(),
            bio.trim(),
            { isLoading = false },
            { err ->
                isLoading = false
                errorMessage = err
            }
        )
    }

    fun submitLogin() {
        if (emailOrIdentifier.isBlank()) {
            errorMessage = "Please enter your username or email."
            return
        }
        if (password.isBlank()) {
            errorMessage = "Please enter your password."
            return
        }

        isLoading = true
        errorMessage = null
        onLogin(
            emailOrIdentifier.trim(),
            password,
            { isLoading = false },
            { err ->
                isLoading = false
                errorMessage = err
            }
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("auth_screen"),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // Fixed bottom bar guaranteeing the completion button is ALWAYS visible and accessible
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .navigationBarsPadding()
                ) {
                    if (isRegisterMode) {
                        WeChatButton(
                            text = if (isLoading) "Creating Account..." else "Complete Account Creation",
                            leadingIcon = Icons.Default.CheckCircle,
                            enabled = !isLoading,
                            onClick = { submitRegistration() },
                            testTag = "complete_account_creation_button"
                        )
                    } else {
                        WeChatButton(
                            text = if (isLoading) "Signing In..." else "Sign In to WeChat",
                            leadingIcon = Icons.Default.Login,
                            enabled = !isLoading,
                            onClick = { submitLogin() },
                            testTag = "submit_login_button"
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
        ) {
            item {
                // WeChat Brand Emblem
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(WeChatOrangePrimary, WeChatBlueSecondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "WeChat Logo",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "WeChat",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = if (isRegisterMode) "Enter your profile details below, then tap 'Complete Account Creation'." else "Next-Generation Social Communication Platform",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mode Selector Tabs (Sign In / Create Account)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (!isRegisterMode) WeChatOrangePrimary else Color.Transparent)
                                .clickable {
                                    isRegisterMode = false
                                    errorMessage = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sign In",
                                fontWeight = FontWeight.Bold,
                                color = if (!isRegisterMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isRegisterMode) WeChatOrangePrimary else Color.Transparent)
                                .clickable {
                                    isRegisterMode = true
                                    errorMessage = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Create Account",
                                fontWeight = FontWeight.Bold,
                                color = if (isRegisterMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error Message banner
                AnimatedVisibility(visible = errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            if (isRegisterMode) {
                // Registration Form fields
                item {
                    WeChatTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = "Full Name *",
                        leadingIcon = Icons.Default.Badge,
                        testTag = "reg_fullname_input"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    WeChatTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = "Unique Username (@handle) *",
                        leadingIcon = Icons.Default.AlternateEmail,
                        testTag = "reg_username_input"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    WeChatTextField(
                        value = emailOrIdentifier,
                        onValueChange = { emailOrIdentifier = it },
                        label = "Email Address *",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        testTag = "reg_email_input"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    WeChatTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Phone Number (with Country Code)",
                        leadingIcon = Icons.Default.Phone,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        testTag = "reg_phone_input"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    WeChatTextField(
                        value = dob,
                        onValueChange = { dob = it },
                        label = "Date of Birth (YYYY-MM-DD)",
                        leadingIcon = Icons.Default.CalendarToday,
                        testTag = "reg_dob_input"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    WeChatTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = "Biography / About You",
                        leadingIcon = Icons.Default.Info,
                        singleLine = false,
                        testTag = "reg_bio_input"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    WeChatTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password (min 6 chars) *",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        testTag = "reg_pass_input"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    WeChatTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirm Password *",
                        leadingIcon = Icons.Default.LockReset,
                        isPassword = true,
                        testTag = "reg_confpass_input"
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    // Also provide in-form complete button for direct inline submission
                    WeChatButton(
                        text = if (isLoading) "Creating Account..." else "Complete Account Creation",
                        leadingIcon = Icons.Default.CheckCircle,
                        enabled = !isLoading,
                        onClick = { submitRegistration() },
                        testTag = "inline_complete_account_creation_button"
                    )

                    Spacer(modifier = Modifier.height(30.dp))
                }
            } else {
                // Login Form fields
                item {
                    WeChatTextField(
                        value = emailOrIdentifier,
                        onValueChange = { emailOrIdentifier = it },
                        label = "Username or Email",
                        leadingIcon = Icons.Default.Person,
                        testTag = "login_identifier_input"
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    WeChatTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        testTag = "login_pass_input"
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    WeChatButton(
                        text = if (isLoading) "Signing In..." else "Sign In to WeChat",
                        leadingIcon = Icons.Default.Login,
                        enabled = !isLoading,
                        onClick = { submitLogin() },
                        testTag = "inline_submit_login_button"
                    )

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}
