# Church map actions

Each church detail page shows a **Visit this church** card before the overview. It uses the existing slate-and-brass Church palette in dark mode and the app's parchment palette in light mode. The name, address and buttons wrap without a fixed card height. Coordinates are used internally for Apple Maps and are no longer displayed in the card.

- **View on map** searches Google Maps for the church name, including the city when present in the name, to open its named listing.
- **Get directions** uses that church name as the Google Maps destination and leaves the starting point and travel mode to Maps.
- Android opens Google Maps through its universal HTTPS links, with the provider's browser fallback.
- iPhone offers Apple Maps and Google Maps. Apple Maps opens a named native `MKMapItem`, compatible with the app's iOS 18.2 minimum. Google uses the same universal links as Android.
- Cathopedia does not request device location access. The selected maps app manages its own location permissions.
- Missing names or missing, non-finite or out-of-range coordinates omit the card. A failed launch displays a retry message; on iPhone the other provider remains available.
- Seven labels are supplied in all nine app languages, and content version 255 refreshes existing installations.

The optional address is stored with church content and carried through the database to the card. Migration `9.sqm` adds the address column without deleting records. Nine addresses have been checked against church or government sources, including the requested Einsiedeln example; `address-sources.json` records those sources. Churches without a supplied address omit that line rather than displaying coordinates or an invented address. Postal street names retain their local spelling.

The 65 stored coordinate pairs support the location card and native Apple Maps destination. Range checks confirm that they are usable coordinates, **not that each pin matches the correct church entrance**. Google Maps resolves the supplied church name; duplicate names may need a more specific location or verified provider listing ID. Street addresses, verified provider listing IDs and an embedded map are not part of this version.

## Validation

Android debug build, iOS simulator Kotlin compilation and four focused navigation tests passed for the named-link change. The full iPhone simulator app build also passed when the map feature was added. Tests cover named search and directions destinations, accented names and URL encoding, signed coordinates, leaving the origin to Maps, and rejection of invalid/missing locations. All 65 bundled churches have coordinates in range, and all nine languages have complete map labels.

Android device checks confirmed visible card labels and both map actions. Both updated Google Maps actions resolved **Saint Mark's Basilica** in Venice by name. `st-marks-named-place.png` shows its place listing, and `named-place-checks.json` records the two named destinations. `implemented.png` shows the installed Cathopedia location card. Maps location permission remained declined during verification.

The address update passed Android build, iOS simulator Kotlin compilation and the four map-link tests. The migration was checked for preservation of existing records. Updating the existing Android installation showed Einsiedeln's published address in place of its coordinates; `einsiedeln-address.png` and `address-checks.json` record this check.

The iPhone build was installed on the simulator. A complete iPhone UI walkthrough remains unverified because the computer-use service could read the simulator but failed to send scrolling and text input (`noWindowsAvailable` / `unknownType`).

Provider references: [Google Maps URLs](https://developers.google.com/maps/documentation/urls/get-started) and [Apple MapKit map items](https://developer.apple.com/documentation/mapkit/mkmapitem).
