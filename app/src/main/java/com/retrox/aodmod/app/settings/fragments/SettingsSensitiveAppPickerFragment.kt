package com.retrox.aodmod.app.settings.fragments

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.settings.adapters.AppsAdapter
import com.retrox.aodmod.app.settings.models.App
import com.retrox.aodmod.app.util.isSystemApp
import com.retrox.aodmod.pref.XPref
import dev.chrisbanes.insetter.applySystemGestureInsetsToPadding
import dev.chrisbanes.insetter.applySystemWindowInsetsToMargin
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding
import kotlinx.android.synthetic.main.fragment_sensitive_app_picker.*
import kotlinx.coroutines.*
import java.util.*

class SettingsSensitiveAppPickerFragment : Fragment() {

    private val job = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private var shouldShowSystemApps: Boolean = true
    private var searchTerm: String? = null

    private val selectedApps by lazy {
        XPref.getSensitiveApps().toMutableList()
    }

    private val apps: List<App>? by lazy {
        context?.packageManager?.run {
            getInstalledApplications(0).filter { getLaunchIntentForPackage(it.packageName) != null }.map {
                App(it.packageName, it.loadLabel(this), it.isSystemApp)
            }.sortedBy { it.appName.toString().toLowerCase(Locale.getDefault()) }
        } ?: kotlin.run {
            null
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sensitive_app_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        toolbar.run {
            title = ""
            inflateMenu(R.menu.menu_apps)
            setupMenu(menu)
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                activity?.finish()
            }
        }
        context?.let { context ->
            recyclerView.layoutManager = LinearLayoutManager(context)
            getApps(shouldShowSystemApps, searchTerm) { apps ->
                swipeRefreshLayout?.post {
                    swipeRefreshLayout.isEnabled = false
                    swipeRefreshLayout.isRefreshing = true
                }
                recyclerView?.adapter = AppsAdapter(context, apps, selectedApps) { packageName ->
                    if(selectedApps.contains(packageName)) selectedApps.remove(packageName)
                    else selectedApps.add(packageName)
                    commit()
                }
                recyclerView?.adapter?.notifyDataSetChanged()
                swipeRefreshLayout?.postDelayed({
                    swipeRefreshLayout?.isRefreshing = false
                }, 500)
            }
        }
        searchBox.setOnEditorActionListener { v, actionId, event ->
            val result = if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                swipeRefreshLayout.isRefreshing = true
                val imm: InputMethodManager = swipeRefreshLayout.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                getApps(shouldShowSystemApps, searchBox.text.toString()) {
                    searchTerm = searchBox.text.toString()
                    val adapter = recyclerView.adapter as AppsAdapter
                    adapter.apps = it
                    adapter.notifyDataSetChanged()
                    swipeRefreshLayout.isRefreshing = false
                    if (it.isEmpty()) {
                        recyclerView.visibility = View.GONE
                        empty_list.visibility = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        empty_list.visibility = View.GONE
                    }
                }
                true
            } else false
            result
        }
        searchBox.addTextChangedListener {
            if (it?.isNotEmpty() == true) {
                search_clear.visibility = View.VISIBLE
            } else {
                search_clear.visibility = View.GONE
            }
        }
        search_clear.setOnClickListener {
            searchTerm = null
            searchBox.editableText.clear()
            swipeRefreshLayout.isRefreshing = true
            val imm: InputMethodManager = swipeRefreshLayout.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            getApps(shouldShowSystemApps, searchTerm) {
                searchTerm = searchBox.text.toString()
                val adapter = recyclerView.adapter as AppsAdapter
                adapter.apps = it
                adapter.notifyDataSetChanged()
                swipeRefreshLayout.isRefreshing = false
                if (it.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    empty_list.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    empty_list.visibility = View.GONE
                }
            }
        }
        search_clear.visibility = View.GONE
        recyclerView.applySystemWindowInsetsToPadding(bottom = true)
        toolbar.applySystemWindowInsetsToPadding(top = true)
    }

    private fun setupMenu(menu: Menu) = menu.run {
        val checkBox = findItem(R.id.menu_show_system)
        checkBox.isChecked = shouldShowSystemApps
        checkBox.setOnMenuItemClickListener {
            checkBox.isEnabled = false
            checkBox.isChecked = !checkBox.isChecked
            toggleSystemApps(checkBox.isChecked) {
                checkBox.isEnabled = true
            }
            true
        }
    }

    private fun getApps(showSystemApps: Boolean, searchString: String?, callback: ((apps: List<App>) -> Unit)? = null) {
        swipeRefreshLayout.isRefreshing = true
        uiScope.launch {
            withContext(Dispatchers.IO) {
                apps?.let { apps ->
                    val appList = apps.filter {
                        (!it.isSystemApp || showSystemApps) && (searchString == null || it.appName.toString().toLowerCase(Locale.getDefault()).contains(searchString.toLowerCase(Locale.getDefault())))
                    }
                    withContext(Dispatchers.Main) {
                        callback?.invoke(appList)
                    }
                }
            }
        }
    }

    private fun toggleSystemApps(shouldShow: Boolean, callback: (() -> Unit)) {
        swipeRefreshLayout.isRefreshing = true
        val adapter = recyclerView.adapter as AppsAdapter
        getApps(shouldShow, searchTerm) {
            adapter.apps = it
            adapter.notifyDataSetChanged()
            shouldShowSystemApps = shouldShow
            swipeRefreshLayout.isRefreshing = false
            callback.invoke()
        }
    }

    private fun commit() = GlobalScope.launch {
        val sensitiveApps = selectedApps.joinToString(",")
        AppPref.sensitiveApps = sensitiveApps
    }

}