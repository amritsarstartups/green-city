package technica.com.greencity.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import technica.com.greencity.DatabaseApp;
import technica.com.greencity.R;
import technica.com.greencity.RequestHelper;
import technica.com.greencity.Utils;


/**
 * Created by Amanpreet Singh on 4/23/2017.
 */
public class SubmittedRequests extends Fragment implements AdapterView.OnItemClickListener {
    RecyclerAdapter adapter;private RecyclerView mRecyclerView; private LinearLayoutManager mLinearLayoutManager;

    ArrayList<RequestHelper> arrayList2;

    String TAG = "SubmittedRequests";

    TextView gtp; // title custom in punjabi
    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    DatabaseApp mainDb;
    TextView noRequestTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.submitted_request_layout, container, false);
        arrayList2 = new ArrayList<>();
        mainDb = new DatabaseApp(getActivity());
        arrayList2 = mainDb.getRequestsLocal();
        if (arrayList2.size() == 0) {
            noRequestTextView = (TextView) rootView.findViewById(R.id.noRequestTextView);
            noRequestTextView.setVisibility(View.VISIBLE);
        }

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        //  mLinearLayoutManager = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL, true);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        adapter = new RecyclerAdapter();

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAlpha(0.0f);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.animate().alphaBy(1.0f);
        adapter.notifyDataSetChanged();

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

   /* class MySecondAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arrayList2.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_drawer_second, null);
            final ImageView plantImage = (ImageView) convertView.findViewById(plantImage);
            TextView plantType = (TextView) convertView.findViewById(plantType);
            TextView plantName = (TextView) convertView.findViewById(plantName);
            TextView address = (TextView) convertView.findViewById(address);
            Log.e(TAG, "getView: url : " + Utils.DOMAIN + arrayList2.get(position).getUrlImage());

            Picasso.with(getContext())
//                    .load(Utils.DOMAIN+arrayList2.get(position).completeAddress)
                    .load(arrayList2.get(position).urlImage)
                    .error(R.drawable.icon_green)
                    .placeholder(R.drawable.icon_green) // optional
                    .into(plantImage);

            plantType.setText(Html.fromHtml("<i>Plant Type:</i> ") + arrayList2.get(position).getPlantType() + "|");
            plantName.setText(Html.fromHtml("<i>Plant Name:</i> ") + arrayList2.get(position).getPlantName());
            address.setText(Html.fromHtml("<i>Address :</i> ") + arrayList2.get(position).getCompleteAddress().replace("&&&", ""));

            return convertView;
        }
    }*/
    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>
            implements View.OnClickListener {
        private LayoutInflater inflater;
        public RecyclerAdapter() {
            inflater = LayoutInflater.from(getActivity());
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.e(TAG, "onCreateViewHolder: ");
            View view = inflater.inflate(R.layout.item_drawer_second, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            Log.e(TAG, "onBindViewHolder: postion: " + position);
            Context context = holder.plantImage.getContext();
            RequestHelper current = arrayList2.get(position);
            holder.plantType.setText(Html.fromHtml("<i>Plant Type:</i> ") + arrayList2.get(position).getPlantType() + "|");
            holder.plantName.setText(Html.fromHtml("<i>Plant Name:</i> ") + arrayList2.get(position).getPlantName());
            holder.address.setText(Html.fromHtml("<i>Address :</i> ") + arrayList2.get(position).getCompleteAddress().replace("&&&", ""));
            String link=Utils.DOMAIN+arrayList2.get(position).urlImage;
            Picasso.with(getActivity())
//                    .load(Utils.DOMAIN+arrayList2.get(position).completeAddress)
                    .load(link)
                    .error(R.drawable.icon_green)
                    .resize(250,250)
                    .placeholder(R.drawable.icon_green) // optional
                    .into(holder.plantImage);
            Log.e(TAG, "onBindViewHolder: url : imag : "+Utils.DOMAIN+arrayList2.get(position).urlImage );


        }

        @Override
        public int getItemCount() {
            return arrayList2.size();
        }

        @Override
        public void onClick(View view) {
        }

        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            LinearLayout mainLayout;
            TextView plantType,plantName,address;
            ImageView plantImage;


            public MyViewHolder(View itemView) {
                super(itemView);

                plantImage=(ImageView)itemView.findViewById(R.id.plantImage);

                mainLayout = (LinearLayout) itemView.findViewById(R.id.layout);
                mainLayout.setOnClickListener(this);
                // textView.setText(list.get());
                 plantType = (TextView) itemView.findViewById(R.id.plantType);
                 plantName = (TextView) itemView.findViewById(R.id.plantName);
                 address = (TextView) itemView.findViewById(R.id.address);

            }

            @Override
            public void onClick(View view) {

            }}
    }

}
