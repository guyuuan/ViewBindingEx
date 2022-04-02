package cn.chitanda.viewbindingex.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import cn.chitanda.viewbindingex.databinding.FragmentHomeDialogBinding
import cn.chitanda.viewbindingex.viewBinding
import kotlin.math.roundToInt

/**
 * @author: Chen
 * @createTime: 2022/4/2 15:29
 * @description:
 **/
class HomeDialog : DialogFragment() {
    private val binding by viewBinding {
        FragmentHomeDialogBinding.inflate(layoutInflater)
    }


    override fun onStart() {
        super.onStart()
        val dm = resources.displayMetrics
        dialog?.window?.attributes = dialog?.window?.attributes?.apply {
            height = (dm.heightPixels * 0.5).roundToInt()
            width = (dm.widthPixels * 0.75).roundToInt()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.message.text = "Hello, this is a dialog"
    }
}