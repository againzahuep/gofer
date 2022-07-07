package com.couplace.gofer.decorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class FixedMarginItemDecorator(private val margin: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.apply {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = margin
            }
            left = margin
            bottom = margin
            right = margin
        }
    }
}