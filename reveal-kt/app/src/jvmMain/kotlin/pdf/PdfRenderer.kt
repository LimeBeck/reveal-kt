package dev.limebeck.application.pdf

import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright

class PdfRenderer {
    suspend fun render(url: String): ByteArray =
        Playwright.create(
            Playwright
                .CreateOptions()
                .setEnv(
                    mapOf("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD" to "1")
                )
        ).use { pw ->
            val type = pw.chromium()
            val browser = type.launch()
            val ctx = browser.newContext()
            return with(ctx.newPage()) {
                navigate(url)
                waitForTimeout(2000.0)
                pdf(
                    Page.PdfOptions()
                        .setPrintBackground(true)
                        .setPreferCSSPageSize(true)
                        .setDisplayHeaderFooter(false)
                        .setLandscape(true)
                )
            }
        }
}
