package com.example.rickandmorty.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.Adapter
import com.example.rickandmorty.CharacterItem
import com.example.rickandmorty.R
import com.example.rickandmorty.Result
import com.example.rickandmorty.charactersViewModel

class MainActivity : AppCompatActivity() {

    lateinit var layoutManager: GridLayoutManager
    private val model: charactersViewModel by viewModels()
    private val adapter = Adapter { position ->
        val positionOnCharacter = model.handleClick(position)
        if (positionOnCharacter) {
            startCharacterEpisodeActivity(position)
        }
    }

    private fun startCharacterEpisodeActivity(pos: Int){
        intent = Intent(this, CharacterActivity::class.java)
        val chosenChar = model.getCharacterByPosition(pos) as CharacterItem.CharacterInfo
        var description: ArrayList<String> = arrayListOf()
        description.add(chosenChar.character.image)
        description.add(chosenChar.character.name)
        description.add(chosenChar.character.status)
        description.add(chosenChar.character.type)
        description.add(chosenChar.character.gender)
        description.add(chosenChar.character.species)
        description.add(chosenChar.character.origin.name)
        intent.putExtra("CharacterData", description)
        intent.putExtra("CharacterEpis", chosenChar.character.episode as ArrayList<String>)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setContentView(R.layout.activity_main)

        title = ""

        val recyclerView: RecyclerView = findViewById(R.id.rView)
        layoutManager = GridLayoutManager(this, 2)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        observe()
        adapter.submitList(model.characterList.value)

        val searchET: EditText = findViewById(R.id.search_edit_text)
        searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                model.search(s)
            }
        })
    }

    fun onCellClickListener(data: ArrayList<String>, ep: ArrayList<String>) {
        intent = Intent(this, CharacterActivity::class.java)
        intent.putExtra("CharacterData", data)
        intent.putExtra("CharacterEpis", ep)
        startActivity(intent)
    }

    fun observe() {
        model.characterData.observe(this, Observer {
            adapter.submitList(it)
        })
    }
}