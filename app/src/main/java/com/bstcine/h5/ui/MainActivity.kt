package com.bstcine.h5.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.bstcine.h5.CineApplication
import com.bstcine.h5.CineConfig
import com.bstcine.h5.R
import com.bstcine.h5.base.BaseWebFragment
import com.bstcine.h5.ui.csub.CSubFragment
import com.bstcine.h5.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var navigation: BottomNavigationView

    private var mCurrentPrimaryItem: Fragment? = null

    private var mNextItemId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            if (mCurrentPrimaryItem is BaseWebFragment) {
                (mCurrentPrimaryItem as BaseWebFragment).emitJs("android_call_h5_test", "joe")
            }
        }

        findViewById<FloatingActionButton>(R.id.fab).setOnLongClickListener {
            CineApplication.INSTANCE.logout()
            ToastUtils.showLong("logout success.")
            reloadFragment()
            true
        }

        navigation = findViewById(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            val itemId = item.itemId

            if ((itemId == R.id.action_learn || itemId == R.id.action_mine) && !CineApplication.INSTANCE.isLogin()) {
                mNextItemId = itemId
                ActivityUtils.startActivityForResult(this@MainActivity, LoginActivity::class.java, REQUEST_LOGIN)
                return@OnNavigationItemSelectedListener false
            }

            val mFragmentManager = supportFragmentManager
            val mCurTransaction = mFragmentManager.beginTransaction()

            val name = makeFragmentName(itemId)
            var fragment = mFragmentManager.findFragmentByTag(name)
            if (fragment != null) {
                mCurTransaction.show(fragment)
            } else {
                fragment = getItem(itemId)
                mCurTransaction.add(R.id.container, fragment!!, name)
            }

            if (mCurrentPrimaryItem != null && fragment !== mCurrentPrimaryItem) {
                mCurTransaction.hide(mCurrentPrimaryItem!!)
            }

            mCurTransaction.commitAllowingStateLoss()

            mNextItemId = null
            mCurrentPrimaryItem = fragment
            true
        })
        navigation.selectedItemId = R.id.action_store
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState);解决重启Fragment重叠问题
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_LOGIN && resultCode == 1) {
            reloadFragment()
        }
    }

    private fun makeFragmentName(id: Int): String {
        return "android:switcher:" + R.id.container + ":" + id
    }

    private fun getItem(itemId: Int): Fragment? {
        var selectedFragment: Fragment? = null
        when (itemId) {
            R.id.action_learn -> selectedFragment = BaseWebFragment.newInstance(CineConfig.H5_URL_LEARN)
            R.id.action_store -> selectedFragment = BaseWebFragment.newInstance(CineConfig.H5_URL_STORE)
            R.id.action_mine -> selectedFragment = BaseWebFragment.newInstance(CineConfig.H5_URL_MINE)
            R.id.action_csub -> selectedFragment = CSubFragment()
        }
        return selectedFragment
    }

    private fun reloadFragment() {
        val mFragmentManager = supportFragmentManager
        val mCurTransaction = mFragmentManager.beginTransaction()
        for (fragment in mFragmentManager.fragments) {
            mCurTransaction.remove(fragment)
        }
        mCurTransaction.commitNowAllowingStateLoss()
        mCurrentPrimaryItem = null
        navigation.selectedItemId = mNextItemId ?: R.id.action_store
    }

    companion object {
        private const val REQUEST_LOGIN = 10001
    }
}