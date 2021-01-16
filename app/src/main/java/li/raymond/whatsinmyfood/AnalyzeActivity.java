package li.raymond.whatsinmyfood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalyzeActivity extends AppCompatActivity {
	Uri imageUri;
	TextView textView;
	TextRecognizer recognizer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_analyze);

		textView = findViewById(R.id.textView);
		Intent intent = getIntent();
		String imagePath = intent.getExtras().getString("imageUri");
		imageUri = Uri.parse(imagePath);

		try {
			runTextRecognition();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void runTextRecognition() throws IOException {
		recognizer =  new TextRecognizer.Builder(getApplicationContext()).build();
		Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

		if(recognizer.isOperational()) {
			Frame frame = new Frame.Builder().setBitmap(bitmap).build();

			final SparseArray<TextBlock> items = recognizer.detect(frame);
			if (items.size() != 0) {
				StringBuilder stringBuilder = new StringBuilder();
				for (int i=0 ; i<items.size(); i++) {
					TextBlock item = items.valueAt(i);
					stringBuilder.append(item.getValue());
				}
				createTable(stringBuilder.toString().toLowerCase());
			}

		}
	}

	private void createTable(String basicResult) {
		textView.setText(basicResult);
		String formattedResult = basicResult.toLowerCase()
				.split("ingredients: ")[1].replaceAll("[^a-zA-Z0-9\\(\\)\\s]", " ");
		String[] ingredientList = formattedResult.split("  ");

		for (int i = 0; i < ingredientList.length; i++) {
			Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(ingredientList[i]);
			if(m.find()) {
				ingredientList[i] = m.group(1);
			}
		}
		populateTable(ingredientList);
	}

	private void populateTable(String[] iList) {

	}


}