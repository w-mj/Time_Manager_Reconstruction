package wesayallright.timemanager.surface;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import wesayallright.timemanager.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Me.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Me#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Me extends Fragment {

    ArrayList<String> functions = new ArrayList<String>();

    private OnFragmentInteractionListener mListener;
    public MainActivity callback;

    public Me() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Me.
     */
    // TODO: Rename and change types and number of parameters
    public static Me newInstance(MainActivity callback) {
        Me fragment = new Me();
        fragment.callback = callback;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        functions.add("注册");
        functions.add("登录");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout =  inflater.inflate(R.layout.fragment_me, container, false);
        ListView functionList = (ListView)layout.findViewById(R.id.fm_functionList);
        ListAdapter functionListAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,
                functions);
        functionList.setAdapter(functionListAdapter);
        functionList.setOnItemClickListener((parent, view, position, id) -> {
            Log.i("ME", String.valueOf(position));
            switch (position)
            {
                case 0:
                    mListener.onFragmentInteraction("ShowFragment", "SignUp");
                    break;
                case 1:
                    mListener.onFragmentInteraction("ShowFragment", "Login");
                    break;
            }
        });
        return layout;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String cmd, String msg);;
    }

}
