package com.example.supersos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.supersos.MainMenu.AkunPage
import com.example.supersos.MainMenu.HistoryPage
import com.example.supersos.MainMenu.HomePage
import com.example.supersos.MainMenu.InstancesPage
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ismaeldivita.chipnavigation.ChipNavigationBar

@Suppress("DEPRECATION")
class MainPage : AppCompatActivity() {

    val fragment = HomePage()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        openMainFragment()
        supportActionBar?.hide()

        var menu_bottom = findViewById<ChipNavigationBar>(R.id.bottom_navigation)
        menu_bottom.setItemSelected(R.id.menu_home)

//        menu_bottom.showBadge(R.id.menu_instance)
        menu_bottom.setOnItemSelectedListener {
            when (it) {

                R.id.menu_home -> {
                    openMainFragment()
                }

                R.id.menu_history -> {
                    val historyFragment = HistoryPage()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frag_container_nav, historyFragment).commit()

                }

//                R.id.menu_instance -> {
//                    val instanceFragment = InstancesPage()
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.frag_container_nav, instanceFragment).commit()
////                    menu_bottom.dismissBadge(R.id.menu_instance)
//                }

                R.id.menu_account -> {
                    val accountFragment = AkunPage()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frag_container_nav, accountFragment).commit()
                }
            }
        }
    }

    private fun openMainFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frag_container_nav, fragment)
        transaction.commit()
    }
}
