package com.example.locationsearchmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.locationsearchmap.databinding.ActivityMainBinding
import com.example.locationsearchmap.model.LocationLatLngEntity
import com.example.locationsearchmap.model.SearchResultEntity
import com.example.locationsearchmap.utility.RetrofitUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope { //비동기로 코드를 구성하기 위해서 코루틴 인터페이스 명시

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main //어떤 쓰레드에서 동작을 할시 Dispatcher로 명시를 해준다.

    private lateinit var job: Job

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: SearchRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()

        initAdapter()
        initViews()
        initData()
        setData()
        bindViews()
    }

    private fun bindViews() = with(binding) {
        searchButton.setOnClickListener { //버튼을 누를때 API동작
            searchKeyword(searchBarInputView.text.toString())
        }
    }

    private fun initViews() = with(binding) {
        emptyResultTextView.isVisible = false
        recylcerView.adapter = adapter
    }

    private fun initAdapter() {
        adapter = SearchRecyclerAdapter()
    }

    private fun initData() {
        adapter.notifyDataSetChanged()
    }

    private fun setData() {
        val dataList = (0..10).map {
            SearchResultEntity(
                name = "빌딩 $it",
                fullAddress = "주소 $it",
                locationLatLng = LocationLatLngEntity(
                    it.toFloat(),
                    it.toFloat()
                )
            )
        }
        adapter.setSearchResultList(dataList) {
            Toast.makeText(this, "빌딩 이름 : ${it.name} 주소 : ${it.fullAddress}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun searchKeyword(keywordString : String) {
        launch(coroutineContext){ // 코루틴의 비동기 실행 함수 인자로는 어느 Context에서 동작할지 명시한 Dispatcher이 들어간다.
            try{
                withContext(Dispatchers.IO){// API데이터를 받기위해 IO Thread로 전환 한다.
                    val response = RetrofitUtil.apiService.getSearchLocation(
                        keyword = keywordString
                    )
                    if(response.isSuccessful){
                        val body = response.body()
                        withContext(Dispatchers.Main){// IO Thread에서 데이터를 받아왔기 때문에 Main Thread로 전환한다.
                            Log.e("response",body.toString())
                        }
                    }
                }
            }catch(e : Exception){
                e.printStackTrace()
                Toast.makeText(this@MainActivity,"검색하는 과정에서 에러가 발생했습니다. : ${e.message}",Toast.LENGTH_SHORT).show()
            }
        }
    }
}