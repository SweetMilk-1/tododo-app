package ru.tododo.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import ru.tododo.feature.tasks.details.TaskDetailFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.commit {
            replace(R.id.main, TaskDetailFragment.newInstance())
            addToBackStack(null)
        }
    }
}