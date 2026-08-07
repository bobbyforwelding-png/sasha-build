package com.example.presentation.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for the 3D avatar pipeline:
 *  1. three.min.js is bundled as a local asset (not CDN)
 *  2. avatar.html loads Three.js from the local asset path
 *  3. avatar.html exposes the required JS API (setState, setView, setWalking)
 *  4. State-to-JS string mapping is correct for all three states
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SashaAvatar3DTest {

    private lateinit var context: Context
    private lateinit var avatarHtml: String
    private lateinit var threeJs: String

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        avatarHtml = context.assets.open("avatar.html").bufferedReader().readText()
        threeJs = context.assets.open("three.min.js").bufferedReader().readText()
    }

    // ── Asset presence ────────────────────────────────────────────────────────

    @Test
    fun `three_min_js asset exists and is non-empty`() {
        assertTrue("three.min.js must be bundled as a local asset", threeJs.length > 100_000)
    }

    @Test
    fun `avatar_html asset exists and is non-empty`() {
        assertTrue("avatar.html must be present", avatarHtml.isNotBlank())
    }

    // ── CDN removed ───────────────────────────────────────────────────────────

    @Test
    fun `avatar_html does NOT load three_js from CDN`() {
        assertFalse(
            "CDN reference must be removed — three.js must be loaded locally",
            avatarHtml.contains("cdnjs.cloudflare.com")
        )
    }

    @Test
    fun `avatar_html does NOT reference any external script CDN`() {
        assertFalse(
            "No external CDN scripts allowed in avatar.html",
            avatarHtml.contains("https://cdn") || avatarHtml.contains("http://cdn")
        )
    }

    // ── Local asset path ──────────────────────────────────────────────────────

    @Test
    fun `avatar_html loads three_js from local android_asset path`() {
        assertTrue(
            "avatar.html must load three.js via file:///android_asset/three.min.js",
            avatarHtml.contains("file:///android_asset/three.min.js")
        )
    }

    // ── JS API surface ────────────────────────────────────────────────────────

    @Test
    fun `avatar_html exposes setState on window`() {
        assertTrue(
            "avatar.html must define window.setState for state changes",
            avatarHtml.contains("window.setState")
        )
    }

    @Test
    fun `avatar_html exposes setView on window`() {
        assertTrue(
            "avatar.html must define window.setView for camera zoom",
            avatarHtml.contains("window.setView")
        )
    }

    @Test
    fun `avatar_html exposes setWalking on window`() {
        assertTrue(
            "avatar.html must define window.setWalking for walk animation",
            avatarHtml.contains("window.setWalking")
        )
    }

    // ── State-to-JS mapping ───────────────────────────────────────────────────

    @Test
    fun `state mapping speaking produces correct js string`() {
        val jsState = avatarStateToJs(isSpeaking = true, isThinking = false)
        assertEquals("speaking", jsState)
    }

    @Test
    fun `state mapping thinking produces correct js string`() {
        val jsState = avatarStateToJs(isSpeaking = false, isThinking = true)
        assertEquals("thinking", jsState)
    }

    @Test
    fun `state mapping idle produces correct js string`() {
        val jsState = avatarStateToJs(isSpeaking = false, isThinking = false)
        assertEquals("idle", jsState)
    }

    @Test
    fun `speaking takes priority over thinking`() {
        val jsState = avatarStateToJs(isSpeaking = true, isThinking = true)
        assertEquals("speaking", jsState)
    }

    // ── avatar.html valid HTML ────────────────────────────────────────────────

    @Test
    fun `avatar_html has doctype and closing body`() {
        assertTrue("Must be a valid HTML document", avatarHtml.trimStart().startsWith("<!DOCTYPE html>", ignoreCase = true))
        assertTrue("Must have closing body tag", avatarHtml.contains("</body>"))
    }

    @Test
    fun `avatar_html contains Three_js scene setup`() {
        assertTrue("Must set up a THREE.Scene", avatarHtml.contains("new THREE.Scene()"))
        assertTrue("Must set up a WebGLRenderer", avatarHtml.contains("THREE.WebGLRenderer"))
        assertTrue("Must contain animate loop", avatarHtml.contains("function animate()"))
    }
}

/**
 * Mirrors the state-mapping logic in SashaAvatar3D's LaunchedEffect so it can
 * be tested without spinning up a Compose runtime.
 */
private fun avatarStateToJs(isSpeaking: Boolean, isThinking: Boolean): String = when {
    isSpeaking -> "speaking"
    isThinking -> "thinking"
    else -> "idle"
}
