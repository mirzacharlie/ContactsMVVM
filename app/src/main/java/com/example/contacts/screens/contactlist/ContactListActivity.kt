package com.example.contacts.screens.contactlist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.contacts.R
import com.example.contacts.adapters.ContactAdapter
import com.example.contacts.data.AppDatabase
import com.example.contacts.screens.addcontact.AddContactActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URI
import java.util.logging.Level.parse

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

        viewmodel.contactList.observe(this, Observer {
            adapter.contacts = it
        })

    }

    fun onClickAddContact(view: View) {
        val intent = Intent(this, AddContactActivity::class.java)
        startActivity(intent)
    }
}
