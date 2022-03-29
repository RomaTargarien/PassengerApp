package com.example.passengerapp.ui.screens.passengerlist

import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passengerapp.R
import com.example.passengerapp.databinding.FragmentPassengerListBinding
import com.example.passengerapp.model.ui.PassengerLayout
import com.example.passengerapp.ui.screens.contract.CustomAction
import com.example.passengerapp.ui.screens.contract.HasCustomAction
import com.example.passengerapp.ui.util.extensions.createItemTouchHelper
import com.example.youngchemist.ui.custom.snack_bar.CustomSnackBar
import com.example.youngchemist.ui.custom.snack_bar.CustomSnackBar.Companion.setOnClickListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PassengerListFragment : Fragment(), HasCustomAction {

    val viewModel: PassengerListViewModel by viewModel()
    private lateinit var binding: FragmentPassengerListBinding
    private lateinit var passengersAdapter: PassengerLayoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentPassengerListBinding.inflate(inflater, container, false).also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setUpRecyclerView()
        collectAdapterData()
    }

    override fun getCustomAction(): CustomAction = CustomAction(
        R.drawable.ic_baseline_add_24
    ) {
        findNavController().navigate(R.id.action_passengerListFragment_to_passengerCreatingFragment)
    }

    private fun collectAdapterData() {
        lifecycleScope.launch {
            viewModel.pagingDataLayout.collectLatest {
                passengersAdapter.submitData(it)
            }
        }
        lifecycleScope.launch {
            passengersAdapter.loadStateFlow.collect { loadState ->
                binding.bnRetry.isVisible = loadState.source.refresh is LoadState.Error
                binding.swipeToRefresh.isRefreshing = loadState.source.refresh is LoadState.Loading
            }
        }
        lifecycleScope.launch {
            viewModel.refreshSharedFlow.collect {
                passengersAdapter.refresh()
            }
        }
    }

    private fun drawDeleteBasket(canvas: Canvas,rectF: RectF,alpha: Int) {
        val paint = Paint()
        paint.alpha = alpha
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_icon_trash)
        canvas.drawBitmap(bitmap, null, rectF, paint)
    }

    private fun setUpRecyclerView() {
        passengersAdapter = PassengerLayoutAdapter()
        binding.rvPassengers.apply {
            adapter = passengersAdapter.withLoadStateHeaderAndFooter(
                header = PassengerLoadStateAdapter { passengersAdapter.retry() },
                footer = PassengerLoadStateAdapter { passengersAdapter.retry() }
            )
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            val simpleItemTouchHelper = createItemTouchHelper(
                swipeDirection = ItemTouchHelper.LEFT,
                onSwipedAction = { position ->
                    passengersAdapter.getPassengerLayout(position)?.let {
                        undoDelete(it)
                        viewModel.deletePassengerById(it.id)
                    }
                },
                onChildDrawAction = { canvas, viewHolder,dX,iconDest ->
                    val alpha = ((-dX / viewHolder.itemView.width.toFloat()) * 255).toInt()
                    drawDeleteBasket(canvas,iconDest,alpha)
                }
            )
            ItemTouchHelper(simpleItemTouchHelper).attachToRecyclerView(this)
        }
        binding.swipeToRefresh.setOnRefreshListener {
            passengersAdapter.refresh()
            binding.swipeToRefresh.isRefreshing = true
        }
        passengersAdapter.setOnExpandedClickListener {
            it.first.toggleSelection()
            passengersAdapter.notifyItemChanged(it.second, it.first.selected)
        }
    }

    private fun undoDelete(passenger: PassengerLayout) {
        CustomSnackBar.make(activity?.window?.decorView?.rootView as ViewGroup, passenger.name)
            .setOnClickListener {
                viewModel.undoDelete(passenger)
                it.dismiss()
            }
            .setAnchorView(binding.snackbarAnchor)
            .setDuration(3000)
            .show()
    }
}