package com.example.clientjetpack.Res

import com.example.clientjetpack.R


class XmlsFilesHandler {

    companion object {
        // Moved to companion object to allow static access
        val xmlResources = listOf(
            Pair("marker_info_window", R.layout.marker_info_window),
            Pair("info_window_container", R.id.info_window_container),
            Pair("R.drawable.location_arrow", R.drawable.location_arrow),

        )
    }
}
