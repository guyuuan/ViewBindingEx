package cn.chitanda.viewbindingex.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import cn.chitanda.viewbindingex.databinding.FragmentDashboardBinding
import cn.chitanda.viewbindingex.viewBinding

class DashboardFragment : Fragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding by viewBinding(FragmentDashboardBinding::inflate)

    private val viewModel by viewModels<DashboardViewModel> ()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.text.observe(viewLifecycleOwner){
            binding.textDashboard.text = it
        }
    }
}