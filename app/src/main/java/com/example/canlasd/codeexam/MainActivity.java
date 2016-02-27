package com.example.canlasd.codeexam;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends Activity {

    final static String LOG = "log_check";
    private String PREFS_COUNT = "Contest";
    String company_input, email_input;
    int count = 1;
    final static String email = "contest.ideaone@gmail.com";
    EditText text_company, text_email;
    TextView com_req, email_req;
    RelativeLayout rel, sec_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // lock keyboard when app opens
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // save initial value of counter

        SharedPreferences settings = getSharedPreferences(PREFS_COUNT, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("counter", count);
        editor.commit();

        // initial UI elements

        text_company = (EditText) findViewById(R.id.company_input);
        text_email = (EditText) findViewById(R.id.email_input);

        com_req = (TextView) findViewById(R.id.company_required);
        email_req = (TextView) findViewById(R.id.email_required);

        // initialize layouts

        rel = (RelativeLayout) findViewById(R.id.relativeLayout);
        sec_layout = (RelativeLayout) findViewById(R.id.second_layout);
        sec_layout.setVisibility(View.INVISIBLE);


        // function when user clicks enter button

        Button enter_data = (Button) findViewById(R.id.enter);
        enter_data.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                // hide keyboard

                hideKeyboard(MainActivity.this);


                // get user data

                company_input = text_company.getText().toString();
                email_input = text_email.getText().toString();

                if (company_input.matches("")) {

                    com_req.setText("Required");
                    com_req.setTextColor(Color.RED);

                    return;
                } else if (email_input.matches("")) {

                    email_req.setText("Required");
                    email_req.setTextColor(Color.RED);

                    return;

                }

                // save data in using shared preferences
                SharedPreferences settings = getSharedPreferences(PREFS_COUNT, MODE_PRIVATE);
                int counter = settings.getInt("counter", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("email " + counter, email_input);
                editor.putString("company " + counter, company_input);
                editor.putInt("counter", ++count);
                editor.commit();

                text_company.setText("");
                text_email.setText("");
                email_req.setText("");
                com_req.setText("");

                // send email
                sendNewEmail();

                // show GOOD LUCK message and disappear after 5 secs
                rel.setVisibility(View.INVISIBLE);
                sec_layout.setVisibility(View.VISIBLE);

                Runnable mRunnable;
                Handler mHandler = new Handler();

                mRunnable = new Runnable() {

                    @Override
                    public void run() {

                        sec_layout.setVisibility(View.INVISIBLE);
                        rel.setVisibility(View.VISIBLE);

                    }
                };

                mHandler.postDelayed(mRunnable, 5 * 1000);

            }

        });
    }


    protected void sendNewEmail() {


        String message = "Company name: " + company_input + "\n" + "Email: " + email_input;
        Log.i(LOG, message);

        //Creating SendMail object
        SendMail sm = new SendMail(this, email, "Contest Entry", message);

        //Executing sendmail to send email
        sm.execute();


    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager)
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token
        // from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}


