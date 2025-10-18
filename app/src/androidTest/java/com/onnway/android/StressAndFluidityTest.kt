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
class StressAndFluidityTest {

    private lateinit var device: UiDevice

    companion object {
        private const val TOTAL_CYCLES = 5
        private const val SCROLL_DURATION_SEC = 30
        private const val ZOOM_DURATION_SEC = 30
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
        handlePermissionIfNeeded()
        Thread.sleep(3000)
    }

    private fun handlePermissionIfNeeded() {
        try {
            val allowButton = device.findObject(UiSelector().text("While using the app"))
            if (allowButton.waitForExists(3000)) {
                allowButton.click()
                Thread.sleep(2000)
                Log.d("ANIMATION_TEST", "Permission granted")
            }
        } catch (e: Exception) {
            // Permission already granted
        }
    }

    @Test
    fun animationFluidityStressTest() {
        Log.d("ANIMATION_TEST", "========================================")
        Log.d("ANIMATION_TEST", "ANIMATION FLUIDITY TEST STARTED")
        Log.d("ANIMATION_TEST", "Total Cycles: $TOTAL_CYCLES")
        Log.d("ANIMATION_TEST", "========================================")

        val startTime = System.currentTimeMillis()

        // Initial setup
        if (!setupRouteParameters()) {
            Log.e("ANIMATION_TEST", "Setup failed, aborting")
            return
        }

        for (cycle in 1..TOTAL_CYCLES) {
            Log.d("ANIMATION_TEST", "")
            Log.d("ANIMATION_TEST", "========== CYCLE $cycle/$TOTAL_CYCLES ==========")

            try {
                // Step 1: Create route
                createRoute(cycle)

                // Step 2: Wait for animation to complete
                waitForAnimation(cycle)

                // Step 3: Scroll test (30 seconds)
                scrollTest(cycle)

                // Step 4: Map zoom test (30 seconds)
                mapZoomTest(cycle)

                // Step 5: Go back and prepare for next cycle
                if (cycle < TOTAL_CYCLES) {
                    prepareNextCycle(cycle)
                }

            } catch (e: Exception) {
                Log.e("ANIMATION_TEST", "Cycle $cycle failed: ${e.message}")
            }
        }

        val totalTime = (System.currentTimeMillis() - startTime) / 1000 / 60
        Log.d("ANIMATION_TEST", "")
        Log.d("ANIMATION_TEST", "========================================")
        Log.d("ANIMATION_TEST", "TEST COMPLETED")
        Log.d("ANIMATION_TEST", "Total Time: $totalTime minutes")
        Log.d("ANIMATION_TEST", "========================================")
    }

    private fun setupRouteParameters(): Boolean {
        try {
            Thread.sleep(2000)
            Log.d("ANIMATION_TEST", "Setting up route parameters...")

            // Select city
            scrollDown()
            val tallinn = device.findObject(By.text("🇪🇪 Tallinn"))
            if (tallinn == null) {
                Log.e("ANIMATION_TEST", "Tallinn not found")
                return false
            }
            tallinn.click()
            Thread.sleep(1500)
            Log.d("ANIMATION_TEST", "✓ Tallinn selected")

            // Select category: Social Media Spots
            scrollDown()
            val socialMedia = device.findObject(By.text("Social Media Spots"))
            if (socialMedia == null) {
                Log.e("ANIMATION_TEST", "Social Media Spots not found")
                return false
            }
            socialMedia.click()
            Thread.sleep(500)
            Log.d("ANIMATION_TEST", "✓ Social Media Spots selected")

            // Select budget: Mid Range
            scrollDown()
            val midRange = device.findObject(By.text("Mid Range"))
            if (midRange == null) {
                Log.e("ANIMATION_TEST", "Mid Range not found")
                return false
            }
            midRange.click()
            Thread.sleep(500)
            Log.d("ANIMATION_TEST", "✓ Mid Range selected")

            // Select duration: Medium
            scrollDown()
            val medium = device.findObject(By.text("Medium (1 day)"))
            if (medium == null) {
                Log.e("ANIMATION_TEST", "Medium duration not found")
                return false
            }
            medium.click()
            Thread.sleep(500)
            Log.d("ANIMATION_TEST", "✓ Medium duration selected")

            return true

        } catch (e: Exception) {
            Log.e("ANIMATION_TEST", "Setup failed: ${e.message}")
            return false
        }
    }

    private fun createRoute(cycle: Int) {
        Log.d("ANIMATION_TEST", "[$cycle] Creating route...")

        // CREATE MY ROUTE butonunu bulana kadar scroll yap
        var scrollCount = 0
        var buttonFound = false

        while (scrollCount < 10 && !buttonFound) {
            scrollDown()
            Thread.sleep(500)
            scrollCount++

            // Her 2 scroll'da bir kontrol et
            if (scrollCount % 2 == 0) {
                val createButton = device.findObject(By.text("CREATE MY ROUTE"))
                if (createButton != null) {
                    Log.d("ANIMATION_TEST", "[$cycle] ✓ CREATE button found after $scrollCount scrolls")
                    createButton.click()
                    Thread.sleep(2000)
                    buttonFound = true
                    return
                }
            }
        }

        if (!buttonFound) {
            Log.e("ANIMATION_TEST", "[$cycle] ✗ CREATE button NOT FOUND after $scrollCount scrolls")
        }
    }

    private fun waitForAnimation(cycle: Int) {
        Log.d("ANIMATION_TEST", "[$cycle] Waiting for animation to complete...")

        val animationStartTime = System.currentTimeMillis()
        var waitCount = 0
        val maxWait = 120 // 120 seconds max wait

        // Wait for loading to disappear
        while (waitCount < maxWait) {
            val loading = device.findObject(UiSelector().textContains("Teaching the map"))
            if (!loading.exists()) {
                val animationDuration = (System.currentTimeMillis() - animationStartTime) / 1000
                Log.d("ANIMATION_TEST", "[$cycle] ✓ Animation completed in ${animationDuration}s")
                Thread.sleep(3000) // Extra wait to ensure route is fully loaded
                return
            }
            Thread.sleep(1000)
            waitCount++
        }

        Log.e("ANIMATION_TEST", "[$cycle] ✗ Animation timeout after ${maxWait}s")
    }

    private fun scrollTest(cycle: Int) {
        Log.d("ANIMATION_TEST", "[$cycle] Starting scroll test (${SCROLL_DURATION_SEC}s)...")

        val scrollStartTime = System.currentTimeMillis()
        val scrollEndTime = scrollStartTime + (SCROLL_DURATION_SEC * 1000L)
        var scrollCount = 0

        while (System.currentTimeMillis() < scrollEndTime) {
            // Scroll down
            scrollDown()
            Thread.sleep(150)
            scrollCount++

            // Scroll up
            scrollUp()
            Thread.sleep(150)
            scrollCount++

            if (scrollCount % 20 == 0) {
                val elapsed = (System.currentTimeMillis() - scrollStartTime) / 1000
                Log.d("ANIMATION_TEST", "[$cycle] Scroll progress: ${elapsed}s / ${SCROLL_DURATION_SEC}s")
            }
        }

        Log.d("ANIMATION_TEST", "[$cycle] ✓ Scroll test completed ($scrollCount scrolls)")
    }

    private fun mapZoomTest(cycle: Int) {
        Log.d("ANIMATION_TEST", "[$cycle] Scrolling up to find map...")

        // Map EN YUKARIDA - YUKARI scroll ile ara
        var scrollCount = 0
        var mapFound = false

        while (scrollCount < 30 && !mapFound) {
            scrollUp()  // YUKARI scroll (map yukarıda)
            Thread.sleep(300)
            scrollCount++

            // Her 3 scroll'da bir kontrol et
            if (scrollCount % 3 == 0) {
                val mapText = device.findObject(By.textContains("Route Map"))
                if (mapText != null) {
                    Log.d("ANIMATION_TEST", "[$cycle] ✓ Map found after $scrollCount scrolls")
                    mapFound = true
                    break
                }
            }
        }

        if (!mapFound) {
            Log.e("ANIMATION_TEST", "[$cycle] ✗ Map not found after $scrollCount scrolls")
        }

        Thread.sleep(1000)
        Log.d("ANIMATION_TEST", "[$cycle] Starting zoom test (${ZOOM_DURATION_SEC}s)...")

        val zoomStartTime = System.currentTimeMillis()
        val zoomEndTime = zoomStartTime + (ZOOM_DURATION_SEC * 1000L)
        var zoomCount = 0

        while (System.currentTimeMillis() < zoomEndTime) {
            // Zoom in
            zoomIn()
            Thread.sleep(300)
            zoomCount++

            // Zoom out
            zoomOut()
            Thread.sleep(300)
            zoomCount++

            if (zoomCount % 10 == 0) {
                val elapsed = (System.currentTimeMillis() - zoomStartTime) / 1000
                Log.d("ANIMATION_TEST", "[$cycle] Zoom progress: ${elapsed}s / ${ZOOM_DURATION_SEC}s")
            }
        }

        Log.d("ANIMATION_TEST", "[$cycle] ✓ Zoom test completed ($zoomCount zoom operations)")
    }

    private fun prepareNextCycle(cycle: Int) {
        Log.d("ANIMATION_TEST", "[$cycle] Preparing for next cycle...")

        // Create New Route EN AŞAĞIDA - AŞAĞI scroll ile ara
        var scrollCount = 0
        var buttonFound = false

        while (scrollCount < 30 && !buttonFound) {
            scrollDown()  // AŞAĞI scroll (button aşağıda)
            Thread.sleep(300)
            scrollCount++

            // Her 3 scroll'da bir kontrol et
            if (scrollCount % 3 == 0) {
                val newRouteButton = device.findObject(By.text("Create New Route"))
                if (newRouteButton != null) {
                    Log.d("ANIMATION_TEST", "[$cycle] ✓ Button found after $scrollCount scrolls")
                    newRouteButton.click()
                    Thread.sleep(3000)
                    buttonFound = true
                    reSelectParameters()
                    return
                }
            }
        }

        if (!buttonFound) {
            Log.e("ANIMATION_TEST", "[$cycle] ✗ Create New Route button not found after $scrollCount scrolls")
        }
    }

    private fun reSelectParameters() {
        try {
            Thread.sleep(4000)  // 2 saniye → 4 saniye (daha fazla bekle)

            Log.d("ANIMATION_TEST", "Searching for Tallinn...")

            // Tallinn'i bekleyerek ara
            val tallinn = device.wait(
                Until.findObject(By.text("🇪🇪 Tallinn")),
                10000L  // 10 saniye bekle
            )

            if (tallinn != null) {
                Log.d("ANIMATION_TEST", "Tallinn found, clicking...")
                tallinn.click()
                Thread.sleep(2000)
                Log.d("ANIMATION_TEST", "✓ Tallinn clicked")
            } else {
                Log.e("ANIMATION_TEST", "✗ Tallinn NOT FOUND!")
                return
            }

            // Social Media Spots
            scrollDown()
            val socialMedia = device.wait(
                Until.findObject(By.text("Social Media Spots")),
                5000L
            )
            socialMedia?.click()
            Thread.sleep(2000)

            // Mid Range
            scrollDown()
            val midRange = device.wait(
                Until.findObject(By.text("Mid Range")),
                5000L
            )
            midRange?.click()
            Thread.sleep(2000)

            // Medium
            scrollDown()
            val medium = device.wait(
                Until.findObject(By.text("Medium (1 day)")),
                5000L
            )
            medium?.click()
            Thread.sleep(2000)

            Log.d("ANIMATION_TEST", "Parameters re-selected")

        } catch (e: Exception) {
            Log.e("ANIMATION_TEST", "Re-select failed: ${e.message}")
        }
    }

    private fun scrollDown() {
        try {
            device.swipe(
                device.displayWidth / 2,
                device.displayHeight * 3 / 4,
                device.displayWidth / 2,
                device.displayHeight / 4,
                5
            )
        } catch (e: Exception) {
            Log.w("ANIMATION_TEST", "Scroll down failed")
        }
    }

    private fun scrollUp() {
        try {
            device.swipe(
                device.displayWidth / 2,
                device.displayHeight / 4,
                device.displayWidth / 2,
                device.displayHeight * 3 / 4,
                5
            )
        } catch (e: Exception) {
            Log.w("ANIMATION_TEST", "Scroll up failed")
        }
    }

    private fun zoomIn() {
        try {
            val zoomInButton = device.findObject(By.desc("Zoom in"))
                ?: device.findObject(By.descContains("Zoom in"))
                ?: device.findObject(By.descContains("zoom in"))

            if (zoomInButton != null) {
                zoomInButton.click()
            } else {
                Log.w("ANIMATION_TEST", "Zoom in button not found")
            }
        } catch (e: Exception) {
            Log.w("ANIMATION_TEST", "Zoom in failed: ${e.message}")
        }
    }

    private fun zoomOut() {
        try {
            val zoomOutButton = device.findObject(By.desc("Zoom out"))
                ?: device.findObject(By.descContains("Zoom out"))
                ?: device.findObject(By.descContains("zoom out"))

            if (zoomOutButton != null) {
                zoomOutButton.click()
            } else {
                Log.w("ANIMATION_TEST", "Zoom out button not found")
            }
        } catch (e: Exception) {
            Log.w("ANIMATION_TEST", "Zoom out failed: ${e.message}")
        }
    }
}