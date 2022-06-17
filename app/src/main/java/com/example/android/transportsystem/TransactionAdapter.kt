package com.example.android.transportsystem

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter (private val transactionList : ArrayList<Transaction>) : RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TransactionAdapter.TransactionHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transaction_card, parent, false)

        return TransactionHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TransactionAdapter.TransactionHolder, position: Int) {
        val transaction: Transaction = transactionList[position]
        val currentDate = transaction.date.toString().toCharArray()
        holder.date.text = "${currentDate[0]}${currentDate[1]}${currentDate[2]}${currentDate[3]}-" +
                "${currentDate[4]}${currentDate[5]}-${currentDate[6]}${currentDate[7]}                          " +
                "${currentDate[8]}${currentDate[9]}:${currentDate[10]}${currentDate[11]}:${currentDate[12]}${currentDate[13]}"
        holder.money.text = transaction.money.toString() + " zł"
        holder.id.text = transaction.id
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    public class TransactionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date = itemView.findViewById<TextView>(R.id.transactioncard_date)
        val money = itemView.findViewById<TextView>(R.id.transactioncard_money)
        val id = itemView.findViewById<TextView>(R.id.transactioncard_id)
    }
}