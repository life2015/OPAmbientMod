package com.retrox.aodmod.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import android.util.Log

open class LiveEvent<T>() : MediatorLiveData<T>() {

    constructor(initialValue: T) : this() {
        value = initialValue
    }

    private val observers = mutableMapOf<Observer<T>, StartVersionObserver>()
    private val wrappedObservers = mutableMapOf<StartVersionObserver, Observer<T>>()
    private var version = 0

//    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
//        val wrapped = observers[observer] ?: StartVersionObserver(version, observer).also {
//            observers[observer] = it
//            wrappedObservers[it] = observer
//        }
//        super.observe(owner, wrapped)
//    }

    fun observeNewOnly(owner: LifecycleOwner, observer: Observer<T>) {
        val wrapped = observers[observer] ?: StartVersionObserver(version, observer).also {
            observers[observer] = it
            wrappedObservers[it] = observer
        }
        super.observe(owner, wrapped)
    }

    override fun observeForever(observer: Observer<in T>) {
        val wrapped = observers[observer] ?: StartVersionObserver(version, observer as Observer<T>).also {
            observers[observer] = it
            wrappedObservers[it] = observer
        }
        super.observeForever(wrapped)
    }

    override fun removeObserver(observer: Observer<in T>) {
        // since we dont' know where this will be called from, `observer` could be
        // the original observable or the wrapped observable, so we need to make sure
        // we update our current observer state and pass the wrapped observer to the
        // super.
        val originalObserver = wrappedObservers.remove(observer) ?: observer
        val wrapped = observers.remove(originalObserver)
        if (wrapped == null) { // 修复使用LiveData标准observe方式最后无法撤销观察的bug
            super.removeObserver(observer)
            return
        }
        if (observer == originalObserver) wrappedObservers.remove(wrapped)
        super.removeObserver(wrapped)
    }

    override fun setValue(value: T?) {
        version++
        super.setValue(value)
    }

    // Used when T is Unit, to make calls easier
    fun call() {
        value = null
    }

    internal inner class StartVersionObserver(
        startVersion: Int,
        val observer: Observer<T>
    ) : Observer<T> {

        private var lastSeenVersion = startVersion

        override fun onChanged(t: T?) {
            if (lastSeenVersion < this@LiveEvent.version) {
                lastSeenVersion = this@LiveEvent.version
                observer.onChanged(t)
            }
        }
    }

}