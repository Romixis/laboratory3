package com.example.rickandmorty.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.*
import java.io.InputStream
import java.net.URL

class CharacterActivity : AppCompatActivity() {

    lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character)

        val description = intent.getStringArrayListExtra("CharacterData")
        val episodes = intent.getStringArrayListExtra("CharacterEpis")
        val eps: ArrayList<String> = episodes as ArrayList<String>
        Log.d("episodes", eps.toString())

        val adapter by lazy { EpisodeAdapter() }
        val model = EpisodeViewModel(episodes)

        val recyclerView: RecyclerView = findViewById(R.id.ep_rView)
        layoutManager = LinearLayoutManager(this)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar);

        setTitle(description?.get(1))

        val statusIcon: ImageView = findViewById(R.id.statusIcon)
        val avatarImage: ImageView = findViewById(R.id.avatarAC)
        val name: TextView = findViewById(R.id.nameAC)
        val statusSpecies: TextView = findViewById(R.id.statusSpeciesText)
        val gender: TextView = findViewById(R.id.genderText)
        val planet: TextView = findViewById(R.id.planetText)

        Log.d("desc", description.toString())

        val i: InputStream = URL(description?.get(0)).openStream()
        val image = BitmapFactory.decodeStream(i)
        avatarImage.setImageBitmap(image)

        if (description?.get(2) == "Alive")
            statusIcon.setImageResource(R.mipmap.ic_alive)
        if (description?.get(2) == "Dead")
            statusIcon.setImageResource(R.mipmap.ic_death)
        if (description?.get(2) == "unknown")
            statusIcon.setImageResource(R.mipmap.ic_unknown)

        name.text = description?.get(1)
        statusSpecies.text = description?.get(2) + " - " + description?.get(5)
        gender.text = description?.get(4)
        planet.text = description?.get(6)

        model.episodeList.observe(this, Observer {
            adapter.submitList(it)
        })
    }
}