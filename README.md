# Setup

### Step.1 Adding dependencies

build.gradle(project)

```
dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.39")
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31"
        classpath 'com.google.dagger:hilt-android-gradle-plugin:2.39'
   }
```
build.gradle(app)
```
plugins {
    id 'kotlin-kapt'
    id("dagger.hilt.android.plugin")
}

dependencies {
    //Room
    implementation 'androidx.room:room-runtime:2.4.2'
    annotationProcessor("androidx.room:room-compiler:2.4.2")

    kapt("androidx.room:room-compiler:2.4.2")
    implementation "androidx.room:room-ktx:2.4.2"

    //hilt
    implementation("com.google.dagger:hilt-android:2.39")
    kapt("com.google.dagger:hilt-android-compiler:2.39")
    implementation "androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03"
    kapt "androidx.hilt:hilt-compiler:1.0.0"
    implementation "androidx.hilt:hilt-navigation-compose:1.0.0"

    //Coroutine
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2'
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.5.2"

    // Coroutine Lifecycle Scopes
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0"
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.4.0"


    //Retrofit
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    }
```
### Step.2 Create **Kotlin data class From JSON**

![](https://i.imgur.com/HVuwaVY.png)

if **Kotlin data class File from JSON** not exists

>File --> Settings --> Plugins --> Browse Repositories --> Search JsonToKotlinClass


### Step.3 Adding Hilt Classes and Retrofit
create /TriviaApplication
```kotlin=
@HiltAndroidApp
class TriviaApplication: Application(){
}
```
AndrioidManifest.xml
```xml=
<uses-permission android:name="android.permission.INTERNET"/>
<application
        android:name=".TriviaApplication">
        ...
</application>
```
/util/Constants
```kotlin=
object Constants {
    val BASE_URL = "https://raw.githubusercontent.com/itmmckernan/triviaJSON/master/"
}
```
/network/QuestionApi
```kotlin=
@Singleton
interface QuestionApi {
    @GET(value="world.json")
    suspend fun getAllQuestions(): Question
}
```
/di/AppModule
```kotlin=
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideQuestionApi(): QuestionApi{
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuestionApi::class.java)
    }
}
```
### Step.4 Create Repository and Wrapper Class
/data/DataOrException
```kotlin=
data class DataOrException<T, Boolean, E: Exception>(
    var data: T? = null,
    var loading: Boolean? = null,
    var e: E? = null
)
```
/repository/QuestionRepository
```kotlin=
class QuestionRepository @Inject constructor(
    private val api: QuestionApi
){
    private val dataOrException
    = DataOrException<ArrayList<QuestionItem>, Boolean, Exception>()
    suspend fun getAllQuestions(): DataOrException<ArrayList<QuestionItem>,Boolean,Exception>{
        try {
            dataOrException.loading = true
            dataOrException.data = api.getAllQuestions()
            if (dataOrException.data.toString().isNotEmpty()){
                dataOrException.loading = false
            }

        } catch (exception: Exception){
            dataOrException.e = exception
            Log.d("TAG", "getAllQuestions${dataOrException.e!!.localizedMessage}")
        }
        return dataOrException
    }
}
```
### Step.5 Creating ViewModel
adding provideQuestionRepository to /di/AppModule
```kotlin=
@Singleton
@Provides
fun provideQuestionRepository(api: QuestionApi)
= QuestionRepository(api)
```
/screens/QuestionViewModel
```kotlin=
@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val repository: QuestionRepository
) :ViewModel()
{
    private val data: MutableState<DataOrException<ArrayList<QuestionItem>,Boolean, Exception>>
    = mutableStateOf(DataOrException(null, true, Exception("")))

    init {
        getAllQuestions()
    }

    private fun getAllQuestions(){
        viewModelScope.launch {
            data.value.loading = true
            data.value = repository.getAllQuestions()
            if (data.value.data.toString().isNotEmpty()){
                data.value.loading = false
            }
        }
    }
}
```
Step.6 Test
/MainActivity
```kotlin=
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TriviaQuizTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TriviaHome()
                }
            }
        }
    }
}

@Composable
fun TriviaHome(viewModel: QuestionViewModel = hiltViewModel()){
    Questions(viewModel)
}

@Composable
fun Questions(viewModel: QuestionViewModel) {
    val questions = viewModel.data.value.data?.toMutableList()
    Log.d("MainActivity", "Questions: ${questions?.size}")
}
```
lauch application successfully and see **Logcat**
>2022-06-03 18:41:05.350 2440-2440/com.example.darren.triviaquiz D/MainActivity: Questions: 4875

### Next: using the data fetched from API and build UI
