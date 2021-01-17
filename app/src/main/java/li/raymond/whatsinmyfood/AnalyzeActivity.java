package li.raymond.whatsinmyfood;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.LinkMovementMethod;
import android.util.SparseArray;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.paris.Paris;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnalyzeActivity extends AppCompatActivity {
	Uri imageUri;
	TextRecognizer recognizer;
	TableLayout table;
	TableRow tableRow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_analyze);
		table = findViewById(R.id.ingredientTable);
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
		recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
		Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
		if (recognizer.isOperational()) {
			Frame frame = new Frame.Builder().setBitmap(bitmap).build();
			final SparseArray<TextBlock> items = recognizer.detect(frame);
			if (items.size() != 0) {
				StringBuilder stringBuilder = new StringBuilder();
				for (int i = 0; i < items.size(); i++) {
					TextBlock item = items.valueAt(i);
					stringBuilder.append(item.getValue());
				}
				// Can swap between keyIngredients and allIngredients
				createTable(keyIngredients(stringBuilder.toString().toLowerCase()));
			}
		}
	}

	private String[] allIngredients(String basicResult) {
		String formattedResult = basicResult.toLowerCase()
				.split("ingredients: ")[1].replaceAll("[^a-zA-Z0-9\\-\\s]", " ").replaceAll("[ \t\n\r]*", " ");

		String[] ingredientList = formattedResult.split(" {2}");
		for (int i = 0; i < ingredientList.length; i++) {
			Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(ingredientList[i]);
			if (m.find()) {
				ingredientList[i] = m.group(1);
			}
		}
		return ingredientList;
	}

	private Map<String, String> ingredients = Stream.of(new String[][]{
			{"Soy lecithin", ""},
			{"Polyglycerol", "Doe"},
			{"polyricinoleate", ""}
	}).collect(Collectors.toMap(data -> data[0], data -> data[1]));

	private String[] keyIngredients(String basicResult) {
		ArrayList<String> ingredientList = new ArrayList<>();
		for (String ingredient : ingredients.keySet()) {
			if (basicResult.contains(ingredient.toLowerCase())) {
				ingredientList.add(ingredient);
			}
		}
		alertDialog("1", basicResult);
		return ingredientList.toArray(new String[0]);
	}

	private void createTable(String[] iList) {
		for (int i = 0; i < iList.length; i++) {
			TableRow ingredientRow = new TableRow(this);
			Paris.style(ingredientRow).apply(R.style.IngredientRow);
			TextView ingredient = new TextView(this);
			ingredient.setText(iList[i]);
			ingredient.setTextAppearance(R.style.Ingredient);
			ingredientRow.addView(ingredient);
			table.addView(ingredientRow);
			TableRow descriptionRow = new TableRow(this);
			Paris.style(descriptionRow).apply(R.style.DescriptionRow);
			TextView description = new TextView(this);
			description.setText(getDescription(iList[i]));
			description.setTextAppearance(R.style.Description);
			descriptionRow.addView(description);
			table.addView(descriptionRow);
		}
	}

	private String getDescription(String ingredient) {
		return ingredients.get(ingredient);
	}

	// Dialog used to send the user a message
	private void alertDialog(String title, String message) {
		AlertDialog alert = new AlertDialog.Builder(this).setMessage(message)
				.setTitle(title).setCancelable(false)
				.setPositiveButton("Ok", (dialog, id) -> {
				}).create();
		alert.show();
		((TextView) alert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
	}
}