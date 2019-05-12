# Android KTX

- Android KTX란 Android Jetpack 제품군의 일부인 Kotlin 확장 프로그램 세트로, Kotlin 사용을 위해 Jetpack과 Android 플랫폼 API를 최적화 해주는 시스템.

- 참고 링크.

  [Android KTX | Android Developers](https://developer.android.com/kotlin/ktx.html)

  [Android를 위한 코틀린 개발용 확장 라이브러리인 Android KTX를 소개합니다.](https://developers-kr.googleblog.com/2018/02/introducing-android-ktx-even-sweeter-kotlin-development-for-android.html)

1. Start

   ```
    repositories {
    		google()
    	}
   ```

   - build.gradle 파일에 다음 코드를 추가한다.
   - 각 모듈에 맞는 패키지를 Gradle에 추가해서 사용한다. 
     - [모듈 확인](https://developer.android.com/kotlin/ktx.html#modules)

2. KTX를 이용한 Code Sample

   1. SharedPreferences (androidx.core.content)

      - Kotlin

        ```kotlin
        sharedPreferences.edit()
                   .putBoolean(key, value)
                   .apply()
        ```

        

      - Kotlin with Android KTX

        ```kotlin
        sharedPreferences.edit { 
            putBoolean(key, value) 
        }
        ```

        

   2. Fragment (androidx.fragment:fragment-ktx)

      - Kotlin

        ```kotlin
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.my_fragment_container, myFragment, FRAGMENT_TAG)
                .commitAllowingStateLoss()
        ```

        

      - Kotlin with Android KTX

        ```kotlin
        supportFragmentManager.transaction(allowStateLoss = true) {
                        replace(R.id.my_fragment_container, myFragment, FRAGMENT_TAG)
                    }
        ```

        

   3. SQLite (androidx.sqlite:sqlite-ktx)

      - Kotlin

        ```kotlin
        db.beginTransaction()
            try {
                // insert data
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        ```

        

      - Kotlin with Android KTX

        ```kotlin
        db.transaction {
                // insert data
        	    }
        ```

        