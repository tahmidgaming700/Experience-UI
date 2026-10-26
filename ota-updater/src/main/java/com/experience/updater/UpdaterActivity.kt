package com.experience.updater

import android.app.Activity
import android.os.Bundle
import android.os.Build
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

/**
 * Experience UI OTA front-end.
 *
 * This app can fetch and verify a package. A normal third-party APK cannot
 * write Android system partitions or invoke the privileged update_engine
 * service. Actual installation is deliberately gated until a ROM-integrated,
 * device-supported installer is implemented and verified.
 */
class UpdaterActivity : Activity() {
    private val manifestUrl = "https://raw.githubusercontent.com/tahmidgaming700/Experience-UI/main/ota/manifest.json"
    private lateinit var status: TextView
    private lateinit var details: TextView
    private lateinit var channel: Spinner
    private lateinit var checkButton: Button
    private lateinit var downloadButton: Button
    private lateinit var progress: ProgressBar
    private lateinit var footerPhase: TextView
    private lateinit var footerBrand: TextView
    private var releaseUrl: String = ""
    private var releaseHash: String = ""
    private var busy = false

    private val experienceBrand: String
        get() = if (Build.MANUFACTURER.contains("Huawei", ignoreCase = true) ||
            Build.BRAND.contains("Huawei", ignoreCase = true) ||
            Build.MANUFACTURER.contains("Honor", ignoreCase = true) ||
            Build.BRAND.contains("Honor", ignoreCase = true)) {
            "HARMONY EXPERIENCE UI"
        } else {
            "EXPERIENCE UI"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(5, 16, 43)
        window.navigationBarColor = Color.rgb(5, 16, 43)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(5, 16, 43))
        }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 28, 28, 24)
            setBackgroundColor(Color.rgb(5, 16, 43))
        }
        fun label(value: String, size: Float, color: Int, bold: Boolean = false) =
            TextView(this).apply {
                text = value
                textSize = size
                setTextColor(color)
                if (bold) setTypeface(typeface, Typeface.BOLD)
                setPadding(0, 7, 0, 7)
            }

        content.addView(label(experienceBrand, 13f, Color.rgb(100, 202, 255), true))
        content.addView(label("System updater", 30f, Color.WHITE, true))
        content.addView(label("Monthly security releases • Early access", 14f, Color.LTGRAY))
        channel = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@UpdaterActivity,
                android.R.layout.simple_spinner_dropdown_item,
                listOf("Stable — monthly security releases", "Early access — Harmony Experience UI 2 (Android 13)")
            )
        }
        content.addView(channel, ViewGroup.LayoutParams(-1, -2))
        details = label("Select a channel and check the published update manifest.", 15f, Color.WHITE)
        content.addView(details)
        status = label("Not checked yet", 13f, Color.rgb(150, 190, 235))
        content.addView(status)

        checkButton = Button(this).apply {
            text = "Check for updates"
            setOnClickListener { checkUpdates() }
        }
        content.addView(checkButton, ViewGroup.LayoutParams(-1, -2))

        downloadButton = Button(this).apply {
            text = "Download and verify update"
            isEnabled = false
            setOnClickListener { downloadAndVerify() }
        }
        content.addView(downloadButton, ViewGroup.LayoutParams(-1, -2))

        content.addView(label(
            "Safety: this updater does not pretend to install a package. A recovery-free install requires a supported A/B or ROM-integrated privileged update service. This build has not established that capability on the Huawei Y9 JKM-LX2.",
            12f, Color.LTGRAY
        ))

        root.addView(ScrollView(this).apply { addView(content) },
            LinearLayout.LayoutParams(-1, 0, 1f))

        // Fixed bottom panel, inspired by classic Android firmware update screens.
        val footer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 10, 22, 12)
            setBackgroundColor(Color.rgb(7, 22, 53))
        }
        progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            progress = 0
            visibility = View.GONE
        }
        footerBrand = label(experienceBrand, 13f, Color.rgb(100, 202, 255), true).apply {
            gravity = android.view.Gravity.CENTER
        }
        footer.addView(footerBrand)
        progress.progressDrawable?.setColorFilter(Color.rgb(40, 190, 255), android.graphics.PorterDuff.Mode.SRC_IN)
        footer.addView(progress, ViewGroup.LayoutParams(-1, 5))
        footerPhase = label("READY TO CHECK", 11f, Color.rgb(190, 215, 245), true).apply {
            gravity = android.view.Gravity.CENTER
        }
        footer.addView(footerPhase)
        root.addView(footer, ViewGroup.LayoutParams(-1, -2))
        setContentView(root)
    }

    private fun setBusy(value: Boolean) {
        busy = value
        checkButton.isEnabled = !value
        downloadButton.isEnabled = !value && releaseUrl.startsWith("https://") &&
            releaseHash.matches(Regex("[a-fA-F0-9]{64}"))
        channel.isEnabled = !value
    }

    private fun checkUpdates() {
        if (busy) return
        status.text = "Checking manifest…"
        footerPhase.text = "CHECKING FOR UPDATES"
        setBusy(true)
        val selectedChannel = channel.selectedItemPosition
        Thread {
            var connection: HttpURLConnection? = null
            try {
                connection = (URL(manifestUrl).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 12000
                    readTimeout = 12000
                    setRequestProperty("Accept", "application/json")
                }
                val code = connection.responseCode
                if (code !in 200..299) error("Manifest request returned HTTP $code")
                val json = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
                val release = json.getJSONObject(if (selectedChannel == 1) "early_access" else "stable")
                val version = release.optString("version", "Unknown version")
                val patch = release.optString("security_patch", "Not specified")
                val notes = release.optString("release_notes", "No release notes")
                val url = release.optString("package_url").trim()
                val hash = release.optString("sha256").trim()
                val valid = url.startsWith("https://") && hash.matches(Regex("[a-fA-F0-9]{64}"))
                runOnUiThread {
                    releaseUrl = if (valid) url else ""
                    releaseHash = if (valid) hash.lowercase() else ""
                    details.text = "$version\nSecurity patch: $patch\n$notes"
                    status.text = if (valid) {
                        "A package URL and SHA-256 are published. Download will verify the file; installation is separately capability-gated."
                    } else {
                        "Manifest reachable, but no valid package URL and SHA-256 are published yet."
                    }
                    footerPhase.text = if (valid) "UPDATE AVAILABLE" else "NO VERIFIED PACKAGE AVAILABLE"
                    setBusy(false)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    status.text = "Update check failed: ${e.message ?: "network error"}"
                    footerPhase.text = "UPDATE CHECK FAILED"
                    setBusy(false)
                }
            } finally {
                connection?.disconnect()
            }
        }.start()
    }

    private fun downloadAndVerify() {
        if (busy) return
        if (!releaseUrl.startsWith("https://") ||
            !releaseHash.matches(Regex("[a-fA-F0-9]{64}"))) {
            status.text = "Cannot download: release URL or SHA-256 is missing."
            footerPhase.text = "UPDATE NOT VERIFIED"
            return
        }
        setBusy(true)
        progress.visibility = View.VISIBLE
        progress.isIndeterminate = true
        progress.progress = 0
        footerPhase.text = "DOWNLOADING UPDATE"
        status.text = "Downloading package over HTTPS…"
        val url = releaseUrl
        val expectedHash = releaseHash
        Thread {
            var connection: HttpURLConnection? = null
            try {
                connection = (URL(url).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 15000
                    readTimeout = 30000
                    instanceFollowRedirects = true
                    setRequestProperty("Accept-Encoding", "identity")
                }
                val code = connection.responseCode
                if (code !in 200..299) error("Package request returned HTTP $code")
                val length = connection.contentLengthLong
                val destination = File(filesDir, "pending-experience-update.zip")
                val digest = MessageDigest.getInstance("SHA-256")
                var total = 0L
                connection.inputStream.use { input ->
                    destination.outputStream().use { output ->
                        val buffer = ByteArray(64 * 1024)
                        while (true) {
                            val count = input.read(buffer)
                            if (count < 0) break
                            total += count
                            if (total > 2L * 1024 * 1024 * 1024) error("Package exceeds 2 GiB safety limit")
                            digest.update(buffer, 0, count)
                            output.write(buffer, 0, count)
                            if (length > 0) {
                                val percent = ((total * 100L) / length).coerceIn(0, 100).toInt()
                                runOnUiThread {
                                    progress.isIndeterminate = false
                                    progress.progress = percent
                                    footerPhase.text = "DOWNLOADING UPDATE • $percent%"
                                }
                            }
                        }
                    }
                }
                val actualHash = digest.digest().joinToString("") { "%02x".format(it) }
                if (!actualHash.equals(expectedHash, ignoreCase = true)) {
                    destination.delete()
                    error("SHA-256 mismatch. The downloaded file was deleted.")
                }
                if (total == 0L) {
                    destination.delete()
                    error("Downloaded package is empty.")
                }
                runOnUiThread {
                    progress.isIndeterminate = false
                    progress.progress = 100
                    status.text = "Package downloaded and SHA-256 verified. It is staged privately, not installed."
                    footerPhase.text = "UPDATE VERIFIED • INSTALLER NOT AVAILABLE"
                    Toast.makeText(
                        this,
                        "Verified package staged. No supported recovery-free installer is configured.",
                        Toast.LENGTH_LONG
                    ).show()
                    setBusy(false)
                }
            } catch (e: Exception) {
                File(filesDir, "pending-experience-update.zip").delete()
                runOnUiThread {
                    status.text = "Download/verification failed: ${e.message ?: "network error"}"
                    footerPhase.text = "UPDATE FAILED — NOTHING INSTALLED"
                    setBusy(false)
                }
            } finally {
                connection?.disconnect()
            }
        }.start()
    }
}
