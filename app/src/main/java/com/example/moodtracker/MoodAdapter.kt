package com.example.moodtracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class MoodAdapter(private val moods: List<UserMood>) :
    RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    class MoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUser: TextView = view.findViewById(R.id.tvUser)
        val tvMood: TextView = view.findViewById(R.id.tvMood)
        val tvMainNote: TextView = view.findViewById(R.id.tvMainNotes)
        val tvAdditionalNotes: TextView = view.findViewById(R.id.tvAdditionalNote)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
        val tvTimestamp: TextView = view.findViewById(R.id.tvTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood = moods[position]
        holder.tvUser.text = "User: ${mood.userId}"
        holder.tvMood.text = "Mood: ${mood.selectedMood}"
        holder.tvMainNote.text = "Main Note: ${mood.mainNote}"
        holder.tvAdditionalNotes.text = "Additional Notes: ${mood.additionalNotes ?: "-"}"
        holder.tvDetails.text = "Location: ${mood.location}, Weather: ${mood.weather}"

        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        holder.tvTimestamp.text = "Logged: ${sdf.format(Date(mood.timestamp))}"
    }

    override fun getItemCount(): Int = moods.size
}
