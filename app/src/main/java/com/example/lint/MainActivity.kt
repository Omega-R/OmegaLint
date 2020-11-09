package com.example.lint

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    val s: String = ""
    companion object {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val l =
            "lintlintlintlintlintlintlintlintlintlintlintlintlintlintlintlintlintlintlinlintlintlintlintlintlintlintlintlintlintlssntlintlintlintlintlintli" +
                    "nssintlintllintlintlintlintlintintlintlintlint"


        val ctx: Context = this
    }


    @SuppressLint( "FunctionCheck")
    fun emptyFun() {
        //nothing
    }
    protected val s2: String = ""

}
