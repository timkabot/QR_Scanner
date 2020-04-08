package com.app.qrscanner.presentation.global.list

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.qrscanner.R
import com.app.qrscanner.domain.entities.Code
import com.app.qrscanner.utils.inflate

class CodesAdapter(val codes: ArrayList<Code>) : RecyclerView.Adapter<CodesAdapter.CodesHolder>(){

    inner class CodesHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(code: Code){
            val mainString = itemView.findViewById<TextView>(R.id.codeTextView)
            mainString.text = code.data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CodesHolder {
        return CodesHolder(parent.inflate(R.layout.card_code,false))
    }

    override fun getItemCount() = codes.size

    override fun onBindViewHolder(holder: CodesHolder, position: Int) {
        holder.bind(codes[position])
    }
}