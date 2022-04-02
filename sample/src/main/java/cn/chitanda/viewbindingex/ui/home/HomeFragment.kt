package cn.chitanda.viewbindingex.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import cn.chitanda.viewbindingex.databinding.FragmentHomeBinding
import cn.chitanda.viewbindingex.viewBinding

class HomeFragment : Fragment() {


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding by viewBinding { FragmentHomeBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<HomeViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.text.observe(viewLifecycleOwner) {
            binding.textHome.text = it
        }
        binding.button.setOnClickListener {
            HomeDialog().show(childFragmentManager,"dialog")
        }
    }
}