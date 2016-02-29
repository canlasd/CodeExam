package com.example.canlasd.codeexam;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends Activity {

    private final static String LOG = "log_check";
    private String company_input;
    private String email_input;

    private final static String email = "contest.ideaone@gmail.com";
    private EditText text_company;
    private EditText text_email;
    private TextView com_req;
    private TextView email_req;
    private RelativeLayout rel;
    private RelativeLayout sec_layout;
    private File file;
    private final static String regex = "^[\\w!#$%&'*+/=?`{|}~^-]" +
            "+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)" +
            "+[a-zA-Z]{2,6}$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // setting variable to current filepath - to be used later
        String img_path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + getString(R.string.entries_directory);

        File dir = new File(img_path);
        dir.mkdir();

        file = new File(dir, getString(R.string.file_name));

        // lock keyboard when app opens
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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

                company_input = text_company.getText().toString().trim();
                email_input = text_email.getText().toString().trim();

                if (company_input.matches(getString(R.string.blank))) {

                    com_req.setText(R.string.required);
                    com_req.setTextColor(Color.RED);

                    return;
                } else if (email_input.matches(getString(R.string.blank))) {

                    email_req.setText(R.string.required);
                    email_req.setTextColor(Color.RED);

                    return;

                } else if (!validateEmail(email_input)) {
                    Toast.makeText(MainActivity.this, R.string.enter_email, Toast.LENGTH_LONG)
                            .show();

                    return;
                }

                // get current date and time

                SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format));
                String date_time = sdf.format(new Date());

                String result = getString(R.string.time_entry) + date_time + "\n"
                        + getString(R.string.email_entry)
                        + email_input + "\n" + getString(R.string.company_entry)
                        + company_input;

                // store result to data.txt file
                writeToFile(result);

                text_company.setText(getString(R.string.blank));
                text_email.setText(getString(R.string.blank));
                email_req.setText(getString(R.string.blank));
                com_req.setText(getString(R.string.blank));

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


    private void sendNewEmail() {


        String message = getString(R.string.company_name) + company_input +
                "\n" + getString(R.string.email_string) + email_input;
        Log.i(LOG, message);

        //Creating SendMail object
        SendMail sm = new SendMail(this, email, getString(R.string.contest_entry), message);

        //Executing sendmail to send email
        sm.execute();


    }

    private void hideKeyboard(Activity activity) {
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

    private void writeToFile(String data) {
        try {

            String separator = System.getProperty(getString(R.string.line_separator));
            FileOutputStream fos = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.append(separator);
            osw.append(data);
            osw.append(separator);
            osw.append(separator);
            osw.flush();
            osw.close();
            fos.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private boolean validateEmail(String email_data) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email_data);
        return matcher.matches();
    }


}


