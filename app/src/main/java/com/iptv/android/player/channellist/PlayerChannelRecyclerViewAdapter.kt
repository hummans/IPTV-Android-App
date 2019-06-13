package com.iptv.android.player.channellist


import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.iptv.android.R
import com.iptv.android.m3u.M3UPlaylist
import com.iptv.android.player.channellist.PlayerChannelListFragment.OnPlayerChannelListFragmentInteractionListener
import kotlinx.android.synthetic.main.item_player_channel.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnPlayerChannelListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class PlayerChannelRecyclerViewAdapter(
//    private val mValues: List<DummyItem>,
    private val mCategories: List<M3UPlaylist>,
    var selectedCategoryIndex: Int = -1,
    var selectedChannelIndex: Int = -1,
    private val mListener: OnPlayerChannelListFragmentInteractionListener?
) : RecyclerView.Adapter<PlayerChannelRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private var visibleCategoryIndex = 0

    private val selectedColor by lazy { Color.parseColor("#66C88F45") }

    init {
        mOnClickListener = View.OnClickListener { v ->
            notifyItemChanged(selectedChannelIndex)
            selectedChannelIndex = v.tag as Int
            selectedCategoryIndex = visibleCategoryIndex
            val item = mCategories[visibleCategoryIndex].playlistItems[selectedChannelIndex]
            v.setBackgroundColor(selectedColor)

            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onChannelSelected(item)
        }

        visibleCategoryIndex = selectedCategoryIndex
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_player_channel, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mCategories[visibleCategoryIndex].playlistItems[position]
        holder.mIdView.text = ""
        holder.mContentView.text = item.itemName

        if (visibleCategoryIndex == selectedCategoryIndex && selectedChannelIndex == position) {
            holder.itemView.setBackgroundColor(selectedColor)
        }else{
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        with(holder.mView) {
            tag = position
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mCategories[visibleCategoryIndex].playlistItems.size

    fun setCategoryIndex(newCategoryIndex: Int) {
        visibleCategoryIndex = newCategoryIndex
        notifyDataSetChanged()
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
