package com.example.contacts.screens.contactlist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.R
import com.example.contacts.adapters.ContactAdapter
import com.example.contacts.data.AppDatabase
import com.example.contacts.screens.addcontact.AddContactActivity
import kotlinx.android.synthetic.main.activity_main.*

class ContactListActivity : AppCompatActivity() {

    lateinit var db: AppDatabase
    lateinit var adapter: ContactAdapter
    lateinit var viewmodel: ContactMainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = AppDatabase.getInstance(this)

        viewmodel = ViewModelProvider.AndroidViewModelFactory(application).create(
            ContactMainViewModel::class.java)

        adapter = ContactAdapter()
        rvContacts.adapter = adapter
        adapter.onContactClickListener = object : ContactAdapter.OnContactClickListener{
            override fun onContactClick(position: Int) {
                val intent = Intent(this@ContactListActivity, AddContactActivity::class.java)
                val contact = adapter.contacts[position]
                Log.d("SENDING_CONTACT_ID", contact.id.toString())
                intent.putExtra("id", contact.id?.toLong())
                startActivity(intent)
            }
        }

        val itemTouchHelper =
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    removeContact(viewHolder.adapterPosition)
                }

                private fun removeContact(position: Int) {
                    val contact = adapter.contacts[position]
                    viewmodel.deleteContact(contact)
                }
            })

        itemTouchHelper.attachToRecyclerView(rvContacts)

        viewmodel.contactList.observe(this, Observer {
            adapter.contacts = it
        })

    }

    fun onClickAddContact(view: View) {
        val intent = Intent(this, AddContactActivity::class.java)
        startActivity(intent)
    }
}
