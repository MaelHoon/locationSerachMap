package com.example.locationsearchmap

import android.view.View
import android.view.viewG
import androidx.recyclerview.widget.RecyclerView

class SearchRecyclerAdapter : RecyclerView.Adapter<> {
    class SearchResultItemViewHolder(itemView : View, val searchResultClickListener : (Any) -> Unit) : RecyclerView.ViewHolder(itemView){
        fun bindData(data:Any) = with(itemView){

        }
    }

}