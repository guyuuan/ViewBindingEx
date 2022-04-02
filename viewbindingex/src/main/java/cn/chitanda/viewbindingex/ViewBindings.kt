package cn.chitanda.viewbindingex

import android.util.Log
import androidx.annotation.MainThread
import androidx.core.app.ComponentActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author: Chen
 * @createTime: 2022/4/2 11:36
 * @description:
 **/
internal const val TAG = "ViewBindingEx"

interface ViewBindingProperty<in T : LifecycleOwner, out V : ViewBinding> :
    ReadOnlyProperty<T, V> {

    @MainThread
    fun createViewBinding(): V

    @MainThread
    fun clear()
    interface Factory<out V : ViewBinding> {
        fun create(): V
    }
}

abstract class LifecycleViewBindingProperty<in T : LifecycleOwner, out V : ViewBinding>
    (private val factory: ViewBindingProperty.Factory<V>) :
    ViewBindingProperty<T, V> {
    private var binding: V? = null

    override fun createViewBinding(): V = factory.create()

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        binding?.let { return it }
        val lifecycle = thisRef.lifecycle
        val viewBinding = createViewBinding()
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            Log.w(
                TAG, "Access to viewBinding after Lifecycle is destroyed or hasn't created yet. " +
                        "The instance of viewBinding will be not cached."
            )
        } else {
            binding = viewBinding
            lifecycle.doOnDestroy(::clear)
        }
        return viewBinding
    }


    override fun clear() {
        binding = null
    }

    private inline fun Lifecycle.doOnDestroy(crossinline action: () -> Unit) {
        addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    action()
                    source.lifecycle.removeObserver(this)
                }
            }
        })
    }
}

class ActivityViewBindingProperty<in T : ComponentActivity, out V : ViewBinding>(factory: Factory<V>) :
    LifecycleViewBindingProperty<T, V>(factory) {

    class Factory<out V : ViewBinding>(private val inflater: () -> V) :
        ViewBindingProperty.Factory<V> {
        override fun create(): V = inflater()
    }
}

class FragmentViewBindingProperty<in T : Fragment, out V : ViewBinding>(factory: Factory<V>) :
    LifecycleViewBindingProperty<T, V>(factory) {

    class Factory<out V : ViewBinding>(private val inflater: () -> V) :
        ViewBindingProperty.Factory<V> {
        override fun create(): V = inflater()
    }
}

class DialogFragmentViewBindingProperty<in T : DialogFragment, out V : ViewBinding>(factory: Factory<V>) :
    LifecycleViewBindingProperty<T, V>(factory) {

    class Factory<out V : ViewBinding>(private val inflater: () -> V) :
        ViewBindingProperty.Factory<V> {
        override fun create(): V = inflater()
    }
}

fun <T : ComponentActivity, V : ViewBinding> ComponentActivity.viewBinding(inflater: () -> V): ActivityViewBindingProperty<T, V> {
    return ActivityViewBindingProperty(
        factory = ActivityViewBindingProperty.Factory(
            inflater
        )
    )
}

fun <T : Fragment, V : ViewBinding> Fragment.viewBinding(inflater: () -> V): FragmentViewBindingProperty<T, V> {
    return FragmentViewBindingProperty(
        factory = FragmentViewBindingProperty.Factory(
            inflater
        )
    )
}
