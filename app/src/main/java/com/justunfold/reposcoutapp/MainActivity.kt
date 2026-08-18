package com.justunfold.reposcoutapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.justunfold.reposcoutapp.features.explore.ExploreScreen
import com.justunfold.reposcoutapp.ui.theme.RepoScoutAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RepoScoutAppTheme {
                ExploreScreen(
                    onNavigateToSearch = {
                        Toast.makeText(this, "Search clicked (will connect in navigation step)", Toast.LENGTH_SHORT).show()
                    },
                    onNavigateToDetail = { owner, repo ->
                        Toast.makeText(this, "Clicked $owner / $repo", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

