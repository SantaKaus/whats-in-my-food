package li.raymond.whatsinmyfood;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

	static final int REQUEST_IMAGE_CAPTURE = 1;
	Button takePhotoButton;
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
		analyzeImageButton.setVisibility(View.GONE);
		imagePreview = findViewById(R.id.preview);

		selectImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				analyzeImageButton.setVisibility(View.VISIBLE);
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

	private void chooseImage() {
		Intent chooseImageIntent = new Intent();
		chooseImageIntent.setType("image/*");
		chooseImageIntent.setAction(Intent.ACTION_GET_CONTENT);

		// pass the constant to compare it with the returned requestCode
		startActivityForResult(Intent.createChooser(chooseImageIntent, "Select Picture"), SELECT_PICTURE);
	}

	// this function is triggered when user selects the image from the imageChooser
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && (requestCode == SELECT_PICTURE || requestCode == REQUEST_IMAGE_CAPTURE)) {
			// Get the url of the image from data
			imageUri = data.getData();
			if (null != imageUri) {
				// update the preview image in the layout
				imagePreview.setImageURI(imageUri);
			}
		}
	}

	private void analyzeIngredients() {
		Intent intent = new Intent(this, AnalyzeActivity.class);
		intent.putExtra("imageUri", imageUri.toString());
		startActivity(intent);
	}


	private File createImageFile() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		alertDialog("", String.valueOf(storageDir));
		File image = File.createTempFile(imageFileName, ".jpg", storageDir);
		if (image == null) {
			alertDialog("", "Image is null");
		}
		imageUri = Uri.fromFile(image);
		return image;
	}

	private void takePhoto() {
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				alertDialog(getString(R.string.createImageFileFailedTitle), getString(R.string.createImageFileFailedMsg) + ex.getMessage());
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				try {
					takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
				} catch (Exception e) {
					alertDialog("Error", e.getMessage());
				}
			}
		}
	}

	// Dialog used to send the user a message
	private void alertDialog(String title, String message) {
		AlertDialog alert = new AlertDialog.Builder(this).setMessage(message)
				.setTitle(title).setCancelable(false)
				.setPositiveButton("Ok", (dialog, id) -> {}).create();
		alert.show();
		((TextView) alert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
	}
}