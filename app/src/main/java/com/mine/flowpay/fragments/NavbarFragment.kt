package com.mine.flowpay.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.mine.flowpay.HomeActivity
import com.mine.flowpay.LikesActivity
import com.mine.flowpay.ProfileActivity
import com.mine.flowpay.R
import com.mine.flowpay.SearchActivity

class NavbarFragment : Fragment() {

    private lateinit var homeNavItem: LinearLayout
    private lateinit var searchNavItem: LinearLayout
    private lateinit var likesNavItem: LinearLayout
    private lateinit var profileNavItem: LinearLayout

    private lateinit var homeIcon: ImageView
    private lateinit var searchIcon: ImageView
    private lateinit var likesIcon: ImageView
    private lateinit var profileIcon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_navbar, container, false)

        // Initialize views
        homeNavItem = view.findViewById(R.id.homeNavItem)
        searchNavItem = view.findViewById(R.id.searchNavItem)
        likesNavItem = view.findViewById(R.id.likesNavItem)
        profileNavItem = view.findViewById(R.id.profileNavItem)

        homeIcon = view.findViewById(R.id.homeIcon)
        searchIcon = view.findViewById(R.id.searchIcon)
        likesIcon = view.findViewById(R.id.likesIcon)
        profileIcon = view.findViewById(R.id.profileIcon)

        // Set click listeners
        homeNavItem.setOnClickListener {
            if (activity !is HomeActivity) {
                val intent = Intent(activity, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                activity?.finish()
                activity?.overridePendingTransition(0, 0)
            }
        }

        searchNavItem.setOnClickListener {
            if (activity !is SearchActivity) {
                val intent = Intent(activity, SearchActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                activity?.finish()
                activity?.overridePendingTransition(0, 0)
            }
        }

        likesNavItem.setOnClickListener {
            if (activity !is LikesActivity) {
                val intent = Intent(activity, LikesActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                activity?.finish()
                activity?.overridePendingTransition(0, 0)
            }
        }

        profileNavItem.setOnClickListener {
            if (activity !is ProfileActivity) {
                val intent = Intent(activity, ProfileActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                activity?.finish()
                activity?.overridePendingTransition(0, 0)
            }
        }

        // Update icons based on current activity
        updateIcons()

        return view
    }

    private fun updateIcons() {
        // Reset all icons to default
        homeIcon.setImageResource(R.drawable.ic_home)
        searchIcon.setImageResource(R.drawable.ic_search)
        likesIcon.setImageResource(R.drawable.ic_heart)
        profileIcon.setImageResource(R.drawable.ic_profile)

        // Set the appropriate icon to green based on current activity
        when (activity) {
            is HomeActivity -> homeIcon.setImageResource(R.drawable.ic_home_green)
            is SearchActivity -> searchIcon.setImageResource(R.drawable.ic_search_green)
            is LikesActivity -> likesIcon.setImageResource(R.drawable.ic_heart_green)
            is ProfileActivity -> profileIcon.setImageResource(R.drawable.ic_profile_green)
        }
    }
}
