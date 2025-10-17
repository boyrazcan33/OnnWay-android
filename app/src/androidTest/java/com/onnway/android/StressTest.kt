package com.onnway.android

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StressTest {

    private lateinit var device: UiDevice

    companion object {
        private const val TEST_DURATION_MINUTES = 2
        private const val WAIT_TIMEOUT = 60000L
        private const val PACKAGE_NAME = "com.onnway.android"
    }

    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()
        Thread.sleep(1000)

        val context = InstrumentationRegistry.getInstrumentation().context
        val intent = context.packageManager.getLaunchIntentForPackage(PACKAGE_NAME)
        intent?.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        Thread.sleep(5000)

        // Handle permission if dialog appears
        handlePermissionIfNeeded()

        Thread.sleep(3000)
    }

    private fun handlePermissionIfNeeded() {
        try {
            val allowButton = device.findObject(UiSelector().text("While using the app"))
            if (allowButton.waitForExists(3000)) {
                allowButton.click()
                Thread.sleep(2000)
                Log.d("STRESS_TEST", "Permission granted")
            }
        } catch (e: Exception) {
            // Permission already granted or not needed
        }
    }

    @Test
    fun routeQueryStressTest() {
        Log.d("STRESS_TEST", "========================================")
        Log.d("STRESS_TEST", "TEST STARTED - $TEST_DURATION_MINUTES minutes")
        Log.d("STRESS_TEST", "========================================")

        val startTime = System.currentTimeMillis()
        val endTime = startTime + (TEST_DURATION_MINUTES * 60 * 1000L)

        var callCount = 0
        var successCount = 0

        if (!setupInitialSelection()) {
            Log.e("STRESS_TEST", "Setup failed, aborting")
            return
        }

        while (System.currentTimeMillis() < endTime) {
            callCount++

            try {
                scrollDown()

                val createButton = device.wait(
                    Until.findObject(By.text("CREATE MY ROUTE")),
                    10000L
                )

                if (createButton != null && createButton.isEnabled) {
                    Log.d("STRESS_TEST", "Call #$callCount - Clicking CREATE...")
                    createButton.click()
                    Thread.sleep(2000)

                    waitForLoading()
                    Thread.sleep(3000)



                    // Scroll down and search every 3 scrolls
                    scrollDown()
                    Thread.sleep(500)
                    scrollDown()
                    Thread.sleep(500)
                    scrollDown()
                    Thread.sleep(500)

                    Log.d("STRESS_TEST", "Searching for 'Create New Route' after 3 scrolls...")
                    var newRouteButton = device.findObject(By.text("Create New Route"))

// If not found, scroll 3 more times
                    if (newRouteButton == null) {
                        scrollDown()
                        Thread.sleep(500)
                        scrollDown()
                        Thread.sleep(500)
                        scrollDown()
                        Thread.sleep(500)

                        Log.d("STRESS_TEST", "Searching again after 6 scrolls...")
                        newRouteButton = device.findObject(By.text("Create New Route"))
                    }

// If still not found, one final scroll
                    if (newRouteButton == null) {
                        scrollDown()
                        Thread.sleep(500)

                        Log.d("STRESS_TEST", "Final search after 7 scrolls...")
                        newRouteButton = device.findObject(By.text("Create New Route"))
                    }

                    if (newRouteButton != null) {
                        Log.d("STRESS_TEST", "Call #$callCount - SUCCESS")
                        successCount++
                        newRouteButton.click()
                        Thread.sleep(3000)
                        reSelectParameters()
                    } else {
                        Log.e("STRESS_TEST", "Call #$callCount - New Route button not found")
                    }
                } else {
                    Log.e("STRESS_TEST", "Call #$callCount - CREATE button not found")
                }

                if (callCount % 5 == 0) {
                    val elapsed = (System.currentTimeMillis() - startTime) / 1000 / 60
                    Log.d("STRESS_TEST", "📊 $callCount calls | $successCount success | $elapsed min")
                }

            } catch (e: Exception) {
                Log.e("STRESS_TEST", "Call #$callCount - ERROR: ${e.message}")
            }
        }

        Log.d("STRESS_TEST", "========================================")
        Log.d("STRESS_TEST", "TEST COMPLETED")
        Log.d("STRESS_TEST", "Total: $callCount | Success: $successCount")
        Log.d("STRESS_TEST", "========================================")
    }

    private fun waitForLoading() {
        var waitCount = 0
        while (waitCount < 60) {
            val loading = device.findObject(UiSelector().textContains("Teaching the map"))
            if (!loading.exists()) {
                Log.d("STRESS_TEST", "Loading done after ${waitCount}s")
                return
            }
            Thread.sleep(1000)
            waitCount++
        }
    }

    private fun scrollDown() {
        try {
            device.swipe(
                device.displayWidth / 2,
                device.displayHeight * 3 / 4,
                device.displayWidth / 2,
                device.displayHeight / 4,
                20
            )
            Thread.sleep(500)
        } catch (e: Exception) {
            Log.w("STRESS_TEST", "Scroll failed")
        }
    }

    private fun setupInitialSelection(): Boolean {
        try {
            Thread.sleep(2000)

            val tallinn = device.findObject(By.text("🇪🇪 Tallinn"))
            if (tallinn == null) return false
            tallinn.click()
            Thread.sleep(1500)

            scrollDown()
            val food = device.findObject(By.text("Food & Restaurants"))
            if (food == null) return false
            food.click()
            Thread.sleep(500)

            scrollDown()
            val midRange = device.findObject(By.text("Mid Range"))
            if (midRange == null) return false
            midRange.click()
            Thread.sleep(500)

            scrollDown()
            val shortDuration = device.findObject(By.text("Short (3-4 hours)"))
            if (shortDuration == null) return false
            shortDuration.click()
            Thread.sleep(500)

            Log.d("STRESS_TEST", "Initial setup complete")
            return true

        } catch (e: Exception) {
            Log.e("STRESS_TEST", "Setup failed: ${e.message}")
            return false
        }
    }

    private fun reSelectParameters() {
        try {
            Thread.sleep(2000)

            // Select Tallinn
            val tallinn = device.findObject(By.text("🇪🇪 Tallinn"))
            tallinn?.click()
            Thread.sleep(1500)
            Log.d("STRESS_TEST", "Re-selected Tallinn")

            // Scroll THEN select Food
            scrollDown()
            Thread.sleep(500)
            val food = device.findObject(By.text("Food & Restaurants"))
            food?.click()
            Thread.sleep(800)
            Log.d("STRESS_TEST", "Re-selected Food")

            // Scroll THEN select Mid Range
            scrollDown()
            Thread.sleep(500)
            val midRange = device.findObject(By.text("Mid Range"))
            midRange?.click()
            Thread.sleep(800)
            Log.d("STRESS_TEST", "Re-selected Mid Range")

            // Scroll THEN select Short
            scrollDown()
            Thread.sleep(500)
            val shortDuration = device.findObject(By.text("Short (3-4 hours)"))
            shortDuration?.click()
            Thread.sleep(800)
            Log.d("STRESS_TEST", "Re-selected Short")

            Log.d("STRESS_TEST", "Re-selection complete")

        } catch (e: Exception) {
            Log.e("STRESS_TEST", "Re-select failed: ${e.message}")
        }
    }
}