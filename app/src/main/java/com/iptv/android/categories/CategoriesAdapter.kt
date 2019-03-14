package com.iptv.android.categories

/**
 * Developed by tcbaras on 25.02.2019.
 */

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.iptv.android.R
import com.iptv.android.m3u.M3UPlaylist
import kotlinx.android.synthetic.main.item_category.view.*
import kotlin.properties.Delegates

class CategoriesAdapter : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>(), Filterable {

    internal var data: List<M3UPlaylist> by Delegates.observable(emptyList()) { _, _, _ ->
        filteredData.clear()
        filteredData.addAll(data)
        notifyDataSetChanged()
    }

    private var filteredData: MutableList<M3UPlaylist> = mutableListOf()

    internal var listener: CategorySelectListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false))

    override fun getItemCount() = filteredData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(filteredData[position], listener)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.iv_channel
        val name: TextView = itemView.item_name
        val totalCount: TextView = itemView.tv_total_count

        fun bind(model: M3UPlaylist, listener: CategorySelectListener?) {
            name.text = model.playlistName

//            var char = "A"
//            model.playlistName?.let {
//                if (it.isNotEmpty()) {
//                    char = it[0].toString()
//                }
//            }
//            val textDrawable = TextDrawable.builder()
//                .buildRoundRect((adapterPosition + 1).toString(), ColorGenerator.MATERIAL.randomColor, 100)
//            icon.setImageDrawable(textDrawable)
            model.playlistItems?.let { totalCount.text = "Toplam Yayın: ${model.playlistItems.size}" }

            itemView.setOnClickListener { listener?.let { listener.onCategorySelected(model) } }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence?): FilterResults {
                if (query.isNullOrEmpty()) {
                    filteredData.clear()
                    filteredData.addAll(data)
                } else {
                    filteredData.clear()
                    for (category in data) {
                        category.playlistName?.let {
                            if (it.contains(query, ignoreCase = true)) {
                                filteredData.add(category)
                            }
                        }
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredData
                return filterResults
            }

            override fun publishResults(query: CharSequence?, filterResults: FilterResults?) {
                try {
                    filteredData = filterResults?.values as MutableList<M3UPlaylist>
                    notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    interface CategorySelectListener {
        fun onCategorySelected(category: M3UPlaylist)
    }
}
