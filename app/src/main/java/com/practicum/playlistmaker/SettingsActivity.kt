package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonBack = findViewById<ImageView>(R.id.button_back_settings)
        buttonBack.setOnClickListener {
            finish()
        }

        val shareApp = findViewById<ImageView>(R.id.buttonShareApp)
        shareApp.setOnClickListener {
            val message = getString(R.string.https_course_android_developer)
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.type = "text/plane"
            startActivity(Intent.createChooser(shareIntent, getString(R.string.select_app)))
        }

        val sendToSupport = findViewById<ImageView>(R.id.buttonSendSupport)
        sendToSupport.setOnClickListener {
            val message = getString(R.string.subject_text_mail)
            val messageTheme = getString(R.string.message_text_mail)
            val mail = arrayOf(getString(R.string.my_mail_yandex))
            val sendToSupportIntent = Intent(Intent.ACTION_SENDTO)
            sendToSupportIntent.data = Uri.parse("mailto:")
            sendToSupportIntent.putExtra(Intent.EXTRA_EMAIL, mail)
            sendToSupportIntent.putExtra(Intent.EXTRA_TEXT, message)
            sendToSupportIntent.putExtra(Intent.EXTRA_SUBJECT, messageTheme)
            startActivity(sendToSupportIntent)
        }

        val userAgreement = findViewById<ImageView>(R.id.buttonUserAgreement)
        userAgreement.setOnClickListener {
            val urlOffer = getString(R.string.offer_yandex)
            val userAgreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlOffer))
            startActivity(userAgreementIntent)
        }

    }
}