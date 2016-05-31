package com.soagrowers.android.tripcomputer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import com.soagrowers.android.utils.AndroidUtils;
import com.soagrowers.android.utils.Log;

import java.text.DecimalFormat;


/**
 * Created by Ben on 16/12/13.
 */
public class DistanceChargePreferenceDialog extends DialogPreference {

    private static final String TAG = DistanceChargePreferenceDialog.class.getSimpleName();

    private Log log;
    private NumberPicker poundsPicker;
    private NumberPicker shillingsPicker;
    private NumberPicker pencePicker;
    private DecimalFormat rateFormatter;

    private String mSelectedValue;

    /**
     * Constructor. Builds the Dialogue basics (like setting the POS & NEG buttons.
     *
     * @param context
     * @param attrs
     */

    public DistanceChargePreferenceDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        log = Log.getInstance(context);
        setDialogLayoutResource(R.layout.distancecharge_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogIcon(null);
        rateFormatter = new DecimalFormat(AndroidUtils.getInstance(context).getString(R.string.fixed_rate_format));
        rateFormatter.applyPattern(AndroidUtils.getInstance(context).getString(R.string.fixed_rate_format));
    }


    @SuppressWarnings("all")
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        String price = this.mSelectedValue;
        log.v(TAG, "Current price: £" + price);
        int dotIndex = price.indexOf(".");
        int poundValue = Integer.valueOf(price.substring(0, dotIndex)).intValue();
        int shillingsValue = Integer.valueOf(price.substring(dotIndex + 1, dotIndex + 2)).intValue();
        int penceValue = Integer.valueOf(price.substring(dotIndex + 2)).intValue();

        poundsPicker = (NumberPicker) view.findViewById(R.id.numberpicker_pounds);
        poundsPicker.setMinValue(0);
        poundsPicker.setMaxValue(99);
        poundsPicker.setValue(poundValue);
        poundsPicker.setWrapSelectorWheel(false);

        shillingsPicker = (NumberPicker) view.findViewById(R.id.numberpicker_shillings);
        shillingsPicker.setMinValue(0);
        shillingsPicker.setMaxValue(9);
        shillingsPicker.setValue(shillingsValue);
        shillingsPicker.setWrapSelectorWheel(false);

        pencePicker = (NumberPicker) view.findViewById(R.id.numberpicker_pence);
        pencePicker.setMinValue(0);
        pencePicker.setMaxValue(9);
        pencePicker.setValue(penceValue);
        pencePicker.setWrapSelectorWheel(false);
    }

    @SuppressWarnings("all")
    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);

        //If the user clicked 'OK'
        if (which == DialogInterface.BUTTON_POSITIVE) {
            int pounds = poundsPicker.getValue() * 100;
            int shillings = shillingsPicker.getValue() * 10;
            int pence = pencePicker.getValue();
            float value = (pounds + shillings + pence);
            mSelectedValue = rateFormatter.format(value / 100);
            log.d(TAG, "Chosen unit charge value is now: £" + this.mSelectedValue);
        }
    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // When the user selects "OK", persist the new value
        if (positiveResult) {
            //persist the value of the current setting on exit...
            super.callChangeListener(mSelectedValue);
            super.persistString(mSelectedValue);
            log.d(TAG, "New unit charge value has been persisted in SharedPrefs: " + mSelectedValue);
        }
    }


    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {

        // Restore existing state
        if (restorePersistedValue) {
            log.v(TAG, "Getting the currently persisted CHARGE PER UNIT");
            this.mSelectedValue = this.getPersistedString(AndroidUtils.getInstance(getContext()).getString(R.string.charge_per_unit));
        } else {
            // Set the default based on the XML attribute...
            if (null != defaultValue) {
                log.v(TAG, "Persisting the default CHARGE PER UNIT (based on the XML's 'default' attribute)");
                log.v(TAG, "Default CHARGE value given was: " + defaultValue);
                this.mSelectedValue = (String) defaultValue;
            } else {
                log.v(TAG, "The XML's 'default' attribute for CHARGE PER UNIT was missing or null");
                String charge = AndroidUtils.getInstance(getContext()).getString(R.string.charge_per_unit);
                this.mSelectedValue = charge;
            }

            //Setting the initial value.
            log.i(TAG, "Initialising the CHARGE setting's value to: " + this.mSelectedValue);
            persistString(this.mSelectedValue);
        }
    }


    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        String defaultValue = AndroidUtils.getInstance(getContext()).getString(R.string.charge_per_unit);
        this.mSelectedValue = defaultValue;
        return a.getString(index);
    }

}
