package com.jamal2367.youtubestatistics

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.elevation.SurfaceColors
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.util.regex.Pattern

class DisplayMessageActivity : AppCompatActivity() {
    private val youtubeRegex = Pattern.compile("^(https?://)?((www\\.)?((m\\.)|(music\\.))?youtube\\.com|youtu\\.be)/.+$", Pattern.CASE_INSENSITIVE)
    private val youtubeSHORTSSTARTINDEX = 27
    private val youtubeSHORTSENDINDEX = 38
    private val youtubeVIDEOIDSTARTINDEX = 17
    private val youtubeVIDEOIDENDINDEX = 28
    private val apiBASEURL = "https://returnyoutubedislikeapi.com/"
    private val apiGETVOTESQUERY = "votes?videoId="
    private val oEMBEDFORMATURL = "https://youtube.com/oembed?format=json&url="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message)

        supportActionBar?.title = getString(R.string.statistics_overview)

        setupStatusBarAndNavigationBarColors()
        setupCardViewBackground()

        startIntent()
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

    private fun startIntent() {
        val message: String? = intent.getStringExtra(Intent.EXTRA_TEXT) ?: intent.getStringExtra(EXTRA_MESSAGE)

        if (message?.let { youtubeRegex.matcher(it).find() } != true) {
            errorPopup()
        }
        getDataAPI(message)
    }

    private fun errorPopup() {
        MaterialAlertDialogBuilder(this@DisplayMessageActivity, R.style.ThemeOverlay_App_MaterialAlertDialog).apply {
            setTitle(getString(R.string.error))
            setMessage(getString(R.string.only_youtube_urls_supported))
            setCancelable(false)
            setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                finish()
            }
            create().show()
        }
    }

    private fun getDataAPI(youTubeLink: String?) {
        val videoID: String? = when {
            youTubeLink?.contains("https://youtu.be") == true -> youTubeLink.substring(youtubeVIDEOIDSTARTINDEX, youtubeVIDEOIDENDINDEX)
            youTubeLink?.contains("https://youtube.com/shorts") == true -> youTubeLink.substring(youtubeSHORTSSTARTINDEX, youtubeSHORTSENDINDEX)
            youTubeLink?.contains("youtube.com") == true -> youTubeLink.split("=".toRegex(), limit = 2).getOrNull(1)
            else -> null
        }

        val finalURL = "$apiBASEURL$apiGETVOTESQUERY$videoID"
        val oEmbedURL = "$oEMBEDFORMATURL$youTubeLink"
        val textViewLikes: TextView = findViewById(R.id.YTLikes)
        val textViewDislikes: TextView = findViewById(R.id.YTDislikes)
        val textViewViews: TextView = findViewById(R.id.YTViews)
        val textViewVideoLink: TextView = findViewById(R.id.YTVideoLink)
        val textViewRatio: TextView = findViewById(R.id.YTRating)
        val videoTitle: TextView = findViewById(R.id.videoTitle)
        val thumbnailView: ImageView = findViewById(R.id.thumbnail_View)
        textViewVideoLink.isSelected = true
        videoTitle.isSelected = true

        val myRequest = StringRequest(Request.Method.GET, finalURL,
            { response: String? ->
                try {
                    val myJsonObject = JSONObject(response.toString())
                    textViewLikes.text = addComma(myJsonObject.getString(getString(R.string.likes)))
                    textViewDislikes.text = addComma(myJsonObject.getString(getString(R.string.dislikes)))
                    textViewViews.text = addComma(myJsonObject.getString(getString(R.string.viewcount)))
                    textViewVideoLink.text = youTubeLink
                    val rating = myJsonObject.getString(getString(R.string.rating))
                    textViewRatio.text = if (rating.length >= 3) {
                        "${rating.substring(0, 3)}${getString(R.string.stars)}"
                    } else {
                        getString(R.string.no_rating)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { _: VolleyError? -> errorDownloading() }
        )

        val oEmbedRequest = StringRequest(Request.Method.GET, oEmbedURL,
            { response: String? ->
                try {
                    val myJsonObject = JSONObject(response.toString())
                    videoTitle.text = myJsonObject.getString(getString(R.string.api_title))
                    val thumbnailUrl = myJsonObject.getString(getString(R.string.api_thumbnail_url))
                    Picasso.get().load(thumbnailUrl).also {
                        it.into(thumbnailView)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { _: VolleyError? -> errorDownloading() }
        )

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(myRequest)
        requestQueue.add(oEmbedRequest)
    }

    private fun errorDownloading() {
        val downloadFailed = getString(R.string.download_failed)
        val unknownTitle = getString(R.string.unknown_title)

        val textViewLikes: TextView = findViewById(R.id.YTLikes)
        val textViewDislikes: TextView = findViewById(R.id.YTDislikes)
        val textViewViews: TextView = findViewById(R.id.YTViews)
        val textViewVideoLink: TextView = findViewById(R.id.YTVideoLink)
        val textViewRating: TextView = findViewById(R.id.YTRating)

        textViewLikes.text = downloadFailed
        textViewDislikes.text = downloadFailed
        textViewRating.text = downloadFailed
        textViewViews.text = downloadFailed
        textViewVideoLink.text = downloadFailed
        textViewVideoLink.text = unknownTitle
    }

    private fun addComma(number: String): String {
        return number
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
    }

    companion object {
        const val EXTRA_MESSAGE = "com.jamal2367.youtubestatistics.MESSAGE"
    }
}