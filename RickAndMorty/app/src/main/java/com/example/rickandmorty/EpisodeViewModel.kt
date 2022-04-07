package com.example.rickandmorty

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EpisodeViewModel(var episodes: ArrayList<String>) : ViewModel() {
    private val mService = App.apiService
    val episodesData: MutableLiveData<List<EpItem>> = MutableLiveData()
    val episodeList: LiveData<List<EpItem>> get() = episodesData
    var copy: ArrayList<EpisodeData> = arrayListOf()

    init {
        getInfo()
    }

    fun getInfo() {
        var numbersOfEpisode: ArrayList<Int> = arrayListOf()
        for (i in episodes.indices) {
            numbersOfEpisode.add(episodes[i].substring(40).toInt())
        }
        Log.d("list2", numbersOfEpisode.toString())

        mService.getEpisodeList(numbersOfEpisode).enqueue(object : Callback<EpisodeData> {
            override fun onResponse(
                call: Call<EpisodeData>,
                response: Response<EpisodeData>
            ) {
                episodesData.value = response.body() as ArrayList<EpItem>
            }

            override fun onFailure(call: Call<EpisodeData>, t: Throwable) {
                Log.d("фейл", t.message.toString())
                episodesData.value = emptyList()
            }
        })
    }

}