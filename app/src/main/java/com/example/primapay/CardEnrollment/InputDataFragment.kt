package com.example.primapay.CardEnrollment

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.primapay.R
import com.idemia.wa.api.Failure
import com.idemia.wa.api.WaCredentialStatus
import com.idemia.wa.api.WaPaymentScheme
import com.idemia.wa.api.WalletAgentApi
import com.idemia.wa.api.wms.*
import kotlinx.android.synthetic.main.ce_fragment_input_data.*
import java.text.SimpleDateFormat
import java.util.*




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [InputDataFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [InputDataFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class InputDataFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    val myCalendar = Calendar.getInstance()

    lateinit var walletAgentApi: WalletAgentApi
    lateinit var enrollmentService: EnrollmentService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        walletAgentApi = WalletAgentApi.getInstance(context)
        enrollmentService = walletAgentApi.getService(EnrollmentService::class.java)
    }

    private fun loading(){
        progressBar.visibility = View.VISIBLE
        activity!!.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun noLoading(){
        progressBar.visibility = View.INVISIBLE
        activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }


    fun enroll(){
        loading()
        Log.d("calendar", myCalendar.get(Calendar.DAY_OF_MONTH).toString())
        Log.d("calendar", myCalendar.get(Calendar.MONTH).toString())
        Log.d("calendar", myCalendar.get(Calendar.YEAR).toString())
        val waCardData = WaCardDataWithBirthDate(
            editNomor.text.toString().toCharArray(),
            WaCardBirthDate(myCalendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0').toCharArray(), myCalendar.get(Calendar.MONTH).toString().padStart(2, '0').toCharArray(),  myCalendar.get(Calendar.YEAR).toString().toCharArray())
        )

        val waAuthData = WaAuthData(WaAuthType.AUTH_CODE, "aae64a06de44b4fb".toCharArray())
        val enrollmentParams = WaEnrollmentParams.Builder(waCardData)
            .setAuthData(waAuthData)
            .setConsumerLanguageCode(Locale("id"))
            .setPurpose(WaTokenPurpose.QR_CODE)
            .setIin(WaIin("586160".toCharArray()))
            .setPaymentScheme(WaPaymentScheme.WISE)
            .build()

        enrollmentService.enrollCard(enrollmentParams, object : EnrollmentListener {
            override fun onCardEnrollmentStarted(list: List<String>) {
                Log.d("MyTest", "onCardEnrollmentStarted")
            }

            override fun onTokenEnrollmentStarted(s: String) {
                Log.d("MyTest", "onTokenEnrollmentStarted")
                Log.d("MyTest", s)
            }

            override fun onTokenEnrolled(waCredentialStatus: WaCredentialStatus) {
                Log.d("MyTest", "onTokenEnrolled")
                Log.d("MyTest", waCredentialStatus.name)
            }

            override fun onAcceptTnc(waTnc: WaTnc, tncCallback: TncCallback) {
                noLoading()
                Log.d("MyTest", "onAcceptTnc")
                AlertDialog.Builder(context!!)
                    .setTitle("TnC")
                    .setMessage(waTnc.content)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setNegativeButton("No", null)
                    .setPositiveButton("yes") { _, _ ->
                        tncCallback.accept()
                    }.show()
            }

            override fun onSetPin(setPinCallback: SetPinCallback) {
                Log.d("MyTest", "onSetPin")
                setPinCallback.setPin(editPin.text.toString().toCharArray())
            }

            override fun onSelectIdvOption(list: List<WaIdvOption>, selectIdvOptionCallback: SelectIdvOptionCallback) {
                Log.d("MyTest", list.toString())
                Log.d("MyTest", "startloop"+list.size.toString())
                for (i in list) {
                    Log.d("MyTest", i.hint.toString())
                    Log.d("MyTest", i.type.toString())
                    Log.d("MyTest", i.id.toString())
                }
                Log.d("MyTest", "onSelectIdvOption")
                selectIdvOptionCallback.selectIdvOption(list[1])
//                selectIdvOptionCallback.selectIdvOption()
            }

            override fun onSubmitOtp(otpAuthCallback: OtpAuthCallback) {
                otpAuthCallback.submitOtp(WaOtp("1234".toCharArray()))
                Log.d("MyTest", "onSubmitOtp")
            }

            override fun onFailure(failure: Failure) {
                Log.d("MyTest", "Failure")
                Log.d("MyTest", failure.toString())
                Log.d("MyTest", failure.exception.toString())
                Log.d("MyTest", failure.errorCode.toString())
                noLoading()
                AlertDialog.Builder(context!!)
                    .setTitle("Error")
                    .setMessage(failure.toString())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("yes") { _, _ ->
                        findNavController().navigate(R.id.toBack)
                    }.show()
            }

            override fun onComplete() {
                Log.d("MyTest", "onComplete")
                noLoading()
                Toast.makeText(context, "Complete ", Toast.LENGTH_LONG)
                findNavController().navigate(R.id.toBack)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ce_fragment_input_data, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd MMMM yy" //In which you need put here
            val sdf = SimpleDateFormat(myFormat, Locale("id"))

            editDate.setText(sdf.format(myCalendar.time))
            val nextField = editDate.focusSearch(View.FOCUS_DOWN)
            nextField?.requestFocus()
        }

        editDate.keyListener = null

        editDate.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus)
                DatePickerDialog(
                    context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show()
        }

        btnSubmitCard.setOnClickListener {
            enroll()
        }

        // set defaul callendar
        myCalendar.set(1993, 8, 6)
        val myFormat = "dd MMMM yy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale("id"))

        editDate.setText(sdf.format(myCalendar.time))

        progressBar.visibility = View.INVISIBLE

    }

    // TODO: Rename method, update argument and hook method into UI event
//    fun onButtonPressed(uri: Uri) {
//        listener?.onFragmentInteraction(uri)
//    }

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
        listener = null
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
         * @return A new instance of fragment InputDataFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InputDataFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
