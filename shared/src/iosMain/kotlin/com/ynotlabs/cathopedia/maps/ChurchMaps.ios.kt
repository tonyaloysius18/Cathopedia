package com.ynotlabs.cathopedia.maps

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKLaunchOptionsDirectionsModeDefault
import platform.MapKit.MKLaunchOptionsDirectionsModeKey
import platform.MapKit.MKMapItem
import platform.MapKit.MKPlacemark

actual val supportsAppleChurchMaps: Boolean = true

@OptIn(ExperimentalForeignApi::class)
actual fun openAppleChurchMap(location: ChurchMapLocation, directions: Boolean): Boolean {
    val placemark = MKPlacemark(coordinate = CLLocationCoordinate2DMake(location.latitude, location.longitude))
    val item = MKMapItem(placemark = placemark).apply { name = location.name }
    val options = if (directions) {
        mapOf<Any?, Any>(MKLaunchOptionsDirectionsModeKey to MKLaunchOptionsDirectionsModeDefault)
    } else null
    return item.openInMapsWithLaunchOptions(options)
}
