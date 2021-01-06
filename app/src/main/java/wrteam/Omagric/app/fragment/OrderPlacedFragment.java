package wrteam.Omagric.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import wrteam.Omagric.app.R;
import wrteam.Omagric.app.activity.MainActivity;
import wrteam.Omagric.app.helper.ApiConfig;
import wrteam.Omagric.app.helper.Constant;
import wrteam.Omagric.app.helper.Session;
import wrteam.Omagric.app.helper.VolleyCallback;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class OrderPlacedFragment extends Fragment {
    View root;
    Activity activity;
    ProgressBar progressBar;
    Button btnShopping, btnSummary;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_order_placed, container, false);
        activity = getActivity();
        Session session = new Session(activity);
        progressBar = root.findViewById(R.id.progressBar);
        btnShopping = root.findViewById(R.id.btnShopping);
        btnSummary = root.findViewById(R.id.btnSummary);
        setHasOptionsMenu(true);


        RemoveAllItemFromCart(activity, session);
        activity.invalidateOptionsMenu();

        return root;
    }

    public void RemoveAllItemFromCart(final Activity activity, Session session) {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.REMOVE_FROM_CART, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            Constant.TOTAL_CART_ITEM = 0;
                            progressBar.setVisibility(View.GONE);

                            btnSummary.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    MainActivity.homeClicked = false;
                                    MainActivity.categoryClicked = false;
                                    MainActivity.favoriteClicked = false;
                                    MainActivity.trackingClicked = false;
                                    MainActivity.active = null;
                                    startActivity(new Intent(activity, MainActivity.class).putExtra("from", "track_order").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                }
                            });

                            btnShopping.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(activity, MainActivity.class).putExtra("from", "").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                }
                            });

                        }
                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.CART_URL, params, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.order_placed);
        activity.invalidateOptionsMenu();
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(false);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
    }

}