package com.example.contacts.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.contacts.pojo.Contact
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts")
    fun getAllContacts(): LiveData<List<Contact>>

    @Query("SELECT * FROM contacts WHERE id == :id")
    fun getContact(id: Long): Single<Contact>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addContact(contact: Contact): Completable

    @Update
    fun updateContact(contact: Contact): Completable

    @Delete
    fun deleteContact(contact: Contact)
}