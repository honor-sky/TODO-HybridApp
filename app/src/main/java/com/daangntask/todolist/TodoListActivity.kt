package com.daangntask.todolist

import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.daangntask.todolist.databinding.ActivityTodoListBinding


class TodoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoListBinding
    private lateinit var bridge: TodoWebBridge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.settings.javaScriptEnabled = true // 웹뷰의 자바스크립트 기능 활성화
        binding.webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        binding.webView.settings.domStorageEnabled = true

        bridge = TodoWebBridge(applicationContext, { updateWebView() })
        binding.webView.addJavascriptInterface(bridge, "TodoWebBridge")  // JavascriptInterface 추가

        // 웹뷰가 완전히 로딩 된 후 목록 표시
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                bridge.initTodos()
                updateWebView()
            }
        }
        binding.webView.loadUrl("http://127.0.0.1:8000/")

    }


    fun updateWebView() {
        binding.webView.evaluateJavascript("loadTodos()", null)
    }

}