package vmokshagroup.com.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by anshikas on 20-06-2016.
 */
public class ICBlueCustomAdapter  extends BaseAdapter{

    Context context;
    ArrayList<BluetoothDevice> icBluelist;

//    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();



    public ICBlueCustomAdapter(Context context, ArrayList<BluetoothDevice> icBluelist) {
        this.context = context;
        this.icBluelist = icBluelist;



    }

    public int getCount() {
        return icBluelist.size();
    }

    @Override
    public Object getItem(int position) {
        return icBluelist.get(position);
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
            convertView = mInflater.inflate( R.layout.le_device_list, null);
            holder = new Holder();
            holder.scanDeviceName = (TextView) convertView.findViewById(R.id.le_device_id);


            convertView.setTag(holder);
        } else
            holder = (Holder) convertView.getTag();
        if(icBluelist.size()>0){
            if((icBluelist.get(position).getName())!=null){
                holder.scanDeviceName.setText(icBluelist.get(position).getName());
            }else{
                holder.scanDeviceName.setText(icBluelist.get(position).getAddress());
            }
        }




        return convertView;
    }

    public class Holder {
        TextView scanDeviceName;
    }


}
