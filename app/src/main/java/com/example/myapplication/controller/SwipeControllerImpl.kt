package com.example.myapplication.controller

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.utils.ITEM_BUTTON_PADDING
import com.example.myapplication.utils.ITEM_PROJECT_CORNER

internal enum class ButtonsState {
    GONE,
    RIGHT_VISIBLE
}

class SwipeControllerImpl : ItemTouchHelper.Callback() {
    private var context: Context? = null

    private var swipeBack = false

    private var buttonShowedState = ButtonsState.GONE

    private var editButtonInstance: RectF? = null

    private var deleteButtonInstance: RectF? = null

    private var currentItemViewHolder: RecyclerView.ViewHolder? = null

    private var buttonsActions: SwipeController? = null

    private val leftActionWidth = 600F

    fun setController(swipeController: SwipeController, context: Context) {
        this.buttonsActions = swipeController
        this.context = context
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, LEFT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = buttonShowedState != ButtonsState.GONE
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
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
        var dXTempt = dX
        if (actionState == ACTION_STATE_SWIPE) {
            if (buttonShowedState != ButtonsState.GONE) {
                if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) dXTempt =
                    dXTempt.coerceAtMost(-leftActionWidth)
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dXTempt,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            } else {
                setTouchListener(
                    c,
                    recyclerView,
                    viewHolder,
                    dXTempt,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        if (buttonShowedState == ButtonsState.GONE) {
            super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                dXTempt,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
        currentItemViewHolder = viewHolder
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack =
                event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack) {
                if (dX < -leftActionWidth)
                    buttonShowedState = ButtonsState.RIGHT_VISIBLE

                if (buttonShowedState != ButtonsState.GONE) {
                    setTouchDownListener(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    setItemsClickable(recyclerView, false)
                }
            }
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchDownListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchUpListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                super@SwipeControllerImpl.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    0f,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                recyclerView.setOnTouchListener { _, _ -> false }
                setItemsClickable(recyclerView, true)
                swipeBack = false

                if (buttonsActions != null && editButtonInstance != null && editButtonInstance?.contains(
                        event.x,
                        event.y
                    ) == true
                ) {
                    if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                        buttonsActions?.actionEdit(viewHolder.adapterPosition)
                    }
                } else if (buttonsActions != null && deleteButtonInstance != null && deleteButtonInstance?.contains(
                        event.x,
                        event.y
                    ) == true
                ) {
                    if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                        buttonsActions?.actionDelete(viewHolder.adapterPosition)
                    }
                }
                buttonShowedState = ButtonsState.GONE
                currentItemViewHolder = null
            }
            false
        }
    }

    private fun setItemsClickable(recyclerView: RecyclerView, isClickable: Boolean) {
        for (i in 0 until recyclerView.childCount) {
            recyclerView.getChildAt(i).isClickable = isClickable
        }
    }

    private fun drawButtons(canvas: Canvas, viewHolder: RecyclerView.ViewHolder) {
        val itemView = viewHolder.itemView
        val paint = Paint()

        //Get button drawable resource
        val icEdit = context?.resources?.getDrawable(R.drawable.ic_edit)
        val icDelete = context?.resources?.getDrawable(R.drawable.ic_delete)

        // Get screen destiny (1 ~~ 160dpi)
        val scale = context?.resources?.displayMetrics?.density ?: 1F

        //Define corner radius
        val corners = ITEM_PROJECT_CORNER * scale
        val padding = ITEM_BUTTON_PADDING * scale

        //Right edit button
        val editButton = RectF(
            itemView.right - leftActionWidth - corners,
            itemView.top.toFloat(),
            itemView.right - leftActionWidth / 2,
            itemView.bottom.toFloat()
        )
        paint.color = Color.LTGRAY
        canvas.drawRoundRect(editButton, 0f, 0f, paint)

        //Right delete button
        val deleteButton = RectF(
            itemView.right - leftActionWidth / 2 - corners,
            itemView.top.toFloat(),
            itemView.right.toFloat(),
            itemView.bottom.toFloat()
        )
        paint.color = Color.LTGRAY
        canvas.drawRoundRect(deleteButton, corners, corners, paint)

        //Set bound
        icEdit?.setBounds(
            (itemView.right - leftActionWidth - corners + padding).toInt(),
            (itemView.top + padding).toInt(),
            (itemView.right - leftActionWidth / 2 - padding).toInt(),
            (itemView.bottom - padding).toInt()
        )
        icEdit?.draw(canvas)

        icDelete?.setBounds(
            (itemView.right - leftActionWidth / 2 - corners + padding).toInt(),
            (itemView.top + padding).toInt(),
            (itemView.right - padding).toInt(),
            (itemView.bottom - padding).toInt()
        )
        icDelete?.draw(canvas)

        //Button instance
        editButtonInstance = null
        deleteButtonInstance = null
        if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
            editButtonInstance = editButton
            deleteButtonInstance = deleteButton
        }
    }

    fun onDraw(c: Canvas) {
        if (currentItemViewHolder != null) {
            drawButtons(c, currentItemViewHolder ?: return)
        }
    }
}
