package com.lateph.godeals.retrofit.model
import android.databinding.*
import com.lateph.godeals.BR
import com.lateph.godeals.utlis.ObservableViewModel

object CreateOpportunity {
    data class Result(val name: String, val code: Int, val status: Int)

    class Body : BaseObservable(){
        var roomNeeded: ObservableInt = ObservableInt(1)
        var personPerRoom: ObservableInt = ObservableInt(1)
        var checkInDate: ObservableField<String> = ObservableField("1997-01-01")
        var checkOutDate: ObservableField<String> = ObservableField("1997-01-01")
        var notes: ObservableField<String> = ObservableField("")
        var areaIds: ObservableArrayList<String> = ObservableArrayList()
        var additionalService: ObservableArrayList<Int>  = ObservableArrayList()


        fun setArea(data: List<String>){
            areaIds.clear()
            areaIds.addAll(data)
//            notifyChange()
            notifyPropertyChanged(BR.areasText)
        }

        @Bindable
        fun getAreasText() : String {
            return areaIds.joinToString()
//            Picasso.get().load(url).error(error).into(view)
        }
    }

    data class Error(val name:String, val message: String, val data: ListError)
    data class ListError(val login: List<String>, val password: List<String>)
}