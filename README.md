<h1 align="center">Restaurant Explorer</h1>

## Library and Frameworks
- Minimum SDK level 21
- [Kotlin](https://kotlinlang.org/) based, [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) for asynchronous.
- Dagger-Hilt (alpha) for dependency injection.
- JetPack
    - LiveData - notify domain layer data to views.
    - Lifecycle - dispose of observing data when lifecycle state changes.
    - ViewModel - UI related data holder, lifecycle aware.
    - Navigation - Navigating to restaurant details.
- Architecture
    - MVVM Architecture (Model - ViewModel - Model)
    - Repository pattern
- [Retrofit2 & OkHttp3](https://github.com/square/retrofit) - construct the REST APIs and paging network data.
- [Retrofit](https://square.github.io/retrofit/) - A type-safe HTTP client for Android and Java.
- [coil](https://github.com/coil-kt/coil)
- [Material-Components](https://github.com/material-components/material-components-android) - Material design components
- Testing
    - [MockK](https://mockk.io/) - Unit testing
    - [JUnit5](https://github.com/mannodermaus/android-junit5)
    - [kotlinx-coroutines-test](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/)
    
## APK file for installation
[Click here](/art/restaurant.apk) to download apk.

## Demo
<img src="/art/recording.gif" height="569" width="256">

## Missing Implementation.
- Didn't cover instrumentation test cases. 
- Haven't made use of SavedStateHandle provided by ViewModel.