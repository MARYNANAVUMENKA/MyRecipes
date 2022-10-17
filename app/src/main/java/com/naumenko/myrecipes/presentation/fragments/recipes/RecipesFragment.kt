package com.naumenko.myrecipes.presentation.fragments.recipes

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.naumenko.myrecipes.R
import com.naumenko.myrecipes.domain.models.Result
import com.naumenko.myrecipes.adapters.RecipesAdapter
import com.naumenko.myrecipes.adapters.ResultActionListener
import com.naumenko.myrecipes.databinding.FragmentRecipesBinding
import com.naumenko.myrecipes.presentation.viewmodels.MainViewModel
import com.naumenko.myrecipes.presentation.viewmodels.RecipeViewModel
import com.naumenko.myrecipes.util.NetworkListener
import com.naumenko.myrecipes.util.NetworkResult
import com.naumenko.myrecipes.util.observeOnce
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RecipesFragment : Fragment(), SearchView.OnQueryTextListener {

    private val args by navArgs<RecipesFragmentArgs>()

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipeViewModel: RecipeViewModel
//    private val mAdapter by lazy { RecipesAdapter() }
    private lateinit var mAdapter: RecipesAdapter

    private lateinit var networkListener: NetworkListener

    override fun onResume() {
        super.onResume()
        if(mainViewModel.recyclerViewState != null){
            binding.recyclerview.layoutManager?.onRestoreInstanceState(mainViewModel.recyclerViewState)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recipeViewModel = ViewModelProvider(requireActivity())[RecipeViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.recipes_menu, menu)

                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@RecipesFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        mAdapter = RecipesAdapter(object : ResultActionListener {
            override fun onRecipesDetails(result: Result) {
                Log.d("RecipesFragment", result.toString())
                val action =
                    RecipesFragmentDirections.actionRecipesFragmentToDetailsActivity(result)
                findNavController().navigate(action)
            }

        })
        setupRecyclerView()


        recipeViewModel.readBackOnline.observe(viewLifecycleOwner) {
            recipeViewModel.backOnline = it
        }

        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    Log.d("NetworkListener", status.toString())
                    recipeViewModel.networkStatus = status
                    recipeViewModel.showNetworkStatus()
                    readDatabase()
                }
        }

        binding.recipesFab.setOnClickListener {
            if (recipeViewModel.networkStatus) {
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
            } else {
                recipeViewModel.showNetworkStatus()
            }
        }


        return binding.root
    }


    private fun setupRecyclerView() {

        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.adapter = mAdapter
//        showShimmerEffect()
    }



    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchApiData(query)
        }
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        return true
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty() && !args.backFromBottomSheet) {
                    Log.d("RecipesFragment", "readDatabase called!")
//                    mAdapter.recipes = database.first().foodRecipe.results
                     mAdapter.submitList(database.first().foodRecipe.results)
//                    hideShimmerEffect()
                } else {
                    requestApiData()
                }
            }
        }
    }

    private fun requestApiData() {
        Log.d("RecipesFragment", "requestApiData called!")
        mainViewModel.getRecipes(recipeViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
//                    hideShimmerEffect()
//                    mAdapter.recipes =  response.data!!.results
                    response.data?.results.let { mAdapter.submitList(it) }
                    Log.d("RecipesFragment", response.data?.results?.first().toString())
//                    response.data?.let { mAdapter.setData(it) }
                    recipeViewModel.saveMealAndDietType()
                }
                is NetworkResult.Error -> {
//                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
//                    showShimmerEffect()
                }
            }
        }
    }

    private fun searchApiData(searchQuery: String) {
//        showShimmerEffect()
        mainViewModel.searchRecipes(recipeViewModel.applySearchQuery(searchQuery))
        mainViewModel.searchedRecipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
//                    hideShimmerEffect()
                    val foodRecipe = response.data
//                    mAdapter.recipes=foodRecipe!!.results
                    foodRecipe?.results.let { mAdapter.submitList(it) }
//                    foodRecipe?.let { mAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
//                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
//                    showShimmerEffect()
                }
            }
        }
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observe(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
//                    mAdapter.recipes = database.first().foodRecipe.results
                    mAdapter.submitList(database.first().foodRecipe.results)
//                    mAdapter.setData(database.first().foodRecipe)
                }
            }
        }
    }

//    private fun showShimmerEffect() {
//        binding.shimmerFrameLayout.startShimmer()
//        binding.shimmerFrameLayout.visibility = View.VISIBLE
//        binding.recyclerview.visibility = View.GONE
//    }
//
//    private fun hideShimmerEffect() {
//        binding.shimmerFrameLayout.stopShimmer()
//        binding.shimmerFrameLayout.visibility = View.GONE
//        binding.recyclerview.visibility = View.VISIBLE
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.recyclerViewState =
            binding.recyclerview.layoutManager?.onSaveInstanceState()
        _binding = null
    }
}