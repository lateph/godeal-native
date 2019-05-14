package com.example.primapay.CardEnrollment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.primapay.R
import com.idemia.wa.api.WaCard
import com.idemia.wa.api.WaToken
import com.idemia.wa.api.WalletAgentApi
import com.idemia.wa.api.lifecycle.LifecycleService
import com.idemia.wa.api.wallet.WalletService
import kotlinx.android.synthetic.main.ce_fragment_card_detail.*
import com.idemia.wa.api.lifecycle.LifecycleChangeListener
import com.idemia.wa.api.Failure
import com.idemia.wa.api.qr.consumer.QrConsumerTransactionService
import com.example.primapay.MainActivity
import com.example.primapay.ScanActivity
import com.idemia.wa.api.qr.*
import com.idemia.wa.api.qr.merchant.WaQrPimType
import android.os.StrictMode
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CardDetail.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CardDetail.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CardDetail : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var walletAgentApi: WalletAgentApi
    lateinit var wallettService: WalletService
    lateinit var lifecycleService: LifecycleService
    lateinit var tokens: List<WaToken>
    var cards :List<WaCard>  = emptyList()
    lateinit var card: WaCard
//    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        walletAgentApi = WalletAgentApi.getInstance(context)
        wallettService = walletAgentApi.getService(WalletService::class.java)
        lifecycleService = walletAgentApi.getService(LifecycleService::class.java)


        cards = wallettService.getCards()
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ce_fragment_card_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        btnTrx.setOnClickListener {
//            val intent = Intent(context, ScanActivity::class.java)
//            startActivityForResult(intent, 1)
            val waQrIndonesiaAdditionalDataFieldTemplate = WaQrIndonesiaAdditionalDataFieldTemplate.Builder()
                .billNumber("100020")
                .mobileNumber("825516100020")
                .storeLabel("some-store-label")
                .loyaltyNumber("some-loyalty-number")
                .referenceLabel("some-reference-label")
                .customerLabel("some-customer-label")
                .terminalLabel("AI912364")
                .transactionPurpose("some-transaction-purpose")
                .additionalConsumerDataRequest("some-additionalConsumerDataRequest")
                .cryptogram("merchant-cryptogram")
                .build()

            val token = tokens.elementAtOrNull(0)?.credentialStatus.toString()
            val merchantData = WaQrMerchantData.Builder()
                .mPan("9360001430002241492")
                .merchantId("000885000224149").merchantName("RETAIL UAT").merchantCity("JAKARTA").merchantCountryCode("ID").merchantCategoryCode("5411").expirationDate("0421")
                .build()
            val dateF = SimpleDateFormat("MMDDhhmmss")
            val date = Date()
            val transactionData= WaQrTransactionData.Builder().amount(2033).currency("360").waQrPimType(WaQrPimType.DYNAMIC).localDateTime("1910291527").processingCode(
                BigInteger.valueOf(260000))
                .merchantTerminalId(String.format("%-16s", "AI912364"))
                .waQrAdditionalData(WaQrIndonesiaAdditionalData("asd",WaQrIndonesiaMerchantCriteria.UKE))
                .waQrAdditionalDataNational(WaQrIndonesiaAdditionalDataNational("90-210", waQrIndonesiaAdditionalDataFieldTemplate)).build()
            val paymentInput = WaPaymentInput(tokens.get(0).id)
            val qrTransactionService =
                WalletAgentApi.getInstance(context).getService(QrConsumerTransactionService::class.java)

            try{
                val waQrTransaction = qrTransactionService.processQrTransaction(merchantData, transactionData, paymentInput)

                txttransactionId.text = waQrTransaction.transactionId.transactionId
                txtcustomerData.text = waQrTransaction.customerData
                txtinvoiceNumber.text = waQrTransaction.invoiceNumber
                txtstatus.text = waQrTransaction.status.toString()
            }
            catch (e: Exception){
                Log.d("pay", e.toString())
            }


        }
        super.onActivityCreated(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                val result = data.getStringExtra("result")
                Log.d("QR", result)
                txtBarcode.text = result

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    fun loadStatus(){
        tokens = wallettService.getTokens(card.id)

        textView22.text = tokens.elementAtOrNull(0)?.credentialStatus.toString()

        textView26.text = tokens.elementAtOrNull(0)?.status.toString()

        textView24.text = tokens.size.toString()

        cardId.text = card.id.value
    }

    fun suspend(){
        tokens = wallettService.getTokens(card.id)
        val token = tokens[0]
        lifecycleService.suspend(token.id, object : LifecycleChangeListener {
            override fun onFailure(failure: Failure) {
                Log.d("life", failure.exception.message)
                Toast.makeText(context, failure.exception.message, Toast.LENGTH_LONG).show()
            }
            override fun onSuccess() {
                Toast.makeText(context, "success", Toast.LENGTH_LONG).show()
                loadStatus()
            }
        })
    }

    fun resumption(){
        tokens = wallettService.getTokens(card.id)
        val token = tokens[0]
        lifecycleService.resume(token.id, object : LifecycleChangeListener {
            override fun onFailure(failure: Failure) {
                Log.d("life", failure.exception.message)
                Toast.makeText(context, failure.exception.message, Toast.LENGTH_LONG).show()
            }
            override fun onSuccess() {
                Toast.makeText(context, "success", Toast.LENGTH_LONG).show()
                loadStatus()
            }
        })
    }

    fun deletion(){
        tokens = wallettService.getTokens(card.id)
        val token = tokens[0]
        lifecycleService.delete(token.id, object : LifecycleChangeListener {
            override fun onFailure(failure: Failure) {
                Log.d("life", failure.exception.message)
                Toast.makeText(context, failure.exception.message, Toast.LENGTH_LONG).show()
            }
            override fun onSuccess() {
                Toast.makeText(context, "success", Toast.LENGTH_LONG).show()
                loadStatus()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        card = cards.find {
            it.id.value == arguments?.getString("id")
        }!!

        loadStatus()

        btnDelete.setOnClickListener {
            deletion()
        }

        btnResume.setOnClickListener {
            resumption()
        }

        btnSuspend.setOnClickListener {
            suspend()
        }

        super.onViewCreated(view, savedInstanceState)
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
         * @return A new instance of fragment CardDetail.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CardDetail().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
