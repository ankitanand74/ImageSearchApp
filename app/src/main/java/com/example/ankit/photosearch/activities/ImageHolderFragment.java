package com.example.ankit.photosearch.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.ankit.photosearch.R;
import com.example.ankit.photosearch.adapter.MyImageHoldingRecyclerViewAdapter;
import com.example.ankit.photosearch.utils.AppConstants;
import com.example.ankit.photosearch.utils.ConnectivityUtils;
import com.example.ankit.photosearch.utils.Utils;
import com.example.ankit.photosearch.utils.VolleySingleton;

public class ImageHolderFragment extends Fragment {

    private static final String TAG = ImageHolderFragment.class.getSimpleName();
    private int mColumnCount = 2;
    private OnImageHolderIInteractionListener mListener;
    private String searchString;
    private BroadcastReceiver mReceiver;
    private IntentFilter intentFilter;
    private ProgressBar loadingIndicator;
    private RecyclerView recyclerView;
    private MyImageHoldingRecyclerViewAdapter myImageHoldingRecyclerViewAdapter;
    private TextView emptyTextView;
    private List<String> imageList;


    public ImageHolderFragment() {
    }

    public static ImageHolderFragment newInstance(String searchQuery) {
        ImageHolderFragment fragment = new ImageHolderFragment();
        Bundle args = new Bundle();
        args.putInt(AppConstants.ARG_COLUMN_COUNT, 2);
        args.putString(AppConstants.SEARCH_STRING, searchQuery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(AppConstants.ARG_COLUMN_COUNT);
            searchString = getArguments().getString(AppConstants.SEARCH_STRING);
        }

        Utils.setContext(getActivity());

        imageList = new ArrayList<>();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "   network status on receive");
                updateUI();

            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list2, container, false);

        loadingIndicator = (ProgressBar) view.findViewById(R.id.loadIndicator);
        emptyTextView = (TextView) view.findViewById(R.id.empty);

        if (!ConnectivityUtils.isConnected(getActivity())) {
            emptyTextView.setText(R.string.no_internet);
        }

        // Set the adapter
        if (view instanceof LinearLayout) {
            Context context = view.getContext();
            recyclerView = view.findViewById(R.id.recyclerView);
            myImageHoldingRecyclerViewAdapter = new MyImageHoldingRecyclerViewAdapter(imageList, mListener, getActivity());
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            recyclerView.setAdapter(myImageHoldingRecyclerViewAdapter);
        }
        volleyRequest(getUri(), 0);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnImageHolderIInteractionListener) {
            mListener = (OnImageHolderIInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //Volley request for json string
    public void volleyRequest(String volleysearchString, final int addFlag) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, volleysearchString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingIndicator.setVisibility(View.GONE);
                        updateUIPostExecute(Utils.extractImages(response), addFlag);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingIndicator.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);

                String message = null;
                if (error instanceof NetworkError) {
                    message = getResources().getString(R.string.connection_error);
                } else if (error instanceof ServerError) {
                    message = getResources().getString(R.string.server_error);
                } else if (error instanceof AuthFailureError) {
                    message = getResources().getString(R.string.connection_error);
                } else if (error instanceof ParseError) {
                    message = getResources().getString(R.string.parse_error);
                } else if (error instanceof TimeoutError) {
                    message = getResources().getString(R.string.timeout_error);
                }

                emptyTextView.setText(message);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", AppConstants.APP_CLIENT_ID);
                return headers;
            }
        };
        VolleySingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue().cancelAll(this);
        VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    public void updateUIPostExecute(List<String> response, int addFlag) {

        if (addFlag == 0) {
            imageList.clear();
        }

        if(response != null && response.size()!=0){
            imageList.addAll(response);
            myImageHoldingRecyclerViewAdapter.notifyDataSetChanged();
        }else{
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(R.string.no_results);
        }

    }

    public String getSearchString() {
        return searchString;
    }

    public String getUri() {
        return Utils.getUri(getSearchString()).toString();
    }

    public void loadNextDataFromApi(int offset) {
        Uri.Builder uriBuilder = Utils.getUri(getSearchString());
        uriBuilder.appendQueryParameter("start", "" + offset);
        volleyRequest(uriBuilder.toString(), 1);
    }

    private void updateUI() {
        if (ConnectivityUtils.isConnected(getActivity())) {
            emptyTextView.setText(R.string.no_images);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(R.string.no_internet);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public interface OnImageHolderIInteractionListener {
        void onListImageHolderInteraction(String imageUrl, ImageView imageView, ProgressBar progressBar);
    }
}
