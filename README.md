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
    
## Code Coverage and Reporting
  - This project uses Jacoco configuration to measure code coverage and currently the most widely used.
  - SonarQube is used for inspecting the Code Quality and Security codebase. 
  - SonarQube consumes Jacoco test coverage report and displays it on [sonarcloud](https://sonarcloud.io/dashboard?id=vipulshah2010_RestaurantExplorer)
    
    **Run following command from root folder to generate JaCoco reports and view test coverage on** [sonarcloud](https://sonarcloud.io/dashboard?id=vipulshah2010_RestaurantExplorer)
     ```
    ./gradlew clean sonarqube
    ```
   
## Please note.
for assignment purpose secure.properties is committed to repo, so reviewers don't need to set it up, 
In real time projects, this file will be added to .gitignore. Every developer will have his own set of credentials locally stored in secure.properties file.
    
## APK file for installation
[Click here](https://github.com/vipulshah2010/RestaurantExplorer/blob/master/art/restaurant.apk) to download apk.

## Demo
<img src="/art/recording.gif" height="569" width="256">

## Missing Implementation.
- Didn't cover instrumentation test cases. 