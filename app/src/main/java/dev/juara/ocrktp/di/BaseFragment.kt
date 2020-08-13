package dev.juara.ocrktp.di

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<V : ViewDataBinding> : Fragment() {

    lateinit var binding: V

    open val screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    @LayoutRes
    abstract fun screenLayout(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, screenLayout(), container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        initViews()
        setupLiveDataObservation()
        return binding.root
    }

    open fun initViews() {}
    open fun setupLiveDataObservation() {}

    override fun onResume() {
        activity?.requestedOrientation = screenOrientation
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

}