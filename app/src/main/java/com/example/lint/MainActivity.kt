package com.example.lint

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lint.very_big_very_very_very_very_very_very_very_very_very_very_very_very_veryvery_very_very_very_very_very_very_very_very_very_very_very_veryvery_very_very_very_very_very_very_very_very_very_very_very_very.BAdclass

class Main : InternetBus, AppCompatActivity() {
    final val s: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val l = "lintlintlintlintlintlintlintlintlintlintlintlintlintlintlintlintlintlintlinlintlintlintlintlintlintlintlintlintlintlssntlintlintlintlintlintli" +
                "nssintlintllintlintlintlintlintintlintlintlint"


        BAdclass()

        val ctx: Context = this

        val list = mutableListOf<Int>()
        list.add(1)
        list.add(1)
        list.add(1)
        list.add(1)
        list.add(1)

        val al = "lint"

        try {
            val s = 2
        } catch (e: Exception) {
            val sa = 2
        }
    }

    private fun sss(a: Int, b: Int, c: Int, d: Int, f: Int, k: Int) {}
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
        TODO("Not yet implemented")
    }
}
