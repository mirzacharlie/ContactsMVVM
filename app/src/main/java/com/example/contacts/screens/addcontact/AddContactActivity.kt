package com.example.contacts.screens.addcontact

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import com.example.contacts.R
import com.example.contacts.data.AppDatabase
import com.example.contacts.pojo.Contact
import com.example.contacts.screens.contactlist.ContactMainViewModel
import com.squareup.picasso.Picasso
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_contact.*

class AddContactActivity : AppCompatActivity() {

    val IMG_REQUEST_CODE = 111

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

                ivPhoto.load(it.imgUri) {
                    placeholder(R.drawable.avatar)
                }

                if (it.isEmployee == true) {
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
    }

    fun onClickSaveContact(view: View) {
        if (validation()) {
            val contact = Contact()
            contact.id = id
            contact.firstName = etFirstName.text.toString().trim()
            contact.lastName = etLastName.text.toString().trim()
            contact.personalPhone = etPersonalPhone.text.toString().trim()
            contact.isEmployee = cbEmployee.isChecked
            contact.position = etPosition.text.toString().trim()
            contact.workPhone = etWorkPhone.text.toString().trim()
            contact.imgUri = imgUri

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
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMG_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMG_REQUEST_CODE) {
            imgUri = data?.dataString
            ivPhoto.load(imgUri)
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
