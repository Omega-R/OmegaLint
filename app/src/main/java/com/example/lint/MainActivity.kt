package com.example.lint

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {

    }

    val SSs = 2

    fun spme() {
        String
            ?.toString()
    }


    fun createLauncher() {
        createActivityLauncher()
    }


    private fun createActivityLauncher(): String {
        return "createLauncher()"
    }

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val cTX: Context = this
        String
            .toString()
        String::class.java


        val list = listOf<String>()

        list.forEach { line ->
            val s = line
        }
    }

    private fun getTabName(position: Int): String {
        return when (position) {
            0 ->  getString(R.string.app_name)
            else -> getString(R.string.app_name)
        }
    }


    @SuppressLint("OMEGA_NOT_EXCEED_MAX_LINE_LENGTH")
    private fun SSsome(): Int {
        val s = 2
        val list = listOf("")
        list.forEach { line ->
            val some = line
        }

        val s3 = 1
        return when (s) {
            s3 -> { 0 }

            0, 2 -> {
                val some = 2
                1
            }
            else ->  2
        }

    }

    class SomeClass() {
        val soURL = 2
    }


    fun emptyFun() {
        try {
            val s = 2
        } catch (e: Exception) {
            throw e
        }
    }

    object Auth : Screen()

    open class Screen() {}

    fun some(): String {
        return "S"
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
