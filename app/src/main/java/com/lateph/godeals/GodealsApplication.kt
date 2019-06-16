package com.lateph.godeals
import com.lateph.godeals.retrofit.model.ListArea
import com.zeugmasolutions.localehelper.LocaleAwareApplication
class GodealsApplication : LocaleAwareApplication() {
    companion object {
        var listCities = emptyList<ListArea.Data>()
    }
}