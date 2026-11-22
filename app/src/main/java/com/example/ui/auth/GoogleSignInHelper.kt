package com.example.ui.auth

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val AUTH_TAG = "Auth"

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        trySend(firebaseAuth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

sealed interface GoogleSignInOutcome {
    data object Success : GoogleSignInOutcome
    data object Cancelled : GoogleSignInOutcome
    data object NoAuthorizedAccount : GoogleSignInOutcome
    data class Error(val message: String) : GoogleSignInOutcome
}

object GoogleSignInHelper {

    /**
     * Silent auto sign-in for returning users using GetGoogleIdOption (single option per request).
     */
    suspend fun attemptAutoSignIn(context: Context): GoogleSignInOutcome {
        return try {
            val webClientId = context.getString(R.string.default_web_client_id)
            val option = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(true)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(option)
                .build()
            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                Firebase.auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
                GoogleSignInOutcome.Success
            } else {
                GoogleSignInOutcome.NoAuthorizedAccount
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: GetCredentialCancellationException) {
            Log.w(AUTH_TAG, "User cancelled auto sign-in", e)
            GoogleSignInOutcome.Cancelled
        } catch (e: NoCredentialException) {
            Log.i(AUTH_TAG, "No previously authorized Google credential found for auto sign-in")
            GoogleSignInOutcome.NoAuthorizedAccount
        } catch (e: Exception) {
            Log.w(AUTH_TAG, "Auto sign-in skipped", e)
            GoogleSignInOutcome.NoAuthorizedAccount
        }
    }

    /**
     * Interactive Google Sign-In triggered by an explicit button click using GetSignInWithGoogleOption
     * (single option per request).
     */
    suspend fun signInInteractive(context: Context): GoogleSignInOutcome {
        return try {
            val webClientId = context.getString(R.string.default_web_client_id)
            val option = GetSignInWithGoogleOption.Builder(webClientId)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(option)
                .build()
            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                Firebase.auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
                GoogleSignInOutcome.Success
            } else {
                GoogleSignInOutcome.Error("Unexpected credential type received.")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: GetCredentialCancellationException) {
            Log.w(AUTH_TAG, "User cancelled Google Sign-In", e)
            GoogleSignInOutcome.Cancelled
        } catch (e: Exception) {
            Log.e(AUTH_TAG, "Interactive Google Sign-In failed", e)
            GoogleSignInOutcome.Error(e.localizedMessage ?: "Google ile giriş yapılamadı.")
        }
    }

    /**
     * Signs out cleanly by clearing Credential Manager state inside NonCancellable before
     * calling Firebase.auth.signOut(), preventing auto-sign-in bounce-back loops.
     */
    suspend fun signOut(context: Context) {
        withContext(NonCancellable) {
            runCatching {
                CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
            }.onFailure { error ->
                Log.w(AUTH_TAG, "Failed to clear Credential Manager state on sign-out", error)
            }
            Firebase.auth.signOut()
        }
    }
}
