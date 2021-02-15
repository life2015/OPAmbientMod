package com.retrox.aodmod.app.settings.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.retrox.aodmod.R
import com.retrox.aodmod.app.settings.models.App
import com.retrox.aodmod.app.util.AppIconRequestHandler
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_app.view.*

class AppsAdapter(context: Context, var apps : List<App>, private val enabledApps: MutableList<String>, private val onAppSelected : (packageName: String) -> Unit) : RecyclerView.Adapter<AppsAdapter.ViewHolder>() {

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            layoutInflater.inflate(R.layout.item_app, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.itemView.title.text = app.appName
        val uri = Uri.parse("${AppIconRequestHandler.SCHEME_PNAME}:${app.packageName}")
        Picasso.get().load(uri).into(holder.itemView.icon)
        holder.itemView.check.isChecked = enabledApps.contains(app.packageName)
        holder.itemView.setOnClickListener {
            onSelected(holder.adapterPosition, app)
        }
        holder.itemView.check.setOnClickListener {
            onSelected(holder.adapterPosition, app)
        }
    }

    private fun onSelected(position: Int, app: App){
        onAppSelected.invoke(app.packageName)
        notifyItemChanged(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}