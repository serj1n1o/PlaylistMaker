package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBackSettings.setOnClickListener {
            finish()
        }

        binding.switchTheme.apply {
            isChecked = (application as App).isDarkTheme
            setOnCheckedChangeListener { _, isChecked ->
                (application as App).switchTheme(isChecked)
            }
        }


        binding.buttonShareApp.setOnClickListener {
            val message = getString(R.string.https_course_android_developer)
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plane"
            }

            startActivity(Intent.createChooser(shareIntent, getString(R.string.select_app)))

        }

        binding.buttonSendSupport.setOnClickListener {
            val message = getString(R.string.subject_text_mail)
            val messageTheme = getString(R.string.message_text_mail)
            val mail = arrayOf(getString(R.string.my_mail_yandex))
            val sendToSupportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, mail)
                putExtra(Intent.EXTRA_TEXT, message)
                putExtra(Intent.EXTRA_SUBJECT, messageTheme)
            }
            startActivity(sendToSupportIntent)
        }

        binding.buttonUserAgreement.setOnClickListener {
            val urlOffer = getString(R.string.offer_yandex)
            val userAgreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlOffer))
            startActivity(userAgreementIntent)
        }

    }
}