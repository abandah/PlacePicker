package com.place.picker

/**
 * Created by Abandah on 9/23/2020.
 */
interface PlacePickerListener {
    fun onPlaceSuccessful ( addressData : AddressData)
    fun onPlaceError(error : String)
}