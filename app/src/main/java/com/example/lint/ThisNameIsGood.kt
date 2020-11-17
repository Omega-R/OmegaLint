package com.example.lint

import android.annotation.SuppressLint
import android.content.Context
import javax.security.auth.callback.Callback

class ThisNameIsGood(context: Context, some: Int) : InternetBus {
    fun so(callback: Callback, ctx: Context) {
        callback
        emptyFun()
    }

    fun sss(a: Int, b: Int, c: Int, d: Int, f: Int, k: Int) {}

    val str = "s_words_"

    @SuppressLint("FunctionCheck")
    override fun some111(
        a: Int,
        a1: String,
        a2: String,
        a3: String,
        a4: String,
        b2: String,
        b3: String,
        b4: String,
        b5: String,
        b6: String,
        b7: String,
        b11: String
    ) {
        //nuasdasd
    }

    fun emptyFun () {
        val s = 1
    }

}