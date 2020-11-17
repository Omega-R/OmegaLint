package com.example.lint

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

	companion object {
		const val SS_S_SFDAS = 2

		private const val EXTRA_PHOTO = "photo"

		fun createIntent(context: Context, photo: String): Intent {
			return Intent(context, MainActivity::class.java)
				.putExtra(EXTRA_PHOTO, photo)
		}
	}

	fun spme() {
		String?.toString()
	}


	@SuppressLint("NOT_LEFT_EMPTY_BODY")
	private fun some() {

	}


	override fun onCreate(savedInstanceState: Bundle?) {

		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)


		val ctx: Context = this
		String.toString()


		val list = listOf<String>()

		list.forEach { line ->
			val s = line
		}
	}

	@Suppress
	fun emptyFun() {
		//nothing
	}

	protected val s2: String = ""

	fun Get(): String {
		val s = 2
		String.toString()
		return "some"
	}

	fun aAA() {

	}

}
