package com.example.passengerapp.ui.screens

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import com.example.passengerapp.R
import com.example.passengerapp.databinding.ActivityMainBinding
import com.example.passengerapp.ui.screens.contract.CustomAction
import com.example.passengerapp.ui.screens.contract.GoBackAppBarBehavior
import com.example.passengerapp.ui.screens.contract.HasCustomAction


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragment: Fragment? = null
    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            currentFragment = f
            updateUI()
        }
    }
    private lateinit var navHostFragment: NavHostFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.childFragmentManager.registerFragmentLifecycleCallbacks(
            fragmentListener,
            false
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        updateUI()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navHostFragment.navController.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun createCustomToolbarAction(action: CustomAction) {
        binding.toolbar.menu.clear()
        val iconDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(this, action.iconRes)!!)
        iconDrawable.setTint(Color.WHITE)
        val menuItem = binding.toolbar.menu.add("")
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        menuItem.icon = iconDrawable
        menuItem.setOnMenuItemClickListener {
            action.onCustomAction.invoke()
            return@setOnMenuItemClickListener true
        }
    }

    private fun updateUI() {
        currentFragment?.let { fragment ->
            supportActionBar?.setDisplayHomeAsUpEnabled(fragment is GoBackAppBarBehavior)
            if (fragment is HasCustomAction) {
                createCustomToolbarAction(fragment.getCustomAction())
            } else {
                binding.toolbar.menu.clear()
            }
        }
    }
}