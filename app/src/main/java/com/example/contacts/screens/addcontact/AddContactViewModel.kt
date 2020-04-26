package com.example.contacts.screens.addcontact

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.contacts.data.AppDatabase
import com.example.contacts.pojo.Contact
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AddContactViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    var contact = MutableLiveData<Contact>()

    private val compositeDisposable = CompositeDisposable()

    private fun getContactFromDb(id: Long): Single<Contact> = db.contactDao().getContact(id)

    fun setContactToLiveData(id: Long) {
        val disposable = getContactFromDb(id).subscribeOn(Schedulers.io())
            .subscribe({
                Log.d("DATA_GET", "Is success: ${it.id}")
                contact.postValue(it)
            },
                {
                    Log.d("DATA_GET", "Is failed: ${it.message}")
                })
        compositeDisposable.add(disposable)
    }

    fun insert(contact: Contact) {
        db.contactDao().addContact(contact)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
            }, {
                Log.d("DATA_INSERT", "Is failed: ${it.message}")
            }).let {
                compositeDisposable.add(it)
                Log.d("DATA_INSERT", "is success")
            }
    }

    fun update(contact: Contact){
        db.contactDao().updateContact(contact)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
            }, {
                Log.d("DATA_UPDATE", "Is failed: ${it.message}")
            }).let {
                compositeDisposable.add(it)
                Log.d("DATA_UPDATE", "is success")
            }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}