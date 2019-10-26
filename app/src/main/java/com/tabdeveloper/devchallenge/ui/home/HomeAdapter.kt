package com.tabdeveloper.devchallenge.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tabdeveloper.devchallenge.R
import com.tabdeveloper.devchallenge.data.model.VideoListModel
import com.tabdeveloper.devchallenge.data.model.VideoModel
import kotlinx.android.synthetic.main.home_itemview.view.*

class HomeAdapter(val context: Context, val videoListModel: VideoListModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.home_itemview, parent, false)
        return VideoListViewHolder(v)
    }

    override fun getItemCount(): Int {
        return videoListModel.objects.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as VideoListViewHolder).bind(videoListModel.objects[position])
    }

}

class VideoListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(videoModel: VideoModel) {
        itemView.home_itemview_title.text = videoModel.name
//        itemView.itemview_card_icon.setImageResource(card.deck.getIcon())
//        itemView.itemview_card_icon.setColorFilter(ContextCompat.getColor(itemView.context, card.deck.getColor()))
//        itemView.itemview_card_title.text = card.title
//        itemView.itemview_card_deck.text = card.deck.getName(itemView.context)
//        itemView.itemview_card_effect.text = itemView.context.getString(R.string.cardlist_effect, card.getSubtypeText(itemView.context), card.box.getName(itemView.context))
//        itemView.setOnClickListener {
//            Crashlytics.log("CardListFragment - card - open - ${card.id} ${card.title}")
//            ContextCompat.startActivity(itemView.context, CardActivity.newIntent(itemView.context, card), null)
//        }
    }
}