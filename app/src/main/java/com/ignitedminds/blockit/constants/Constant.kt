package com.ignitedminds.blockit.constants

import android.util.Log

class Constant {

    companion object {

        enum class LinksTypes{
            VIDEO,
            ARTICLE
        }

        val whitelistedSystemPackage = listOf("com.android.chrome", "com.google.android.youtube")


        fun logger(input: String) {
            Log.d("BlockIt", input)
        }

    }
}