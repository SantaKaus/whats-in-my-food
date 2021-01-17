package li.raymond.whatsinmyfood;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class SearchActivity extends AppCompatActivity {
    Button searchButton;
    EditText searchBox;
    TextView output;
    Python python;
    String ingredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchButton = findViewById(R.id.searchButton);
        searchBox = findViewById(R.id.searchBox);
        output = findViewById(R.id.output);

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        python = Python.getInstance();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(searchBox.getText().toString());
            }
        });
    }

    private void search(String ingredient) {
        try {
            String description = python.getModule("main").callAttr("main", ingredient).toString();
            output.setText(description);
        } catch (Exception e){
            output.setText("Compound not found!");
        }


    }
}