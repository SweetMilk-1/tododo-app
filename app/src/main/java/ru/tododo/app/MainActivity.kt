package ru.tododo.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragmentContainer) as NavHostFragment


        val navController = navHostFragment.navController
        val bottomNavBar = findViewById<BottomNavigationView>(R.id.bottomNavBar)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavBar.setupWithNavController(navController)

        val rootView = findViewById<View>(R.id.main)

        applyTopAndBottomInsets(rootView)
        requestNotificationPermission()
    }

    private fun applyTopAndBottomInsets(rootView: View) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top, bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }

    companion object {

        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1
    }
}