package co.rinn.androidbluetoothdebugger;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

//required java imports
import java.util.ArrayList;
import java.util.Set;


public class SearchAndPair extends AppCompatActivity {

    //widgets
    Button btnPaired;
    ListView deviceList;

    private boolean DEBUG=true;

    //Bluetooth
    private BluetoothAdapter myBluetooth = null; //device bluetooth adapter
    private Set<BluetoothDevice> pairedDevices; //pairable devices, inside set instead of array or other container so each is uniquely represented
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_and_pair);

        //Creating the widgets, or getting references to the ID not sure yet
        btnPaired = (Button) findViewById(R.id.PairButton);
        deviceList = (ListView) findViewById(R.id.BTConnection);


        myBluetooth = BluetoothAdapter.getDefaultAdapter(); //get current device adapter

        //error checking
        if(myBluetooth == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available.", Toast.LENGTH_LONG).show();
            finish(); //close app, no use in keeping open if bt is broken, since this is a BT debugger afterall
        }
        else if (!myBluetooth.isEnabled()) { //check if it is enabled
            //not enabled, so ask user to enable
            Intent turnBTOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTOn, 1);
        }

        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevicesList();
            }
        });
    }

    //this will create a list of the paired devices, will need a seperate function for available connections, but this will be first
    private void pairedDevicesList()
    {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList<String> list = new ArrayList<>();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        }
        else
        {
            //small message that will appear if no paired devices are found
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
            //TODO: Should I only show new devices if no paired are found? Or should I put a different listView in for discovered devices?
        }

        //final means it can't be changed after this line
        //arrayadapter is used to "translate" the arraylist data into something the ListView can use
        final ArrayAdapter adapter= new ArrayAdapter(this,android.R.layout.simple_list_item_1, list); //TODO: Fix this damn line and get rid of the warning

        deviceList.setAdapter(adapter); //define the listview adapter for display/usage
        deviceList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

        }
    };

}
