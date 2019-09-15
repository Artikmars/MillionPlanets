package com.artamonov.millionplanets;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

/** A simple {@link Fragment} subclass. */
public class MainOptionsFragment extends Fragment {

    OnButtonPressedListener mCallback;
    private Bundle bundle;

    public MainOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        Log.i("myLogs", "MainFragment onAttach: ");
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnButtonPressedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    context.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onDestroyView() {
        Log.i("myLogs", "MainFragment onDestroyView: ");
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("myLogs", "MainFragment onDetach: ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("myLogs", "MainFragment onCreate: ");
        if (getArguments() != null) {
            bundle = getArguments();
            Log.i("myLogs", "bundle: " + bundle);
        } else {
            Log.i("myLogs", "bundle is null: ");
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("myLogs", "MainFragment onDetach: ");

        View view = inflater.inflate(R.layout.fragment_main_options, container, false);
        final Button button = view.findViewById(R.id.scan);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mCallback.onScanPressed();

                        /*
                        if (bundle != null) {
                            Log.i("myLogs", "bundle is Not NULL");
                            Fragment scanResultFragment = new ScanResultFragment();
                            scanResultFragment.setArguments(bundle);
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            //   Fragment fragment = fm.findFragmentById(R.id.scan_result_fragment);
                            fm.beginTransaction().replace(R.id.main_options_fragment, scanResultFragment).commit();
                        } else {
                            Log.i("myLogs", "bundle is NULL");
                            Fragment scanResultFragment = new ScanResultFragment();
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                          //  Fragment scanResultFragment = fm.findFragmentById(R.id.sc);
                            fm.beginTransaction().replace(R.id.main_options_fragment, scanResultFragment).commit();
                        }*/

                    }
                });
        return view;
    }

    public void onScan(View view) {
        mCallback.onScanPressed();
    }

    public interface OnButtonPressedListener {
        public void onScanPressed();
    }
}
