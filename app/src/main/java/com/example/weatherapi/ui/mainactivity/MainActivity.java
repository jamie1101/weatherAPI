package com.example.weatherapi.ui.mainactivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapi.uitls.api.soap.ApiClient;
import com.example.weatherapi.uitls.api.soap.GetApi;
import com.example.weatherapi.R;
import com.example.weatherapi.uitls.api.soap.response.WeatherResponse;

import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private TextView result;
    private Button search;
    private Spinner location_spinner,element_spinner,time_spinner;
    private String selected_location,selected_element,selected_time;
    private String[] location_data,element_data,time_data,tw_element;

    private ApiClient apiClient;
    private GetApi getApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        result = findViewById(R.id.result);
        search = findViewById(R.id.search);
        location_spinner = findViewById(R.id.locationName);
        element_spinner = findViewById(R.id.elementName);
        time_spinner = findViewById(R.id.time);

        location_data = getResources().getStringArray(R.array.location_data);
        element_data = getResources().getStringArray(R.array.element_data);
        time_data = getResources().getStringArray(R.array.time_data);
        tw_element = getResources().getStringArray(R.array.tw_element);

        apiClient = new ApiClient();
        getApi = apiClient.myWeatherApi().create(GetApi.class);

        setSpinner();
        search.setOnClickListener(view -> getWeather(selected_location,selected_element,selected_time));

    }

    private void setSpinner() {
        ArrayAdapter location_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, location_data);
        ArrayAdapter element_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, element_data);
        ArrayAdapter time_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, time_data);

        location_spinner.setAdapter(location_adapter);
        location_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_location = location_spinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });

        element_spinner.setAdapter(element_adapter);
        element_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_element = element_spinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        time_spinner.setAdapter(time_adapter);
        time_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_time = time_spinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void getWeather(String selectedLocation, String selectedElement, String selectedTime) {
        String authorization = "CWA-745454D2-A8B2-41D1-A46D-DB35C98E07F0";
        if (selectedElement.equals("All"))
            selectedElement = "";
        String finalSelectedElement = selectedElement;

        getApi.getWeatherApi(authorization,selectedLocation,selectedElement)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<WeatherResponse>() {
                    @Override
                    public void onNext( WeatherResponse weatherResponse) {
                        result.setText("");
                        List time_list = Arrays.asList(time_data);
                        List element_list = Arrays.asList(element_data);
                        if(weatherResponse.getElementSize() != 1){
                            for (int i = 0; i < weatherResponse.getElementSize(); i++) {
                                result.append(tw_element[i] + weatherResponse.getDataByTime(i,time_list.indexOf(selectedTime))+"\n");
                            }
                        }
                        else {
                            result.setText(tw_element[element_list.indexOf(finalSelectedElement)] + weatherResponse.getDataByTime(0,time_list.indexOf(selectedTime))+"\n");
                        }
                    }

                    @Override
                    public void onError( Throwable e) {
                        Log.d("test", "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.d("test", "onComplete: ");
                    }
                });
    }
}