package com.app.qrscanner.presentation.global.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.qrscanner.R
import com.app.qrscanner.domain.entities.CodeType
import com.app.qrscanner.domain.entities.CodeType.*
import com.app.qrscanner.utils.getImageForCodeType
import com.app.qrscanner.utils.getNameForCodeType
import com.app.qrscanner.utils.inflate
import kotlinx.android.synthetic.main.card_codetype.view.*

class CodeTypesAdapter(private val codeTypes: List<CodeType>) :
    RecyclerView.Adapter<CodeTypesAdapter.Holder>() {

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(codeType: CodeType) {
            itemView.run {
                codeType.also {
                    codeTypeImage.setImageResource(getImageForCodeType(it))
                    codeTypeName.text = getNameForCodeType(it)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(parent.inflate(R.layout.card_codetype, false))
    }

    override fun getItemCount() = codeTypes.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(codeTypes[position])
    }
}