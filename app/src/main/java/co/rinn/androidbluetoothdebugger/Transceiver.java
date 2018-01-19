package co.rinn.androidbluetoothdebugger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Transceiver extends AppCompatActivity {

    Button sendBtn;
    TextView rxText;
    EditText txText;
    String address;

    private boolean DEBUG = false;

    List<String> rxStrings = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transceiver);

        sendBtn = (Button)findViewById(R.id.TransmitButton);
        rxText = (TextView)findViewById(R.id.RxText);
        txText = (EditText)findViewById(R.id.TxText);

        //get the values from the intent
        Intent newInt = getIntent();
        address = newInt.getStringExtra(SearchAndPair.EXTRA_ADDRESS);
        DEBUG = newInt.getBooleanExtra("debug", false);


        if (DEBUG) {
            Receive( "address:" + address);
        }

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

        if (DEBUG) {
            if (rxStrings.size() == 0) {
                //for the address label
                rxStrings.add(textReceived);
            }
            else {
                //the FILO stack using LL instead
                //TODO::Replace rxStrings with a Stack<String> instead since this is doing the same thing
                rxStrings.add(1, textReceived);
            }

            if (rxStrings.size() > displayMax) {
                rxStrings.remove(displayMax); //remove the last node
            }
            setRxText(rxStrings);
        }
        else {

            rxStrings.add(0,textReceived);
            if(rxStrings.size() > displayMax) {
                rxStrings.remove(displayMax);
            }
            setRxText(rxStrings);
        }

    }
    private void setRxText(List<String> args) {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);

        for(String arg : args) {
            if (DEBUG) { //when the DEBUG flag is false, these won't need to be displayed
                if (arg.startsWith("address")) {
                    formatter.format("Address::%s\n",address);
                }
                else {
                    formatter.format("%s\n",arg);
                }
            }
            else {
                formatter.format("%s\n",arg);
            }
        }
        rxText.setText(sb);
    }
}
