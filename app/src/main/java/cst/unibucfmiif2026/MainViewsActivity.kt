package cst.unibucfmiif2026

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainViewsActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContentView(R.layout.activity_main_views)
		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}

		Log.e("TAG", "views - onCreate:")
	}

	override fun onStart() {
		super.onStart()

		Log.e("TAG", "views - onStart:")
	}

	override fun onResume() {
		super.onResume()

		Log.e("TAG", "views - onResume:")
	}

	override fun onPause() {
		super.onPause()

		Log.e("TAG", "views - onPause:")
	}

	override fun onStop() {
		super.onStop()

		Log.e("TAG", "views - onStop:")
	}

	override fun onDestroy() {
		super.onDestroy()

		Log.e("TAG", "views - onDestroy:")
	}
}