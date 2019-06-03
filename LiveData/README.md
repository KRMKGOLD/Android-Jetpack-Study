# LiveData

- 참고 링크

[LiveData Overview | Android Developers](https://developer.android.com/topic/libraries/architecture/livedata?hl=ko)

[Android Architecture Component - LiveData](https://www.charlezz.com/?p=363)

- LiveData : 데이터의 변경을 확인할 수 있는 Observable 클래스

  - 그냥 Observable 클래스가 아닌 생명주기를 알고 있는 Observable 클래스
  - 생명주기를 존중하기 때문에 활성 상태(STARTED, RESUMED)에서만 데이터가 업데이트된다.

  

- LiveData의 장점

  - 데이터와 UI를 동기화 : 앱의 생명주기의 변경에 따라 UI를 매번 갱신할 필요가 없고, 옵저버객체내에서 UI를 갱신하는것으로 통합시킬 수 있다.
  - 메모리 누수 방지 : 옵저버는 Lifecycle 객체와 바인딩되어 있고, 연관된 생명주기가 소멸시에 옵저버도 같이 메모리상에서 정리된다.
  - 액티비티 종료에 의한 크래시 방지 : 옵저버 생명주기가 비활성화 (DESTROYED, INITIALIZED, RESUMED) 된다면, LiveData의 데이터 변경 이벤트를 받지 않는다.
  - 생명주기 관리가 필요가 없다  : LiveData는 Lifecycle을 가지고 있기 때문에, Observe만 하면 된다.
  - 항상 업데이트 되어있는 데이터 : 라이프사이클이 비활성화되어 있다가 활성화되면 최신 데이터를 받아온다.
  - 상황에 따른 상태변경 : 디바이스가 회전되는 등, UI가 재구성되는 경우에도 즉시 최신의 데이터를 받아온다.
  - 자원 공유 : 싱글톤 패턴을 사용하여 LiveData를 확장해 시스템 서비스를 둘러싸면 앱에 어느 부분에서든 공유할 수 있고, LiveData를 상속해 Custom LiveData를 만들 수 있다.

  

- LiveData 사용법

  1. 데이터를 저장할 LiveData 인스턴스를 생성한다. (ViewModel 클래스 등)
  2. LiveData의 Observer 객체를 만들고, onChanged()를 구현해 LiveData의 데이터가 변경될 때를 구현한다. (Observer 객체는 UI 컨트롤러( ex : Activity, Fragment)에 생성해 준다.)
  3. observe() 메소드를 이용해서 Observer를 LiveData에 연결할 수 있고, observe() 메소드는 LifecycleOwner 객체를 포함해야 한다. (Activity나 Fragment는 LifeCycleOwner를 implement하고 있다.)

  - Note : observeForever(Observer) 메소드를 이용하여 LifeCycleOwner 없이 Observer를 등록할 수는 있지만, Observer는 항상 활성화되어있는 상태이므로 수정사항에 대한 업데이트된 데이터를 항상 받고, removeObserver(Observer) 메소드를 이용해 Observer를 제거할 수 있다.

  

- LiveData 객체 생성 : LiveData는 Collections를 포함한 어떤 데이터든 감쌀 수 있다. 보통 ViewModel 객체에 LiveData 객체가 저장되고, 다음 예시와 같이 getter 방법을 통해 접근하게 된다.

  ```
    class NameViewModel : ViewModel() {
    
        // String을 감싼 LiveData
        val currentName: MutableLiveData<String> by lazy {
            MutableLiveData<String>()
        }
    
        // ViewModel의 나머지 부분을 설정
    }
    // 하지만 아직 LiveData의 초깃값은 설정되어있지 않다.
  ```

  - Note : LiveData와 ViewModel은 Activity나 Fragment가 거대해지는 것을 피한다. UI Controller는 오직 UI 갱신 역할만을 하면 되고, 전처럼 Data에 관련된 로직을 가지고 있을 필요가 없다. 직접 관리 시에는 LifeCycle과 관련되어 처리해야 할 것이 늘어나기 때문에 UI Controller와 LiveData를 분리하기 위해서 ViewModel이 LiveData를 관리하고 UI Controller는 Observer로 ViewModel을 관찰해 UI를 갱신하면 되기 때문에 UI 변화에서도 LiveData 객체는 유지할 수 있다.

- LiveData 관찰 : onCreate()에서 데이터 관측하는 것이 가장 좋음

  - onResume() 같은 곳에 observe가 위치하면 중복 호출을 배제하기 힘들다.

  - STARTED 상태 시 바로 Data를 UI에 표현할 수 있기 때문이다.

  - LiveData는 데이터 변경 시에만 업데이트를 하지만, 예외적으로 Observer가 비활성 상태에서 활성 상태로 변하면. 마지막으로 활성 상태가 된 이후 값이 변경되었을 경우에만 업데이트를 통해 데이터를 수신한다.

  - LiveData가 관찰을 시작하는 방법

    ```kotlin
    class NameActivity : AppCompatActivity() {
    
        private lateinit var model: NameViewModel
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
    
            // onCreate에서 활동을 설정하는 다른 코드들
    
            // ViewModel 받기
            model = ViewModelProviders.of(this).get(NameViewModel::class.java)
    
    
            // UI 업데이트 Observer 세팅
            val nameObserver = Observer<String> { newName ->
                // Update the UI, in this case, a TextView.
                nameTextView.text = newName
            }
    
            // observe()를 통해서 LiveData를 감지하고, this(LifeCycleOwner)와 Observer(nameObserver)를 넘긴다.
            model.currentName.observe(this, nameObserver)
        }
    }
    ```

- LiveData의 데이터 변경

  - LiveData의 데이터를 변경할 수 있는 메소드는 public으로 없음

  - MutableLiveData를 이용 (LiveData를 상속함), MutableLiveData는 setValue(T)와 postValue(T)를 가지고 있다.

    ```kotlin
    button.setOnClickListener {
        val anotherName = "John Doe"
        model.currentName.setValue(anotherName)
    }
    ```

    

- Room과 사용 : Room에서 return 값을 LiveData로 갖는 Observable queries를 지원해준다.. 데이터베이스가 변경될때 LiveData도 같이 업데이트 된다. 필요하다면 쿼리로 인해 생성된 코드는 비동기적으로 백그라운드 쓰레드에서 생성된다.

- Kotlin Coroutines과 사용

  [Use Kotlin coroutines with Architecture components | Android Developers](https://developer.android.com/topic/libraries/architecture/coroutines?hl=ko#livedata)

- LiveData의 확장 : LiveData를 상속받아 확장하는 방식이다.

  ```kotlin
    class StockLiveData(symbol: String) : LiveData<BigDecimal>() {
        private val stockManager = StockManager(symbol)
    
        private val listener = { price: BigDecimal ->
            value = price
        }
    
        override fun onActive() {
            stockManager.requestPriceUpdates(listener)
        }
    
        override fun onInactive() {
            stockManager.removeUpdates(listener)
        }
    }
  ```

  - onActive()는 LiveData가 활성화된 옵저버를 갖고 있을 때 호출된다.

  - onInactive() LiveData가 활성화된 옵저버를 하나도 가지지 않고 있을 때 호출된다.

  - setValue(T)는 LiveData의 값을 변경하고 옵저버에게 변경사항을 알려준다.

    ```kotlin
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val myPriceListener: LiveData<BigDecimal> = ...
        myPriceListener.observe(this, Observer<BigDecimal> { price: BigDecimal? ->
            // UI 업데이트
        })
    }
    ```

  - 위 StockLiveData를 이용해 작성할 때, observe()를 통해 LifeCycleOwner(fragment)와 Observer를 넘겨주고 있다. LifeCycleOwner를 보내는 이유는, active 상태일때만 data 변경에 호출하기 위해서고, Lifecycle이 파괴되면 Observer도 자동으로 제거하기 위해서이다.

  - LiveData가 Lifeycle를 알고 있다는 것은 activity, fragment, server 간에 LiveData를 공유할 수 있다는 뜻이다.

  - 싱글톤 작성방식

    ```kotlin
    class StockLiveData(symbol: String) : LiveData<BigDecimal>() {
        private val stockManager: StockManager = StockManager(symbol)
    
        private val listener = { price: BigDecimal ->
            value = price
        }
    
        override fun onActive() {
            stockManager.requestPriceUpdates(listener)
        }
    
        override fun onInactive() {
            stockManager.removeUpdates(listener)
        }
    
        companion object {
            private lateinit var sInstance: StockLiveData
    
            @MainThread
            fun get(symbol: String): StockLiveData {
                sInstance = if (::sInstance.isInitialized) sInstance else StockLiveData(symbol)
                return sInstance
            }
        }
    }
    ```

    ```kotlin
    class MyFragment : Fragment() {
    
        override fun onActivityCreated(savedInstanceState: Bundle?) {
            StockLiveData.get(symbol).observe(this, Observer<BigDecimal> { price: BigDecimal? ->
                // Update the UI.
            })
    
        }
    ```

- LIveData의 변형

  - LiveData의 data의 변경 등 부가적인 기능들을 Observer를 이용해서 얻고 싶은 경우들을 지원해준다.

  - Transformation.map()

    ```kotlin
      val userLiveData: LiveData<User> = UserLiveData()
      val userName: LiveData<String> = Transformations.map(userLiveData) {
          user -> "${user.name} ${user.lastName}"
      }
    ```

  - Transformations.switchMap()

    ```kotlin
      private fun getUser(id: String): LiveData<User> {
        ...
      }
      val userId: LiveData<String> = ...
      val user = Transformations.switchMap(userId) { id -> getUser(id) }
    ```

    map()과 switchMap()은 비슷해보이지만 다르다. map()에서는 두번째 인자의 함수의 리턴값이 object면 되지만 switchMap()에서는 리턴값이 반드시 LiveData이여야 한다.

  - Transformation 메소드를 옵저버의 Lifecycle 기반으로 해 정보를 가져올 수 있다, Transformation은 Observer가 LiveData를 반환하는 것을 감시하고 있지 않는 한, 별도의 작업을 하지 않는다. 그 이유는 Transformation은 lazy하게 작동하기 때문이다. 생명주기와 관련된 활동은 추가적인 호출이나 의존성 없이 암묵적으로 생명주기를 물려주게 된다.

  - ViewModel 내에서 Lifecycle이 필요하다면, 이 때 Transformation을 사용하면 좋다.

    ```kotlin
      class MyViewModel(private val repository: PostalCodeRepository) : ViewModel() {
      
          private fun getPostalCode(address: String): LiveData<String> {
              // 이렇게 사용하지 말아라!
              return repository.getPostCode(address)
          }
      }
    ```

    - 이렇게 사용한다면 UI 컨트롤러에서는 전 LiveData를 해제했다가 getPostalCode()를 호출할 때마다 다시 등록해야 하고, UI 컨트롤러가 변경되는 등 다시 생성되면 getPostCode()를 또 호출하고 이전의 데이터는 의미가 없어진다.

      ```kotlin
      class MyViewModel(private val repository: PostalCodeRepository) : ViewModel() {
          private val addressInput = MutableLiveData<String>()
          val postalCode: LiveData<String> = Transformations.switchMap(addressInput) {
                  address -> repository.getPostCode(address) }
      
      
          private fun setInput(address: String) {
              addressInput.value = address
          }
      }
      ```

    - Transformations로 구현하면 postalCode 필드는 final(val) public 이 된다. (변경할 이유가 없어지기 때문에). postalCode는 addressInput의 변형으로 정의되었고. 이는 repository.getPostCode() 메소드가 addressInput의 내용이 달라질 때 마다 호출된다는 것이다.

    - Obsever가 추가될때까지는 아무 일이 일어나지 않는다.

    - 이런 알고리즘은 Livedata를 생성하고 lazy하게 계산되는 것을 허용하게 해 준다.

    - 이렇게 사용하면 ViewModel은 쉽게 LiveData의 객체를 참조하고 변형규칙을 최상위에서 정의하게 될 수 있다.

- 새로운 Transformation 규칙

  - MediatorLiveData를 이용하여 Custom Transformation을 제작할 수 있다.

- [LiveData Source 합치기](https://developer.android.com/jetpack/docs/guide#addendum)

  - MediatorLIveData는 Livedata의 서브 클래스이기 때문에 LiveData를 합칠 수 있게 해 준다.
  - MediatorLivedata의 Observer들은 source가 변경될 때마다 이벤트가 호출된다.
  - 액티비티나 프레그먼트에서 필요한건 오로지 MediatorLiveData의 감지 뿐이다. 이를 통해 여러개의 소스로부터 변경사항을 감지 할 수 있다.