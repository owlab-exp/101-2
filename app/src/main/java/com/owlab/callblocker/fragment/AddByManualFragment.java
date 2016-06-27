package com.owlab.callblocker.fragment;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.owlab.callblocker.CONS;
import com.owlab.callblocker.MainActivity;
import com.owlab.callblocker.R;
import com.owlab.callblocker.contentprovider.CallBlockerDb;
import com.owlab.callblocker.contentprovider.CallBlockerDbHelper;
import com.owlab.callblocker.contentprovider.CallBlockerProvider;

/**
 * Created by ernest on 6/26/16.
 */
public class AddByManualFragment extends Fragment {
    public static final String TAG = AddByManualFragment.class.getSimpleName();

    private EditText phoneNumberET;
    private EditText displayNameET;
    RadioGroup matchMethodRG;
    private FloatingActionButton doneFab;

    Animation rotateForwardAppear;
    Animation rotateBackwardDisappear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.add_by_manual_layout, container, false);

        rotateForwardAppear = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_forward_appear);
        rotateBackwardDisappear = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_backward_disappear);

        matchMethodRG = (RadioGroup) fragmentView.findViewById(R.id.radio_group_match_method);
        phoneNumberET = (EditText) fragmentView.findViewById(R.id.add_by_manual_phone_number);
        phoneNumberET.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        displayNameET = (EditText) fragmentView.findViewById(R.id.add_by_manual_display_name);
        doneFab = (FloatingActionButton) fragmentView.findViewById(R.id.fab_done);

        doneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phoneNumberET.getText().toString().replaceAll("[^\\d]", "");
                String displayName = displayNameET.getText().toString();
                int matchMethodInt = 0;
                switch (matchMethodRG.getCheckedRadioButtonId()) {
                    case R.id.match_method_exact:
                        matchMethodInt = CONS.MATCH_METHOD_EXACT;
                        break;
                    case R.id.match_method_starts_with:
                        matchMethodInt = CONS.MATCH_METHOD_STARTS_WITH;
                        break;
                    default:
                }
                Log.d(TAG, ">>>>> data: (" + phoneNumber + ", " + displayName + ", " + matchMethodInt + ")");

                if(phoneNumber.isEmpty()) {
                    Toast.makeText(getActivity(), "Empty number", Toast.LENGTH_SHORT).show();
                    return;
                }
                CallBlockerDbHelper callBlockerDbHelper = new CallBlockerDbHelper(getActivity());
                if(callBlockerDbHelper.isBlockedNumber(phoneNumber)) {
                    //already exists in the table
                    Toast.makeText(getActivity(), "The number already exists", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    ContentValues values = new ContentValues();
                    values.put(CallBlockerDb.COLS_BLOCKED_NUMBER.PHONE_NUMBER, phoneNumber);
                    values.put(CallBlockerDb.COLS_BLOCKED_NUMBER.DISPLAY_NAME, displayName);
                    values.put(CallBlockerDb.COLS_BLOCKED_NUMBER.MATCH_METHOD, matchMethodInt);

                    Uri rowUri = getActivity().getContentResolver().insert(CallBlockerProvider.BLOCKED_NUMBER_URI, values);

                    if(Long.parseLong(rowUri.getLastPathSegment()) > 0) {
                        Toast.makeText(getActivity(), phoneNumber + " added", Toast.LENGTH_SHORT).show();
                        getFragmentManager().popBackStack(ViewPagerContainerFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    } else {
                        Toast.makeText(getActivity(), phoneNumber + " add failed, duplicate?", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        matchMethodRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch(checkedId) {
                    case R.id.match_method_exact:
                        Log.d(TAG, ">>>>> radio button: exact");
                        break;
                    case R.id.match_method_starts_with:
                        Log.d(TAG, ">>>>> radio button: startWith");
                        break;
                }
            }
        });

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        doneFab.startAnimation(rotateForwardAppear);
        MainActivity mainActivity = (MainActivity)getActivity();
        ActionBar mainActionBar = mainActivity.getSupportActionBar();
        if(mainActionBar != null) {
            mainActionBar.setTitle(R.string.title_add_by_manual);
            mainActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        doneFab.startAnimation(rotateBackwardDisappear);
    }
}