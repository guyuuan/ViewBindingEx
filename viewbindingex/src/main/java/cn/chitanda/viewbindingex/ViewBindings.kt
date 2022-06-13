package cn.chitanda.viewbindingex

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.MainThread
import androidx.core.app.ComponentActivity
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
    fun createViewBinding(thisRef:T): V

    @MainThread
    fun clear()
    interface Factory<T,out V : ViewBinding> {
        fun create(thisRef:T): V
    }
}

abstract class LifecycleViewBindingProperty<in T : LifecycleOwner, out V : ViewBinding>
    (private val factory: ViewBindingProperty.Factory<T,V>) :
    ViewBindingProperty<T, V> {
    private var binding: V? = null

    override fun createViewBinding(thisRef:T): V = factory.create(thisRef)

    abstract fun getLifecycleOwner(thisRef: T): LifecycleOwner

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        binding?.let { return it }
        val lifecycle = getLifecycleOwner(thisRef).lifecycle
        val viewBinding = createViewBinding(thisRef)
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
        Log.d(TAG, "clear: ")
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

class ActivityViewBindingProperty<in T : ComponentActivity, out V : ViewBinding>(factory: Factory<T,V>) :
    LifecycleViewBindingProperty<T, V>(factory) {

    class Factory<T:ComponentActivity,out V : ViewBinding>(private val inflater: () -> V) :
        ViewBindingProperty.Factory<T,V> {
        override fun create(thisRef:T): V = inflater()
    }

    override fun getLifecycleOwner(thisRef: T): LifecycleOwner = thisRef
}

class FragmentViewBindingProperty<in T : Fragment, out V : ViewBinding>(factory: Factory<T,V>) :
    LifecycleViewBindingProperty<T, V>(factory) {

    class Factory<T:Fragment,out V : ViewBinding>(private val inflater: (T) -> V) :
        ViewBindingProperty.Factory<T,V> {
        override fun create(thisRef:T): V = inflater(thisRef)
    }

    override fun getLifecycleOwner(thisRef: T): LifecycleOwner = thisRef.viewLifecycleOwner

}

fun <T : ComponentActivity, V : ViewBinding> ComponentActivity.viewBinding(inflater: WithLayoutInflater<V>): ActivityViewBindingProperty<T, V> {
    return ActivityViewBindingProperty(
        factory = ActivityViewBindingProperty.Factory(
            inflater = { inflater(layoutInflater) }
        )
    )
}

inline fun <T : Fragment, V : ViewBinding> Fragment.viewBinding(crossinline inflater: WithLayoutInflater<V>): FragmentViewBindingProperty<T, V> {
    return FragmentViewBindingProperty(
        factory = FragmentViewBindingProperty.Factory(
            inflater = { inflater(layoutInflater) }
        )
    )
}

@JvmName("fragmentViewBindByBind")
inline fun <T : Fragment, V : ViewBinding> Fragment.viewBinding(
    crossinline bind: WithViewBind<V>,
    crossinline getRoot: (T) -> View = Fragment::requireView
): FragmentViewBindingProperty<T, V> {
    return FragmentViewBindingProperty(
        factory = FragmentViewBindingProperty.Factory(
            inflater = {
                bind(getRoot(it))
            }
        )
    )
}

typealias WithLayoutInflater<V> = (LayoutInflater) -> V
typealias WithViewBind<V> = (View) -> V