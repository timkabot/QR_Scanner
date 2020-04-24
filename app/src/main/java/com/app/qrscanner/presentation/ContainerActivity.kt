package com.app.qrscanner.presentation

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.app.qrscanner.R
import com.app.qrscanner.Screens
import com.app.qrscanner.domain.entities.Code
import com.app.qrscanner.domain.entities.CodeStatus
import com.app.qrscanner.domain.entities.CodeType
import com.app.qrscanner.domain.interactors.CodeTypeInteractor
import com.app.qrscanner.domain.interactors.DatabaseInteractor
import com.app.qrscanner.presentation.global.BaseFragment
import com.app.qrscanner.presentation.global.CreateCodeBaseFragment
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.activity_main_container.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command
import java.lang.Exception


class ContainerActivity : AppCompatActivity() {

    private val navigatorHolder by inject<NavigatorHolder>()
    private val router by inject<Router>()
    private val databaseInteractor by inject<DatabaseInteractor>()
    private val codeTypeInteractor by inject<CodeTypeInteractor>()
    private val mainVM: MainViewModel by viewModel()
    private lateinit var mInterstitialAd: InterstitialAd
    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = adViewContainer.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_container)
        initActionBar()
        setupNavigationBar()
        initListeners()
        initAds()
        router.newRootScreen(Screens.ScanQRScreen)
    }

    private fun initListeners() {


        settingsButton.setOnClickListener {
            mainVM.goToScreen(Screens.SettingsScreen)
        }

        backButton.setOnClickListener { router.exit() }

        createQrButton.setOnClickListener {
            if (currentFragment is CreateCodeBaseFragment) {
                val frg = currentFragment as CreateCodeBaseFragment
                val (result, schema) = frg.createCode()
                if (result != "") {
                    mainVM.goToScreen(Screens.ShowCreatedQRScreen(result))

                    var codeType = codeTypeInteractor.getCodeTypeForSchema(schema)
                    if(codeType == CodeType.URI) codeType = codeTypeInteractor.getSiteType(result)

                    println("Trying to save \n ${result}")
                    databaseInteractor.saveCodeInDatabase(
                        Code(
                            data = result,
                            status = CodeStatus.CREATED,
                            type = codeType
                        )
                    )
                }
            }
        }
    }

    private fun initActionBar() {
        supportActionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            customView = myToolbar
        }
        setSupportActionBar(customToolbar.myToolbar)
        setStatusBarGradient(this)
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
        get() {
            if (supportFragmentManager.findFragmentById(R.id.container) is CreateCodeBaseFragment)
                return supportFragmentManager.findFragmentById(R.id.container) as? CreateCodeBaseFragment
            return supportFragmentManager.findFragmentById(R.id.container) as? BaseFragment
        }

    private fun initAds(){
        val adView = AdView(this)
        adViewContainer.addView(adView)

        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
        adView.adSize = adSize

        //bannerAd
        adView.loadAd(AdRequest.Builder().build())

        //interstitial ad
        mInterstitialAd = InterstitialAd(this).apply {
            adUnitId = "ca-app-pub-3940256099942544/1033173712"
            loadAd(AdRequest.Builder().build())
            adListener = object : AdListener() {
                override fun onAdClosed() {
                    mInterstitialAd.loadAd(AdRequest.Builder().build())
                }
            }
        }

        mainVM.adsEvent.observe(this, Observer {
            println("Need to show ads")
            if (mInterstitialAd.isLoaded) {
                println("Truing to show ads")
                mInterstitialAd.show()
            }
        })
    }
    private fun setupNavigationBar() {
        fab.setOnClickListener {
            mainVM.goToScreen(Screens.ScanQRScreen)
            bottomNavigationView.selectedItemId = R.id.nagivation_readBarcode
        }
        //bottomNavigationView.itemTextColor = R.drawable.gradient_background
        //bottomNavigationView.itemIconTintList = null
        bottomNavigationView.selectedItemId = R.id.nagivation_readBarcode
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nagivation_history -> {
                    mainVM.goToScreen(Screens.HistoryScreen)
                }
                R.id.nagivation_createBarcode -> {
                    mainVM.goToScreen(Screens.CreateQRScreen)

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
