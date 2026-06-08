package cst.unibucfmiif2026.ui.pages

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cst.unibucfmiif2026.ui.theme.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.getValue

@Composable
fun LbLogo() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(50))
                .background(LbGreen)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "movie tracker",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = LbTextPrimary,
            letterSpacing = (-0.5).sp
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "your personal film journal",
        fontSize = 12.sp,
        color = LbTextSecondary,
        letterSpacing = 0.5.sp
    )
}

@Composable
fun LbTabRow(
    selectedIndex: Int,
    onLoginTab: () -> Unit,
    onRegisterTab: () -> Unit
) {
    val tabs = listOf("SIGN IN", "SIGN UP")
    val actions = listOf(onLoginTab, onRegisterTab)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(LbBorderSubtle)
            .height(1.dp)
    )

    Row(modifier = Modifier.fillMaxWidth()) {
        tabs.forEachIndexed { index, label ->
            val isSelected = selectedIndex == index
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) LbTextPrimary else LbTextMuted,
                animationSpec = tween(durationMillis = 250),
                label = "tab_color_$index"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { actions[index]() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    // Indicator animat
                    val indicatorWidth by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0f,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                        label = "indicator_$index"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(indicatorWidth)
                            .height(2.dp)
                            .background(LbGreen)
                    )
                }
            }
        }
    }
}

@Composable
fun LbTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val borderColor = when {
        isError -> LbError
        value.isNotEmpty() -> LbGreen
        else -> LbBorder
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = LbTextSecondary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = LbTextMuted, fontSize = 14.sp) },
            singleLine = true,
            isError = isError,
            keyboardOptions = keyboardOptions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = LbTextPrimary,
                unfocusedTextColor = LbTextPrimary,
                focusedBorderColor = LbGreen,
                unfocusedBorderColor = borderColor,
                errorBorderColor = LbError,
                focusedContainerColor = LbSurface,
                unfocusedContainerColor = LbSurface,
                errorContainerColor = LbSurface,
                cursorColor = LbGreen
            ),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        )
        errorMessage?.let {
            Text(
                text = it,
                color = LbError,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun LbPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    showStrength: Boolean = false
) {
    val strength = when {
        value.length >= 10 && value.any { it.isDigit() } && value.any { !it.isLetterOrDigit() } -> 4
        value.length >= 8 && value.any { it.isDigit() } -> 3
        value.length >= 6 -> 2
        value.isNotEmpty() -> 1
        else -> 0
    }
    val strengthColor = when (strength) {
        4 -> LbGreen
        3 -> LbGreen
        2 -> LbOrange
        else -> LbError
    }
    val strengthLabel = when (strength) {
        4 -> "Strong"
        3 -> "Good"
        2 -> "Medium — add a number"
        1 -> "Too short"
        else -> ""
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = LbTextSecondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            if (showStrength && strength > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(strengthColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = strengthLabel.uppercase(),
                        fontSize = 9.sp,
                        color = strengthColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            isError = isError,
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = if (value.isNotEmpty()) LbGreen else LbTextMuted
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = LbTextPrimary,
                unfocusedTextColor = LbTextPrimary,
                focusedBorderColor = LbGreen,
                unfocusedBorderColor = if (isError) LbError else LbBorder,
                errorBorderColor = LbError,
                focusedContainerColor = LbSurface,
                unfocusedContainerColor = LbSurface,
                errorContainerColor = LbSurface,
                cursorColor = LbGreen
            ),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        )

        if (showStrength && strength > 0) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(4) { i ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (i < strength) strengthColor else LbBorder)
                    )
                }
            }
        }

        errorMessage?.let {
            Text(
                text = it,
                color = LbError,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun LbPrimaryButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = LbGreen,
            contentColor = Color(0xFF14120E),
            disabledContainerColor = LbGreen.copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color(0xFF14120E),
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 0.8.sp
            )
        }
    }
}

@Composable
fun LbSecondaryButton(
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = LbTextSecondary
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, LbBorder)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            letterSpacing = 0.6.sp
        )
    }
}

@Composable
fun LbDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = LbBorderSubtle
        )
        Text(
            text = "  or  ",
            color = LbTextMuted,
            fontSize = 11.sp,
            letterSpacing = 0.5.sp
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = LbBorderSubtle
        )
    }
}

@Composable
fun LbErrorMessage(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(LbError.copy(alpha = 0.1f))
            .border(1.dp, LbError.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message,
            color = LbError,
            fontSize = 12.sp
        )
    }
}

@Composable
fun LbGoogleButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = LbTextPrimary
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, LbBorder)
    ) {
        Canvas(modifier = Modifier.size(18.dp)) {
            val r = size.minDimension / 2f
            val cx = size.width / 2f
            val cy = size.height / 2f
            drawArc(color = Color(0xFF4285F4), startAngle = -45f, sweepAngle = 180f, useCenter = false, style = Stroke(width = r * 0.35f))
            drawArc(color = Color(0xFF34A853), startAngle = 45f, sweepAngle = 90f, useCenter = false, style = Stroke(width = r * 0.35f))
            drawArc(color = Color(0xFFFBBC05), startAngle = 135f, sweepAngle = 90f, useCenter = false, style = Stroke(width = r * 0.35f))
            drawArc(color = Color(0xFFEA4335), startAngle = -135f, sweepAngle = 90f, useCenter = false, style = Stroke(
                width = r * 0.35f
            )
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "CONTINUE WITH GOOGLE",
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            letterSpacing = 0.6.sp
        )
    }
}