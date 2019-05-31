package com.retrox.aodmod.app.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.retrox.aodmod.app.state.AppState
import com.retrox.aodmod.shared.SharedContentManager
import org.jetbrains.anko.runOnUiThread
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread

object Utils {
    private var pid = ""

    fun findProcessAndKill(context: Context, processName: String = "com.oneplus.aod") {
        try {
            var line: String
            val process = Runtime.getRuntime().exec("su")
            val stdin = process.outputStream
            val stderr = process.errorStream
            val stdout = process.inputStream

            stdin.write("ps -e \n".toByteArray())
            stdin.flush()
            stdin.write("exit\n".toByteArray())
            stdin.close()

            var br = BufferedReader(InputStreamReader(stdout))
            br.lineSequence().forEach {
                if (it.contains(processName)) {
                    killProxy(it, context)
                }
            }

            br.close()
            br = BufferedReader(InputStreamReader(stderr))
            br.lineSequence().forEach {
                Log.e("[Error]", it)
            }

            br.close()

            process.waitFor()
            process.destroy()

            kill(pid, context) // kill aod

        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(context, "出现错误，可能是无法获取Root权限", Toast.LENGTH_SHORT).show()
        }
    }

    // 这个本来没有什么卵用 但是要规避ProGuard的蜜汁警告
    private fun killProxy(it: String, context: Context) {
        val strings = it.split(" ".toRegex()).filterNot { it == "" || it == " " }
        Log.d("[PidTEST]", strings.toString())

        if (strings.isNotEmpty()) {
            context.runOnUiThread {
                Toast.makeText(
                    context,
                    "查询成功：Pid:${strings[1]} 已重启息屏程序 点击按钮可以再次重启",
                    Toast.LENGTH_SHORT
                ).show()

                thread {
                    SharedContentManager.resetStateFile()
                    AppState.refreshStatus("Process Kill")
                }
            }
            pid = strings[1]
        }
    }

    private fun kill(pid: String, context: Context) {
        if (pid == "") {
            Toast.makeText(context, "未查询到pid，您的手机可能不是一加，如果是，请联系开发者", Toast.LENGTH_SHORT).show()
        }
        try {
            val process = Runtime.getRuntime().exec("su")
            val stdin = process.outputStream
            val stderr = process.errorStream
            val stdout = process.inputStream

            stdin.write("kill $pid \n".toByteArray())
            stdin.flush()
            stdin.write("exit\n".toByteArray())
            stdin.close()

            var br = BufferedReader(InputStreamReader(stdout))
            br.lineSequence().forEach {

            }

            br.close()
            br = BufferedReader(InputStreamReader(stderr))
            br.lineSequence().forEach {
                Log.e("[Error]", it)
            }

            br.close()

            process.waitFor()
            process.destroy()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}
