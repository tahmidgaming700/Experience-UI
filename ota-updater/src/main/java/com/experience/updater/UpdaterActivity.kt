package com.experience.updater

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class UpdaterActivity : Activity() {
    private val manifestUrl = "https://raw.githubusercontent.com/tahmidgaming700/Experience-UI/main/ota/manifest.json"
    private lateinit var status: TextView
    private lateinit var details: TextView
    private lateinit var channel: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(5, 16, 43)
        window.navigationBarColor = Color.rgb(5, 16, 43)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 34, 28, 28)
            setBackgroundColor(Color.rgb(5, 16, 43))
        }
        fun label(value: String, size: Float, color: Int, bold: Boolean = false) =
            TextView(this).apply {
                text = value
                textSize = size
                setTextColor(color)
                if (bold) setTypeface(typeface, Typeface.BOLD)
                setPadding(0, 8, 0, 8)
            }
        root.addView(label("EXPERIENCE UI", 13f, Color.rgb(100, 202, 255), true))
        root.addView(label("System updater", 30f, Color.WHITE, true))
        root.addView(label("Monthly security releases • Early access", 14f, Color.LTGRAY))
        channel = Spinner(this).apply {
            adapter = ArrayAdapter(this@UpdaterActivity, android.R.layout.simple_spinner_dropdown_item,
                listOf("Stable — monthly security releases", "Early access — Experience UI 2 (Android 13)"))
        }
        root.addView(channel, ViewGroup.LayoutParams(-1, -2))
        details = label("Select a channel and check the published update manifest.", 15f, Color.WHITE)
        root.addView(details)
        status = label("Not checked yet", 13f, Color.rgb(150, 190, 235))
        root.addView(status)
        root.addView(Button(this).apply {
            text = "Check for updates"
            setOnClickListener { checkUpdates() }
        }, ViewGroup.LayoutParams(-1, -2))
        root.addView(label(
            "This initial version checks published metadata only. It does not install a GSI or apply security patches by itself. OTA packages must be signed, device-compatible, and installed by a supported update mechanism.",
            12f, Color.LTGRAY))
        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun checkUpdates() {
        status.text = "Checking manifest…"
        Thread {
            try {
                val connection = (URL(manifestUrl).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 12000
                    readTimeout = 12000
                    setRequestProperty("Accept", "application/json")
                }
                val code = connection.responseCode
                if (code !in 200..299) error("Manifest request returned HTTP $code")
                val json = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
                connection.disconnect()
                val release = json.getJSONObject(if (channel.selectedItemPosition == 1) "early_access" else "stable")
                val version = release.optString("version", "Unknown version")
                val patch = release.optString("security_patch", "Not specified")
                val notes = release.optString("release_notes", "No release notes")
                val url = release.optString("package_url")
                val hash = release.optString("sha256")
                runOnUiThread {
                    details.text = "$version\nSecurity patch: $patch\n$notes"
                    status.text = if (url.startsWith("https://") && hash.matches(Regex("[a-fA-F0-9]{64}")))
                        "Package URL and SHA-256 metadata are published. Download/install support is not enabled yet."
                    else "Manifest reachable, but no valid package URL and SHA-256 are published yet."
                }
            } catch (e: Exception) {
                runOnUiThread { status.text = "Update check failed: ${e.message ?: "network error"}" }
            }
        }.start()
    }
}
