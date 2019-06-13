package com.iptv.android.player.channellist

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iptv.android.R
import com.iptv.android.categories.CategoriesActivity
import com.iptv.android.player.channellist.dummy.DummyContent.DummyItem
import com.muparse.M3UItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_channel_list.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [PlayerChannelListFragment.OnPlayerChannelListFragmentInteractionListener] interface.
 */
class PlayerChannelListFragment : Fragment() {

    // TODO: Customize parameters
    private var selectedCategoryIndex = 0
    private var selectedChannelIndex = 0
    private var columnCount = 1

    private lateinit var rvList: RecyclerView
    private var listener: OnPlayerChannelListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            selectedCategoryIndex = it.getInt(ARG_CATEGORY_INDEX)
            selectedChannelIndex = it.getInt(ARG_CHANNEL_INDEX)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_channel_list, container, false)

        // Set the adapter
        rvList = view.findViewById<RecyclerView>(R.id.list)
        with(rvList) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }

            var channelList: MutableList<DummyItem> = mutableListOf()
            CategoriesActivity.categories[0].playlistItems?.map {
                channelList.add(DummyItem(it.itemName!!, "", ""))
            }
            adapter = PlayerChannelRecyclerViewAdapter(
                CategoriesActivity.categories,
                selectedCategoryIndex,
                selectedChannelIndex,
                listener
            )

        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tvPlayerCategoryName.text = CategoriesActivity.categories[selectedCategoryIndex].playlistName

        ivPlayerNextCategory.setOnClickListener {
            if (selectedCategoryIndex + 1 < CategoriesActivity.categories.size) {
                selectedCategoryIndex++
                val selectedCategory = CategoriesActivity.categories[selectedCategoryIndex]
                tvPlayerCategoryName.text = selectedCategory.playlistName
                (rvList.adapter as PlayerChannelRecyclerViewAdapter).setCategoryIndex(selectedCategoryIndex)
            }
        }
        ivPlayerPreviousCategory.setOnClickListener {
            if (selectedCategoryIndex > 0) {
                selectedCategoryIndex--
                val selectedCategory = CategoriesActivity.categories[selectedCategoryIndex]
                tvPlayerCategoryName.text = selectedCategory.playlistName
                (rvList.adapter as PlayerChannelRecyclerViewAdapter).setCategoryIndex(selectedCategoryIndex)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPlayerChannelListFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnPlayerChannelListFragmentInteractionListener {
        fun onChannelSelected(item: M3UItem?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_CATEGORY_INDEX = "category-index"
        const val ARG_CHANNEL_INDEX = "channel-index"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(selectedCategoryIndex: Int, selectedChannelIndex: Int) =
            PlayerChannelListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_CATEGORY_INDEX, selectedCategoryIndex)
                    putInt(ARG_CHANNEL_INDEX, selectedChannelIndex)
                }
            }
    }
}
