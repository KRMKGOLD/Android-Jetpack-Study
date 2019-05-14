# LifeCycle-Aware Components

- 참고 링크

[Handling Lifecycles with Lifecycle-Aware Components | Android Developers](https://developer.android.com/topic/libraries/architecture/lifecycle?hl=ko)

[Android Architecture Components #2 - Handling Lifecycles](https://tourspace.tistory.com/24?category=788397)

- androidx.arch.lifecycle 패키지로 구성되어 있으며, 현재 컴포넌트(액티비티나 프래그먼트)의 상태에 따라 처리되어야 하는 코드를 쉽게 만들어 주는 라이브러리(class & interface)

- Lifecycle을 알지 못하면 메모리 누수나 충돌같은 현상이 일어날 수 있다.

  internal class MyLocationListener( private val context: Context, private val callback: (Location) -> Unit ) {

  ```
    fun start() {
        // connect to system location service
    }
  
    fun stop() {
        // disconnect from system location service
    }
  ```

  }

  class MyActivity : AppCompatActivity() { private lateinit var myLocationListener: MyLocationListener

  ```
    override fun onCreate(...) {
        myLocationListener = MyLocationListener(this) { location ->
            // update UI
        }
    }
  
    public override fun onStart() {
        super.onStart()
        myLocationListener.start()
        // manage other components that need to respond
        // to the activity lifecycle
    }
  
    public override fun onStop() {
        super.onStop()
        myLocationListener.stop()
        // manage other components that need to respond
        // to the activity lifecycle
    }
  ```

  }

- 간단하고 좋은 코드처럼 보이지만, 한 개의 구성 요소가 아닌 여러 개의 구성 요소를 관리하면 생명 주기 코드에 많은 코드가 사용될 수 있다.

- Lifecycle : 현 컴포넌트의 Lifecycle 상태에 대한 정보를 저장하며, 다른 객체가 그 상태를 알 수 있게 해줌.

  ![img](https://developer.android.com/images/topic/libraries/architecture/lifecycle-states.png?hl=ko)

  - Lifecycle는 states와 events로 구성된다. 
    - states : Lifecycle의 현재 상태.
      - CREATED: onCreate() 이후나 onStop() 직전에 바뀜
      - DESTROYED : onDestory()가 불리기 직전에 바뀜
      - INITIALIZED: onCreate()가 불리기 직전에 바뀜
      - RESUMED: onResume()이 불린 이후에 바뀜
      - STARTED: onStart() 이후나, onPause() 직전에 바뀜
    - events : Lifecycle 이벤트로, 액티비티와 프래그먼트의 콜백 이벤트에 매핑된다.
      - ON_ANY
      - ON_CREATE
      - ON_DESTROY
      - ON_PAUSE
      - ON_RESUME
      - ON_START

- Activity나 Fragment의 생명주기를 분리해서 모니터링 할 수 있게 만들어준다.

  class MyActivity : AppCompatActivity() { private lateinit var myLocationListener: MyLocationListener

  ```
    override fun onCreate(...) {
        myLocationListener = MyLocationListener(this, lifecycle) { location ->
            // update UI
        }
        Util.checkUserStatus { result ->
            if (result) {
                myLocationListener.enable()
            }
        }
    }
  ```

  }

- MyLocationListener에 lifecycle를 생성자로 넘겨준다.

- 이제 MyLocationListener에서 MainActivity의 Lifecycle을 관리할 수 있다!

  internal class MyLocationListener( private val context: Context, private val lifecycle: Lifecycle, private val callback: (Location) -> Unit ) {

  ```
    private var enabled = false
  
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        if (enabled) {
            // connect
        }
    }
  
    fun enable() {
        enabled = true
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            // connect if not connected
        }
    }
  
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        // disconnect if connected
    }
  ```

  }

- Observer가 Lifecycle를 확인할수 있게 되면서, 스스로 생명 주기 관리가 가능하게 되고, 이런 클래스를 'Lifecycle-aware components'라고 한다.

- Custom LifecycleOwner

  - Support Library 26.1.0부터 이미 LifecycleOwner가 Activity와 Fragment에 구현되어 있다면, LifecycleRegistry 클래스를 사용하면 되지만...

    class MyActivity : Activity(), LifecycleOwner {

    ```
      private lateinit var lifecycleRegistry: LifecycleRegistry
    
      override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
    
          lifecycleRegistry = LifecycleRegistry(this)
          lifecycleRegistry.markState(Lifecycle.State.CREATED)
      }
    
      public override fun onStart() {
          super.onStart()
          lifecycleRegistry.markState(Lifecycle.State.STARTED)
      }
    
      override fun getLifecycle(): Lifecycle {
          return lifecycleRegistry
      }
    ```

    }

  - 이벤트를 해당 클래스로 직접 전달해서 사용해주어야 한다.

- Lifecycle-aware components의 좋은 예시

  1. Activity나 Fragment같은 UI controller를 최대한 가볍게 유지하라. UI controller가 데이터를 수집하지 않도록 하고 이를 ViewModel에 위임하라.
  2. LiveData를 observing하여, View에 변경사항을 반영하도록 구성한다.
  3. ViewModel은 UI controller와 나머지 앱 구성요소를 연결하는 역할을 하도록 한다. ViewModel에서 data를 직접 fetch하는것이 아니라, fetching 역할을 하는 적절한 component와 연결하는 작업을 해야한다.
  4. Data binding을 사용하면 view와 UI component간 관계를 좀더 clean하게 해줄 수 있다.
  5. UI가 복잡하다면 Presenter를 이용하여 UI 변경을 관리하라.
  6. ViewModel에서 view나 Activity context를 참조하면 안된다. ViewModel은 activity와 생명주기가 다른기 때문에 leak을 발생시킬 수 있다.