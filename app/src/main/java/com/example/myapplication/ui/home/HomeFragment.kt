package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.utils.generateListProjects
import kotlinx.android.synthetic.main.fragment_home.*
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.myapplication.controller.SwipeControllerImpl
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Canvas
import android.widget.Toast
import com.example.myapplication.controller.SwipeController

class HomeFragment : Fragment(), SwipeController {

    override fun actionEdit(position: Int) {
        homeViewModel.apply {
            Toast.makeText(
                context,
                listProjects.value?.get(position)?.startDate,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun actionDelete(position: Int) {
        homeViewModel.apply {
            listProjects.value?.removeAt(position)
            homeAdapter?.notifyItemRemoved(position)
            homeAdapter?.notifyItemRangeChanged(position, homeAdapter?.itemCount ?: return)
        }
    }

    private lateinit var homeViewModel: HomeViewModel

    private var homeAdapter: HomeAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.button_home).setOnClickListener {
            val action = HomeFragmentDirections
                .actionHomeFragmentToHomeSecondFragment("From HomeFragment")
            NavHostFragment.findNavController(this@HomeFragment)
                .navigate(action)
        }

        homeViewModel.apply {
            listProjects.value = generateListProjects()

            homeAdapter = HomeAdapter(
                listProjects.value,
                context
            )

            val homeLayoutManager = LinearLayoutManager(context)

            recycler_project?.apply {
                layoutManager = homeLayoutManager
                adapter = homeAdapter
            }

            //Attach controller to Fragment
            val swipeController = SwipeControllerImpl()

            //
            swipeController.setController(
                swipeController = this@HomeFragment,
                context = context?: return
            )

            //
            val itemTouchHelper = ItemTouchHelper(swipeController)
            itemTouchHelper.attachToRecyclerView(recycler_project)

            //Item decoration
            recycler_project.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                    swipeController.onDraw(c)
                }
            })
        }
    }
}
