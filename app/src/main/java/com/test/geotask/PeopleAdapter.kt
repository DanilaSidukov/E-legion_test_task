package com.test.geotask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.geotask.Settings.Companion.previousPosition
import java.util.Collections

class PeopleAdapter(private var list: List<PeopleItem>) :
    RecyclerView.Adapter<PeopleAdapter.ViewHolder>(), PersonClickListener {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PeopleAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.people_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeopleAdapter.ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            onItemClickListener(list[position], position)
        }

        val person = list[position]
        holder.avatar.setImageResource(person.avatar)
        holder.name.text = person.name
        holder.distance.text = person.distance.toString()
    }

    override fun getItemCount() = list.size

    class ViewHolder(item: View): RecyclerView.ViewHolder(item){
        val avatar: ImageView = item.findViewById(R.id.person_avatar)
        val name: TextView = item.findViewById(R.id.person_name)
        val distance: TextView = item.findViewById(R.id.person_distance_data)
    }

    fun updateList(newList: List<PeopleItem>){
        println("update here")
        list = newList
    }

    override fun onItemClickListener(person: PeopleItem, position: Int) {
        if (position != 0 && list[0].id == 0){
            Collections.swap(list, 0, position)
            previousPosition = position
            person.distance = "0 m"
        } else if (position == 0){
            Collections.swap(list, previousPosition, 0)
        }
        updateList(list)
    }
}

interface PersonClickListener{
    fun onItemClickListener(person: PeopleItem, position: Int)
}