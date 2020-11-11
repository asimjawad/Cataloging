package com.example.cataloging.datamodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(val name: String,val image: String,val quantity: Int,val price : Double) : Parcelable{
    constructor(): this("","",0,0.00)
}