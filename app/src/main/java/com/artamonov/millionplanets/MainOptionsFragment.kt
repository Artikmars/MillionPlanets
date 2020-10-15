package com.artamonov.millionplanets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

/** A simple [Fragment] subclass.  */
class MainOptionsFragment : Fragment() {
    var mCallback: OnButtonPressedListener? = null
    private var bundle: Bundle? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        mCallback = try {
            context as OnButtonPressedListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                    "$context must implement OnHeadlineSelectedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            bundle = arguments
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_options, container, false)
        val button = view.findViewById<Button>(R.id.scan)
        button.setOnClickListener {
            mCallback!!.onScanPressed()

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
        return view
    }

    fun onScan(view: View?) {
        mCallback!!.onScanPressed()
    }

    interface OnButtonPressedListener {
        fun onScanPressed()
    }
}