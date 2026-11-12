
package com.iraqicyberlab.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    private WebView webView;
    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new CyberBridge(), "Android");

        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
    }

    private void sendResult(String result) {
        runOnUiThread(() -> {
            String js = "window.onNativeResult(" +
                    org.json.JSONObject.quote(
                            "{\"result\":" +
                            org.json.JSONObject.quote(result) + "}"
                    ) + ")";
            webView.evaluateJavascript(js, null);
        });
    }

    private class CyberBridge {

        @JavascriptInterface
        public void scanLocalPorts(String host, String ports) {
            executor.execute(() -> {
                StringBuilder result = new StringBuilder();
                result.append("Local Port Scan: ").append(host).append("\n\n");

                if (!host.equals("127.0.0.1") &&
                    !host.equals("localhost")) {
                    sendResult("مسموح بفحص الجهاز المحلي فقط.");
                    return;
                }

                String[] list = ports.split(",");
                for (String item : list) {
                    try {
                        int port = Integer.parseInt(item.trim());
                        if (port < 1 || port > 65535) continue;

                        try (Socket socket = new Socket()) {
                            socket.connect(
                                new InetSocketAddress(host, port), 500
                            );
                            result.append(port).append(" : OPEN\n");
                        } catch (IOException e) {
                            result.append(port).append(" : CLOSED\n");
                        }
                    } catch (Exception e) {
                        result.append(item).append(" : ERROR\n");
                    }
                }
                sendResult(result.toString());
            });
        }

        @JavascriptInterface
        public void lookupDNS(String domain) {
            executor.execute(() -> {
                try {
                    InetAddress[] addresses =
                            InetAddress.getAllByName(domain);

                    StringBuilder result =
                            new StringBuilder("DNS Results\n\n");

                    for (InetAddress address : addresses) {
                        result.append(address.getHostAddress())
                              .append("\n");
                    }
                    sendResult(result.toString());

                } catch (Exception e) {
                    sendResult("DNS Error: " + e.getMessage());
                }
            });
        }

        @JavascriptInterface
        public void analyzeHTTP(String address) {
            executor.execute(() -> {
                HttpURLConnection connection = null;

                try {
                    URL url = new URL(address);
                    String scheme = url.getProtocol();

                    if (!scheme.equals("https") &&
                        !scheme.equals("http")) {
                        sendResult("يسمح فقط بروابط HTTP وHTTPS.");
                        return;
                    }

                    connection =
                        (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    connection.setInstanceFollowRedirects(false);
                    connection.setRequestProperty(
                        "User-Agent", "IRAQI-CYBER-LAB/1.0"
                    );

                    int code = connection.getResponseCode();

                    StringBuilder result =
                            new StringBuilder("HTTP Analysis\n\n");

                    result.append("URL: ").append(address).append("\n");
                    result.append("Status: ").append(code).append("\n\n");
                    result.append("Security Headers:\n");

                    String[] headers = {
                        "Strict-Transport-Security",
                        "Content-Security-Policy",
                        "X-Content-Type-Options",
                        "X-Frame-Options",
                        "Referrer-Policy"
                    };

                    for (String header : headers) {
                        String value = connection.getHeaderField(header);
                        result.append(header).append(": ")
                              .append(value == null ? "MISSING" : value)
                              .append("\n");
                    }

                    sendResult(result.toString());

                } catch (Exception e) {
                    sendResult("HTTP Error: " + e.getMessage());
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        executor.shutdown();
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}

