package com.example.lint

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

	private val someaaa = "photo11111111111111111111111111111111111111111111111111111111111"

	companion object {

		const val ASAS = 2 * 1

		val some = 2

		private val FREQUENCIES = listOf(50, 60)

		private const val EXTRA_PHOTO =
			"photo1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111"

		fun ppp() {
			String.toString()?.toString()
		}

	}



	fun someFunction() {

	}

	fun some(): Int = 1

	val s = 2

	fun createIntent(context: Context, photo: String): Intent = Intent()

	fun spme() {
		String.toString()
		val tag = emptyList<String>()
		tag.forEach {
			Log.d("some", it)
		}


	}

	override fun onCreate(savedInstanceState: Bundle?) {

		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)


		val cTX: Context = this

		var croppingBlock: (() -> Int)? = null

		try {

		} catch (e: Exception) {
			val some = 2
		}

		val list = listOf<String>()

		list.forEach { line ->
			val s = line
		}
	}

	private fun asome() {
		createIntent(this, "")
		emptyURL({ val s = 2 }, { val s = 2 })
	}

	private fun emptyURL(function: () -> Unit, function2: () -> Unit) {
		//nothing
	}

	@SuppressLint("OMEGA_ABBREVIATION_AS_WORD")
	val soURL = 2

	class SomeClass() {

	}


	fun em(): Int {
		try {
			val s = 2
		} catch (e: Exception) {
			throw e
		}

		return when (1) {
			1 -> 2
			else -> 0
		}

	}

	protected val s2: String = ""


	fun agset() {
		val s = 2
		s.toString()
		//nothing
	}

	fun aAa() {
		//nothing
	}

	data class GeneralSettings(
		var doorStatus: Int = 1,
		var keepDoorOpen: Boolean = false,
		var keepDoorOpenSip: Boolean = false,
		var doorOpenDuration: Int = 5,
		var callDuration: Int = 5,
		var talkDuration: Int = 5,
		var conciergeApartment: Int = 100,
		val conciergeApartment1: Int = 100,
		var conciergeApartment2: Int = 100,
		val conciergeApartment3: Int = 100,
		var conciergeApartment5: Int = 100
	)

	class BasePresenter<VIEW: RecyclerView>
}
