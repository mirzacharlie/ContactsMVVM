package com.example.contacts.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "contacts")
data class Contact(

    @NotNull
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,

    var firstName: String? = null,

    var lastName: String? = null,

    var imgUri: String? = null,

    var type: Int? = null,
    
    var position: String? = null,

    var personalPhone: String? = null,

    var workPhone: String? = null
){
   companion object {
       const val TYPE_EMPLOYEE = 1
       const val TYPE_FRIEND = 0
   }
}
