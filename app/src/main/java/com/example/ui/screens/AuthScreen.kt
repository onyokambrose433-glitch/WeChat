package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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
    var phone by remember { mutableStateOf("+1 ") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("1998-05-12") }
    var bio by remember { mutableStateOf("Ready to connect on WeChat.") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("auth_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            item {
                // WeChat Brand Emblem
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(20.dp))
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
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "WeChat",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = if (isRegisterMode) "Create your verified digital social profile" else "Next-Generation Social Communication Platform",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Mode Selector Tabs (Sign In / Register)
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

                Spacer(modifier = Modifier.height(20.dp))

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
                // Registration Form
                item {
                    WeChatTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = "Full Name",
                        leadingIcon = Icons.Default.Badge,
                        testTag = "reg_fullname_input"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    WeChatTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = "Unique Username (@handle)",
                        leadingIcon = Icons.Default.AlternateEmail,
                        testTag = "reg_username_input"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    WeChatTextField(
                        value = emailOrIdentifier,
                        onValueChange = { emailOrIdentifier = it },
                        label = "Email Address",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        testTag = "reg_email_input"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    WeChatTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Phone Number with Country Code",
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
                        label = "Personal Biography",
                        leadingIcon = Icons.Default.Info,
                        singleLine = false,
                        testTag = "reg_bio_input"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    WeChatTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password (min 6 chars)",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        testTag = "reg_pass_input"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    WeChatTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirm Password",
                        leadingIcon = Icons.Default.LockReset,
                        isPassword = true,
                        testTag = "reg_confpass_input"
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    WeChatButton(
                        text = if (isLoading) "Creating Cryptographic Profile..." else "Register & Connect",
                        enabled = !isLoading,
                        onClick = {
                            if (password != confirmPassword) {
                                errorMessage = "Passwords do not match."
                                return@WeChatButton
                            }
                            if (fullName.isBlank() || username.isBlank() || emailOrIdentifier.isBlank()) {
                                errorMessage = "Please fill in all required fields."
                                return@WeChatButton
                            }
                            isLoading = true
                            errorMessage = null
                            onRegister(
                                fullName,
                                username,
                                emailOrIdentifier,
                                phone,
                                password,
                                dob,
                                bio,
                                { isLoading = false },
                                { err ->
                                    isLoading = false
                                    errorMessage = err
                                }
                            )
                        },
                        testTag = "submit_registration_button"
                    )
                }
            } else {
                // Login Form
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
                    Spacer(modifier = Modifier.height(24.dp))

                    WeChatButton(
                        text = if (isLoading) "Verifying Credentials..." else "Sign In to WeChat",
                        enabled = !isLoading,
                        onClick = {
                            if (emailOrIdentifier.isBlank() || password.isBlank()) {
                                errorMessage = "Please enter your username/email and password."
                                return@WeChatButton
                            }
                            isLoading = true
                            errorMessage = null
                            onLogin(
                                emailOrIdentifier,
                                password,
                                { isLoading = false },
                                { err ->
                                    isLoading = false
                                    errorMessage = err
                                }
                            )
                        },
                        testTag = "submit_login_button"
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Quick Demo Login hint
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = WeChatBlueSecondary.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WeChatBlueSecondary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = WeChatBlueSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Initial Accounts Ready in Database",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = WeChatBlueSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Username: alex_chen or sophia_v\nPassword: WeChat@2026\nOr create your own unique account above!",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
