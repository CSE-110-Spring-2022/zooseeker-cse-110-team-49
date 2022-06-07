package com.example.zooseeker;

import static com.example.zooseeker.ZooDataItem.VertexInfo.Kind.GATE;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    /*
    MS1
     */
    Map<String, ZooDataItem.VertexInfo> vInfo;
    TextView currentLocation;
    Dialog dialog;

    /*
    MS2
     */
    ListManager lm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * MS2
         */
        lm = new ListManager(this);


        /**
         * MS1
         */
        // vInfo = ZooDataItem.loadVertexInfoJSON(this, "sample_node_info.json");
        Map<String, ArrayList<String>> reversedInfo = lm.getReversedInfo();

        List<String> possible_list_AL = new ArrayList<String>();
        List<String> nodeNameList = new ArrayList<String>();

        lm.getExhibitInfo().forEach((k, v) -> {
            possible_list_AL.addAll(v.tags);
            nodeNameList.add(v.name);
        });

        //-----------
        ArrayList<String> gate_list = new ArrayList<>();
        currentLocation = findViewById(R.id.setCurrentLocation);
        for (Map.Entry<String, ZooDataItem> entry : lm.getExhibitInfo().entrySet()) {
            if (entry.getValue().kind.equals(GATE)) {
                gate_list.add(entry.getValue().name);
            }
        }
        String gate_name = "";
        for(String i:gate_list){
            if(i.toLowerCase().contains("gate") || i.toLowerCase().contains("main")){
                gate_name=i;
                break;
            }
        }
        if(gate_name.length()==0 && gate_list.size()!=0){
            gate_name=gate_list.get(0);
        }
        if(gate_list.size()!=0){
            currentLocation.setText(gate_name);
        }

        int index=-1;
        for(int i=0;i<nodeNameList.size();i++){
            if(nodeNameList.get(i).equals(gate_name)){
                nodeNameList.remove(i);
                index=i;
                break;
            }
        }
        if(index!=-1){
            String first_elem=nodeNameList.get(0);
            nodeNameList.set(0, gate_name);
            nodeNameList.add(first_elem);
        }



        //-----------
        // search list
        List<String> new_possible_list_AL = possible_list_AL.stream().distinct().collect(Collectors.toList());
        String[] possible_list = new String[new_possible_list_AL.size()];

        // all possible location
        possible_list = new_possible_list_AL.toArray(possible_list);

        // dropdown menu
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dialog = new Dialog(MainActivity.this);
               dialog.setContentView(R.layout.search_location_spinner);
               dialog.getWindow().setLayout(1000, 1200);
               dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
               ArrayAdapter<String> adapter_curLocation = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, nodeNameList);
               dialog.show();
               EditText editText = dialog.findViewById(R.id.edit_text);
               ListView listView = dialog.findViewById(R.id.list_view);
               listView.setAdapter(adapter_curLocation);
               editText.addTextChangedListener(new TextWatcher() {
                   @Override
                   public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                   }
                   @Override
                   public void onTextChanged(CharSequence s, int start, int before, int count) {
                       adapter_curLocation.getFilter().filter(s);
                   }
                   @Override
                   public void afterTextChanged(Editable s) {

                   }
               });
               listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                       currentLocation.setText(adapter_curLocation.getItem(position));
                       dialog.dismiss();
                   }
               });

            }
        });


        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.searchInput);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, possible_list);
        textView.setAdapter(adapter);

        findViewById(R.id.myList).setOnClickListener(view -> {
            String currentL = currentLocation.getText().toString();
            String id = "entrance_exit_gate";
            for (Map.Entry<String, ZooDataItem> entry : lm.getExhibitInfo().entrySet()) {
                String key = entry.getKey();
                ZooDataItem value = entry.getValue();
                if (value.name == currentL) {
                    id = key;
                }
            }
            Intent intent = new Intent(MainActivity.this, ExhibitListViewActivity.class);
            intent.putExtra("currentId", id);
            String returnResult = "";
            startActivityForResult(intent, 2);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 2) {
            String message = data.getStringExtra("MESSAGE");
            currentLocation.setText(lm.getExhibitInfo().get(message).name);
            findViewById(R.id.myList).setOnClickListener(view -> {
                String currentL = currentLocation.getText().toString();
                String id = message;
                for (Map.Entry<String, ZooDataItem> entry : lm.getExhibitInfo().entrySet()) {
                    String key = entry.getKey();
                    ZooDataItem value = entry.getValue();
                    if (value.name == currentL) {
                        id = key;
                    }
                }
                Intent intent = new Intent(MainActivity.this, ExhibitListViewActivity.class);
                intent.putExtra("currentId", id);
                String returnResult = "";
                startActivityForResult(intent, 2);
            });
        }
    }

    public void setOnClicktoSearch(View view) {
        Intent search_page = new Intent(this, DisplaySearchResultsActivity.class);
        TextView search_input = findViewById(R.id.searchInput);
        search_page.putExtra("input", search_input.getText().toString());
        startActivity(search_page);
    }

    public void openMap(View view) {
        Intent map = new Intent(this, MapsActivity.class);
        startActivity(map);
    }
}