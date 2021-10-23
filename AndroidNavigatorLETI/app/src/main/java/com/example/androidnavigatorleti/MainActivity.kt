package com.example.androidnavigatorleti

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.androidnavigatorleti.data.UserDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var db: UserDatabase

    // Слушатель реагирующий на смену destination в графе
    private val onDestinationChangedListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        toolbar?.title = destination.label
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.AppTheme_Main)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val host: NavHostFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment?
                ?: return
        val navController = host.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNav?.setupWithNavController(navController)
        toolbar?.setNavigationOnClickListener { onBackPressed() }

        // При переходе по графу дергается этот listener
        navController.addOnDestinationChangedListener(onDestinationChangedListener)

        db = Room.databaseBuilder(
                applicationContext,
                UserDatabase::class.java, "androidnavigatorleti"
        ).build()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}