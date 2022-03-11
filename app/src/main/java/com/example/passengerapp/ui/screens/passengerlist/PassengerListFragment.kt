package com.example.passengerapp.ui.screens.passengerlist

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passengerapp.R
import com.example.passengerapp.databinding.FragmentPassengerListBinding
import com.example.passengerapp.model.Passenger
import com.example.youngchemist.ui.custom.snack_bar.CustomSnackBar
import com.example.youngchemist.ui.custom.snack_bar.CustomSnackBar.Companion.setOnClickListener
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PassengerListFragment : Fragment() {

    private lateinit var binding: FragmentPassengerListBinding
    private lateinit var passengersAdapter: PassengerAdapter
    private lateinit var bitmap: Bitmap
    val viewModel: PassengerListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPassengerListBinding.inflate(inflater, container, false)
        setUpRecyclerView()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_icon_trash)
        setUpNavigation()
        collectAdapterData()
        enableSwipe()
    }

    private fun collectAdapterData() {
        lifecycleScope.launch {
            viewModel.pagingData.collectLatest {
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

    private fun enableSwipe() {
        val simpleItemTouchHelper =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.bindingAdapterPosition
                    if (direction == ItemTouchHelper.LEFT) {
                        passengersAdapter.getPassenger(position)?.let {
                            undoDelete(it)
                            viewModel.deletePassengerById(it.id)
                        }
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    val p = Paint()
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        val itemView = viewHolder.itemView
                        val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                        val width = height / 3
                        if (dX < 0) {
                            val iconDest = RectF(
                                itemView.right.toFloat() - 2 * width,
                                itemView.top.toFloat() + width,
                                itemView.right.toFloat() - width,
                                itemView.bottom.toFloat() - width
                            )
                            val alpha = ((-dX / itemView.width.toFloat()) * 255).toInt()
                            p.alpha = alpha
                            c.drawBitmap(bitmap, null, iconDest, p)
                        }
                    }
                }
            }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchHelper)
        itemTouchHelper.attachToRecyclerView(binding.rvPassengers)
    }

    private fun setUpNavigation() {
        binding.ivAddPassenger.setOnClickListener {
            findNavController().navigate(R.id.action_passengerListFragment_to_passengerCreatingFragment)
        }
    }

    private fun setUpRecyclerView() {
        passengersAdapter = PassengerAdapter()
        binding.rvPassengers.run {
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
            val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            adapter = passengersAdapter.withLoadStateHeaderAndFooter(
                header = PassengerLoadStateAdapter { passengersAdapter.retry() },
                footer = PassengerLoadStateAdapter { passengersAdapter.retry() }
            )
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        }

        passengersAdapter.setOnAirlineDetailsClickListener { view, passenger ->
            val airlineGson = Gson().toJson(passenger)
            val toAirlineDetailsFragment =
                PassengerListFragmentDirections.actionPassengerListFragmentToAirlineDetailsFragment(
                    airlineGson
                )
            val extraInfoSharedElement = FragmentNavigatorExtras(view to passenger.id)
            navigate(toAirlineDetailsFragment, extraInfoSharedElement)
        }

        binding.swipeToRefresh.setOnRefreshListener {
            passengersAdapter.refresh()
            binding.swipeToRefresh.isRefreshing = true
        }
    }

    private fun undoDelete(passenger: Passenger) {
        CustomSnackBar.make(activity?.window?.decorView?.rootView as ViewGroup, passenger.name)
            .setOnClickListener {
                viewModel.undoDelete(passenger)
                it.dismiss()
            }
            .setAnchorView(binding.snackbarAnchor)
            .setDuration(3000)
            .show()
    }

    private fun navigate(destination: NavDirections, extraInfo: FragmentNavigator.Extras) =
        with(findNavController()) {
            Log.d("TAG", currentDestination.toString())
            currentDestination?.getAction(destination.actionId)
                ?.let { navigate(destination, extraInfo) }
        }
}