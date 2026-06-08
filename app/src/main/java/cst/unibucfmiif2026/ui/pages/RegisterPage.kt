package cst.unibucfmiif2026.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cst.unibucfmiif2026.R
import cst.unibucfmiif2026.ui.theme.*
import cst.unibucfmiif2026.utils.isValidEmail
import cst.unibucfmiif2026.utils.isValidPassword

@Composable
fun RegisterPage(
    onLoginClick: () -> Unit = {},
    onRegisterClick: (email: String, password: String, onSuccess: () -> Unit) -> Unit = { _, _, _ -> },
    onRegisterSuccess: () -> Unit = {},
    onContinueAsGuest: () -> Unit = {},
    onGoogleSignIn: () -> Unit = {},
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onErrorDismiss: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val invalidEmailMessage = stringResource(R.string.invalid_email)
    val invalidPasswordMessage = stringResource(R.string.invalid_password)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LbBackground)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        LbLogo()

        Spacer(modifier = Modifier.height(40.dp))

        LbTabRow(
            selectedIndex = 1,
            onLoginTab = onLoginClick,
            onRegisterTab = {}
        )

        Spacer(modifier = Modifier.height(28.dp))

        LbTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
                onErrorDismiss()
            },
            label = "EMAIL",
            placeholder = "your@email.com",
            isError = emailError != null,
            errorMessage = emailError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        LbPasswordField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
                onErrorDismiss()
            },
            label = "PASSWORD",
            isVisible = isPasswordVisible,
            onToggleVisibility = { isPasswordVisible = !isPasswordVisible },
            isError = passwordError != null,
            errorMessage = passwordError,
            showStrength = true
        )

        errorMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            LbErrorMessage(message = it)
        }

        Spacer(modifier = Modifier.height(24.dp))

        LbPrimaryButton(
            text = if (isLoading) "" else "CREATE ACCOUNT",
            onClick = {
                var isValid = true
                if (!email.isValidEmail()) {
                    emailError = invalidEmailMessage
                    isValid = false
                }
                if (!password.isValidPassword()) {
                    passwordError = invalidPasswordMessage
                    isValid = false
                }
                if (isValid) onRegisterClick(email, password, onRegisterSuccess)
            },
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        LbDivider()

        Spacer(modifier = Modifier.height(16.dp))
        LbGoogleButton(onClick = onGoogleSignIn)

        Spacer(modifier = Modifier.height(16.dp))

        LbSecondaryButton(
            text = "CONTINUE AS GUEST",
            onClick = onContinueAsGuest
        )

        Spacer(modifier = Modifier.height(20.dp))

//        Row(
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = "Already a member? ",
//                color = LbTextSecondary,
//                fontSize = 13.sp
//            )
//            TextButton(
//                onClick = onLoginClick,
//                contentPadding = PaddingValues(0.dp)
//            ) {
//                Text(
//                    text = "Sign in",
//                    color = LbBlue,
//                    fontSize = 13.sp,
//                    fontWeight = FontWeight.SemiBold
//                )
//            }
//        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPagePreview() {
    RegisterPage()
}