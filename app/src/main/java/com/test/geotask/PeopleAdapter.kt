package com.test.geotask

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shuhart.stickyheader.StickyAdapter
import java.util.Collections

class PeopleAdapter(
    private var list: List<Person>,
    private val personClickListener: PersonClickListener
) : RecyclerView.Adapter<PeopleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PeopleAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.people_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val person = list[position]

        holder.itemView.setOnClickListener {
            personClickListener.onItemClickListener(person)
        }

        holder.avatar.setImageResource(person.avatar)
        holder.name.text = person.name
        holder.distance.text = person.distance
    }

    override fun getItemCount() = list.size

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val avatar: ImageView = item.findViewById(R.id.person_avatar)
        val name: TextView = item.findViewById(R.id.person_name)
        val distance: TextView = item.findViewById(R.id.person_distance_data)
    }

    fun updateList(newList: List<Person>) {
        list = newList
        notifyDataSetChanged()
    }

}

interface PersonClickListener {
    fun onItemClickListener(person: Person)

}