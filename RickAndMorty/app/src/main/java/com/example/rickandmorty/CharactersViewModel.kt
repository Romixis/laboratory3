package com.example.rickandmorty

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rickandmorty.retrofitpkg.RetrofitServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val NUMBER_PAGE_INDEX = 1

class charactersViewModel() : ViewModel() {
    val mService = App.apiService
    val characterData: MutableLiveData<List<CharacterItem>> = MutableLiveData()
    val characterList: LiveData<List<CharacterItem>> get() = characterData
    lateinit var copyOfCharacters: List<CharacterItem.CharacterInfo>

    init {
        getCharacters(1)
    }

    private fun getCharacters(page: Int) {
        characterData.value = characterData.value.let { it?.slice(0 until (it.size - 1)) }
        mService.getCharactersList(page).enqueue(object : Callback<CharacterData> {

            override fun onResponse(
                call: Call<CharacterData>,
                response: Response<CharacterData>
            ) {
                characterData.value = convertResponceToCharList(response)

                Log.d("responce", characterData.value!!.size.toString())

            }

            override fun onFailure(call: Call<CharacterData>, t: Throwable) {
                Log.d("фейл", t.message.toString())
                characterData.value = emptyList()
            }
        })
    }

    private fun convertResponceToCharList(resp: Response<CharacterData>): List<CharacterItem> {
        val oldData = characterData.value ?: listOf()
        val tempStorage = mutableListOf<CharacterItem>()
        resp.body()?.let { responceBody ->
            responceBody.results.forEach { char ->
                tempStorage.add(CharacterItem.CharacterInfo(char))
            }
            responceBody.info.next?.let { linkToNextPage ->
                val nexPageNumber = linkToNextPage.split('=')[NUMBER_PAGE_INDEX].toInt()
                tempStorage.add(CharacterItem.NextPage(nexPageNumber))
            }
        }
        return oldData.plus(tempStorage)
        Log.d("tag", oldData.size.toString())
    }

    fun handleClick(position: Int): Boolean {
        return if (characterData.value?.get(position) is CharacterItem.NextPage) {
            (characterData.value?.get(position) as CharacterItem.NextPage).nextpageid?.let {
                getCharacters(it)
            }
            false
        } else {
            true
        }
    }

    fun getCharacterByPosition(pos: Int): CharacterItem? {
        return characterData.value?.get(pos)
    }

    fun search(s: CharSequence) {
         var results = mutableListOf<CharacterItem.CharacterInfo>()
         if (s.isNotBlank()) {
             copyOfCharacters.forEach { character ->
                 if (character.character.name.toLowerCase().contains(s))
                     results.add(character)

             }
         } else
             results.addAll(copyOfCharacters)

         characterData.value = results
     }
}