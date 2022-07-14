package com.couplace.gofer.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.couplace.gofer.R
import com.couplace.gofer.model.Product
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class ProductListAdapter( private val interaction: Interaction) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var viewLoading: View
    private lateinit var viewItem: View
    private var retryPageLoad = false
    // View Types

    private val isLoadingAdded = false
    private var positionUpdateState = false
    interface Interaction {

        fun onItemSelected(position: Int, item: Product)

        fun restoreListPosition()

    }

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        viewItem = inflater.inflate(R.layout.items_recyclerview_adapter, parent, false);

        return ProductViewHolder(viewItem, interaction)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {

            is ProductViewHolder -> {
                holder.bind(differ.currentList.get(position))

            }

        }

    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun isItemEmpty(): Boolean = differ.currentList.isNullOrEmpty()

    fun isPositionUpdate(): Boolean {
        return this.positionUpdateState
    }

    fun setPositionUpdateState(value: Boolean) {
        this.positionUpdateState = value
    }

    fun addProduct(product: Product?) {
        differ.currentList.add(product)
        notifyItemInserted(differ.currentList.size - 1)
    }

    fun submitList(list: List<Product>?) {
        val commitCallback = Runnable {
            // if process died must restore list position
            // very annoying
            interaction?.restoreListPosition()
        }
        differ.submitList(list, commitCallback)
        val diffResult = DiffUtil.calculateDiff(ProductDiffUtilCallback(this.differ.currentList, list!!.toMutableList()))
        this.differ.currentList.clear()
        this.differ.currentList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    fun getProduct(index: Int): Product? {
        return try{
            differ.currentList[index]
        }catch (e: IndexOutOfBoundsException){
            e.printStackTrace()
            null
        }
    }


    class ProductViewHolder(
        itemView: View,
        private val interaction: Interaction?,
    ) : RecyclerView.ViewHolder(itemView) {

        lateinit var imageView: ImageView

        lateinit var textView: TextView

        fun bind(product: Product) = with(itemView) {

            itemView.elevation = 10.0f
            itemView.translationZ = 10.0f

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .centerCrop()
                .error(R.drawable.ic_launcher_background)

            imageView = itemView.findViewById(R.id.image_product)

            imageView?.let {
                Glide.with(itemView.context)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(product.image)
                    .into(it)
            }

            textView = itemView.findViewById(R.id.title_product)
            textView?.text = product.name

            itemView.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION) {
                    Toast.makeText(
                        itemView.context,
                        itemView.context.getString(R.string.please_wait_while_adapter_changes),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                interaction?.onItemSelected(adapterPosition, product)

            }
        }

    }

    fun showRetry(show: Boolean)
    {
        retryPageLoad = show;
        notifyItemChanged(differ.currentList.size - 1);
    }


    class ProductDiffUtilCallback(private val oldList: MutableList<Product>, private val newList: MutableList<Product>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].name == newList[newItemPosition].name
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return when {
                oldList[oldItemPosition].name == newList[newItemPosition].name -> true
                else -> false
            }
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }

    }
}
