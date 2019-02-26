package com.iptv.android.list

/**
 * Developed by tcbaras on 25.02.2019.
 */

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.iptv.android.R
import com.muparse.M3UItem
import kotlinx.android.synthetic.main.item_playlist.view.*
import kotlin.properties.Delegates

class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    internal var data: List<M3UItem> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    internal var listener: PlayItemSelectListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position], listener)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.iv_channel
        val name: TextView = itemView.item_name

        fun bind(model: M3UItem, listener: PlayItemSelectListener?) {
            name.text = model.itemName

            var char = "A"
            model.itemName?.let { char = it[0].toString() }
            val textDrawable = TextDrawable.builder()
                .buildRoundRect(char, ColorGenerator.MATERIAL.randomColor, 100)
            icon.setImageDrawable(textDrawable)

            itemView.setOnClickListener { listener?.let { listener.onPlayItemSelected(model) } }
        }
    }

    interface PlayItemSelectListener {
        fun onPlayItemSelected(m3UItem: M3UItem)
    }
}
