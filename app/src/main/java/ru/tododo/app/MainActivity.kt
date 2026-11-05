package ru.tododo.app

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragmentContainer) as NavHostFragment


        val navController = navHostFragment.navController
        val bottomNavBar = findViewById<BottomNavigationView>(R.id.bottomNavBar)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavBar.setupWithNavController(navController)

        val rootView = findViewById<View>(R.id.main)

        applyTopPaddingWhenSdk35(rootView)
    }

    private fun applyTopPaddingWhenSdk35(rootView: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                // Применяем верхнюю вставку как padding, чтобы содержимое не перекрывалось строкой состояния/ActionBar
                view.updatePadding(top = insets.top)
                WindowInsetsCompat.CONSUMED
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }
}