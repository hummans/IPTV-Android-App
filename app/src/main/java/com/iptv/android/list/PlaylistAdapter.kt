package com.iptv.android.list

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
import com.bumptech.glide.Glide
import com.iptv.android.R
import com.muparse.M3UItem
import kotlinx.android.synthetic.main.item_playlist.view.*
import java.util.*
import kotlin.properties.Delegates

class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>(), Filterable {

    internal var data: List<M3UItem> by Delegates.observable(emptyList()) { _, _, _ ->
        filteredData.clear()
        filteredData.addAll(data)
        notifyDataSetChanged()
    }

    private var filteredData: MutableList<M3UItem> = mutableListOf()

    internal var listener: PlayItemSelectListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_playlist3, parent, false))

    override fun getItemCount() = filteredData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(filteredData[position], listener)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.iv_channel
        val name: TextView = itemView.item_name

        fun bind(model: M3UItem, listener: PlayItemSelectListener?) {
            name.text = model.itemName
          //  icon.setImageResource(icons[adapterPosition % icons.size])
            Glide.with(icon.context).load(model.itemIcon).placeholder(R.drawable.placeholder).fitCenter().into(icon)

            itemView.setOnClickListener { listener?.let { listener.onPlayItemSelected(model) } }
        }

        private var icons =
            listOf(R.drawable.tv2, R.drawable.tv3, R.drawable.tv4, R.drawable.tv5, R.drawable.tv6, R.drawable.tv7)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence?): FilterResults {
                if (query.isNullOrEmpty()) {
                    filteredData.clear()
                    filteredData.addAll(data)
                } else {
                    filteredData.clear()
                    for (m3U in data) {
                        m3U.itemName?.let {
                            if (it.contains(query, ignoreCase = true)) {
                                filteredData.add(m3U)
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
                    filteredData = filterResults?.values as ArrayList<M3UItem>
                    notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    interface PlayItemSelectListener {
        fun onPlayItemSelected(m3UItem: M3UItem)
    }
}
