package com.app.qrscanner.presentation.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.qrscanner.R
import com.app.qrscanner.Screens
import com.app.qrscanner.presentation.global.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : BaseFragment() {
    override val layoutRes = R.layout.fragment_history
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewPagerAdapter = ViewPagerAdapter(this)
        viewpager.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, viewpager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.scan)
                1 -> tab.text = getString(R.string.create)
            }
        }.attach()
        //tabLayout.setupWithViewPager(viewpager)
    }

}

private class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int) = when (position) {
        SCAN_HISTORY -> Screens.ScannedBarCodesSubScreen.fragment
        CREATED_BARCODES_HISTORY -> Screens.CreatedBarCodesSubScreen.fragment
        else -> Screens.ScannedBarCodesSubScreen.fragment
    }

    companion object {
        private const val SCAN_HISTORY = 0
        private const val CREATED_BARCODES_HISTORY = 1
    }
}

