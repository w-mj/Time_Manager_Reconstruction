package wmj.timemanager.dayViewFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wmj.timemanager.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DayView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayView extends Fragment {

    private int mDay, mWeek;

    public DayView() {
        // Required empty public constructor
    }

    /**
     * 日视图通过哪一周的星期几确定一天.
     * @param week 哪一周, 与weekList的索引保持一致
     * @param day 星期几, 0为周日
     * @return A new instance of fragment DayView.
     */
    public static DayView newInstance(int week, int day) {
        DayView fragment = new DayView();
        Bundle args = new Bundle();
        args.putInt("week", week);
        args.putInt("day", day);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mDay = savedInstanceState.getInt("day");
        mWeek = savedInstanceState.getInt("week");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_day_view, container, false);
        return v;
    }


}
