package syp.peppercaretaker.smalltalk.screen

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import syp.peppercaretaker.smalltalk.R
import syp.peppercaretaker.smalltalk.viewmodel.SmallTalkViewModel

@Composable
fun SmallTalkScreen(viewModel: SmallTalkViewModel) {

    val permissionGranted = remember { mutableStateOf(false) }

    RequestAudioPermission {
        permissionGranted.value = true
        Log.d("LoginScreen", "Audio-Berechtigung wurde erteilt.")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp),
            contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                viewModel.startSpeechRecognition()
            },
            modifier = Modifier.size(300.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                if (viewModel.buttonPressed.value == true) Color.Red else Color.Blue),
            enabled = !(viewModel.isSpeaking.value == true)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_microphone),
                contentDescription = "Microphone Icon",
                tint = Color.White,
                modifier = Modifier.size(150.dp)
            )
        }
    }
}

@Composable
fun RequestAudioPermission(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            Toast.makeText(context, "Mikrofon-Berechtigung erforderlich", Toast.LENGTH_SHORT)
                .show()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            onPermissionGranted()
        }
    }
}