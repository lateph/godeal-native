package com.example.primapay.CardEnrollment

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.primapay.R
import com.example.primapay.adapter.ListCardAdapter
import com.google.gson.GsonBuilder
import com.idemia.wa.api.wallet.WalletService
import com.idemia.wa.api.wms.*
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.ce_fragment_list_card.*
import com.idemia.wa.api.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ListCardFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ListCardFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ListCardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
//    private var listener: OnFragmentInteractionListener? = null
    lateinit var walletAgentApi: WalletAgentApi
    lateinit var pairingService: PairingService
    lateinit var wallettService: WalletService
    lateinit var resourceService: ResourcesService
    lateinit var rxPermissions: RxPermissions
    private val toppings = arrayOf("FINGERPRINT", "PIN", "LOCAL")

    var cards :List<WaCard>  = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        walletAgentApi = WalletAgentApi.getInstance(context)
        pairingService = walletAgentApi.getService(PairingService::class.java)
        wallettService = walletAgentApi.getService(WalletService::class.java)
        resourceService = walletAgentApi.getService(ResourcesService::class.java)
//        cards = wallettService.getCards()
        val gson = GsonBuilder().setPrettyPrinting().create()
        Log.d("list card", gson.toJson(cards))

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rxPermissions = RxPermissions(activity!!)
        //        rxPermissions = RxPermissions(activity!!) // where this is an Activity instance // Must be done during an initialization phase like onCreate

        if (pairingService.isPaired) {
            statusPair.text = "Pair True"
            statusPair2.text = "Pair True"
        } else {
            statusPair.text = "Pair False"
            statusPair2.text = "Pair False"
        }
        pairBtn.setOnClickListener {
            rxPermissions.request(Manifest.permission.READ_PHONE_STATE).subscribe { granted ->
                if (granted) { // Always true pre-M
                    if(pairingService.isPaired){
                        unPairDevice();
                    }
                    else{
                        pairDevice();
                    }

                } else {
                    // Oups permission denied
                }
            }
        }
        pairBtn2.setOnClickListener {
            rxPermissions.request(Manifest.permission.READ_PHONE_STATE).subscribe { granted ->
                if (granted) { // Always true pre-M
                    if(pairingService.isPaired){
                        unPairDevice();
                    }
                    else{
                        pairDevice();
                    }

                } else {
                    // Oups permission denied
                }
            }
        }
        btnPay.setOnClickListener {
            if(!cards.isEmpty()){
                val cardId = cards[0].id
                val walletService = walletAgentApi.getService(WalletService::class.java)
                val tokens = walletService.getTokens(cardId)
//                val nfcPaymentService = walletAgentApi.getService(
//                    NfcPaymentService::class.java
//                )
//                val waPaymentInput = WaPaymentInput.Builder(tokens[0].id).build()
//                nfcPaymentService.preparePayment(waPaymentInput, object : PaymentListener {
//                    override fun onFailure(failure: Failure) {
//                        Log.d("payment", failure.exception.message)
//                    }
//                    override fun onPaymentStatus(paymentStatus: WaPaymentStatus) {
//                        Log.d("payment", paymentStatus.result.code.toString())
//                    }
//                })
            }
        }
        btnTambahKartu.setOnClickListener {
            it.findNavController().navigate(com.example.primapay.R.id.toSelectCardType)
        }

//        val toolbar = activity?.findViewById(R.id.toolbar) as Toolbar
//        toolbar.inflateMenu(R.menu.menu_add)
        val toolbarTitle = activity?.findViewById(com.example.primapay.R.id.toolbar_title) as TextView
        toolbarTitle?.text = "Pilih Kartu"

//        resourceService.getResource("V2lzZTs5NDhCMTE5Njg3Nzc4RTBCMUNCNDE0MTE1NTQzNEI0QzU3NEE4RDFCQjc5OTE2QjBFRkJGODZFQTNDMzBGQzRC", object : GetResourceListener{
//            override fun onCompleted(p0: WaResource?) {
//                val bm = BitmapFactory.decodeByteArray(p0?.data, 0, p0!!.data.size)
//                val dm = DisplayMetrics()

//                activity!!.windowManager.defaultDisplay.getMetrics(dm)
//                imageView4.minimumHeight = dm.heightPixels
//                imageView4.minimumWidth = dm.widthPixels
//                imageView4.setImageBitmap(bm)
//            }
//
//            override fun onError(p0: Failure?) {
//                Log.d("error", p0.toString())
//            }
//        })

        val resource = WaResource(WaResourceType.IMAGE_PNG, "V2lzZTs5NDhCMTE5Njg3Nzc4RTBCMUNCNDE0MTE1NTQzNEI0QzU3NEE4RDFCQjc5OTE2QjBFRkJGODZFQTNDMzBGQzRC".toByteArray())
        Log.d("data image ? : ",resource.data.size.toString())

    }

    override fun onResume() {
        super.onResume()
        cards = wallettService.getCards()
        val total = cards.size
        Log.d("total", cards.size.toString())
        textTotalCard.text = "Kartu Anda ($total)"
        if(total == 0){
            cardEmptylayout.visibility = View.VISIBLE
            layoutListViewCard.visibility = View.GONE
        }
        else{
            layoutListViewCard.visibility = View.VISIBLE
            cardEmptylayout.visibility = View.GONE
        }
        listViewCard.layoutManager = LinearLayoutManager(context)
        listViewCard.adapter = ListCardAdapter(cards, context!!, resourceService){
//            Toast.makeText()
            val bundle = Bundle()
            bundle.putString("id", it.id.value)
            findNavController().navigate(R.id.toDetail, bundle)
        }
        listViewCard.setFocusable(false)

    }
    protected fun pairDevice() {
        Log.d("MyTest", "pair devices")
        if (pairingService.isPaired) {
            Log.d("MyTest", "paired")
        } else {
            Log.d("MyTest", "Not Paired")
            pairingService.pairDevice(object : PairingListener {
                override fun onSelectCdCvmType(waCdCvmTypes: Set<WaCdCvmType>, callback: SelectCdCvmCallback) {
                    callback.selectCdCvmType(WaCdCvmType.PIN)
                }

                override fun onSetupPin(callback: SetPinCallback) {
                    callback.skip()
                }

                override fun onFailure(failure: Failure) {
                    Log.d("MyTest", "eror" + failure.exception.message)
                }

                override fun onComplete() {
                    statusPair.text = "Pair: True"
                    statusPair2.text = "Pair: True"
                    Log.d("MyTest", " complete pairing devices")
                }
            })
        }

    }

    protected fun unPairDevice() {
        pairingService.unpairDevice(object : UnpairingListener {
            override fun onFailure(failure: Failure) {
                Log.d("Fail", failure.exception.message)
            }

            override fun onComplete() {
                Log.d("Success", "success unpair device")
                statusPair.text = "Pair: False"
                statusPair2.text = "Pair: False"
            }
        })
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.ce_fragment_list_card, container, false)
        setHasOptionsMenu(true)
        return v
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if(cards.size > 0){
            inflater?.inflate(R.menu.menu_add, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_menu_add -> {
//            this.getV
            view!!.findNavController().navigate(R.id.toSelectCardType)
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
//        listener?.onFragmentInteraction(uri)
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
//    }

    override fun onDetach() {
        super.onDetach()
//        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListCardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListCardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
