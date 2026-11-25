package com.jarvis.service

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.util.Log

/**
 * JARVIS'in gözü ve elleri.
 * Termux/Tasker'a gerek yok, direkt sistem servisi.
 */
class JarvisAccessibilityService : AccessibilityService() {

    companion object {
        var instance: JarvisAccessibilityService? = null
        fun get(): JarvisAccessibilityService? = instance
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d("JARVIS", "Accessibility baglandi - gozler acik")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Arkaplanda her ekran degisimini dinleyebilir, gerekirse otomatik tetiklenir
    }
    override fun onInterrupt() {}

    // === GOZ: Ekrani gor ===
    fun ekraniGor(): EkrankGoruntusu {
        val root = rootInActiveWindow ?: return EkrankGoruntusu(emptyList(), null)
        val nodeList = mutableListOf<UiNode>()

        fun traverse(node: AccessibilityNodeInfo, depth: Int = 0) {
            val text = node.text?.toString() ?: node.contentDescription?.toString() ?: ""
            val bounds = android.graphics.Rect()
            node.getBoundsInScreen(bounds)
            if (text.isNotBlank() && bounds.width() > 0) {
                nodeList.add(UiNode(text, bounds.centerX(), bounds.centerY(), node.isClickable, node.className.toString()))
            }
            for (i in 0 until node.childCount) {
                node.childAt(i)?.let { traverse(it, depth+1) }
            }
        }
        traverse(root)

        // Screenshot Android 14+ accessibility ile (takeScreenshot)
        // Eski versiyonlarda Shizuku ile screencap -p
        return EkrankGoruntusu(nodeList, null)
    }

    // === EL: Dokun ===
    fun dokun(x: Int, y: Int) {
        val path = Path().apply { moveTo(x.toFloat(), y.toFloat()) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()
        dispatchGesture(gesture, null, null)
        Log.d("JARVIS", "Dokunuldu: $x,$y")
    }

    // === EL: Yaz ===
    fun yaz(text: String) {
        // Odakli input'a yaz
        val root = rootInActiveWindow
        val focused = findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
        focused?.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, android.os.Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
        })
        Log.d("JARVIS", "Yazildi: $text")
    }

    fun geriGit() = performGlobalAction(GLOBAL_ACTION_BACK)
    fun anaEkranaGit() = performGlobalAction(GLOBAL_ACTION_HOME)
}

data class UiNode(val text: String, val x: Int, val y: Int, val clickable: Boolean, val type: String)
data class EkrankGoruntusu(val ui: List<UiNode>, val bitmap: Bitmap?)
