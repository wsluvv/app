package com.delirium.films

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.delirium.films.databinding.ActivityMainBinding
import io.realm.Realm

class MainActivity : AppCompatActivity() {
    lateinit var appBarNavController: AppBarConfiguration
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBase()

        val bindingMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingMain.root)
        supportActionBar?.hide()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment

        navController = navHostFragment.navController
        appBarNavController = AppBarConfiguration(navController.graph)

        bindingMain.toolBar.setupWithNavController(navController, appBarNavController)
        bindingMain.toolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.favorite -> {
                    Log.i("MAIN", "Click on heard")
                    navController.navigate(R.id.favoriteFragment)
                    true
                }
                else -> {
                    Log.i("MAIN", "Click on else")
                    false
                }
            }
        }
    }

    private fun initDataBase() {
        Realm.init(this)
        Realm.setDefaultConfiguration(RealmConfiguration().getConfigDB())
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarNavController)
                || super.onSupportNavigateUp()
    }
}