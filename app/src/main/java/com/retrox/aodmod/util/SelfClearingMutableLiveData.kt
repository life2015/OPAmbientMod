package com.retrox.aodmod.util

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *  A MutableLiveData derivative that automatically clears its value after a specified period of time
 *  Use postValue(value, timeout) to post a value & reset to null after the timeout
 *  Use postValue(value) to post the value with no timeout
 *
 *  Note: If postValue(value, timeout) is called before a timeout ends, the timeout will be restarted
 */
class SelfClearingMutableLiveData<T>: MutableLiveData<T>() {

    private var clearJob: Job? = null

    fun postValue(value: T, timeout: Long){
        postValue(value)
        startClearJob(timeout)
    }

    override fun postValue(value: T?) {
        clearJob?.cancel()
        clearJob = null
        super.postValue(value)
    }

    private fun startClearJob(timeout: Long){
        clearJob = GlobalScope.launch {
            delay(timeout)
            postValue(null)
            clearJob = null
        }
    }

}