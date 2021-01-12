package com.example.lint

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    companion object {

        const val SS_S_SFDAS = 2

        val some = 2

        private const val EXTRA_PHOTO = "photo"

        fun createIntent(context: Context, photo: String): Intent {
            return Intent(context, MainActivity ::class.java).putExtra(EXTRA_PHOTO, photo)
        }
        fun someFunction() {}

        fun createLauncher() = { someFunction() }
    }

    val SSs = 2

    fun spme() {
        String?.toString()

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val cTX: Context = this
        String
            .toString()


        val list = listOf<String>()

        list.forEach { line ->
            val s = line
        }
    }

    private fun SSsome ( ) {
        MainActivity .createIntent(this, "")
    }

    class SomeClass() {
        val soURL = 2
    }


    fun emptyFun(): Int {
        try {
            val s = 2
        } catch (e: Exception) {
            throw e
        }


        return  when(1) {
            1 -> 2
            else -> 0
        }

    }

    protected val s2: String = ""


    fun Get() {
        val s = 2
        s.toString()
        //nothing
    }

    fun aAA() {

    }

}
