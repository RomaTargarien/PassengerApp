package com.example.passengerapp.ui.util.extensions

import android.graphics.Canvas
import android.graphics.RectF
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener


fun Fragment.setUpKeyBoardEventListener(action: (Boolean) -> Unit) {
    KeyboardVisibilityEvent.setEventListener(requireActivity(),
        object : KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                action(isOpen)
            }
        })
}

fun Fragment.snackbar(message: String) {
    Snackbar.make(this.requireView(), message, Snackbar.LENGTH_LONG).show()
}

fun Fragment.createItemTouchHelper(
    swipeDirection: Int,
    onSwipedAction: (Int) -> Unit,
    onChildDrawAction: (c: Canvas, viewHolder: RecyclerView.ViewHolder, dX: Float, bitmapRect: RectF) -> Unit
): ItemTouchHelper.SimpleCallback {

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
                if (direction == swipeDirection) {
                    onSwipedAction.invoke(position)
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
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    if (dX < 0) {
                        val itemView = viewHolder.itemView
                        val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                        val width = height / 3
                        val iconDest = RectF(
                            itemView.right.toFloat() - 2 * width,
                            itemView.top.toFloat() + width,
                            itemView.right.toFloat() - width,
                            itemView.bottom.toFloat() - width
                        )
                        onChildDrawAction(c, viewHolder, dX,iconDest)
                    }
                }
            }
        }

    return simpleItemTouchHelper
}
