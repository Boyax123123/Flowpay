package com.mine.flowpay.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mine.flowpay.ProductsListActivity
import com.mine.flowpay.R
import com.mine.flowpay.app.FlowpayApp
import com.mine.flowpay.data.ProductCategory

class GameCategoryAdapter(private val categories: List<ProductCategory>) : 
    RecyclerView.Adapter<GameCategoryAdapter.GameCategoryViewHolder>() {

    class GameCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.iv_game_image)
        val titleView: TextView = view.findViewById(R.id.tv_game_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game_category, parent, false)
        return GameCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameCategoryViewHolder, position: Int) {
        val category = categories[position]

        try {
            // Log the image resource ID for debugging
            android.util.Log.d("GameCategoryAdapter", "Setting image for ${category.category_name}, image resource ID: ${category.image}")

            // Try to get the resource name for debugging
            try {
                val resourceName = holder.itemView.context.resources.getResourceName(category.image)
                android.util.Log.d("GameCategoryAdapter", "Resource name: $resourceName")
            } catch (e: Exception) {
                android.util.Log.e("GameCategoryAdapter", "Could not get resource name for ID: ${category.image}", e)
            }

            // Make sure the ImageView has proper dimensions
            holder.imageView.layoutParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT
            holder.imageView.layoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT

            // Set scaleType to ensure the image is displayed properly
            holder.imageView.scaleType = android.widget.ImageView.ScaleType.FIT_CENTER

            // Log the ImageView state before setting the image
            android.util.Log.d("GameCategoryAdapter", "ImageView before setting image - Width: ${holder.imageView.width}, Height: ${holder.imageView.height}, Visibility: ${holder.imageView.visibility}")

            // Clear any previous image
            holder.imageView.setImageDrawable(null)

            // Use Glide or similar library if available, otherwise use direct resource loading
            try {
                // Set the image resource
                holder.imageView.setImageResource(category.image)
                android.util.Log.d("GameCategoryAdapter", "Image set successfully using setImageResource")
            } catch (e: Exception) {
                android.util.Log.e("GameCategoryAdapter", "Error setting image with setImageResource: ${e.message}", e)
                // Try alternative method
                try {
                    val drawable = holder.itemView.context.resources.getDrawable(category.image, holder.itemView.context.theme)
                    holder.imageView.setImageDrawable(drawable)
                    android.util.Log.d("GameCategoryAdapter", "Image set successfully using getDrawable")
                } catch (e2: Exception) {
                    android.util.Log.e("GameCategoryAdapter", "Error setting image with getDrawable: ${e2.message}", e2)
                }
            }

            // Make sure the image is visible
            holder.imageView.visibility = android.view.View.VISIBLE

            // Force layout update
            holder.imageView.requestLayout()

            // Add a post-layout listener to ensure the image is properly sized and visible
            holder.imageView.post {
                android.util.Log.d("GameCategoryAdapter", "ImageView post-layout - Width: ${holder.imageView.width}, Height: ${holder.imageView.height}, Visibility: ${holder.imageView.visibility}")

                // If the ImageView has no dimensions, try to set them explicitly
                if (holder.imageView.width <= 0 || holder.imageView.height <= 0) {
                    android.util.Log.d("GameCategoryAdapter", "ImageView has no dimensions, setting explicit dimensions")
                    val layoutParams = holder.imageView.layoutParams
                    layoutParams.width = holder.itemView.width
                    layoutParams.height = holder.itemView.width // Make it square
                    holder.imageView.layoutParams = layoutParams
                    holder.imageView.requestLayout()
                }
            }

            // Log the ImageView state after setting the image
            android.util.Log.d("GameCategoryAdapter", "ImageView after setting image - Width: ${holder.imageView.width}, Height: ${holder.imageView.height}, Visibility: ${holder.imageView.visibility}")
        } catch (e: Exception) {
            // Log the error
            android.util.Log.e("GameCategoryAdapter", "Error setting image for ${category.category_name}: ${e.message}", e)
            // If the resource is not found, set a default image
            holder.imageView.setImageResource(R.drawable.img_notfound)
        }

        holder.titleView.text = category.category_name

        // Set click listener for the entire item
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            // Get user ID from application
            val userId = (context.applicationContext as FlowpayApp).loggedInuser?.user_id ?: -1

            val intent = Intent(context, ProductsListActivity::class.java).apply {
                putExtra("CATEGORY_ID", category.category_id)
                putExtra("CATEGORY_NAME", category.category_name)
                putExtra("USER_ID", userId)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = categories.size
}
