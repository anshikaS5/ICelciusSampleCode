package vmokshagroup.com.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by shwethap on 19-01-2016.
 */
public class SSIDDetailsAdapter extends BaseAdapter {

    Context context;
    ArrayList<SSIDDetailsModel> pickuplist;

//    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();



    public SSIDDetailsAdapter(Context context, ArrayList<SSIDDetailsModel> pickuplist) {
        this.context = context;
        this.pickuplist = pickuplist;



    }

    public int getCount() {
        return pickuplist.size();
    }

    @Override
    public Object getItem(int position) {
        return pickuplist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        final View listview = convertView;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.ssid_listview_items, null);
            holder = new Holder();
            holder.mSSIDName = (TextView) convertView.findViewById(R.id.ssid_value);


            convertView.setTag(holder);
        } else
            holder = (Holder) convertView.getTag();

        holder.mSSIDName.setText(pickuplist.get(position).getSSID());
        return convertView;
    }

    public class Holder {
        TextView mSSIDName;
    }


}
