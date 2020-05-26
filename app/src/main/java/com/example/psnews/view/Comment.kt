package com.example.psnews.view

import android.content.Context
import android.content.Intent
import android.hardware.input.InputManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.example.psnews.Adapter.CommentAdapter
import com.example.psnews.Adapter.NewsAdapter

import com.example.psnews.R
import com.example.psnews.databinding.FragmentCommentBinding
import com.example.psnews.extentions.toast
import com.example.psnews.helper.Common
import com.example.psnews.helper.Constants
import com.example.psnews.helper.SharedPrefrenceManager
import com.example.psnews.network.Status
import com.example.psnews.viewmodel.CommentViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.lin_loading_dim
import kotlinx.android.synthetic.main.activity_profile.loading
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_comment.*
import kotlinx.android.synthetic.main.fragment_comment.iv_back
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class Comment : Fragment() {

    lateinit var inputMethodManager:InputMethodManager
    private val sharedPreferences: SharedPrefrenceManager by inject()
    private val commentViewModel: CommentViewModel by inject(parameters = {
        parametersOf(
            requireContext()
        )
    })
    lateinit var binding: FragmentCommentBinding
    private var newsId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            newsId = it.getString(Constants.COMMENT_FRAGMENT_BUNDEL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarComment)
        binding.commentViewModel = commentViewModel
        binding.toolbarComment.title = ""
        binding.toolbarComment.subtitle = ""

        commentViewModel.getNews(newsId!!)

        initViews()
        setupViews()

        return binding.root

    }

    private fun initViews() {
        inputMethodManager =  requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        binding.etSendComment.requestFocus()
    }

    private fun setupViews() {

        commentViewModel.mutableInsertedComment.observe(requireActivity(), Observer {
            when (it.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {

                }
                Status.ERROR -> {
                    requireContext().toast(msg = it.error!!)
                }
            }
        })

        binding.ivBack.setOnClickListener(View.OnClickListener {
            requireActivity().onBackPressed()
        })

        binding.ivSendComment.setOnClickListener {
            if (binding.etSendComment.text.toString().isNotEmpty()) {
                commentViewModel.insertComment(
                    et_send_comment.text.toString(),
                    sharedPreferences.getUser().id.toString(),
                    newsId!!
                )
                val commentAdapter = binding.rclComment.adapter as CommentAdapter
                commentAdapter.insertComment(
                    com.example.psnews.model.Comment(
                        0,
                        et_send_comment.text.toString(),
                        "",
                        sharedPreferences.getUser().id.toString(),
                        newsId!!,
                        sharedPreferences.getUser().name,
                        sharedPreferences.getUser().userAvatar
                    )
                )
                binding.etSendComment.text.clear()
                binding.rclComment.scrollToPosition(0)
            }
        }

        binding.etSendComment.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(ss: Editable?) {
                if (ss.toString().trim().isNotEmpty()) {
                    binding.ivSendComment.alpha = 1f

                } else
                    binding.ivSendComment.alpha = 0.6f

            }

            override fun beforeTextChanged(ss: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(ss: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    companion object {

        @JvmStatic
        fun newInstance(newsId: String) =
            Comment().apply {
                arguments = Bundle().apply {
                    putString(Constants.COMMENT_FRAGMENT_BUNDEL, newsId)
                }
            }
    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = requireActivity().window
            w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(
                requireActivity(),
                R.color.colorPrimary
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requireActivity().getWindow().decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = requireActivity().window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(
                requireActivity(),
                R.color.colorWhite
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requireActivity().getWindow().decorView.systemUiVisibility = 0
            }
        }
    }

}
