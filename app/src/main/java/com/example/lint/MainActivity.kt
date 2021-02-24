package com.example.lint

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    companion object {

        const val SS_S_SFDAS = 2

        val some1 = 2

        private const val EX2TRA_PHO2TO24 = "photo"

        fun createIntent(context: Context, photo: String): Intent {
            return Intent(context, MainActivity :: class.java).putExtra(EX2TRA_PHO2TO24, photo)
        }
        fun someFunction() {}

        fun createLauncher() = { someFunction() }
    }

    val s1 = 2

    fun spme2() {
        String?.toString()

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val cTXbb1: Context = this
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
    data class GeneralSettings(
        var doorStatus: Int = 5,
        var keepDoorOpen: Boolean = false,
        var keepDoorOpenSip: Boolean = false,
        var doorOpenDuration: Int = 5,
        var callDuration: Int = 5,
        var talkDuration: Int = 5,
        var conciergeApartment: Int = 100,
        var relayStatus: Int = 5,
        var relayStatus1: Int = 5,
        var relayStatus2: Int = 5,
        var relayStatus3: Int = 5,
        var relayStatus4: Int = 5,
        var relayStatus55: Int = 5,
        var relayStatus555: Int = 5,
        var relayStatus53: Int = 5,
        var relayStatus54: Int = 5,
        var relayStatus5: Int = 5,
    )


}
