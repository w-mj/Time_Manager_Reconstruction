package wmj.timemanager;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import wmj.InnerLayer.Configure;
import wmj.InnerLayer.MyTools;
import wmj.InnerLayer.control.MyHandler;
import wmj.timemanager.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Me#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Me extends Fragment {

    ArrayList<String> functions = new ArrayList<String>();

    public Me() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Me.
     */
    public static Me newInstance(MainActivity callback) {
        return new Me();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        functions.add("注册");
        functions.add("登录");
        functions.add("从教务处同步课程表");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout =  inflater.inflate(R.layout.fragment_me, container, false);
        ListView functionList = (ListView)layout.findViewById(R.id.fm_functionList);
        ListAdapter functionListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
                functions);
        functionList.setAdapter(functionListAdapter);
        functionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("ME", String.valueOf(position));
                switch (position)
                {
                    case 1:
                        Message msg = new Message();
                        msg.what = MyHandler.SHOW_ACTIVITY;
                        msg.obj = "Login";
                        Configure.handler.sendMessage(msg);
                        break;
                    case 2:
                        Configure.handler.sendMessage(MyTools.showFragmentMessage("SyncCalender"));
                        break;
                }
            }
        });
        return layout;
    }
}
