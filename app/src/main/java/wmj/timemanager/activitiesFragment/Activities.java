package wesayallright.timemanager.surface.activitiesFragment;

import android.content.Context;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wesayallright.timemanager.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Activities.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Activities#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Activities extends Fragment {


    List<ActivityViewItem> activitiesData = new ArrayList<>();


    private OnFragmentInteractionListener mListener;

    public Activities() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment Activities.
     */
    public static Activities newInstance() {
        Activities fragment = new Activities();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("活动界面", "进入onCreate方法");
        // 测试
        for (int i = 0; i < 20; i++) {
            activitiesData.add(new ActivityViewItem("活动"+i, new Date(i + 3600), "A"+i));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.i("活动界面", "进入onCreateView方法");

        View v = inflater.inflate(R.layout.fragment_activities, container, false);
        final ListView activitiesList = (ListView)v.findViewById(R.id.fa_activityList);
        // 首先用inflater取得相应的布局文件,然后再findview.
        // 设置适配器
        ActivityListAdapter ap = new ActivityListAdapter(this.getContext(), activitiesData);
        activitiesList.setAdapter(ap);
        // 监听点击事件
        activitiesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JoinDialog.show(getActivity(), activitiesData.get(position));
            }
        });

        ImageView plus = (ImageView)v.findViewById(R.id.nad_plus);
        plus.setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View v) {
                NewActivityDialog.NewActivity(getActivity());
            }

        });
        return v;
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
        void onFragmentInteraction(String cmd, String msg);
    }
}
