package com.soagrowers.android.tripcomputer.data;

import android.os.Bundle;

import com.soagrowers.android.tripcomputer.BuildConfig;

import java.util.ArrayList;


/**
 * Created by Ben on 03/03/14.
 * <p/>
 * Used for In-App Billing.
 * <p/>
 * <p/>
 * Response Code	Value	Description
 * BILLING_RESPONSE_RESULT_OK	0	Success
 * BILLING_RESPONSE_RESULT_USER_CANCELED	1	User pressed back or canceled a dialog
 * BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE	3	Billing API version is not supported for the type requested
 * BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE	4	Requested product is not available for purchase
 * BILLING_RESPONSE_RESULT_DEVELOPER_ERROR	5	Invalid arguments provided to the API. This error can also indicate that the application was not correctly signed or properly set up for In-app Billing in Google Play, or does not have the necessary permissions in its manifest
 * BILLING_RESPONSE_RESULT_ERROR	6	Fatal error during the API action
 * BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED	7	Failure to purchase since item is already owned
 * BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED	8	Failure to consume since item is not owned
 * <p/>
 * For info on how to test in-app billing:-
 *
 * @see "http://developer.android.com/google/play/billing/billing_testing.html"
 */
public class Skus {

    public static final String SKU_TCV1_SAVE_ALL_JOURNEYS = "tcv1_save_all_journeys";
    public static final String SKU_TCV1_DUMMY_TEST_PRODUCT = "tcv1_dummy_test_product";

    /**
     * When you make an In-app Billing request with this product ID,
     * Google Play responds as though you successfully purchased an item.
     * The response includes a JSON string, which contains fake purchase
     * information (for example, a fake order ID). In some cases, the JSON
     * string is signed and the response includes the signature so you
     * can test your signature verification implementation using these responses.
     */
    public static final String TEST_SKU_PURCHASED_ITEM = "android.test.purchased";

    /**
     * When you make an In-app Billing request with this product ID Google Play
     * responds as though the purchase was canceled. This can occur when an error
     * is encountered in the order process, such as an invalid credit card, or
     * when you cancel a user's order before it is charged.
     */
    public static final String TEST_SKU_CANCELED_ITEM = "android.test.canceled";

    /**
     * When you make an In-app Billing request with this product ID, Google Play
     * responds as though the purchase was refunded. Refunds cannot be initiated
     * through Google Play's in-app billing service. Refunds must be initiated
     * by you (the merchant). After you process a refund request through your
     * Google Wallet merchant account, a refund message is sent to your application
     * by Google Play. This occurs only when Google Play gets notification from
     * Google Wallet that a refund has been made. For more information about refunds,
     * see Handling IN_APP_NOTIFY messages and In-app Billing Pricing.
     */
    public static final String TEST_SKU_REFUNDED_ITEM = "android.test.refunded";

    /**
     * When you make an In-app Billing request with this product ID, Google Play
     * responds as though the item being purchased was not listed in your
     * application's product list.
     */
    public static final String TEST_SKU_UNAVAILABLE_ITEM = "android.test.item_unavailable";

    /**
     * The product ID for the product.
     */
    public static final String SKU_DETAILS_KEY_ID = "productId";

    /**
     * Value must be “inapp” for an in-app product or "subs" for subscriptions.
     */
    public static final String SKU_DETAILS_KEY_TYPE = "type";

    /**
     * Formatted price of the item, including its currency sign. The price does not include tax.
     */
    public static final String SKU_DETAILS_KEY_PRICE = "price";

    /**
     * Title of the product.
     */
    public static final String SKU_DETAILS_KEY_TITLE = "title";

    /**
     * Description of the product.
     */
    public static final String SKU_DETAILS_KEY_DESC = "description";

    /**
     * Used when querying to differentiate Products.
     */
    public static final String SKU_TYPE_INAPP = "inapp";

    /**
     * Used when querying to differentiate Subscriptions.
     */
    public static final String SKU_TYPE_SUBS = "subs";


    public static Bundle getSkuListAsBundle() {

        // Create a List containing the product SKU's we want (up to 20).
        ArrayList<String> skuList = new ArrayList<String>();

        if (BuildConfig.DEBUG) {
            // Add DEBUG Sku's
            skuList.add(TEST_SKU_PURCHASED_ITEM);
        } else {
            // Add REAL SKU's
            skuList.add(Skus.SKU_TCV1_DUMMY_TEST_PRODUCT);
        }

        // Create the bundle
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
        return querySkus;
    }
}
