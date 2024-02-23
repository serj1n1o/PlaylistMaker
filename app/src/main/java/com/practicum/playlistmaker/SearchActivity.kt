package com.practicum.playlistmaker


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
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
    private val listTracks = mutableListOf<Track>()
    private val trackAdapter by lazy { TrackAdapter(listTracks) }
    private lateinit var itunesAPI: ItunesAPI

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder().baseUrl(getString(R.string.base_url_itunes))
            .addConverterFactory(GsonConverterFactory.create()).build()
        itunesAPI = retrofit.create<ItunesAPI>()


        binding.buttonBackSearch.setOnClickListener {
            finish()
        }

        binding.iconClearInput.setOnClickListener {
            binding.inputSearch.setText(INPUT_TEXT_DEFAULT)
            listTracks.clear()
            trackAdapter.notifyDataSetChanged()
            hideKeyboard(binding)
        }

        val inputTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.iconClearInput.visibility = clearInputButtonVisibility(s)
                inputText = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        }
        binding.inputSearch.addTextChangedListener(inputTextWatcher)

        binding.inputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                requestToTracks(inputText, binding)
                hideKeyboard(binding)
                true
            }

            false
        }
    }

    private fun clearInputButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun requestToTracks(searchText: String, binding: ActivitySearchBinding) {
        itunesAPI.getTrack(searchText).enqueue(object : Callback<ItunesResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ItunesResponse>,
                response: Response<ItunesResponse>
            ) {
                listTracks.clear()
                binding.recyclerSearchTrack.isVisible = true
                binding.placeholderError.isVisible = false
                if (response.isSuccessful) {
                    if (response.body()?.tracks?.isNotEmpty() == true) {
                        listTracks.addAll(response.body()?.tracks!!)
                        trackAdapter.notifyDataSetChanged()
                        binding.recyclerSearchTrack.adapter = trackAdapter
                    } else {
                        showPlaceHolder(response.code(), binding)
                    }
                }
            }

            override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                showPlaceHolder(0, binding)
            }

        })
    }

    private fun showPlaceHolder(code: Int, binding: ActivitySearchBinding) {
        if (code in 200..300) {
            binding.apply {
                recyclerSearchTrack.isVisible = false
                buttonUpdate.isVisible = false
                textError.text = getString(R.string.nothing_found_txt)
                placeholderError.isVisible = true
            }
        } else {
            binding.apply {
                recyclerSearchTrack.isVisible = false
                textError.text = getString(R.string.error_found_txt)
                buttonUpdate.isVisible = true
                placeholderError.isVisible = true
                buttonUpdate.setOnClickListener {
                    requestToTracks(inputText, binding)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, inputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val binding = ActivitySearchBinding.inflate(layoutInflater)
        inputText = savedInstanceState.getString(INPUT_TEXT, INPUT_TEXT_DEFAULT)
        binding.inputSearch.apply {
            setText(inputText)
            setSelection(binding.inputSearch.text.length)
        }
    }

    private fun hideKeyboard(binding: ActivitySearchBinding) {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(binding.inputSearch.windowToken, 0)
    }

}