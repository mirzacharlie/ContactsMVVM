package com.example.contacts.screens.addcontact

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.example.contacts.R
import com.example.contacts.data.AppDatabase
import com.example.contacts.pojo.Contact
import kotlinx.android.synthetic.main.activity_add_contact.*

class AddContactActivity : AppCompatActivity() {

    private val IMG_REQUEST_CODE = 111

    lateinit var db: AppDatabase
    lateinit var viewmodel: AddContactViewModel

    var isNew = false
    var id: Long = -1
    var imgUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        db = AppDatabase.getInstance(this)
        viewmodel = ViewModelProvider.AndroidViewModelFactory(application).create(
            AddContactViewModel::class.java
        )

        cbEmployee.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                etPosition.visibility = View.VISIBLE
                etWorkPhone.visibility = View.VISIBLE
            } else {
                etPosition.visibility = View.GONE
                etWorkPhone.visibility = View.GONE
            }
        }

        if (intent.hasExtra("id")) {
            id = intent.getLongExtra("id", -1)

            Log.d("RECEIVED_CONTACT_ID", id.toString())

            viewmodel.setContactToLiveData(id)
            viewmodel.contact.observe(this, Observer {

                etFirstName.setText(it.firstName)
                etLastName.setText(it.lastName)
                etPersonalPhone.setText(it.personalPhone)

                imgUri = it.imgUri
                if (imgUri != null){
                    ivPhoto.load(imgUri) {
                        placeholder(R.drawable.avatar)
                        size(512, 512)
                        transformations(RoundedCornersTransformation(40f))
                    }
                } else {
                    ivPhoto.load(R.drawable.avatar){
                    size(512, 512)
                    transformations(RoundedCornersTransformation(40f))
                    }
                }


                if (it.type == Contact.TYPE_EMPLOYEE) {
                    cbEmployee.isChecked = true
                    etPosition.visibility = View.VISIBLE
                    etWorkPhone.visibility = View.VISIBLE

                    etPosition.setText(it.position)
                    etWorkPhone.setText(it.workPhone)
                }
            })
        } else {
            isNew = true
        }

        viewmodel.getNewImgUri().observe(this, Observer {
            imgUri = it
            ivPhoto.load(imgUri)
        })
    }

    fun onClickSaveContact(view: View) {
        if (validation()) {
            val contact = Contact()
            contact.id = id
            contact.firstName = etFirstName.text.toString().trim()
            contact.lastName = etLastName.text.toString().trim()
            contact.personalPhone = etPersonalPhone.text.toString().trim()
            contact.imgUri = imgUri

            if (cbEmployee.isChecked){
                contact.type =  Contact.TYPE_EMPLOYEE
                contact.position = etPosition.text.toString().trim()
                contact.workPhone = etWorkPhone.text.toString().trim()
            } else {
                contact.type =  Contact.TYPE_FRIEND
                contact.position = null
                contact.workPhone = null
            }

            if (isNew) {
                contact.id = null
                viewmodel.insert(contact)
            } else {
                viewmodel.update(contact)
            }
            finish()
        }
    }

    fun onClickChangePhoto(view: View) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, IMG_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMG_REQUEST_CODE) {
            viewmodel.setNewPhotoUri(Uri.parse(data?.dataString), this)
        }
    }

    private fun validation(): Boolean {
        if (etFirstName.text.isEmpty() || etLastName.text.isEmpty() || etPersonalPhone.text.isEmpty()) {
            Toast.makeText(this, "Основные поля должны быть заполнены", Toast.LENGTH_LONG).show()
        } else if (cbEmployee.isChecked && (etPosition.text.isEmpty() || etWorkPhone.text.isEmpty())) {
            Toast.makeText(
                this,
                "Укажите должность и рабочий телефон или снимите отметку 'Сотрудник'",
                Toast.LENGTH_LONG
            ).show()
        } else {
            return true
        }
        return false
    }

}
