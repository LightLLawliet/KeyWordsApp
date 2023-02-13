package com.example.keywordsapp.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.keywordsapp.ui.main.favorites.FavoritesFragment
import com.example.keywordsapp.ui.main.history.presentation.HistoryFragment
import com.example.keywordsapp.ui.main.search.presentation.SearchFragment


class SectionsPagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HistoryFragment()
            1 -> SearchFragment()
            2 -> FavoritesFragment()
            else -> throw java.lang.IllegalStateException()
        }
    }
}