package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.util.Resource
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TrackRepository) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()
    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.searchTracks(expression)) {
                is Resource.Success -> {
                    consumer.consume(foundTracks = resource.data, errorCode = null)
                }

                is Resource.Error -> {
                    consumer.consume(foundTracks = null, errorCode = resource.resultCode)
                }
            }
        }
    }
}