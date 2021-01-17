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
				createTable(keyIngredients(stringBuilder.toString().toLowerCase().split("ingredients: ")[1].replaceAll("[^a-zA-Z0-9\\-\\s]", " ").replaceAll("[ \t\n\r]*", " ")));
			}
		}
	}

	private String[] allIngredients(String basicResult) {
		String[] ingredientList = basicResult.split(" {2}");
		for (int i = 0; i < ingredientList.length; i++) {
			Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(ingredientList[i]);
			if (m.find())
				ingredientList[i] = m.group(1);
		}
		return ingredientList;
	}

	private Map<String, String> ingredients = Stream.of(new String[][]{
			{"Soy lecithin", "Emulsifier (helps things mix) and lubricant. Mostly harmless since quantities are very small. Can be safely consumed by people with soy allergies. May reduce cholesterol and provides Choline (an essential nutrient). Generally recognized as safe (GRAS)."},
			{"Polyglycerol", "Polyglycerol polyricinoleate (PGPR) is an emulsifier (helps things mix) used with soy lecithin to reduce viscosity (thickness) in chocolates, spreads and salad dressings. Improves texture of baked goods. Generally recognized as safe (GRAS) when consumed in small amounts."},
			{"Maltodextrin", "Eumlsifier (helps things mix). Improves flavor, thickness, and shelf life of food. Carbohydrate supplement. Comes from starch like potatoes. Provides energy without nutrition. May increase cholesterol when consumed in large amounts. Generally recognized as safe (GRAS)."},
			{"Citric acid", "The sour part of lemon juice. Provides flavor and improves shelf life. One of the most common food additives. Added to canned foods to protect against botulism (dangerous disease). Helps the body transform food into energy. Dissolves kidney stones. Generally recognized as safe (GRAS)."},
			{"Monosodium glutamate", "MSG enhances the umami (savory meaty flavor) of foods. Popular in Asian cooking. Stimulates nervous system, making you feel full without eating much. May cause weight gain and asthma attacks, and a dry mouth/throat. Safe in small amounts, harmful in large amounts."},
			{"Red 40 lake", "Allura red synthetic dye made from petroleum (car gas). Adds color. Commonly found in dairy products, sweets, snacks, baked goods, and beverages. May cause allergies and worsen behavior of children with ADHD. Safe in small amounts."},
			{"Yellow 6 lake", "Sunset yellow dye. Adds color. Used in candy, sauces, baked goods and preserved fruits. May cause allergies and worsen behavior of children with ADHD. Safe in small amounts."},
			{"Sodium bicarbonate", "Baking soda. Helps baked goods rise. Safe."},
			{"Disodium inosinate", "Umami (savory meaty flavor) enhancer. Used to avoid putting MSG in the ingredients list. Common in fast foods, including pizza and snacks like potato chips. Some allergies and side effects have been reported. Generally recognized as safe (GRAS)."},
			{"Disodium guanylate", "Amplifies salt's salty taste. Commonly combined with MSG to enhance food flavor. Found in processed foods. Considered safe, but acceptable quantity of intake has yet to be determined due to lack of research."},
			{"TBHQ", "Tertiary butylhydroquinone. Acts as an antioxidant but not natural. Extends shelf life of food (preservative). May damage liver and nervous system. Safety is questionable and banned in many countries."}
	}).collect(Collectors.toMap(data -> data[0], data -> data[1]));

	private String[] keyIngredients(String basicResult) {
		basicResult = basicResult.replaceAll("\\s+", "");
		ArrayList<String> ingredientList = new ArrayList<>();
		for (String ingredient : ingredients.keySet())
			if (basicResult.contains(ingredient.toLowerCase().replaceAll("\\s+", "")))
				ingredientList.add(ingredient);
		alertDialog("2", basicResult);
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