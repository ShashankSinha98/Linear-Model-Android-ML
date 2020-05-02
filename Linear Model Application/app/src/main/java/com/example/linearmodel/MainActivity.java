package com.example.linearmodel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    private EditText inputET;
    private Button inferBtn;
    private TextView predTV;
    private Interpreter tfliteInterpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputET = findViewById(R.id.input_et);
        inferBtn = findViewById(R.id.infer_btn);
        predTV = findViewById(R.id.pred_tv);

        try{
            tfliteInterpreter = new Interpreter(loadModelFile());
        } catch(Exception ex){
            ex.printStackTrace();
        }

        inferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float prediction = doInference(inputET.getText().toString());
                predTV.setText("Predicted Value: "+prediction);
            }
        });

    }

    private float doInference(String inputString) {

        // Input shape is [1]
        float[] inputValue = new float[1];
        inputValue[0] = Float.valueOf(inputString);

        // Output shape is [1][1]
        float[][] outputValue = new float[1][1];

        // Run inference passing the input shape and getting the output shape
        tfliteInterpreter.run(inputValue,outputValue);

        // Inferred value is at [0][0]
        float inferredValue = outputValue[0][0];

        // Return it
        return  inferredValue;

    }

    private MappedByteBuffer loadModelFile() throws IOException {
        // Open the model using an input stream, and memory map it to load
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("linear.tflite");
        FileInputStream inputStream = new FileInputStream((fileDescriptor.getFileDescriptor()));
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
