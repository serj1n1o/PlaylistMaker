package com.practicum.playlistmaker.sharing.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.api.InternalNavigator
import java.io.File
import java.io.FileOutputStream

class InternalNavigatorImpl(private val context: Context) : InternalNavigator {

    override suspend fun saveImageToStorage(artworkUri: Uri, namePlaylist: String): Uri {
        val filePath = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            context.getString(R.string.playlist_covers)
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val fileName = "$namePlaylist.jpg"
        val file = File(filePath, fileName)
        val inputStream = context.contentResolver.openInputStream(artworkUri)
        val outputStream = FileOutputStream(file)

        inputStream.use { input ->
            outputStream.use { output ->
                BitmapFactory
                    .decodeStream(input)
                    .compress(Bitmap.CompressFormat.JPEG, 30, output)
            }
        }
        return file.toUri()
    }


}