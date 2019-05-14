package com.example.primapay.CardEnrollment;

import android.content.Context;
import android.util.Log;
import com.idemia.wa.api.*;
import com.idemia.wa.api.qr.*;
import com.idemia.wa.api.qr.consumer.QrConsumerTransactionService;
import com.idemia.wa.api.qr.merchant.WaQrPimType;
import com.idemia.wa.api.wallet.WalletService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class test {
    protected void trxxxx(Context context, JSONObject arg){
        try {
            JSONObject merchantAccountInformation = hasil.getJSONObject("merchantAccountInformation");
            JSONObject p62 = merchantAccountInformation.getJSONObject("p62");
            WaQrIndonesiaAdditionalDataFieldTemplate waIndo = build62(p62);
            callbackContext.success("horay");
        } catch (Exception e) {
            e.getMessage()
            callbackContext.error(e.getMessage());
            //TODO: handle exception
        }
    }
    protected void trx(Context context, JSONArray arg){
//        WaQrMerchantData merchantData = new WaQrMerchantData.Builder()..build();
        WaQrIndonesiaAdditionalDataFieldTemplate.Builder wqidftbuilder = new WaQrIndonesiaAdditionalDataFieldTemplate.Builder();
        try {
            JSONObject p62 = arg.getJSONObject(0);
            JSONArray datas = p62.getJSONArray("data");
            JSONObject data = datas.getJSONObject(0);
            Iterator< ? > keys = p62.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                if(key == "billNumber"){
                    wqidftbuilder.billNumber(p62.getString(key));
                }
                else if(key == "mobileNumber"){
                    wqidftbuilder.mobileNumber(p62.getString(key));
                }
                else if(key == "storeLabel"){
                    wqidftbuilder.storeLabel(p62.getString(key));
                }
                else if(key == "loyaltyNumber"){
                    wqidftbuilder.loyaltyNumber(p62.getString(key));
                }
                else if(key == "referenceLabel"){
                    wqidftbuilder.referenceLabel(p62.getString(key));
                }
                else if(key == "customerLabel"){
                    wqidftbuilder.customerLabel(p62.getString(key));
                }
                else if(key == "terminalLabel"){
                    wqidftbuilder.terminalLabel(p62.getString(key));
                }
                else if(key == "transactionPurpose"){
                    wqidftbuilder.transactionPurpose(p62.getString(key));
                }
                else if(key == "additionalConsumerDataRequest"){
                    wqidftbuilder.additionalConsumerDataRequest(p62.getString(key));
                }
                // @Todo g jelas crypto label
            }
        } catch (JSONException e) {
            Log.d("trx", e.getMessage());
            e.printStackTrace();
        }

        try {
            JSONObject merchantAccountInformation = arg.getJSONObject(0);
            JSONArray data = merchantAccountInformation.getJSONArray("data");
            JSONObject p26_45 = data.getJSONObject(0);

            WaQrMerchantData merchantData = new WaQrMerchantData.Builder()
                    .mPan(p26_45.getString(""))
                        .merchantId("000885000224149").merchantName("RETAIL UAT").merchantCity("JAKARTA").merchantCountryCode("ID").merchantCategoryCode("5411").expirationDate("0421")
                    .build();

            WaQrIndonesiaAdditionalDataFieldTemplate waQrIndonesiaAdditionalDataFieldTemplate = wqidftbuilder.build();
        }
        catch (Exception e){

        }

    }

    private WaQrTransactionData buildMerchantData(JSONObject hasil, WaQrIndonesiaAdditionalDataFieldTemplate indo, JSONObject p62){
        WaQrTransactionData.Builder waTransactionBuilder = new WaQrTransactionData.Builder();
        try {
            waTransactionBuilder.amount(hasil.getInt("ammount"));
            waTransactionBuilder.currency(hasil.getString("currency"));
            // @Todo check qr type
            waTransactionBuilder.waQrPimType(WaQrPimType.DYNAMIC);
            // @Todo add localDatetime
            SimpleDateFormat dateF = new SimpleDateFormat("MMDDhhmmss");
            waTransactionBuilder.localDateTime(dateF.format(dateF));
            // @Todo Shound be removed
            waTransactionBuilder.processingCode(
                    BigInteger.valueOf(260000));
            waTransactionBuilder.merchantTerminalId(String.format("%-16s", p62.getString("terminalLabel")));
            // @Todo check UKE and soon
            waTransactionBuilder.waQrAdditionalData(new WaQrIndonesiaAdditionalData("asd",WaQrIndonesiaMerchantCriteria.UKE));
            waTransactionBuilder.waQrAdditionalDataNational(new WaQrIndonesiaAdditionalDataNational("90-210", indo));
        } catch (Exception e) {
            Log.d("trx", e.getMessage());
            e.printStackTrace();
        }
        return waTransactionBuilder.build();
    }

    private trx(Context context, WaQrMerchantData merchantData, WaQrTransactionData transactionData, int index){
        WalletAgentApi walletAgentApi = WalletAgentApi.getInstance(context);
        WalletService wallettService = walletAgentApi.getService(WalletService.class);
        List<WaCard> cards = wallettService.getCards();
        WaCard card = cards.get(index);
        List<WaToken> tokens = wallettService.getTokens(card.getId());

        WaPaymentInput paymentInput = new WaPaymentInput(tokens.get(0).getId());
        QrConsumerTransactionService qrTransactionService =
                WalletAgentApi.getInstance(context).getService(QrConsumerTransactionService.class);

        try{
            WaQrTransaction waQrTransaction = qrTransactionService.processQrTransaction(merchantData, transactionData, paymentInput);

            JSONObject jsonCard = new JSONObject();
            jsonCard.put("transactionId", waQrTransaction.getTransactionId().getTransactionId());
            jsonCard.put("customerData", waQrTransaction.getCustomerData());
            jsonCard.put("invoiceNumber", waQrTransaction.getInvoiceNumber());
            jsonCard.put("status", waQrTransaction.getStatus().toString());
        }
        catch ( Exception e){
            Log.d("pay", e.toString());
        }
    }

    private WaQrTransactionData buildTrxData(JSONObject hasil, WaQrIndonesiaAdditionalDataFieldTemplate indo, JSONObject p62){
        WaQrTransactionData.Builder waTransactionBuilder = new WaQrTransactionData.Builder();
        try {
            waTransactionBuilder.amount(hasil.getInt("ammount"));
            waTransactionBuilder.currency(hasil.getString("currency"));
            // @Todo check qr type
            waTransactionBuilder.waQrPimType(WaQrPimType.DYNAMIC);
            // @Todo add localDatetime
            SimpleDateFormat outputFormat  = new SimpleDateFormat("MMDDhhmmss");
            Date date = new Date(System.currentTimeMillis());
            waTransactionBuilder.localDateTime(outputFormat.format(date));
            // @Todo Shound be removed
            waTransactionBuilder.processingCode(
                    BigInteger.valueOf(260000));
            waTransactionBuilder.merchantTerminalId(String.format(p62.getString("terminalLabel")));
            // @Todo check UKE and soon
            waTransactionBuilder.waQrAdditionalData(new WaQrIndonesiaAdditionalData("asd",WaQrIndonesiaMerchantCriteria.UKE));
            waTransactionBuilder.waQrAdditionalDataNational(new WaQrIndonesiaAdditionalDataNational("90-210", indo));
            Log.d("trx", "success");
        } catch (Exception e) {
            Log.d("trx", e.getMessage());
            e.printStackTrace();
        }
        return waTransactionBuilder.build();
    }
}
