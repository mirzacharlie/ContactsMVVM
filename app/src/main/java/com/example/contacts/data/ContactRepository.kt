package com.example.contacts.data

import androidx.lifecycle.LiveData
import com.example.contacts.pojo.Contact

class ContactRepository(private val contactDao: ContactDao) {

    val contacts: LiveData<List<Contact>> = contactDao.getAllContacts()

    fun addContact(contact: Contact){

        contactDao.addContact(contact)
    }
}