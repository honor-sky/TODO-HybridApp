package com.daangntask.todolist

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID


class TodoWebBridge(private val context: Context, val updateWebView : () -> (Unit)) {

    private var todos = mutableListOf<Task>()
    private val gson = Gson()
    private val sharedPref = context.getSharedPreferences("TodoList", MODE_PRIVATE) // 로컬저장소


    /* 자바스크립트 연결을 위한 함수*/

    @JavascriptInterface
    fun loadTasks() : String? {
        //Log.d("TodoWebBridge:loadTasks","${todos}")
        return gson.toJson(todos)
    }


    @JavascriptInterface
    fun showToast(toast: String) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun addTask(id : String, content: String, isDone : Boolean) {
        //Log.d("TodoWebBridge:addTask","${newTask}")
        val taskId = UUID.randomUUID().toString()

        todos.add(Task(id, content, isDone))
        saveTodo()

        /* 브라우저 스토리지를 사용할 경우 주석처리 해줍니다. */
        /*
        CoroutineScope(Dispatchers.Main).launch {
            updateWebView()
        }

         */

    }

    @JavascriptInterface
    fun deleteTask(taskId : String) {
        //Log.d("TodoWebBridge:deleteTask","${taskId}")
        todos.removeAll { it.id == taskId }
        saveTodo()

        /* 브라우저 스토리지를 사용할 경우 주석처리 해줍니다. */
        /*
        CoroutineScope(Dispatchers.Main).launch {
            updateWebView()
        }

         */
    }

    @JavascriptInterface
    fun editTask(taskId : String, content : String) {
        //Log.d("TodoWebBridge:deleteTask","${taskId}")
        todos.map { task -> //todos =
            if (task.id == taskId) {
                //task.copy(content = content)
                task.content = content
            } else {
                task
            }
        }//.toMutableList()

        saveTodo()

        /* 브라우저 스토리지를 사용할 경우 주석처리 해줍니다. */
        /*
        CoroutineScope(Dispatchers.Main).launch {
            updateWebView()
        }

         */

        
    }

    @JavascriptInterface
    fun setIsDone(taskId : String, isDone : Boolean) {
        //Log.d("TodoWebBridge:setIsDone","${taskId}")
        todos = todos.map { task ->
            if (task.id == taskId) {
                task.copy(isDone = isDone)
            } else {
                task
            }
        }.toMutableList()

        saveTodo()
    }


    /* 안드로이드 로컬 스토리지 접근을 위한 함수*/

    // 처음 앱이 실행되면 수행
    fun initTodos() {
        val todosJson = sharedPref.getString("tasks", "[]") // String 형태
        todos = gson.fromJson(todosJson, object : TypeToken<List<Task?>?>() {}.getType())
    }

    private fun saveTodo() {
        val todosJson = gson.toJson(todos)
        sharedPref!!.edit().putString("tasks", todosJson).apply()
    }


}

