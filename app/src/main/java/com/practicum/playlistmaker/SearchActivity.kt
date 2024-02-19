package com.practicum.playlistmaker


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


class SearchActivity : AppCompatActivity() {

    companion object {
        const val INPUT_TEXT = "INPUT_TEXT"
        const val INPUT_TEXT_DEFAULT = ""
    }

    private var inputText = INPUT_TEXT_DEFAULT
    private lateinit var inputSearch: EditText
    private val recyclerSearchTrack by lazy { findViewById<RecyclerView>(R.id.recycler_search_track) }
    private val listTracks = mutableListOf<Track>()
    private val trackAdapter by lazy { TrackAdapter(listTracks) }
    private val placeHolderError by lazy { findViewById<LinearLayout>(R.id.placeholder_error) }
    private val textError by lazy { findViewById<TextView>(R.id.text_error) }
    private val btnUpdate by lazy { findViewById<MaterialButton>(R.id.button_update) }
    private lateinit var itunesAPI: ItunesAPI

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val retrofit = Retrofit.Builder().baseUrl(getString(R.string.base_url_itunes))
            .addConverterFactory(GsonConverterFactory.create()).build()
        itunesAPI = retrofit.create<ItunesAPI>()
        val clearInputButton = findViewById<ImageView>(R.id.icon_clear_input)
        inputSearch = findViewById(R.id.input_search)

        val buttonBack = findViewById<ImageView>(R.id.button_back_search)
        buttonBack.setOnClickListener {
            finish()
        }

        clearInputButton.setOnClickListener {
            inputSearch.setText(INPUT_TEXT_DEFAULT)
            listTracks.clear()
            trackAdapter.notifyDataSetChanged()
            hideKeyboard()
        }

        val inputTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearInputButton.visibility = clearInputButtonVisibility(s)
                inputText = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        }
        inputSearch.addTextChangedListener(inputTextWatcher)

        inputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                requestToTracks(inputText)
                hideKeyboard()
                true
            }

            false
        }
    }

    private fun clearInputButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun requestToTracks(searchText: String) {
        itunesAPI.getTrack(searchText).enqueue(object : Callback<ItunesResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ItunesResponse>,
                response: Response<ItunesResponse>
            ) {
                listTracks.clear()
                recyclerSearchTrack.isVisible = true
                placeHolderError.isVisible = false
                if (response.isSuccessful) {
                    if (response.body()?.tracks?.isNotEmpty() == true) {
                        listTracks.addAll(response.body()?.tracks!!)
                        trackAdapter.notifyDataSetChanged()
                        recyclerSearchTrack.adapter = trackAdapter
                    } else {
                        showPlaceHolder(response.code())
                    }
                }
            }

            override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                showPlaceHolder(0)
            }

        })
    }

    private fun showPlaceHolder(code: Int) {
        if (code in 200..300) {
            recyclerSearchTrack.isVisible = false
            btnUpdate.isVisible = false
            textError.text = getString(R.string.nothing_found_txt)
            placeHolderError.isVisible = true
        } else {
            recyclerSearchTrack.isVisible = false
            textError.text = getString(R.string.error_found_txt)
            btnUpdate.isVisible = true
            placeHolderError.isVisible = true
            btnUpdate.setOnClickListener {
                requestToTracks(inputText)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, inputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(INPUT_TEXT, INPUT_TEXT_DEFAULT)
        inputSearch.setText(inputText)
        inputSearch.setSelection(inputSearch.text.length)
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(inputSearch.windowToken, 0)
    }

}