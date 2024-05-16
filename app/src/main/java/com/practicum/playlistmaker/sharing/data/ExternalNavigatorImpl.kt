package com.practicum.playlistmaker.sharing.data

import android.app.Application
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator

class ExternalNavigatorImpl(private val application: Application) : ExternalNavigator {
    override fun shareLink() {
        val link = application.getString(R.string.https_course_android_developer)
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plane"
        }
        val shareIntent = Intent.createChooser(intent, null)
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        application.startActivity(shareIntent)
    }

    override fun openEmail() {
        val message = application.getString(R.string.message_text_mail)
        val subject = application.getString(R.string.subject_text_mail)
        val mail = arrayOf(application.getString(R.string.my_mail_yandex))
        val sendToSupportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, mail)
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        application.startActivity(sendToSupportIntent)
    }

    override fun openLink() {
        val link = application.getString(R.string.offer_yandex)
        val termsIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(link)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        application.startActivity(termsIntent)
    }
}