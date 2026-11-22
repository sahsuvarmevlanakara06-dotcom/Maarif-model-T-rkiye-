package com.example.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Leaderboard
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.LocalMaarifColors
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val colors = LocalMaarifColors.current
    val scope = rememberCoroutineScope()

    var isSigningIn by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Attempt silent auto sign-in for returning users once on launch
    LaunchedEffect(Unit) {
        isSigningIn = true
        when (val outcome = GoogleSignInHelper.attemptAutoSignIn(context)) {
            is GoogleSignInOutcome.Error -> errorMessage = outcome.message
            else -> Unit
        }
        isSigningIn = false
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(colors.headerNavy, Color(0xFF172554), MaterialTheme.colorScheme.background)
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Emblem & Title
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.1f),
                border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.2f))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_maarif_emblem),
                    contentDescription = "Maarif Portal",
                    modifier = Modifier
                        .padding(10.dp)
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Maarif Portal",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.authSubtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFFCBD5E1)
                    ),
                    textAlign = TextAlign.Center
                )
            }

            // Feature Highlights Card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, colors.cardBorder),
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AuthFeatureRow(
                        icon = Icons.Rounded.School,
                        tint = MaterialTheme.colorScheme.primary,
                        title = strings.authFeatureCurriculumTitle,
                        desc = strings.authFeatureCurriculumDesc
                    )
                    AuthFeatureRow(
                        icon = Icons.Rounded.AutoAwesome,
                        tint = MaterialTheme.colorScheme.tertiary,
                        title = strings.authFeatureDynamicTitle,
                        desc = strings.authFeatureDynamicDesc
                    )
                    AuthFeatureRow(
                        icon = Icons.Rounded.Bolt,
                        tint = colors.potionViolet,
                        title = strings.authFeaturePotionTitle,
                        desc = strings.authFeaturePotionDesc
                    )
                    AuthFeatureRow(
                        icon = Icons.Rounded.Leaderboard,
                        tint = colors.goldColor,
                        title = strings.authFeatureLeaderboardTitle,
                        desc = strings.authFeatureLeaderboardDesc
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    errorMessage?.let { err ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = err,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                ),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (isSigningIn) return@Button
                            scope.launch {
                                isSigningIn = true
                                errorMessage = null
                                when (val outcome = GoogleSignInHelper.signInInteractive(context)) {
                                    is GoogleSignInOutcome.Error -> errorMessage = outcome.message
                                    else -> Unit
                                }
                                isSigningIn = false
                            }
                        },
                        enabled = !isSigningIn,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("google_sign_in_button")
                    ) {
                        if (isSigningIn) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = strings.authSigningIn,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Login,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = strings.authSignInWithGoogle,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthFeatureRow(
    icon: ImageVector,
    tint: Color,
    title: String,
    desc: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
