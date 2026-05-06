package cst.unibucfmiif2026

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import cst.unibucfmiif2026.ui.navigation.AuthNavigation
import cst.unibucfmiif2026.ui.theme.UniBucFMIIF2026Theme

class MainActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			UniBucFMIIF2026Theme {
				AuthNavigation()
			}
		}
		Log.e("TAG", "onCreate:")
	}

	override fun onStart() {
		super.onStart()

		Log.e("TAG", "onStart:")
	}

	override fun onResume() {
		super.onResume()

		Log.e("TAG", "onResume:")
	}

	override fun onPause() {
		super.onPause()

		Log.e("TAG", "onPause:")
	}

	override fun onStop() {
		super.onStop()

		Log.e("TAG", "onStop:")
	}

	override fun onDestroy() {
		super.onDestroy()

		Log.e("TAG", "onDestroy:")
	}
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

	val context = LocalContext.current

	Button(
		onClick = {
			Log.e("TAG", "onClick:")

			val intent = Intent(context, MainViewsActivity::class.java)
			context.startActivity(intent)
			(context as? ComponentActivity)?.finish()
		},
		modifier = Modifier.fillMaxWidth()
	) {
		Text(
			text = "Hello $name!",
			modifier = modifier
		)
	}
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
	UniBucFMIIF2026Theme {
		Greeting("Android")
	}
}

@Preview(showBackground = true)
@Composable
fun AuthNavPreview() {
	UniBucFMIIF2026Theme {
		AuthNavigation()
	}
}