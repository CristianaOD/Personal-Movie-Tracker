package cst.unibucfmiif2026.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cst.unibucfmiif2026.R
import cst.unibucfmiif2026.ui.theme.LbBackground
import cst.unibucfmiif2026.ui.theme.LbBorder
import cst.unibucfmiif2026.ui.theme.LbGreen
import cst.unibucfmiif2026.ui.theme.LbSurface
import cst.unibucfmiif2026.ui.theme.LbSurfaceRaised
import cst.unibucfmiif2026.ui.theme.LbTextMuted
import cst.unibucfmiif2026.ui.theme.LbTextPrimary
import cst.unibucfmiif2026.ui.theme.LbTextSecondary
import cst.unibucfmiif2026.ui.theme.UniBucFMIIF2026Theme

@Composable
fun SettingsPage(
    displayName: String?,
    userEmail: String?,
    isLoggedIn: Boolean,
    isDarkModeEnabled: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onUpdateDisplayName: (String, () -> Unit, (String) -> Unit) -> Unit,
    onLogout: () -> Unit
) {
    var editableName by remember { mutableStateOf(displayName.orEmpty()) }
    var profileMessage by remember { mutableStateOf<String?>(null) }
    var isSavingName by remember { mutableStateOf(false) }

    LaunchedEffect(displayName) {
        editableName = displayName.orEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LbBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.settings_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = LbTextPrimary
        )
        Text(
            text = stringResource(R.string.settings_subtitle),
            fontSize = 12.sp,
            color = LbTextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        ProfileHero(
            displayName = displayName,
            userEmail = userEmail,
            isLoggedIn = isLoggedIn
        )

        Spacer(modifier = Modifier.height(14.dp))

        ProfileSection(title = "ACCOUNT DETAILS") {
            ProfileInfoRow(
                icon = Icons.Outlined.VerifiedUser,
                label = "Status",
                value = if (isLoggedIn) {
                    stringResource(R.string.account_status_logged_in)
                } else {
                    stringResource(R.string.account_status_guest)
                }
            )
            ProfileInfoRow(
                icon = Icons.Outlined.Email,
                label = "Email",
                value = userEmail ?: stringResource(R.string.guest_mode_label)
            )

            if (isLoggedIn) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editableName,
                    onValueChange = {
                        editableName = it
                        profileMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Display name", color = LbTextSecondary) },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = LbTextPrimary,
                        fontSize = 14.sp
                    ),
                    shape = RoundedCornerShape(4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LbGreen,
                        unfocusedBorderColor = LbBorder,
                        focusedContainerColor = LbSurface,
                        unfocusedContainerColor = LbSurface,
                        cursorColor = LbGreen
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        isSavingName = true
                        onUpdateDisplayName(
                            editableName,
                            {
                                isSavingName = false
                                profileMessage = "Profile name updated."
                            },
                            { error ->
                                isSavingName = false
                                profileMessage = error
                            }
                        )
                    },
                    enabled = !isSavingName,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LbGreen,
                        contentColor = Color(0xFF14120E)
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (isSavingName) "Saving..." else "Save profile name",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                profileMessage?.let { message ->
                    Text(
                        text = message,
                        fontSize = 12.sp,
                        color = if (message.contains("updated", ignoreCase = true)) {
                            LbGreen
                        } else {
                            LbTextSecondary
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                Text(
                    text = "Sign in to edit your profile name.",
                    fontSize = 12.sp,
                    color = LbTextMuted,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        ProfileSection(title = "PREFERENCES") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SmallIconBox {
                        Icon(
                            imageVector = Icons.Outlined.Palette,
                            contentDescription = null,
                            tint = LbGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = stringResource(R.string.dark_mode_label),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LbTextPrimary
                        )
                        Text(
                            text = "Switch between dark cinema mode and light mode.",
                            fontSize = 12.sp,
                            color = LbTextSecondary
                        )
                    }
                }

                Switch(
                    checked = isDarkModeEnabled,
                    onCheckedChange = onDarkModeChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF14120E),
                        checkedTrackColor = LbGreen,
                        uncheckedThumbColor = LbTextSecondary,
                        uncheckedTrackColor = LbSurfaceRaised
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = LbTextPrimary)
        ) {
            Icon(
                imageVector = Icons.Outlined.Logout,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = if (isLoggedIn) {
                    stringResource(R.string.logout_btn)
                } else {
                    stringResource(R.string.back_to_login_label)
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ProfileHero(
    displayName: String?,
    userEmail: String?,
    isLoggedIn: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(LbSurface)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(LbGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = Color(0xFF14120E),
                modifier = Modifier.size(30.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = displayName
                    ?.takeIf { it.isNotBlank() }
                    ?: if (isLoggedIn) "Movie fan" else "Guest profile",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = LbTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = userEmail ?: "Local browsing session",
                fontSize = 12.sp,
                color = LbTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(LbSurface)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            fontSize = 10.sp,
            color = LbTextMuted,
            fontWeight = FontWeight.Bold
        )
        content()
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SmallIconBox {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LbGreen,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = LbTextMuted
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = LbTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SmallIconBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(LbSurfaceRaised),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsPagePreview() {
    UniBucFMIIF2026Theme {
        SettingsPage(
            displayName = "Cristiana",
            userEmail = "user@example.com",
            isLoggedIn = true,
            isDarkModeEnabled = true,
            onDarkModeChange = {},
            onUpdateDisplayName = { _, onSuccess, _ -> onSuccess() },
            onLogout = {}
        )
    }
}
