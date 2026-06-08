package cst.unibucfmiif2026.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cst.unibucfmiif2026.ui.theme.*
import cst.unibucfmiif2026.utils.isValidEmail

@Composable
fun ForgotPasswordPage(
    onBack: () -> Unit = {},
    onSendReset: (email: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit = { _, _, _ -> }
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LbBackground)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(52.dp))

        // Back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = LbTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LbLogo()

        Spacer(modifier = Modifier.height(40.dp))

        // Titlu
        Text(
            text = "RESET PASSWORD",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = LbTextPrimary,
            letterSpacing = 1.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enter your email and we'll send you a link to reset your password.",
            fontSize = 13.sp,
            color = LbTextSecondary,
            lineHeight = 20.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        if (successMessage != null) {
            // Success state
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        LbGreen.copy(alpha = 0.08f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Email sent!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = LbGreen
                )
                Text(
                    text = successMessage!!,
                    fontSize = 13.sp,
                    color = LbTextSecondary,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LbSecondaryButton(
                text = "BACK TO SIGN IN",
                onClick = onBack
            )
        } else {
            // Form state
            LbTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                label = "EMAIL",
                placeholder = "your@email.com",
                isError = emailError != null,
                errorMessage = emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            LbPrimaryButton(
                text = if (isLoading) "" else "SEND RESET LINK",
                isLoading = isLoading,
                onClick = {
                    if (!email.isValidEmail()) {
                        emailError = "Please enter a valid email."
                        return@LbPrimaryButton
                    }
                    isLoading = true
                    onSendReset(
                        email,
                        {
                            isLoading = false
                            successMessage = "Check your inbox at $email and follow the link to reset your password."
                        },
                        { error ->
                            isLoading = false
                            emailError = error
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LbSecondaryButton(
                text = "BACK TO SIGN IN",
                onClick = onBack
            )
        }
    }
}