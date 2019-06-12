package com.example.thesis

import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private val titles = arrayOf("Cigarettes you smoked today",
        "Glass of water you drunk today",
        "Steps that you walked today so far",
        "Meals that you skipped today so far",
        "Pages of a book you read today so far",
        "Songs that you listed to day so far",
        "Beers you had today so far",
        "Shots that you do today so far")

    private val details = arrayOf("15",
        "5",
        "0",
        "0",
        "100",
        "25",
        "0",
        "0")

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_view_layout, viewGroup, false)
        return ViewHolder(v)
    }
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.itemTitle.text = titles[i]
        viewHolder.itemDetail.text = details[i]
    }

    override fun getItemCount(): Int {
        return titles.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemImage: ImageView
        var itemTitle: TextView
        var itemDetail: TextView

        init {
            itemImage = itemView.findViewById(R.id.imageView2)
            itemTitle = itemView.findViewById(R.id.item_title)
            itemDetail = itemView.findViewById(R.id.item_detail)

            itemView.setOnClickListener { v: View  ->
                var position: Int = getAdapterPosition()

                val temp = details[position]
                if (position != 2) {
                    details[position] = (temp.toInt() + 1).toString()
                    Snackbar.make(
                        v, "One more point added to your stats",
                        Snackbar.LENGTH_LONG
                    ).setAction("Action", null).show()

                }
                else{
                    Snackbar.make(v, "You have to walk for this stat",
                        Snackbar.LENGTH_LONG).setAction("Action", null).show()
                }
                notifyDataSetChanged()

            }
        }
    }
}