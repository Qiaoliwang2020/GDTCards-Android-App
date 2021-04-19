package com.example.dgtcards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dgtcards.ui.main.CardlistFragment

class cardlist : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cardlist_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, CardlistFragment.newInstance())
                    .commitNow()
        }
    }
}