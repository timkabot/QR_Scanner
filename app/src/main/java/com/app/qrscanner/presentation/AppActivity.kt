package com.app.qrscanner.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.app.qrscanner.R
import com.app.qrscanner.Screens
import com.app.qrscanner.presentation.global.BaseFragment
import kotlinx.android.synthetic.main.activity_main_container.*
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command

class AppActivity : AppCompatActivity() {

    private val navigatorHolder by inject<NavigatorHolder>()
    private val router by inject<Router>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_container)
        setupNavigationBar()
        bottomNavigationView.selectedItemId = R.id.nagivation_readBarcode
        router.newRootScreen(Screens.ScanQRScreen)
    }

    private val navigator: Navigator =
        object : SupportAppNavigator(this, supportFragmentManager, R.id.container) {
            override fun setupFragmentTransaction(
                command: Command?,
                currentFragment: Fragment?,
                nextFragment: Fragment?,
                fragmentTransaction: FragmentTransaction) {
                // Fix incorrect order lifecycle callback of MainFragment
                fragmentTransaction.setReorderingAllowed(true)
            }
        }

    private val currentFragment: BaseFragment?
        get() = supportFragmentManager.findFragmentById(R.id.container) as? BaseFragment


    private fun setupNavigationBar() {
        bottomNavigationView.setOnNavigationItemSelectedListener{ item ->
            when (item.itemId) {
                R.id.nagivation_history -> {
                    router.navigateTo(Screens.HistoryScreen)
                }
                R.id.nagivation_createBarcode -> {
                    router.navigateTo(Screens.CreateQRScreen)
                }
                R.id.nagivation_readBarcode -> {
                    router.navigateTo(Screens.ScanQRScreen)
                }
            }
            true
        }
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }
}
