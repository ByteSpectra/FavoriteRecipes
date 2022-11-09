package com.mobivone.favrecipes.view.activities

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.mobivone.favrecipes.R
import com.mobivone.favrecipes.databinding.ActivityMainBinding
import com.mobivone.favrecipes.model.notification.NotificationWorker
import com.mobivone.favrecipes.utils.Constants
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mNavController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_all_dishes, R.id.navigation_favorite_dishes, R.id.navigation_random_dishes
            )
        )
        setupActionBarWithNavController(mNavController, appBarConfiguration)
        mBinding.navView.setupWithNavController(mNavController)

        if (intent.hasExtra(Constants.NOTIFICATION_ID)) {
            val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, 0)
            mBinding.navView.selectedItemId = R.id.navigation_random_dishes
        }

        startWork()
    }

    override fun onNavigateUp(): Boolean {
        // this is the function due to which we can come back from details fragment to the originated fragment
        return NavigationUI.navigateUp(mNavController, null)
    }

    fun hideBottomNavView(){
        mBinding.navView.clearAnimation()
        mBinding.navView.animate().translationY(mBinding.navView.height.toFloat()).duration = 300
        mBinding.navView.visibility = View.GONE
    }

    fun showBottomNavView(){
        mBinding.navView.clearAnimation()
        mBinding.navView.animate().translationY(0f).duration = 300
        mBinding.navView.visibility = View.VISIBLE
    }

    private fun createConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
        .setRequiresCharging(false)
        .setRequiresBatteryNotLow(true)
        .build()

    private fun createWorkRequest() = PeriodicWorkRequestBuilder<NotificationWorker>(5, TimeUnit.HOURS)
        .setConstraints(createConstraints())
        .build()

    private fun startWork() {
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "MyFavDishPeriodicWork",
                ExistingPeriodicWorkPolicy.KEEP,
                createWorkRequest()
            )
    }
}