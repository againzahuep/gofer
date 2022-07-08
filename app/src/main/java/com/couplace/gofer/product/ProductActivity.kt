package com.couplace.gofer.product

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.LinearLayout
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuAdapter
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.*
import com.couplace.gofer.R
import com.couplace.gofer.auth.FacebookLoginActivity
import com.couplace.gofer.decorator.FixedMarginItemDecorator
import com.couplace.gofer.model.Product
import com.couplace.gofer.productdetails.ProductDetailsActivity
import com.couplace.gofer.repository.DataRepository
import com.couplace.gofer.utils.Utils
import com.couplace.gofer.viewmodel.ViewModelProviderFactory
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.velmurugan.mvvmwithkotlincoroutinesandretrofit.MyViewModelFactory
import dagger.android.AndroidInjection
import javax.inject.Inject


class ProductActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private var isFirstTime: Boolean = false
    lateinit var progressIndicator: CircularProgressIndicator
    lateinit var progressCircleDeterminate: CircularProgressIndicator

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var parentLayout: LinearLayout

    lateinit var productLayoutManager: GridLayoutManager
    var position: Int = 0

    private var value: Int = 1
    lateinit var scrollListener: EndlessRecyclerViewScrollListener

    lateinit var navController: NavController

    lateinit var drawerLayout: DrawerLayout

    lateinit var navigationView: NavigationView

    // Get a reference to the ViewModel scoped to this Fragment
    lateinit var viewModel: ProductViewModel

    lateinit var productListRecycleView: RecyclerView

    lateinit var productListAdapter: ProductListAdapter

    lateinit var allProducts: List<Product>

    @Inject
    lateinit var dataRepository : DataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_product)

        AndroidInjection.inject(this)

        navController = Navigation.findNavController(this, R.id.navigation_graph)

        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView = findViewById(R.id.nav_view)

        appBarConfiguration = AppBarConfiguration(
            setOf(
              R.id.drawer_nav_profile, R.id.drawer_my_products, R.id.drawer_nav_settings, R.id.drawer_close
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navigationView.setupWithNavController(navController)
        navigationView.setCheckedItem(R.id.drawer_nav_profile);
        navigationView.setNavigationItemSelectedListener(this)



        viewModel = ViewModelProvider(this,  MyViewModelFactory(dataRepository)).get(ProductViewModel::class.java)

        progressCircleDeterminate = findViewById(R.id.progressCircleDeterminate)
        parentLayout = findViewById(R.id.parentLayout)

        viewModel.loading.observe(this, Observer {
            if (it) {
                progressCircleDeterminate.visibility = View.VISIBLE
            } else {
                progressCircleDeterminate.visibility = View.GONE
            }
        })
        initRecyclerView()

        viewModel.fetchData().observe(this) {
            isFirstTime = true
            populateList(it)
        }

    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(com.couplace.gofer.R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        when (item.itemId) {
            R.id.home -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true
                } else {
                    return false
                }

            }
            else ->
                return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.navigation_graph, true)
            .build()
        when (menuItem.itemId) {
            R.id.drawer_nav_profile -> {
                navController.navigate(R.id.profileFragment,null,navOptions)
            }
            R.id.drawer_my_products -> {
                navController.navigate(R.id.favoriteProductsFragment, null,navOptions)
            }
            R.id.drawer_nav_settings -> {
                navController.navigate(R.id.settingsFragment, null,navOptions)
            }
            R.id.drawer_close -> {

                if (Utils.getLoginFacebookPref(this, "facebook" )){
                    //logout facebook
                    FacebookLoginActivity.logout()
                }

                else if(Utils.getLoginGooglePref(this, "google" )){
                    //logout google
                    AuthUI.getInstance().signOut(this)
                    FirebaseAuth.getInstance().signOut()
                }
            }

        }
        menuItem.isChecked = true
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.nav_host_fragment),
            drawerLayout
        )
    }

    private fun initRecyclerView() {
        productListRecycleView = findViewById(R.id.recycler_view)

        productListAdapter = ProductListAdapter(
            object : ProductListAdapter.Interaction {
                override fun onItemSelected(pos: Int, product: Product) {
                    //Ir a una pagina de Detalle con un Map
                    val bundle = bundleOf(
                        "product" to product
                    )
                    position = pos
                    val intent  = Intent(this@ProductActivity, ProductDetailsActivity::class.java)
                    intent.putExtra("productBundle", bundle)
                    startActivity(intent)
                }

                override fun restoreListPosition() {
                    productLayoutManager.scrollToPosition(position)
                }
            })


        with(productListRecycleView) {
            layoutManager = GridLayoutManager(
                context,
                2,
                RecyclerView.VERTICAL,
                false
            ) //Reverse order active for view new elements on top


            LinearSnapHelper().also {
                it.attachToRecyclerView(this)
            }
            addItemDecoration(FixedMarginItemDecorator(16))
            setHasFixedSize(true)

            scrollListener = object : EndlessRecyclerViewScrollListener(productLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to the bottom of the list
                    //Create the progress indicator
                    progressIndicator = CircularProgressIndicator(
                        this@ProductActivity,
                        null
                    )
                    // add progress indicator into layout
                    parentLayout.addView(
                        progressIndicator,
                        LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    )
                    // set the progress
                    progressIndicator.progress = 40

                    Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
                        override fun run() {
                            //Call your function here
                            value += 1
                            progressCircleDeterminate.progress = value
                            if (value != 100) {
                                progressIndicator.show()
                                handler.postDelayed(this, 200)//1 sec delay
                            }
                        }

                    }, 0)

                    viewModel.updateProductsList(10, page)

                    viewModel.fetchData().observe(this@ProductActivity){
                        view!!.post(object : Runnable {
                            override fun run() {
                                progressIndicator.hide()
                                populateList(it)
                                productListAdapter.notifyItemRangeInserted(page,page*10)
                            }
                        })
                    }

                    productListRecycleView.addOnScrollListener(scrollListener)

                }
            }
        }
    }

    private fun populateList(newList: List<Product>) {
        if (productListAdapter.isPositionUpdate()) {
            productListAdapter.submitList(newList)
            if(isFirstTime){
                productListRecycleView.scrollToPosition(0)
                isFirstTime = false
            }
            else {
                productListRecycleView.scrollToPosition(newList.size-1)
            }

        } else {
            productListAdapter.submitList(newList)
        }
        productListAdapter.setPositionUpdateState(false)
    }

    private fun isValidDestination(destination: Int): Boolean {
        return destination != Navigation.findNavController(
            this,
            R.id.nav_host_fragment
        ).currentDestination!!
            .id
    }

    abstract class EndlessRecyclerViewScrollListener : RecyclerView.OnScrollListener {
        // The minimum amount of items to have below your current scroll position
        // before loading more.
        private var visibleThreshold = 5

        // The current offset index of data you have loaded
        private var currentPage = 0

        // The total number of items in the dataset after the last load
        private var previousTotalItemCount = 0

        // True if we are still waiting for the last set of data to load.
        private var loading = true

        // Sets the starting page index
        private val startingPageIndex = 0
        var mLayoutManager: RecyclerView.LayoutManager

        constructor(layoutManager: LinearLayoutManager) {
            mLayoutManager = layoutManager
        }

        constructor(layoutManager: GridLayoutManager) {
            mLayoutManager = layoutManager
            visibleThreshold = visibleThreshold * layoutManager.spanCount
        }

        constructor(layoutManager: StaggeredGridLayoutManager) {
            mLayoutManager = layoutManager
            visibleThreshold = visibleThreshold * layoutManager.spanCount
        }

        fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
            var maxSize = 0
            for (i in lastVisibleItemPositions.indices) {
                if (i == 0) {
                    maxSize = lastVisibleItemPositions[i]
                } else if (lastVisibleItemPositions[i] > maxSize) {
                    maxSize = lastVisibleItemPositions[i]
                }
            }
            return maxSize
        }

        // This happens many times a second during a scroll, so be wary of the code you place here.
        // We are given a few useful parameters to help us work out if we need to load some more data,
        // but first we check if we are waiting for the previous load to finish.
        override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
            var lastVisibleItemPosition = 0
            val totalItemCount = mLayoutManager.itemCount
            if (mLayoutManager is StaggeredGridLayoutManager) {
                val lastVisibleItemPositions =
                    (mLayoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
                // get maximum element within the list
                lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
            } else if (mLayoutManager is GridLayoutManager) {
                lastVisibleItemPosition =
                    (mLayoutManager as GridLayoutManager).findLastVisibleItemPosition()
            } else if (mLayoutManager is LinearLayoutManager) {
                lastVisibleItemPosition =
                    (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }

            // If it’s still loading, we check to see if the dataset count has
            // changed, if so we conclude it has finished loading and update the current page
            if (loading && totalItemCount > previousTotalItemCount) {
                loading = false
            // number and total item count.
                previousTotalItemCount = totalItemCount
            }

            // If it isn’t currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            // threshold should reflect how many total columns there are too
            if (!loading && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
                currentPage++
                onLoadMore(currentPage, totalItemCount, view)
                loading = true
            }
        }

        // Call whenever performing new searches
        fun resetState() {
            currentPage = startingPageIndex
            previousTotalItemCount = 0
            loading = true
        }

        // Defines the process for actually loading more data based on page
        abstract fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?)
    }

}