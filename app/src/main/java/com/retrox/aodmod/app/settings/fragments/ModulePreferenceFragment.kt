package com.retrox.aodmod.app.settings.fragments

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.retrox.aodmod.BuildConfig
import com.retrox.aodmod.R
import com.retrox.aodmod.app.XposedUtils
import com.retrox.aodmod.app.alipay.AlipayZeroSdk
import com.retrox.aodmod.app.settings.SettingsActivity
import com.retrox.aodmod.app.settings.fragments.bottomsheet.DebugInfoBottomSheetFragment
import com.retrox.aodmod.app.settings.preference.ButtonsPreference
import com.retrox.aodmod.app.util.Utils
import com.retrox.aodmod.app.view.joinQQGroup
import com.retrox.aodmod.extensions.getDrawableC
import com.retrox.aodmod.extensions.isOP7Pro
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.shared.SharedContentManager
import org.jetbrains.anko.support.v4.act

class ModulePreferenceFragment : GenericPreferenceFragment() {

    private var hasJustRestarted = false

    private val isPickUpDisplayEnabled: Boolean
        get() = Settings.System.getInt(context?.contentResolver, "prox_wake_enabled", 0) != 0

    private val isStoragePermissionGranted: Boolean
        get() = activity?.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_module)
        setupTroubleshootingPreference()
        setupGroupPreference()
    }

    override fun onResume() {
        super.onResume()
        findPreference("module_status_xposed") {
            it.icon = getIconForState(isTaiChiActivated() || isEdXposedActivated())
            it.summary = getXposedStateString()
            getXposedLaunchIntent()?.let { launchIntent ->
                it.setOnPreferenceClickListener {
                    startActivity(launchIntent)
                    true
                }
            }
        }
        findPreference("module_options") {
            it.setOnPreferenceClickListener {
                startActivity(Intent(context, SettingsActivity::class.java))
                true
            }
        }
        findPreference("module_pick_up_display") {
            it.icon = getIconForState(isPickUpDisplayEnabled)
            it.summary = getStringState(
                isPickUpDisplayEnabled,
                R.string.settings_module_status_pick_up_display_desc_off,
                R.string.settings_module_status_pick_up_display_desc_on
            )
            it.setOnPreferenceClickListener {
                val intent = Intent()
                intent.component =
                    ComponentName("com.oneplus.aod", "com.oneplus.settings.SettingsActivity")
                startActivity(intent)
                true
            }
        }
        setupStoragePreference()
        setupModuleStatusPreference()
        findPreference("module_about") {
            it.title = getString(R.string.settings_module_about_info, BuildConfig.VERSION_NAME)
        }
        setupDonatePreference()
    }

    private fun setupStoragePreference() {
        findPreference("module_storage_permission") {
            it.icon = getIconForState(isStoragePermissionGranted)
            it.summary = getStringState(
                isStoragePermissionGranted,
                R.string.settings_module_status_storage_permission_desc_off,
                R.string.settings_module_status_storage_permission_desc_on
            )
            if (!isStoragePermissionGranted) {
                it.setOnPreferenceClickListener {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
                    true
                }
            }
        }
    }

    private fun setupModuleStatusPreference() {
        context?.let { context ->
            val sharedState = SharedContentManager.getSharedState(context)
            val times = sharedState.aodTimes.toInt()
            findPreference("module_status_working") {
                if (times > 0) {
                    hasJustRestarted = false
                    it.icon = context.getDrawableC(R.drawable.ic_module_check)
                    it.summary = getString(R.string.settings_module_status_working_desc_on)
                } else {
                    it.icon =
                        if (hasJustRestarted) context.getDrawableC(R.drawable.ic_module_warning) else context.getDrawableC(
                            R.drawable.ic_module_error
                        )
                    it.summary = getStringState(
                        hasJustRestarted,
                        R.string.settings_module_status_working_desc_off,
                        R.string.settings_module_status_working_desc_just_restarted
                    )
                }
                it.setOnPreferenceClickListener {
                    DebugInfoBottomSheetFragment().show(childFragmentManager, "bs_debug")
                    true
                }
            }
        }
    }

    private fun setupTroubleshootingPreference() {
        findButtonsPreference("module_troubleshooting") {
            it.buttonsOnNewLine = true
            it.clearButtonsList()
            it.addButton(
                R.string.settings_module_troubleshooting_rehooking,
                if(isTaiChiInstalled() && isTaiChiActivated()) R.drawable.ic_taichi else R.drawable.ic_module_extension
            ) {
                if (isTaiChiInstalled()) {
                    val t = Intent("me.weishu.exp.ACTION_MODULE_MANAGE")
                    t.data = Uri.parse("package:" + "com.retrox.aodmod")
                    t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(t)
                } else if (isEdXposedInstalled()) {
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.`package` = "org.meowcat.edxposed.manager"
                    intent.component = ComponentName(
                        "org.meowcat.edxposed.manager",
                        "org.meowcat.edxposed.manager.WelcomeActivity"
                    )
                    intent.putExtra("fragment", 3)
                    startActivity(intent)
                }
            }
            it.addButton(
                R.string.settings_module_troubleshooting_restart_ambient,
                R.drawable.ic_settings_clock_alignment
            ) {
                Utils.findProcessAndKill(
                    it.context,
                    if (isOP7Pro()) "com.android.systemui" else "com.oneplus.aod"
                )
                view?.let { view ->
                    Snackbar.make(
                        view,
                        getString(R.string.settings_module_troubleshooting_restart_ambient_snackbar),
                        Snackbar.LENGTH_LONG
                    ).setGoogleSans().show()
                }
                findPreference("module_status_xposed") { pref ->
                    pref.icon = context?.getDrawableC(R.drawable.ic_module_warning)
                    pref.summary =
                        context?.getString(R.string.settings_module_status_working_desc_just_restarted)
                    hasJustRestarted = true
                }
            }
        }
    }

    private fun setupDonatePreference() {
        findButtonsPreference("module_donate") {
            it.clearButtonsList()
            it.addButton(
                R.string.settings_module_about_donate_alipay,
                R.drawable.ic_alipay
            ) {
                if (AlipayZeroSdk.hasInstalledAlipayClient(context)) {
                    AlipayZeroSdk.startAlipayClient(activity, "fkx08744aqofnhxpvkgd6d0")
                } else {
                    Snackbar.make(listView, getString(R.string.settings_module_about_donate_alipay_not_installed), Snackbar.LENGTH_LONG).setGoogleSans().show()
                }
            }
            it.addButton(
                R.string.settings_module_about_donate_paypal,
                R.drawable.ic_paypal
            ) {
                val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.paypal.me/KieronQuinn"))
                startActivity(intent)
            }
        }
    }

    private fun setupGroupPreference() {
        findButtonsPreference("module_groups") {
            it.clearButtonsList()
            it.addButton(
                R.string.settings_module_groups_telegram,
                R.drawable.ic_module_group_telegram,
                R.color.button_telegram
            ) {
                val telegram = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://t.me/joinchat/FE-DFRPY5fduM2wTsl1Spg")
                );
                startActivity(telegram);
            }
            it.addButton(
                R.string.settings_module_groups_qq,
                R.drawable.ic_module_group_qq,
                R.color.button_qq
            ) {
                val key = "8bW_c8foZfXB1NFZILBsupRDWblY3Lhl"
                context?.joinQQGroup(key)
            }
            it.addButton(
                R.string.settings_module_groups_qq_backup,
                R.drawable.ic_module_group_qq,
                R.color.button_qq
            ) {
                val key = "20ARgc7Mzn0TNIKYJAiCXmfWg2FkPEog"
                context?.joinQQGroup(key)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        setupStoragePreference()
    }

    private fun getXposedStateString(): String {
        if (isTaiChiActivated()) return getString(R.string.settings_module_status_xposed_desc_on_tai)
        if (isEdXposedActivated()) return getString(R.string.settings_module_status_xposed_desc_on_ed)
        if (isTaiChiInstalled()) return getString(R.string.settings_module_status_xposed_desc_off_tai)
        if (isEdXposedInstalled()) return getString(R.string.settings_module_status_xposed_desc_off_ed)
        return getString(R.string.settings_module_status_xposed_desc_not_installed)
    }

    private fun getXposedLaunchIntent(): Intent? {
        //Prioritise TaiChi over EdXposed but only if it's activated in there - some people use both
        if (isTaiChiInstalled() && isTaiChiActivated()) return context?.packageManager?.getLaunchIntentForPackage(
            XposedUtils.TAICHI_PACKAGE_NAME
        )
        if (isEdXposedInstalled()) return context?.packageManager?.getLaunchIntentForPackage(
            XposedUtils.EDXPOSED_PACKAGE_NAME
        )
        if (isTaiChiInstalled()) return context?.packageManager?.getLaunchIntentForPackage(
            XposedUtils.TAICHI_PACKAGE_NAME
        )
        return null
    }

    private fun getIconForState(state: Boolean): Drawable? {
        return if (state) context?.getDrawableC(R.drawable.ic_module_check)
        else context?.getDrawableC(R.drawable.ic_module_cross)
    }

    private fun getStringState(
        state: Boolean,
        @StringRes offString: Int,
        @StringRes onString: Int
    ): String {
        return if (state) getString(onString)
        else getString(offString)
    }

    private fun isTaiChiActivated(): Boolean {
        return XposedUtils.isExpModuleActive(context)
    }

    private fun isEdXposedActivated(): Boolean {
        return XposedUtils.isEdXposedModuleActive()
    }

    private fun isTaiChiInstalled(): Boolean {
        return XposedUtils.isTaiChiInstalled(context)
    }

    private fun isEdXposedInstalled(): Boolean {
        return XposedUtils.isEdXposedInstalled(context)
    }

    fun Context.joinQQGroup(key: String) {
        val intent = Intent()
        intent.data =
            Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$key")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            view?.let {
                Snackbar.make(it, getString(R.string.qq_not_supported), Snackbar.LENGTH_SHORT)
                    .setGoogleSans().show()
            }
        }

    }

}