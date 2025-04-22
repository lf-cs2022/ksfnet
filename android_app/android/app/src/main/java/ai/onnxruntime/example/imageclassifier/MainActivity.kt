// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package ai.onnxruntime.example.imageclassifier

import SharedViewModel
import ai.onnxruntime.*
import ai.onnxruntime.example.imageclassifier.fragment.HistoryFragment
import ai.onnxruntime.example.imageclassifier.fragment.HomeFragment
import ai.onnxruntime.example.imageclassifier.fragment.SettingFragment
import android.Manifest
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.camera.core.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    var fragmentList: MutableList<Fragment>? = null
//    private lateinit var viewModel: SharedViewModel
    //定义底部导航栏用于切换
    var bottomNavigationView: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

//        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        //初始化fragmentList
        fragmentList = ArrayList<Fragment>()
        fragmentList!!.add(HomeFragment())
        fragmentList!!.add(HistoryFragment())
        fragmentList!!.add(SettingFragment())


        //默认显示第一个首页fragment
        showFragment(fragmentList!![0])
        //找到底部导航栏id
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.navBottomView)
        //底部导航栏点击时触发
        bottomNavigationView?.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item -> //用switch报错，改用if实现
            if (item.itemId == R.id.item_home) {
                showFragment(fragmentList!![0]) //显示首页
            }
            else if (item.itemId == R.id.item_history) {
                showFragment(fragmentList!![1]) //显示历史
//            } else if (item.itemId == R.id.menu_discovery) {
//                showFragment(fragmentList!![2]) //显示发现
            }
            else {
                showFragment(fragmentList!![2]) //显示设置
            }
            true
        })
    }

    //显示fragment
    fun showFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        // 替换Fragment
        fragmentTransaction.replace(R.id.navFragment, fragment)
        // 提交事务
        fragmentTransaction.commit()
    }

    companion object {
        public const val TAG = "ORTImageClassifier"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
