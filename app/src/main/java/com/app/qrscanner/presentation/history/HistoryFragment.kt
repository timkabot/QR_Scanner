package com.app.qrscanner.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.qrscanner.R
import com.app.qrscanner.Screens
import com.app.qrscanner.presentation.ContainerActivity
import com.app.qrscanner.presentation.global.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.custom_mytab.view.*
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : BaseFragment() {
    override val layoutRes = R.layout.fragment_history
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private val activity
        get() = getActivity() as ContainerActivity


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewPagerAdapter = ViewPagerAdapter(this)
        viewpager.adapter = viewPagerAdapter


        TabLayoutMediator(tabLayout, viewpager) { tab, position ->
            when (position) {
                0 -> {
                    val customView =
                        LayoutInflater.from(context).inflate(R.layout.custom_mytab, null)
                    customView.imageInTab.setImageResource(R.drawable.scanned)
                    customView.gradientTextTab.text = getString(R.string.scanned)
                    tab.customView = customView
                }
                1 -> {
                    val customView =
                        LayoutInflater.from(context).inflate(R.layout.custom_mytab, null)
                    customView.imageInTab.setImageResource(R.drawable.created)
                    customView.gradientTextTab.text = getString(R.string.created)
                    tab.customView = customView

                }
            }
        }.attach()
    }

    override fun onPause() {
        println("History onPause")
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        changeToolbar(started = true)
    }

    override fun onStop() {
        println("History onStop")
        super.onStop()
    }

    override fun onDestroy() {
        println("History onDestroy")
        super.onDestroy()
    }

    private fun changeToolbar(started: Boolean) {
        if (started) {

            ContainerActivity.setAppBatTitle("История", activity)
            ContainerActivity.changeSettingButtonVisibility(activity, View.VISIBLE)
            ContainerActivity.changeAdsButtonVisibility(activity, View.VISIBLE)
            ContainerActivity.changeNoAdsButtonVisibility(activity, View.GONE)
            ContainerActivity.changeBackButtonVisibility(activity, View.GONE)

        } else {
            ContainerActivity.setAppBatTitle("", activity)
            ContainerActivity.changeBackButtonVisibility(activity, View.VISIBLE)

        }

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

