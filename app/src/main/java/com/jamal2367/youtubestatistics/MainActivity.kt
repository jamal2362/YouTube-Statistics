package com.jamal2367.youtubestatistics

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.elevation.SurfaceColors
import com.jamal2367.youtubestatistics.DisplayMessageActivity.Companion.EXTRA_MESSAGE

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupStatusBarAndNavigationBarColors()
        setupCardViewBackground()

        setupSearchButton()
        setupInfoButton()
    }

    private fun setupStatusBarAndNavigationBarColors() {
        window.statusBarColor = SurfaceColors.SURFACE_2.getColor(this)
        window.navigationBarColor = SurfaceColors.SURFACE_0.getColor(this)
    }

    private fun setupCardViewBackground() {
        val cardView = findViewById<MaterialCardView>(R.id.materialCardView)
        val surfaceColor = SurfaceColors.SURFACE_2.getColor(this)
        cardView.setCardBackgroundColor(surfaceColor)
    }

    private fun setupSearchButton() {
        findViewById<View>(R.id.sendYTQueryButton).setOnClickListener {
            val intent = Intent(this, DisplayMessageActivity::class.java)
            val editText = findViewById<EditText>(R.id.YTTextBox)
            val message = editText.text.toString()
            intent.putExtra(EXTRA_MESSAGE, message)
            startActivity(intent)
        }
    }

    private fun setupInfoButton() {
        findViewById<View>(R.id.infoButton).setOnClickListener {
            MaterialAlertDialogBuilder(this@MainActivity, R.style.ThemeOverlay_App_MaterialAlertDialog).apply {
                setTitle(getString(R.string.list_of_supported_services))
                setMessage(getString(R.string.services))
                setCancelable(false)
                setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                }
                create().show()
            }
        }
    }
}