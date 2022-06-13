# ViewBindingEx



ViewBinding with Kotlin Property Delegate and Lifecycle, no memory leak




## Usage

1. Add maven central to  your project

   ```groovy
    repositories {
           ...
           mavenCentral()
           ...
    }
   ```

2. Add the dependency in your App module

   ```groovy
    dependencies {
         implementation("cn.chitanda:viewbindingex:1.2.0")
    }
   ```

3. **Activity**
   
   ```kotlin
   class MainActivity : AppCompatActivity() {
   
       private  val binding by viewBinding(ActivityMainBinding::inflate)
   
       override fun onCreate(savedInstanceState: Bundle?) {
           setContentView(binding.root)
       }
   }
   ```
   
   **Fragment or DialogFragment**
   
   - Use  ViewBinding.inflate()
   
      ```kotlin
      class HomeFragment : Fragment() {
      	private val binding by viewBinding { FragmentHomeBinding.inflate(layoutInflater) }
        private val viewModel by viewModels<HomeViewModel>()
       	
        override fun onCreateView(
           inflater: LayoutInflater,
           container: ViewGroup?,
           savedInstanceState: Bundle?): View {
           return binding.root
       	}
      
       	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
           super.onViewCreated(view, savedInstanceState)
           binding.textHome.text = "hello world"
       	}
        
      }
      ```
   - Use ViewBinding.bind()
	
      ```kotlin
	   class HomeFragment : Fragment(R.layout.fragment_home) {
      
      	private val binding by viewBinding (FragmentHomeBinding::bind)
      
      	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
           super.onViewCreated(view, savedInstanceState)
           binding.button.setOnClickListener {
               HomeDialog().show(childFragmentManager,"dialog")
           }
        }
        
      }
      ```