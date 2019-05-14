package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.transaction
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.transaction(allowStateLoss = true) {
            replace(R.id.fragment, AFragment())
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            supportFragmentManager.transaction(allowStateLoss = true) {
                when (it.itemId) {
                    R.id.menu_A -> { replace(R.id.fragment, AFragment()) }
                    R.id.menu_B -> { replace(R.id.fragment, BFragment()) }
                    R.id.menu_C -> { replace(R.id.fragment, CFragment()) }
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }
}
