package com.example.contacts.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.example.contacts.R
import com.example.contacts.pojo.Contact
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_contact.view.*

class ContactAdapter : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    var contacts: List<Contact> = listOf()
    set(value){
        field = value
        notifyDataSetChanged()
    }

    var onContactClickListener: OnContactClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun getItemCount() = contacts.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        with(holder){
            itemView.setOnClickListener { onContactClickListener?.onContactClick(position) }
            val contact = contacts[position]

            ivPhoto.load(contact.imgUri){
                placeholder(R.drawable.avatar)
            }

            tvFirstName.text = contact.firstName
            tvLastName.text = contact.lastName
            tvPersonalPhone.text = contact.personalPhone

            if(contact.isEmployee == true){
                line.visibility = View.VISIBLE
                labelPosition.visibility = View.VISIBLE
                labelWorkPhone.visibility = View.VISIBLE

                tvPosition.visibility = View.VISIBLE
                tvWorkPhone.visibility = View.VISIBLE

                tvPosition.text = contact.position
                tvWorkPhone.text = contact.workPhone
            }
        }

    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val ivPhoto = itemView.ivPhoto
        val tvFirstName = itemView.tvFirstName
        val tvLastName = itemView.tvLastName
        val tvPersonalPhone = itemView.tvPersonalPhone
        val tvPosition = itemView.tvPosition
        val tvWorkPhone = itemView.tvWorkPhone
        val line = itemView.viewLine

        val labelPosition = itemView.tvLabelPosition
        val labelWorkPhone = itemView.tvLabelWorkPhone
    }

    interface OnContactClickListener{
        fun onContactClick(position: Int)
    }
}