package dev.limebeck.application.pdf

import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.WaitUntilState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PdfRenderer {
    suspend fun render(url: String, timeoutMillis: Double = 30_000.0): ByteArray = withContext(Dispatchers.IO) {
        val options = Playwright.CreateOptions()
            .setEnv(mapOf("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD" to "1"))

        Playwright.create(options).use { playwright ->
            playwright.chromium().launch().use { browser ->
                browser.newContext().use { context ->
                    val failures = mutableListOf<String>()
                    val page = context.newPage().apply {
                        onPageError { failures += it }
                        onRequestFailed { request -> failures += "${request.url()}: ${request.failure()}" }
                        onResponse { response ->
                            if (response.status() >= 400) {
                                failures += "${response.url()}: HTTP ${response.status()}"
                            }
                        }
                        setDefaultTimeout(timeoutMillis)
                        setDefaultNavigationTimeout(timeoutMillis)
                    }

                    try {
                        page.navigate(url, Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE))
                        page.awaitPresentationReady()
                        val brokenImages = page.evaluate(
                            """
                            () => Array.from(document.images)
                                .filter(img => img.currentSrc && img.naturalWidth === 0)
                                .map(img => img.currentSrc)
                            """.trimIndent()
                        ) as List<*>
                        check(failures.isEmpty() && brokenImages.isEmpty()) {
                            "Presentation resources failed: ${failures + brokenImages}"
                        }
                        page.pdf(
                            Page.PdfOptions()
                                .setPrintBackground(true)
                                .setPreferCSSPageSize(true)
                                .setDisplayHeaderFooter(false)
                                .setLandscape(true)
                        )
                    } catch (error: Exception) {
                        throw IllegalStateException(
                            "PDF export failed for $url (readiness timeout ${timeoutMillis.toLong()} ms). " +
                                "Browser diagnostics: ${failures.joinToString("; ")}. ${error.message}",
                            error
                        )
                    }
                }
            }
        }
    }

    private fun Page.awaitPresentationReady() {
        waitForFunction(
            """
            () => window.revealKtReady === true &&
                window.revealKtPdfReady === true && document.fonts.status === 'loaded' &&
                Array.from(document.images).every(img => img.complete)
            """.trimIndent()
        )
    }
}
