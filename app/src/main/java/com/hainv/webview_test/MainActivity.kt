package com.hainv.webview_test

import android.annotation.TargetApi
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.hainv.webview_test.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)

        WebView.setWebContentsDebuggingEnabled(true)

        setUpWebViewDefaults(binding.mainWebView)

        binding.mainWebView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                Log.i("MAIN", "Requesting permission $request")
                runOnUiThread {
                    request.grant(request.resources)
                }
            }
        }

        binding.mainWebView.settings.javaScriptEnabled = true

        binding.edtUrl.setText("https://clive-web-stg.belive.sg")
//        binding.edtUrl.setText("https://10.30.1.124:3000")

        binding.btLoad.setOnClickListener {
            binding.mainWebView.loadUrl(binding.edtUrl.text.toString())
        }

        binding.btBack.setOnClickListener {
            if (binding.mainWebView.canGoBack())
                binding.mainWebView.goBack()
        }

    }

    /**
     * Convenience method to set some generic defaults for a
     * given WebView
     *
     * @param webView
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setUpWebViewDefaults(webView: WebView) {

        val settings = webView.settings

        // Enable Javascript
        settings.javaScriptEnabled = true

        // Use WideViewport and Zoom out if there is no viewport defined
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true

        // Enable pinch to zoom without the zoom buttons
        settings.builtInZoomControls = true

        // Allow use of Local Storage
        settings.domStorageEnabled = true
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.displayZoomControls = false
        }

        // Enable remote debugging via chrome://inspect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        webView.webViewClient = SSLTolerentWebViewClient()

        // AppRTC requires third party cookies to work
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptThirdPartyCookies(webView, true)
    }
}

// SSL Error Tolerant Web View Client
private class SSLTolerentWebViewClient : WebViewClient() {
    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        handler.proceed() // Ignore SSL certificate errors
    }
}