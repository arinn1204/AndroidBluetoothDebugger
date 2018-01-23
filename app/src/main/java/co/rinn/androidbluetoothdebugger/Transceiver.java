package co.rinn.androidbluetoothdebugger;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Transceiver extends AppCompatActivity {

    Button sendBtn;
    TextView rxText;
    EditText txText;
    String address;

    List<String> rxStrings = new LinkedList<>();

    //Bluetooth shit
    BluetoothAdapter myBluetooth;
    BluetoothSocket btSocket;
    private boolean isBtConnected = false;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private void msg(String s) {
        //just a fast way to display toasts
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transceiver);

        sendBtn = findViewById(R.id.TransmitButton);
        rxText = findViewById(R.id.RxText);
        txText = findViewById(R.id.TxText);

        //get the values from the intent
        Intent newInt = getIntent();
        address = newInt.getStringExtra(SearchAndPair.EXTRA_ADDRESS);

        new ConnectBT().execute();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transmit();
            }
        });
    }

    private void Transmit() {
        //get the text from the edittext
        String textToSend = txText.getText().toString();

        //since right now, there is no bluetooth connection this will be piped directly to the receive function
        Receive(textToSend);

    }
    //currently can only handle one line of text at a time
    private void Receive(String textReceived) {
        int displayMax = 5;
        rxStrings.add(0,textReceived);
        if(rxStrings.size() > displayMax) {
            rxStrings.remove(displayMax);
        }
        setRxText(rxStrings);


    }
    private void setRxText(List<String> args) {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);

        for(String arg : args) {
            formatter.format("%s\n",arg);

        }
        rxText.setText(sb);
    }

    /*
     *  AsyncTask "enables proper and easy use of the UI thread", in other words, it's a helper object for threading.
     *  This class allows you to do things in the background
     *  In this case, this means doing bluetooth functions in the background, like connecting to the other device
     *  AsyncTask has three types: Params, Progress, Result [[Void, Void, Void in this case]]
     *  AsyncTask has four methods that can be overrode: onPreExecute, doInBackground, onProgressUpdate, onPostExecute
     */
    private class ConnectBT extends AsyncTask<Void, Void, Void> {

        private boolean ConnectSuccess = true; //if it's entered here, it has almost connected
        ProgressDialog progress;
        @Override
        protected void onPreExecute() {
            //this will just show a progress dialog
            progress = ProgressDialog.show(Transceiver.this, "Connecting...", "Please wait.");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if(btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter(); //get current device bluetooth
                    BluetoothDevice device = myBluetooth.getRemoteDevice(address); //connects to the other devices bluetooth and checks if it is available
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID); //create RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery(); //turns off current device BT discovery
                    btSocket.connect(); //starts the connection
                }
            }
            catch (IOException e) {
                //do exception handling here, for now going to leave blank
                ConnectSuccess = false;
            }
            return null; //this relates to the third param (Void in this case)
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result); //calls AsyncTask onPostExecute
            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else {
                msg("Connected");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
