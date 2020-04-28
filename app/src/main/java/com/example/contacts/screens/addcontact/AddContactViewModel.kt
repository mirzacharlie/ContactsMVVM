package com.example.contacts.screens.addcontact

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.contacts.data.AppDatabase
import com.example.contacts.pojo.Contact
import com.example.contacts.utils.SingleLiveEvent
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddContactViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    var contact = MutableLiveData<Contact>()

    private var newImgUri: SingleLiveEvent<String> = SingleLiveEvent<String>()

    private val compositeDisposable = CompositeDisposable()

    private fun getContactFromDb(id: Long): Single<Contact> = db.contactDao().getContact(id)

    fun getNewImgUri() = newImgUri

    fun setNewPhotoUri(uri: Uri, context: Context){
        val disposable = copyPhoto(uri, context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                newImgUri.value = it
                Log.d("PHOTO_COPYING", "Is success, new URI: $it")
            },{
                Log.d("PHOTO_COPYING", "Is failed: ${it.message}")
            })
        compositeDisposable.add(disposable)
    }

    fun setContactToLiveData(id: Long) {
        val disposable = getContactFromDb(id)
            .subscribeOn(Schedulers.io())
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

    private fun copyPhoto(uri: Uri, context: Context): Single<String>{

        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val fileName = "JPEG_" + timeStamp + "_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val newFile = File.createTempFile(fileName, ".jpg", storageDir)

        val contentResolver = context.contentResolver
        val inStream: InputStream? = contentResolver.openInputStream(uri)
        val outStream: OutputStream = FileOutputStream(newFile)
        val buf = ByteArray(1024)
        var len: Int = 0
        if (inStream != null) {
            while (inStream.read(buf).also { len = it } > 0) {
                outStream.write(buf, 0, len)
            }
        }
        outStream.close()
        inStream?.close()

        return Single.just(Uri.fromFile(newFile).toString())
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}