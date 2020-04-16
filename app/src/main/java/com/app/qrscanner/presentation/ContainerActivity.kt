package com.app.qrscanner.presentation

import android.app.Activity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.app.qrscanner.R
import com.app.qrscanner.Screens
import com.app.qrscanner.presentation.global.BaseFragment
import kotlinx.android.synthetic.main.activity_main_container.*
import kotlinx.android.synthetic.main.toolbar_custom.*
import kotlinx.android.synthetic.main.toolbar_custom.view.*
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command
import java.util.*


class ContainerActivity : AppCompatActivity() {

    private val navigatorHolder by inject<NavigatorHolder>()
    private val router by inject<Router>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale()
        setContentView(R.layout.activity_main_container)
        initActionBar()
        setupNavigationBar()
        setStatusBarGradient(this)
        initListeners()
        bottomNavigationView.selectedItemId = R.id.nagivation_readBarcode
        router.newRootScreen(Screens.ScanQRScreen)
        println(supportActionBar)
    }

    private fun initListeners() {
        settingsButton.setOnClickListener {
            router.navigateTo(Screens.SettingsScreen)
        }

        backButton.setOnClickListener { router.exit() }
    }

    private fun setLocale() {

        applicationContext.resources.configuration.setLocale(Locale("ru"))
        val primaryLocale = applicationContext.resources.configuration.locales
        println(primaryLocale)

    }

    private fun initActionBar() {
        supportActionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            customView = myToolbar
        }
        setSupportActionBar(customToolbar.myToolbar)
    }

    private val navigator: Navigator =
        object : SupportAppNavigator(this, supportFragmentManager, R.id.container) {
            override fun setupFragmentTransaction(
                command: Command?,
                currentFragment: Fragment?,
                nextFragment: Fragment?,
                fragmentTransaction: FragmentTransaction
            ) {
                // Fix incorrect order lifecycle callback of MainFragment
                fragmentTransaction.setReorderingAllowed(true)
            }
        }

    private val currentFragment: BaseFragment?
        get() = supportFragmentManager.findFragmentById(R.id.container) as? BaseFragment


    private fun setupNavigationBar() {
        fab.setOnClickListener {
            router.navigateTo(Screens.ScanQRScreen)
        }
        //bottomNavigationView.itemTextColor = R.drawable.gradient_background
        //bottomNavigationView.itemIconTintList = null
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nagivation_history -> {
                    router.navigateTo(Screens.HistoryScreen)
                }
                R.id.nagivation_createBarcode -> {
                    router.navigateTo(Screens.CreateQRScreen)
                }
                R.id.nagivation_readBarcode -> {
                }
            }
            true
        }
    }

    private fun setStatusBarGradient(activity: Activity) {
        val window: Window = activity.window
        val background =
            activity.resources.getDrawable(R.drawable.gradient_background)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = activity.resources.getColor(R.color.transparent)
        window.setBackgroundDrawable(background)
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onBackPressed() {
        router.exit()
    }
    companion object {
        fun setAppBatTitle(title: String, activity: ContainerActivity) {
            activity.toolbarTitle?.text = title
        }

        fun changeBackButtonVisibility(activity: ContainerActivity, visibility: Int) {
            activity.backButton.visibility = visibility
        }
        fun changeAdsButtonVisibility(activity: ContainerActivity, visibility: Int) {
            activity.adsButton.visibility = visibility
        }

        fun changeSettingButtonVisibility(activity: ContainerActivity, visibility: Int) {
            activity.settingsButton.visibility = visibility
        }

        fun changeNoAdsButtonVisibility(activity: ContainerActivity, visibility: Int) {
            activity.noAdsButton.visibility = visibility
        }

        fun changeCreateQrButtonVisibility(activity: ContainerActivity, visibility: Int) {
            activity.createQrButton.visibility = visibility
        }
    }
}
