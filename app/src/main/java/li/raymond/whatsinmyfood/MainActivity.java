package li.raymond.whatsinmyfood;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

	Button selectImageButton;
	Button analyzeImageButton;
	ImageView imagePreview;
	int SELECT_PICTURE = 200;
	Uri imageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		selectImageButton = findViewById(R.id.selectImage);
		analyzeImageButton = findViewById(R.id.analyzeImage);
		imagePreview = findViewById(R.id.preview);

		selectImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chooseImage();
			}
		});

		analyzeImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				analyzeIngredients();
			}
		});
	}

	void chooseImage() {

		Intent i = new Intent();
		i.setType("image/*");
		i.setAction(Intent.ACTION_GET_CONTENT);

		// pass the constant to compare it with the returned requestCode
		startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
	}

	// this function is triggered when user selects the image from the imageChooser
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			if (requestCode == SELECT_PICTURE) {
				// Get the url of the image from data
				imageUri = data.getData();
				if (null != imageUri) {
					// update the preview image in the layout
					imagePreview.setImageURI(imageUri);
				}
			}
		}
	}

	public void analyzeIngredients() {
		Intent intent = new Intent(this, AnalyzeActivity.class);
		intent.putExtra("imageUri", imageUri.toString());
		startActivity(intent);
	}
}