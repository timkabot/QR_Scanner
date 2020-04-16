package com.app.qrscanner.presentation.global.list

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.qrscanner.R
import com.app.qrscanner.domain.entities.Code
import com.app.qrscanner.domain.interactors.CodeTypeInteractor
import com.app.qrscanner.presentation.history.scannedCodesHistory.onCodeClickListener
import com.app.qrscanner.utils.inflate

class CodesAdapter(val codes: ArrayList<Code>, val onCodeClickListener: onCodeClickListener) : RecyclerView.Adapter<CodesAdapter.CodesHolder>(){
    val codeTypeInteractor = CodeTypeInteractor()

    inner class CodesHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(code: Code){
            val mainString = itemView.findViewById<TextView>(R.id.codeTextView)
            val codeType = itemView.findViewById<TextView>(R.id.codeTypeTextView)

            val cardImage = itemView.findViewById<ImageView>(R.id.cardImage)
            cardImage.setImageResource(codeTypeInteractor.getImageForCodeType(code.type))
            mainString.text = code.shortDescription
            codeType.text = codeTypeInteractor.getNameForCodeType(code.type, cardImage.context)

            if(mainString.text.isEmpty())
                mainString.text = code.data
            itemView.setOnClickListener {
                onCodeClickListener.onClick(code)
            }
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
